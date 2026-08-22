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
 * Form for adding a brand-new crop record. Dates are picked through
 * DatePickerDialog (never typed) so the stored format is always valid,
 * and the remaining fields are validated before saving.
 */
public class AddCropActivity extends AppCompatActivity {

    private EditText etCropName, etPlotName, etAreaPlanted;
    private Button btnPlantingDate, btnHarvestDate, btnSaveCrop;

    private String plantingDateIso = null;
    private String harvestDateIso = null;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        dbHelper = new DatabaseHelper(this);

        etCropName = findViewById(R.id.etCropName);
        etPlotName = findViewById(R.id.etPlotName);
        etAreaPlanted = findViewById(R.id.etAreaPlanted);
        btnPlantingDate = findViewById(R.id.btnPlantingDate);
        btnHarvestDate = findViewById(R.id.btnHarvestDate);
        btnSaveCrop = findViewById(R.id.btnSaveCrop);

        btnPlantingDate.setOnClickListener(v -> showDatePicker(true));
        btnHarvestDate.setOnClickListener(v -> showDatePicker(false));
        btnSaveCrop.setOnClickListener(v -> saveCrop());
    }

    private void showDatePicker(boolean isPlantingDate) {
        Calendar calendar = Calendar.getInstance();
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

    private void saveCrop() {
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
        if (plantingDateIso == null) {
            Toast.makeText(this, "Please select a planting date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (harvestDateIso == null) {
            Toast.makeText(this, "Please select an expected harvest date", Toast.LENGTH_SHORT).show();
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

        Crop crop = new Crop();
        crop.setCropName(cropName);
        crop.setPlotName(plotName);
        crop.setPlantingDate(plantingDateIso);
        crop.setExpectedHarvestDate(harvestDateIso);
        crop.setAreaPlanted(areaPlanted);

        long id = dbHelper.addCrop(crop);
        if (id > 0) {
            Toast.makeText(this, "Crop saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save crop. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}