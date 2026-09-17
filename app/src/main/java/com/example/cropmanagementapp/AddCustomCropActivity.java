package com.example.cropmanagementapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropmanagementapp.catalog.CropCatalog;
import com.example.cropmanagementapp.catalog.CropImageResolver;
import com.example.cropmanagementapp.catalog.ImageStorageUtils;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.ValidationUtils;

/**
 * Lets a farmer add a crop that isn't in the built-in catalog, with an
 * optional real photo picked from the phone's gallery.
 */
public class AddCustomCropActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_PHOTO = 200;

    private ImageView ivPhotoPreview;
    private EditText etCropName;
    private Spinner spinnerCategory;
    private DatabaseHelper dbHelper;
    private String pickedImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_crop);

        dbHelper = new DatabaseHelper(this);

        ivPhotoPreview = findViewById(R.id.ivPhotoPreview);
        etCropName = findViewById(R.id.etCropName);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnSaveCustomCrop = findViewById(R.id.btnSaveCustomCrop);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CropCatalog.getCategories());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        findViewById(R.id.framePhoto).setOnClickListener(v -> pickPhoto());

        btnCancel.setOnClickListener(v -> finish());
        btnSaveCustomCrop.setOnClickListener(v -> saveCustomCrop());
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            if (selectedUri != null) {
                String savedPath = ImageStorageUtils.copyToInternalStorage(this, selectedUri);
                if (savedPath != null) {
                    pickedImagePath = savedPath;
                    ivPhotoPreview.setPadding(0, 0, 0, 0);
                    ivPhotoPreview.setImageURI(Uri.fromFile(new java.io.File(savedPath)));
                } else {
                    Toast.makeText(this, "Could not load that photo. Please try another.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveCustomCrop() {
        String name = etCropName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etCropName.setError("Please enter a crop name");
            etCropName.requestFocus();
            return;
        }
        if (!ValidationUtils.containsLetter(name)) {
            etCropName.setError("Crop name must include letters, not just numbers");
            etCropName.requestFocus();
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();
        long id = dbHelper.addCustomCropType(name, category, pickedImagePath == null ? "" : pickedImagePath);

        if (id > 0) {
            Toast.makeText(this, "Custom crop added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "That crop name already exists", Toast.LENGTH_SHORT).show();
        }
    }
}