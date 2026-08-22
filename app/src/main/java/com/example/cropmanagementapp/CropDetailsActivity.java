package com.example.cropmanagementapp;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.List;

/**
 * Shows full details for one crop plus its activity log, and provides
 * entry points to edit, delete, or log a new farm activity.
 */
public class CropDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private long cropId;
    private Crop currentCrop;

    private TextView tvCropName, tvPlotName, tvStatusBadge, tvPlantingDate, tvHarvestDate, tvAreaPlanted, tvNoActivities;
    private RecyclerView rvActivities;
    private ActivityLogAdapter activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_details);

        dbHelper = new DatabaseHelper(this);
        cropId = getIntent().getLongExtra("crop_id", -1);

        tvCropName = findViewById(R.id.tvCropName);
        tvPlotName = findViewById(R.id.tvPlotName);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvPlantingDate = findViewById(R.id.tvPlantingDate);
        tvHarvestDate = findViewById(R.id.tvHarvestDate);
        tvAreaPlanted = findViewById(R.id.tvAreaPlanted);
        tvNoActivities = findViewById(R.id.tvNoActivities);
        rvActivities = findViewById(R.id.rvActivities);

        Button btnEditCrop = findViewById(R.id.btnEditCrop);
        Button btnDeleteCrop = findViewById(R.id.btnDeleteCrop);
        Button btnAddActivity = findViewById(R.id.btnAddActivity);

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
        tvPlotName.setText("Plot: " + currentCrop.getPlotName());
        tvPlantingDate.setText("Planted: " + DateUtils.toDisplayFormat(currentCrop.getPlantingDate()));
        tvHarvestDate.setText("Expected harvest: " + DateUtils.toDisplayFormat(currentCrop.getExpectedHarvestDate()));
        tvAreaPlanted.setText("Area planted: " + currentCrop.getAreaPlanted());

        int daysLeft = DateUtils.daysUntil(currentCrop.getExpectedHarvestDate());
        String badgeText;
        int color;
        if (daysLeft == Integer.MIN_VALUE) {
            badgeText = "Unknown";
            color = 0xFF9E9E9E;
        } else if (daysLeft < 0) {
            badgeText = "Overdue by " + Math.abs(daysLeft) + " day(s)";
            color = 0xFFC62828;
        } else if (daysLeft == 0) {
            badgeText = "Harvest due today!";
            color = 0xFFF9A825;
        } else {
            badgeText = daysLeft + " day(s) to harvest";
            color = (daysLeft <= 7) ? 0xFFF9A825 : 0xFF1B5E20;
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