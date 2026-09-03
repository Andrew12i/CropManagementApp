package com.example.cropmanagementapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.adapter.ActivityLogAdapter;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.ActivityLog;
import com.example.cropmanagementapp.model.Crop;

import java.util.Calendar;
import java.util.List;

public class CropDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private long cropId;
    private Crop currentCrop;

    private TextView tvCropName, tvVariety, tvPlotName, tvStatusBadge, tvPlantingDate,
            tvHarvestDate, tvAreaPlanted, tvTotalExpenses, tvNoActivities;
    private Button btnHarvestAction, btnUndoHarvest;
    private RecyclerView rvActivities;
    private ActivityLogAdapter activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_details);

        dbHelper = new DatabaseHelper(this);
        cropId = getIntent().getLongExtra("crop_id", -1);

        tvCropName = findViewById(R.id.tvCropName);
        tvVariety = findViewById(R.id.tvVariety);
        tvPlotName = findViewById(R.id.tvPlotName);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvPlantingDate = findViewById(R.id.tvPlantingDate);
        tvHarvestDate = findViewById(R.id.tvHarvestDate);
        tvAreaPlanted = findViewById(R.id.tvAreaPlanted);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvNoActivities = findViewById(R.id.tvNoActivities);
        rvActivities = findViewById(R.id.rvActivities);

        Button btnEditCrop = findViewById(R.id.btnEditCrop);
        Button btnDeleteCrop = findViewById(R.id.btnDeleteCrop);
        Button btnAddActivity = findViewById(R.id.btnAddActivity);
        btnHarvestAction = findViewById(R.id.btnHarvestAction);
        btnUndoHarvest = findViewById(R.id.btnUndoHarvest);

        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        activityAdapter = new ActivityLogAdapter(new java.util.ArrayList<>());
        rvActivities.setAdapter(activityAdapter);

        btnEditCrop.setOnClickListener(v -> {
            Intent intent = new Intent(CropDetailsActivity.this, EditCropActivity.class);
            intent.putExtra("crop_id", cropId);
            startActivity(intent);
        });

        btnDeleteCrop.setOnClickListener(v -> confirmDelete());

        btnAddActivity.setOnClickListener(v -> {
            Intent intent = new Intent(CropDetailsActivity.this, AddActivityLogActivity.class);
            intent.putExtra("crop_id", cropId);
            startActivity(intent);
        });

        btnHarvestAction.setOnClickListener(v -> showHarvestDialog());
        btnUndoHarvest.setOnClickListener(v -> confirmUndoHarvest());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCropDetails();
        loadActivities();
    }

    private void loadCropDetails() {
        currentCrop = dbHelper.getCrop(cropId);
        if (currentCrop == null) {
            Toast.makeText(this, "This crop record no longer exists.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvCropName.setText(currentCrop.getCropName());
        tvVariety.setText("Variety: " + (TextUtils.isEmpty(currentCrop.getVariety()) ? "Not specified" : currentCrop.getVariety()));
        tvPlotName.setText("Plot: " + currentCrop.getPlotName());
        tvPlantingDate.setText("Planted: " + DateUtils.toDisplayFormat(currentCrop.getPlantingDate()));
        tvHarvestDate.setText("Expected harvest: " + DateUtils.toDisplayFormat(currentCrop.getExpectedHarvestDate()));
        tvAreaPlanted.setText("Area planted: " + currentCrop.getAreaPlanted());

        double totalExpenses = dbHelper.getTotalExpensesForCrop(cropId);
        tvTotalExpenses.setText(String.format("Total expenses logged: KES %.2f", totalExpenses));

        String badgeText;
        int color;

        if (currentCrop.isHarvested()) {
            String yieldText = TextUtils.isEmpty(currentCrop.getYieldAmount()) ? "not recorded" : currentCrop.getYieldAmount();
            badgeText = "Harvested " + DateUtils.toDisplayFormat(currentCrop.getHarvestedDate()) + " — Yield: " + yieldText;
            color = 0xFF1B5E20;
            btnHarvestAction.setText("Edit Harvest Record");
            btnUndoHarvest.setVisibility(View.VISIBLE);
        } else {
            int daysLeft = DateUtils.daysUntil(currentCrop.getExpectedHarvestDate());
            if (daysLeft == Integer.MIN_VALUE) {
                badgeText = "Unknown";
                color = 0xFF616161;
            } else if (daysLeft < 0) {
                badgeText = "Overdue by " + Math.abs(daysLeft) + " day(s)";
                color = 0xFFB71C1C;
            } else if (daysLeft == 0) {
                badgeText = "Harvest due today!";
                color = 0xFFE65100;
            } else {
                badgeText = daysLeft + " day(s) to harvest";
                color = (daysLeft <= 7) ? 0xFFE65100 : 0xFF1B5E20;
            }
            btnHarvestAction.setText("Mark as Harvested");
            btnUndoHarvest.setVisibility(View.GONE);
        }

        tvStatusBadge.setText(badgeText);
        GradientDrawable bg = (GradientDrawable) tvStatusBadge.getBackground().mutate();
        bg.setColor(color);
    }

    private void loadActivities() {
        List<ActivityLog> activities = dbHelper.getActivitiesForCrop(cropId);
        activityAdapter.updateData(activities);

        if (activities.isEmpty()) {
            tvNoActivities.setVisibility(View.VISIBLE);
            rvActivities.setVisibility(View.GONE);
        } else {
            tvNoActivities.setVisibility(View.GONE);
            rvActivities.setVisibility(View.VISIBLE);
        }
    }

    private void showHarvestDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_mark_harvested, null);
        Button btnDialogDate = dialogView.findViewById(R.id.btnDialogHarvestDate);
        EditText etDialogYield = dialogView.findViewById(R.id.etDialogYield);

        final String[] chosenDate = { currentCrop.isHarvested() && !TextUtils.isEmpty(currentCrop.getHarvestedDate())
                ? currentCrop.getHarvestedDate() : DateUtils.todayIso() };
        btnDialogDate.setText(DateUtils.toDisplayFormat(chosenDate[0]));
        if (currentCrop.isHarvested() && !TextUtils.isEmpty(currentCrop.getYieldAmount())) {
            etDialogYield.setText(currentCrop.getYieldAmount());
        }

        btnDialogDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (DateUtils.isValidIsoDate(chosenDate[0])) {
                String[] parts = chosenDate[0].split("-");
                calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
            }
            new DatePickerDialog(this, (view, year, month, day) -> {
                chosenDate[0] = String.format("%04d-%02d-%02d", year, month + 1, day);
                btnDialogDate.setText(DateUtils.toDisplayFormat(chosenDate[0]));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(this)
                .setTitle(currentCrop.isHarvested() ? "Edit Harvest Record" : "Mark as Harvested")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String yield = etDialogYield.getText().toString().trim();
                    if (TextUtils.isEmpty(yield)) {
                        Toast.makeText(this, "Please enter a yield amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dbHelper.markCropHarvested(cropId, chosenDate[0], yield);
                    Toast.makeText(this, "Harvest recorded", Toast.LENGTH_SHORT).show();
                    loadCropDetails();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmUndoHarvest() {
        new AlertDialog.Builder(this)
                .setTitle("Undo harvest status")
                .setMessage("This will move the crop back to your active crop list and clear its recorded yield and harvest date.")
                .setPositiveButton("Undo", (dialog, which) -> {
                    dbHelper.unmarkCropHarvested(cropId);
                    Toast.makeText(this, "Harvest status undone", Toast.LENGTH_SHORT).show();
                    loadCropDetails();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete crop record")
                .setMessage("This will permanently delete \"" + currentCrop.getCropName() +
                        "\" and all its logged activities. This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteCrop(cropId);
                    Toast.makeText(this, "Crop deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}