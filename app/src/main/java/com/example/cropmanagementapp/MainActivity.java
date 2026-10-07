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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cropmanagementapp.adapter.CropAdapter;
import com.example.cropmanagementapp.auth.FarmManager;
import com.example.cropmanagementapp.auth.SessionManager;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.Crop;
import com.example.cropmanagementapp.notifications.CropAlarmScheduler;
import com.example.cropmanagementapp.notifications.CropCheckReceiver;

import java.util.List;

/**
 * Dashboard / Home tab: shows quick totals and crops with harvests due
 * soon for the currently selected farm, plus entry points to add or
 * browse crops. Settings (farms + report + logout) is via the hamburger.
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private FarmManager farmManager;
    private TextView tvTotalCrops, tvTotalPlots, tvNoUpcoming, tvCurrentFarm;
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
        farmManager = new FarmManager(this);

        tvTotalCrops = findViewById(R.id.tvTotalCrops);
        tvTotalPlots = findViewById(R.id.tvTotalPlots);
        tvNoUpcoming = findViewById(R.id.tvNoUpcoming);
        tvCurrentFarm = findViewById(R.id.tvCurrentFarm);
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

        requestNotificationPermissionIfNeeded();
        CropAlarmScheduler.scheduleDaily(this);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2001);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHelper != null) {
            loadSummary();
            new Thread(() -> CropCheckReceiver.runCheck(getApplicationContext())).start();
        }
    }

    private void loadSummary() {
        long farmId = farmManager.getCurrentFarmId();
        tvCurrentFarm.setText(farmManager.getCurrentFarmName());

        int totalCrops = dbHelper.getTotalCropCount(farmId);
        int totalPlots = dbHelper.getDistinctPlotCount(farmId);
        tvTotalCrops.setText(String.valueOf(totalCrops));
        tvTotalPlots.setText(String.valueOf(totalPlots));

        String today = DateUtils.todayIso();
        String cutoff = DateUtils.isoDateNDaysFromNow(UPCOMING_WINDOW_DAYS);
        List<Crop> upcoming = dbHelper.getUpcomingHarvests(today, cutoff, farmId);

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