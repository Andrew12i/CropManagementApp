package com.example.cropmanagementapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.adapter.FarmAdapter;
import com.example.cropmanagementapp.auth.FarmManager;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.ValidationUtils;
import com.example.cropmanagementapp.model.Farm;

import java.util.ArrayList;
import java.util.List;

/** Manage farms: switch between them, add a new one, edit, or delete an empty one. */
public class FarmsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private FarmManager farmManager;
    private RecyclerView rvFarms;
    private FarmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farms);

        dbHelper = new DatabaseHelper(this);
        farmManager = new FarmManager(this);

        rvFarms = findViewById(R.id.rvFarms);
        Button btnAddFarm = findViewById(R.id.btnAddFarm);

        rvFarms.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FarmAdapter(new ArrayList<>(), farmManager.getCurrentFarmId(),
                this::switchToFarm, this::showFarmOptions);
        rvFarms.setAdapter(adapter);

        btnAddFarm.setOnClickListener(v -> showAddFarmDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFarms();
    }

    private void loadFarms() {
        List<Farm> farms = dbHelper.getAllFarms();
        adapter.updateData(farms, farmManager.getCurrentFarmId());
    }

    private void switchToFarm(Farm farm) {
        farmManager.setCurrentFarm(farm.getId(), farm.getName());
        Toast.makeText(this, "Switched to " + farm.getName(), Toast.LENGTH_SHORT).show();
        loadFarms();
    }

    private void showAddFarmDialog() {
        showFarmDialog(null);
    }

    private void showFarmOptions(Farm farm) {
        String[] options = {"Edit", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(farm.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showFarmDialog(farm);
                    } else {
                        confirmDeleteFarm(farm);
                    }
                })
                .show();
    }

    private void showFarmDialog(Farm existingFarm) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        EditText etName = new EditText(this);
        etName.setHint("Farm name");
        layout.addView(etName);

        EditText etLocation = new EditText(this);
        etLocation.setHint("Location (optional)");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (14 * getResources().getDisplayMetrics().density);
        etLocation.setLayoutParams(params);
        layout.addView(etLocation);

        if (existingFarm != null) {
            etName.setText(existingFarm.getName());
            etLocation.setText(existingFarm.getLocation());
        }

        new AlertDialog.Builder(this)
                .setTitle(existingFarm == null ? "Add Farm" : "Edit Farm")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(this, "Please enter a farm name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!ValidationUtils.containsLetter(name)) {
                        Toast.makeText(this, "Farm name must include letters, not just numbers", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (existingFarm == null) {
                        Farm farm = new Farm();
                        farm.setName(name);
                        farm.setLocation(location);
                        long id = dbHelper.addFarm(farm);
                        if (id > 0) {
                            Toast.makeText(this, "Farm added", Toast.LENGTH_SHORT).show();
                            loadFarms();
                        }
                    } else {
                        existingFarm.setName(name);
                        existingFarm.setLocation(location);
                        dbHelper.updateFarm(existingFarm);
                        if (existingFarm.getId() == farmManager.getCurrentFarmId()) {
                            farmManager.setCurrentFarm(existingFarm.getId(), name);
                        }
                        Toast.makeText(this, "Farm updated", Toast.LENGTH_SHORT).show();
                        loadFarms();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteFarm(Farm farm) {
        int cropCount = dbHelper.getCropCountForFarm(farm.getId());
        if (cropCount > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Can't Delete Farm")
                    .setMessage("\"" + farm.getName() + "\" still has " + cropCount +
                            " crop record(s). Delete or move those crops first.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        List<Farm> allFarms = dbHelper.getAllFarms();
        if (allFarms.size() <= 1) {
            Toast.makeText(this, "You must keep at least one farm.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Farm")
                .setMessage("Delete \"" + farm.getName() + "\"? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteFarmIfEmpty(farm.getId());
                    if (deleted) {
                        Toast.makeText(this, "Farm deleted", Toast.LENGTH_SHORT).show();
                        if (farm.getId() == farmManager.getCurrentFarmId()) {
                            long newCurrent = farmManager.getCurrentFarmId(); // self-heals to another farm
                        }
                        loadFarms();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}