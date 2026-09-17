package com.example.cropmanagementapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.adapter.CropCatalogAdapter;
import com.example.cropmanagementapp.catalog.CropCatalog;
import com.example.cropmanagementapp.catalog.CropCatalogItem;
import com.example.cropmanagementapp.catalog.ImageStorageUtils;
import com.example.cropmanagementapp.db.DatabaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A searchable, category-filtered grid of crops (built-in + farmer-added
 * custom ones) used to pick a crop type. Long-press any card to change
 * its photo, delete it (custom crops), or hide it (default crops).
 */
public class BrowseCropsActivity extends AppCompatActivity {

    public static final String EXTRA_CROP_NAME = "crop_name";
    public static final String EXTRA_CROP_CATEGORY = "crop_category";
    public static final String EXTRA_CROP_IMAGE_PATH = "crop_image_path";

    private static final int REQUEST_PICK_PHOTO_FOR_TYPE = 300;

    private DatabaseHelper dbHelper;
    private RecyclerView rvCatalog;
    private LinearLayout llCategoryFilters;
    private EditText etSearch;

    private CropCatalogAdapter adapter;
    private List<CropCatalogItem> allItems;
    private String selectedCategory = "All";
    private String pendingImageCropName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_crops);

        dbHelper = new DatabaseHelper(this);
        rvCatalog = findViewById(R.id.rvCatalog);
        llCategoryFilters = findViewById(R.id.llCategoryFilters);
        etSearch = findViewById(R.id.etSearch);
        Button btnAddCustomCrop = findViewById(R.id.btnAddCustomCrop);

        rvCatalog.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new CropCatalogAdapter(new ArrayList<>(), this::onCropChosen, this::onCropLongPressed);
        rvCatalog.setAdapter(adapter);

        buildCategoryFilters();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnAddCustomCrop.setOnClickListener(v ->
                startActivity(new Intent(BrowseCropsActivity.this, AddCustomCropActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllItems();
        applyFilters();
    }

    private void loadAllItems() {
        Set<String> hidden = dbHelper.getHiddenCropTypes();
        List<CropCatalogItem> combined = new ArrayList<>();

        for (CropCatalogItem defaultItem : CropCatalog.getDefaultItems()) {
            if (hidden.contains(defaultItem.getName())) continue;
            String overrideImage = dbHelper.getCropTypeImage(defaultItem.getName());
            combined.add(new CropCatalogItem(defaultItem.getName(), defaultItem.getCategory(), false, overrideImage));
        }

        for (String[] custom : dbHelper.getCustomCropTypes()) {
            String imagePath = custom.length > 2 && !custom[2].isEmpty() ? custom[2] : dbHelper.getCropTypeImage(custom[0]);
            combined.add(new CropCatalogItem(custom[0], custom[1], true, imagePath));
        }

        allItems = combined;
    }

    private void buildCategoryFilters() {
        llCategoryFilters.removeAllViews();
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.addAll(CropCatalog.getCategories());

        for (String category : categories) {
            TextView pill = new TextView(this);
            pill.setText(category);
            pill.setTextColor(getResources().getColor(R.color.text_primary));
            pill.setBackgroundResource(R.drawable.filter_pill_background);
            pill.setPadding(28, 14, 28, 14);
            pill.setSelected(category.equals(selectedCategory));
            if (pill.isSelected()) pill.setTextColor(getResources().getColor(R.color.white));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 12, 0);
            pill.setLayoutParams(params);
            pill.setGravity(Gravity.CENTER);

            pill.setOnClickListener(v -> {
                selectedCategory = category;
                buildCategoryFilters();
                applyFilters();
            });

            llCategoryFilters.addView(pill);
        }
    }

    private void applyFilters() {
        if (allItems == null) return;
        String searchTerm = etSearch.getText().toString().trim().toLowerCase();
        List<CropCatalogItem> filtered = new ArrayList<>();

        for (CropCatalogItem item : allItems) {
            boolean matchesCategory = "All".equals(selectedCategory) || item.getCategory().equals(selectedCategory);
            boolean matchesSearch = searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm);
            if (matchesCategory && matchesSearch) {
                filtered.add(item);
            }
        }
        adapter.updateData(filtered);
    }

    private void onCropChosen(CropCatalogItem item) {
        Intent result = new Intent();
        result.putExtra(EXTRA_CROP_NAME, item.getName());
        result.putExtra(EXTRA_CROP_CATEGORY, item.getCategory());
        result.putExtra(EXTRA_CROP_IMAGE_PATH, item.getImagePath());
        setResult(RESULT_OK, result);
        finish();
    }

    private void onCropLongPressed(CropCatalogItem item) {
        List<String> options = new ArrayList<>();
        options.add("Change Photo");
        if (item.isCustom()) {
            options.add("Delete Crop");
        } else {
            options.add("Hide from List");
        }

        new AlertDialog.Builder(this)
                .setTitle(item.getName())
                .setItems(options.toArray(new String[0]), (dialog, which) -> {
                    String chosen = options.get(which);
                    if ("Change Photo".equals(chosen)) {
                        pendingImageCropName = item.getName();
                        pickPhotoForType();
                    } else if ("Delete Crop".equals(chosen)) {
                        confirmDeleteCustomCrop(item.getName());
                    } else if ("Hide from List".equals(chosen)) {
                        confirmHideDefaultCrop(item.getName());
                    }
                })
                .show();
    }

    private void pickPhotoForType() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PHOTO_FOR_TYPE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_PHOTO_FOR_TYPE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            if (selectedUri != null && pendingImageCropName != null) {
                String savedPath = ImageStorageUtils.copyToInternalStorage(this, selectedUri);
                if (savedPath != null) {
                    dbHelper.setCropTypeImage(pendingImageCropName, savedPath);
                    Toast.makeText(this, "Photo updated", Toast.LENGTH_SHORT).show();
                    loadAllItems();
                    applyFilters();
                } else {
                    Toast.makeText(this, "Could not load that photo. Please try another.", Toast.LENGTH_SHORT).show();
                }
            }
            pendingImageCropName = null;
        }
    }

    private void confirmDeleteCustomCrop(String cropName) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Crop")
                .setMessage("Remove \"" + cropName + "\" from your crop list? This only removes it from " +
                        "the picker — any crop records you've already saved under this name are kept.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteCustomCropType(cropName);
                    Toast.makeText(this, "Crop removed", Toast.LENGTH_SHORT).show();
                    loadAllItems();
                    applyFilters();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmHideDefaultCrop(String cropName) {
        new AlertDialog.Builder(this)
                .setTitle("Hide from List")
                .setMessage("Hide \"" + cropName + "\" from your crop picker? You can still see any crop " +
                        "records already saved under this name.")
                .setPositiveButton("Hide", (dialog, which) -> {
                    dbHelper.hideCropType(cropName);
                    Toast.makeText(this, "Crop hidden", Toast.LENGTH_SHORT).show();
                    loadAllItems();
                    applyFilters();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}