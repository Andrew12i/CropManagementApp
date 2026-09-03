package com.example.cropmanagementapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.adapter.CropCatalogAdapter;
import com.example.cropmanagementapp.catalog.CropCatalog;
import com.example.cropmanagementapp.catalog.CropCatalogItem;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A searchable, category-filtered grid of crops (built-in + farmer-added
 * custom ones) that the Add/Edit Crop screens launch to pick a crop type.
 * Returns the chosen crop's name and category via the result Intent.
 */
public class BrowseCropsActivity extends AppCompatActivity {

    public static final String EXTRA_CROP_NAME = "crop_name";
    public static final String EXTRA_CROP_CATEGORY = "crop_category";

    private DatabaseHelper dbHelper;
    private RecyclerView rvCatalog;
    private LinearLayout llCategoryFilters;
    private EditText etSearch;

    private CropCatalogAdapter adapter;
    private List<CropCatalogItem> allItems;
    private String selectedCategory = "All";

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
        adapter = new CropCatalogAdapter(new ArrayList<>(), this::onCropChosen);
        rvCatalog.setAdapter(adapter);

        loadAllItems();
        buildCategoryFilters();
        applyFilters();

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

        btnAddCustomCrop.setOnClickListener(v -> showAddCustomCropDialog());
    }

    private void loadAllItems() {
        allItems = new ArrayList<>(CropCatalog.getDefaultItems());
        for (String[] custom : dbHelper.getCustomCropTypes()) {
            allItems.add(new CropCatalogItem(custom[0], custom[1], true));
        }
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
        setResult(RESULT_OK, result);
        finish();
    }

    private void showAddCustomCropDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        EditText etName = new EditText(this);
        etName.setHint("Crop name");
        layout.addView(etName);

        TextView tvCategoryLabel = new TextView(this);
        tvCategoryLabel.setText("Category");
        tvCategoryLabel.setPadding(0, 24, 0, 8);
        layout.addView(tvCategoryLabel);

        Spinner spinnerCategory = new Spinner(this);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CropCatalog.getCategories());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        layout.addView(spinnerCategory);

        new AlertDialog.Builder(this)
                .setTitle("Add Custom Crop")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(this, "Please enter a crop name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!ValidationUtils.containsLetter(name)) {
                        Toast.makeText(this, "Crop name must include letters, not just numbers", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String category = spinnerCategory.getSelectedItem().toString();
                    dbHelper.addCustomCropType(name, category);
                    Toast.makeText(this, "Custom crop added", Toast.LENGTH_SHORT).show();

                    loadAllItems();
                    applyFilters();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}