package com.example.cropmanagementapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.adapter.CropAdapter;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.Crop;
import com.example.cropmanagementapp.report.PdfPrintDocumentAdapter;
import com.example.cropmanagementapp.report.ReportGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        ImageButton btnMenu = findViewById(R.id.btnMenu);

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

        btnMenu.setOnClickListener(this::showDashboardMenu);
    }

    private void showDashboardMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.dashboard_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_all_crops) {
                startActivity(new Intent(MainActivity.this, CropListActivity.class));
                return true;
            } else if (id == R.id.menu_archive) {
                startActivity(new Intent(MainActivity.this, ArchiveActivity.class));
                return true;
            } else if (id == R.id.menu_generate_report) {
                generateAndOfferReport();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void generateAndOfferReport() {
        Toast.makeText(this, "Generating report...", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                ReportGenerator generator = new ReportGenerator(MainActivity.this);
                File reportFile = generator.generateFarmReport();
                runOnUiThread(() -> offerReportActions(reportFile));
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Could not generate report. Please try again.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void offerReportActions(File reportFile) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", reportFile);
        new AlertDialog.Builder(this)
                .setTitle("Report Ready")
                .setMessage("Your farm report has been generated. What would you like to do?")
                .setPositiveButton("Share", (dialog, which) -> shareReport(uri))
                .setNeutralButton("Print", (dialog, which) -> printReport(reportFile))
                .setNegativeButton("Close", null)
                .show();
    }

    private void shareReport(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Farm Report"));
    }

    private void printReport(File reportFile) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        if (printManager != null) {
            printManager.print("Farm_Report", new PdfPrintDocumentAdapter(reportFile, "Farm_Report"), null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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