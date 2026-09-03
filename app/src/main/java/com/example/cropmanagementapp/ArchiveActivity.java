package com.example.cropmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.adapter.ArchivedCropAdapter;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.model.Crop;
import com.example.cropmanagementapp.view.YieldBarChartView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lists harvested crops (the season's history), shows a bar chart
 * comparing total yield by plot, and a matching text summary, so a
 * farmer can compare seasons at a glance or read exact numbers.
 */
public class ArchiveActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvArchivedCrops;
    private LinearLayout llYieldSummary;
    private YieldBarChartView chartYieldByPlot;
    private TextView tvEmptyState;
    private ArchivedCropAdapter adapter;

    private static final Pattern LEADING_NUMBER = Pattern.compile("([0-9]+(\\.[0-9]+)?)");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        dbHelper = new DatabaseHelper(this);
        rvArchivedCrops = findViewById(R.id.rvArchivedCrops);
        llYieldSummary = findViewById(R.id.llYieldSummary);
        chartYieldByPlot = findViewById(R.id.chartYieldByPlot);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        rvArchivedCrops.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArchivedCropAdapter(new java.util.ArrayList<>(), crop -> {
            Intent intent = new Intent(ArchiveActivity.this, CropDetailsActivity.class);
            intent.putExtra("crop_id", crop.getId());
            startActivity(intent);
        });
        rvArchivedCrops.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadArchive();
    }

    private void loadArchive() {
        List<Crop> harvested = dbHelper.getHarvestedCrops(null);
        adapter.updateData(harvested);

        if (harvested.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvArchivedCrops.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvArchivedCrops.setVisibility(View.VISIBLE);
        }

        Map<String, Double> totalsByPlot = new LinkedHashMap<>();
        Map<String, Integer> countsByPlot = new LinkedHashMap<>();
        for (Crop crop : harvested) {
            double amount = parseLeadingNumber(crop.getYieldAmount());
            String plot = crop.getPlotName();
            totalsByPlot.put(plot, totalsByPlot.getOrDefault(plot, 0.0) + amount);
            countsByPlot.put(plot, countsByPlot.getOrDefault(plot, 0) + 1);
        }

        renderYieldSummaryText(totalsByPlot, countsByPlot);
        renderYieldChart(totalsByPlot);
    }

    private void renderYieldSummaryText(Map<String, Double> totalsByPlot, Map<String, Integer> countsByPlot) {
        llYieldSummary.removeAllViews();
        for (String plot : totalsByPlot.keySet()) {
            TextView row = new TextView(this);
            double total = totalsByPlot.get(plot);
            int count = countsByPlot.get(plot);
            String totalText = (total == Math.floor(total)) ? String.valueOf((long) total) : String.valueOf(total);
            row.setText(plot + ":  " + totalText + "  (" + count + " harvest" + (count == 1 ? "" : "s") + ")");
            row.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            row.setTextSize(14);
            row.setPadding(0, 8, 0, 8);
            llYieldSummary.addView(row);
        }
    }

    private void renderYieldChart(Map<String, Double> totalsByPlot) {
        List<String> labels = new ArrayList<>();
        List<Float> values = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totalsByPlot.entrySet()) {
            labels.add(entry.getKey());
            values.add(entry.getValue().floatValue());
        }
        chartYieldByPlot.setData(labels, values);
    }

    private double parseLeadingNumber(String text) {
        if (text == null) return 0;
        Matcher matcher = LEADING_NUMBER.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }
}