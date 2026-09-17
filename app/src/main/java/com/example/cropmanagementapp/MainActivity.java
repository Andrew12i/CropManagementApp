package com.example.cropmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.cropmanagementapp.adapter.CropAdapter;
import com.example.cropmanagementapp.auth.SessionManager;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.Crop;

import java.util.List;

/**
 * Dashboard / Home tab: shows quick totals and crops with harvests due
 * soon, plus entry points to add or browse crops. Settings (report +
 * logout) is reached via the hamburger icon.
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private TextView tvTotalCrops, tvTotalPlots, tvNoUpcoming;
    private RecyclerView rvUpcomingHarvests;
    private CropAdapter adapter;

    private static final int UPCOMING_WINDOW_DAYS = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        tvTotalCrops = findViewById(R.id.tvTotalCrops);
        tvTotalPlots = findViewById(R.id.tvTotalPlots);
        tvNoUpcoming = findViewById(R.id.tvNoUpcoming);
        rvUpcomingHarvests = findViewById(R.id.rvUpcomingHarvests);

        Button btnAddCrop = findViewById(R.id.btnAddCrop);
        Button btnViewCrops = findViewById(R.id.btnViewCrops);
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

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

        btnMenu.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        BottomNavHelper.setup(bottomNav, this, R.id.nav_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHelper != null) {
            loadSummary();
        }
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