package com.example.cropmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.adapter.CropAdapter;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.Crop;

import java.util.List;

/**
 * Dashboard / home screen: shows quick totals and the crops that are due
 * for harvest soon, plus entry points to add or browse crops.
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvTotalCrops, tvTotalPlots, tvNoUpcoming;
    private RecyclerView rvUpcomingHarvests;
    private CropAdapter adapter;

    private static final int UPCOMING_WINDOW_DAYS = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        tvTotalCrops = findViewById(R.id.tvTotalCrops);
        tvTotalPlots = findViewById(R.id.tvTotalPlots);
        tvNoUpcoming = findViewById(R.id.tvNoUpcoming);
        rvUpcomingHarvests = findViewById(R.id.rvUpcomingHarvests);

        Button btnAddCrop = findViewById(R.id.btnAddCrop);
        Button btnViewCrops = findViewById(R.id.btnViewCrops);

        rvUpcomingHarvests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CropAdapter(new java.util.ArrayList<>(), crop -> {
            Intent intent = new Intent(MainActivity.this, CropDetailsActivity.class);
            intent.putExtra("crop_id", crop.getId());
            startActivity(intent);
        });
        rvUpcomingHarvests.setAdapter(adapter);

        btnAddCrop.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddCropActivity.class)));

        btnViewCrops.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CropListActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh every time the dashboard becomes visible so new/edited/
        // deleted crops are always reflected without a manual reload.
        loadSummary();
    }

    private void loadSummary() {
        int totalCrops = dbHelper.getTotalCropCount();
        int totalPlots = dbHelper.getDistinctPlotCount();
        tvTotalCrops.setText(String.valueOf(totalCrops));
        tvTotalPlots.setText(String.valueOf(totalPlots));

        String today = DateUtils.todayIso();
        String cutoff = DateUtils.isoDateNDaysFromNow(UPCOMING_WINDOW_DAYS);
        List<Crop> upcoming = dbHelper.getUpcomingHarvests(today, cutoff);

        adapter.updateData(upcoming);

        if (upcoming.isEmpty()) {
            tvNoUpcoming.setVisibility(View.VISIBLE);
            rvUpcomingHarvests.setVisibility(View.GONE);
        } else {
            tvNoUpcoming.setVisibility(View.GONE);
            rvUpcomingHarvests.setVisibility(View.VISIBLE);
        }
    }
}