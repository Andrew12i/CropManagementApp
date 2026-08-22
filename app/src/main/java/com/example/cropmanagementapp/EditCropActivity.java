package com.example.cropmanagementapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.Crop;

import java.util.Calendar;

/**
 * Pre-fills the form with the existing crop record and saves changes back
 * to the same row. Validation mirrors AddCropActivity.
 */
public class EditCropActivity extends AppCompatActivity {

    private EditText etCropName, etPlotName, etAreaPlanted;
    private Button btnPlantingDate, btnHarvestDate, btnUpdateCrop;

    private String plantingDateIso = null;
    private String harvestDateIso = null;

    private DatabaseHelper dbHelper;
    private long cropId;
    private Crop existingCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_crop);

        dbHelper = new DatabaseHelper(this);
        cropId = getIntent().getLongExtra("crop_id", -1);

        etCropName = findViewById(R.id.etCropName);
        etPlotName = findViewById(R.id.etPlotName);
        etAreaPlanted = findViewById(R.id.etAreaPlanted);
        btnPlantingDate = findViewById(R.id.btnPlantingDate);
        btnHarvestDate = findViewById(R.id.btnHarvestDate);
        btnUpdateCrop = findViewById(R.id.btnUpdateCrop);

        existingCrop = dbHelper.getCrop(cropId);
        if (existingCrop == null) {
            Toast.makeText(this, "This crop record no longer exists.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateFields();

        btnPlantingDate.setOnClickListener(v -> showDatePicker(true));
        btnHarvestDate.setOnClickListener(v -> showDatePicker(false));
        btnUpdateCrop.setOnClickListener(v -> updateCrop());
    }

    private void populateFields() {
        etCropName.setText(existingCrop.getCropName());
        etPlotName.setText(existingCrop.getPlotName());
        etAreaPlanted.setText(existingCrop.getAreaPlanted());

        plantingDateIso = existingCrop.getPlantingDate();
        harvestDateIso = existingCrop.getExpectedHarvestDate();
        btnPlantingDate.setText(DateUtils.toDisplayFormat(plantingDateIso));
        btnHarvestDate.setText(DateUtils.toDisplayFormat(harvestDateIso));
    }

    private void showDatePicker(boolean isPlantingDate) {
        Calendar calendar = Calendar.getInstance();
        String currentIso = isPlantingDate ? plantingDateIso : harvestDateIso;
        if (DateUtils.isValidIsoDate(currentIso)) {
            String[] parts = currentIso.split("-");
            calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String iso = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    String display = DateUtils.toDisplayFormat(iso);
                    if (isPlantingDate) {
                        plantingDateIso = iso;
                        btnPlantingDate.setText(display);
                    } else {
                        harvestDateIso = iso;
                        btnHarvestDate.setText(display);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void updateCrop() {
        String cropName = etCropName.getText().toString().trim();
        String plotName = etPlotName.getText().toString().trim();
        String areaPlanted = etAreaPlanted.getText().toString().trim();

        if (TextUtils.isEmpty(cropName)) {
            etCropName.setError("Crop name is required");
            etCropName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(plotName)) {
            etPlotName.setError("Plot / field name is required");
            etPlotName.requestFocus();
            return;
        }
        if (plantingDateIso == null || harvestDateIso == null) {
            Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!DateUtils.isAfter(harvestDateIso, plantingDateIso)) {
            Toast.makeText(this, "Harvest date must be after the planting date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(areaPlanted)) {
            etAreaPlanted.setError("Area planted is required");
            etAreaPlanted.requestFocus();
            return;
        }

        existingCrop.setCropName(cropName);
        existingCrop.setPlotName(plotName);
        existingCrop.setPlantingDate(plantingDateIso);
        existingCrop.setExpectedHarvestDate(harvestDateIso);
        existingCrop.setAreaPlanted(areaPlanted);

        int rows = dbHelper.updateCrop(existingCrop);
        if (rows > 0) {
            Toast.makeText(this, "Crop updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not update crop. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}