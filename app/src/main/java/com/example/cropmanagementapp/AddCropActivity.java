package com.example.cropmanagementapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.cropmanagementapp.catalog.CropImageResolver;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.db.ValidationUtils;
import com.example.cropmanagementapp.model.Crop;

import java.util.Calendar;

public class AddCropActivity extends AppCompatActivity {

    private static final int REQUEST_BROWSE_CROPS = 100;

    private CardView cardSelectCrop;
    private ImageView ivSelectedCropImage;
    private TextView tvSelectedCropName;
    private EditText etVariety, etPlotName, etAreaPlanted;
    private Button btnPlantingDate, btnHarvestDate, btnSaveCrop;

    private String selectedCropName = null;
    private String selectedCropCategory = null;
    private String plantingDateIso = null;
    private String harvestDateIso = null;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        dbHelper = new DatabaseHelper(this);

        cardSelectCrop = findViewById(R.id.cardSelectCrop);
        ivSelectedCropImage = findViewById(R.id.ivSelectedCropImage);
        tvSelectedCropName = findViewById(R.id.tvSelectedCropName);
        etVariety = findViewById(R.id.etVariety);
        etPlotName = findViewById(R.id.etPlotName);
        etAreaPlanted = findViewById(R.id.etAreaPlanted);
        btnPlantingDate = findViewById(R.id.btnPlantingDate);
        btnHarvestDate = findViewById(R.id.btnHarvestDate);
        btnSaveCrop = findViewById(R.id.btnSaveCrop);

        cardSelectCrop.setOnClickListener(v ->
                startActivityForResult(new Intent(AddCropActivity.this, BrowseCropsActivity.class), REQUEST_BROWSE_CROPS));

        btnPlantingDate.setOnClickListener(v -> showDatePicker(true));
        btnHarvestDate.setOnClickListener(v -> showDatePicker(false));
        btnSaveCrop.setOnClickListener(v -> saveCrop());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BROWSE_CROPS && resultCode == RESULT_OK && data != null) {
            selectedCropName = data.getStringExtra(BrowseCropsActivity.EXTRA_CROP_NAME);
            selectedCropCategory = data.getStringExtra(BrowseCropsActivity.EXTRA_CROP_CATEGORY);
            tvSelectedCropName.setText(selectedCropName);
            tvSelectedCropName.setTextColor(getResources().getColor(R.color.text_primary));
            int imageRes = CropImageResolver.resolve(this, selectedCropName, selectedCropCategory);
            ivSelectedCropImage.setImageResource(imageRes);
        }
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
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveCrop() {
        if (TextUtils.isEmpty(selectedCropName)) {
            Toast.makeText(this, "Please select a crop type", Toast.LENGTH_SHORT).show();
            return;
        }

        String variety = etVariety.getText().toString().trim();
        String plotName = etPlotName.getText().toString().trim();
        String areaPlanted = etAreaPlanted.getText().toString().trim();

        if (TextUtils.isEmpty(variety)) {
            etVariety.setError("Variety is required (enter 'Local' or 'Unknown' if unsure)");
            etVariety.requestFocus();
            return;
        }
        if (!ValidationUtils.containsLetter(variety)) {
            etVariety.setError("Variety must include letters, not just numbers");
            etVariety.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(plotName)) {
            etPlotName.setError("Plot / field name is required");
            etPlotName.requestFocus();
            return;
        }
        if (!ValidationUtils.containsLetter(plotName)) {
            etPlotName.setError("Plot name must include letters, not just numbers");
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
        if (!ValidationUtils.containsLetter(areaPlanted)) {
            etAreaPlanted.setError("Please include a unit, e.g. 2 acres");
            etAreaPlanted.requestFocus();
            return;
        }

        Crop crop = new Crop();
        crop.setCropName(selectedCropName);
        crop.setCategory(selectedCropCategory);
        crop.setVariety(variety);
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