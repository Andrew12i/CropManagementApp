package com.example.cropmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.adapter.CropAdapter;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.model.Crop;

import java.util.List;

/**
 * Full crop list with a live search box that filters by crop name or plot
 * name as the farmer types.
 */
public class CropListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvCrops;
    private EditText etSearch;
    private TextView tvEmptyState;
    private CropAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_list);

        dbHelper = new DatabaseHelper(this);
        rvCrops = findViewById(R.id.rvCrops);
        etSearch = findViewById(R.id.etSearch);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        rvCrops.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CropAdapter(new java.util.ArrayList<>(), crop -> {
            Intent intent = new Intent(CropListActivity.this, CropDetailsActivity.class);
            intent.putExtra("crop_id", crop.getId());
            startActivity(intent);
        });
        rvCrops.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadCrops(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCrops(etSearch.getText().toString());
    }

    private void loadCrops(String searchTerm) {
        List<Crop> crops = dbHelper.getAllCrops(searchTerm);
        adapter.updateData(crops);

        if (crops.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvCrops.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvCrops.setVisibility(View.VISIBLE);
        }
    }
}