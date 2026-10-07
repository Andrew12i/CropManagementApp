package com.example.cropmanagementapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.cropmanagementapp.adapter.FinanceAdapter;
import com.example.cropmanagementapp.auth.FarmManager;
import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.model.FinanceEntry;

import java.util.List;
import java.util.Locale;

public class FinancesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private FarmManager farmManager;
    private TextView tvFarmTotalIncome, tvFarmTotalExpenses, tvFarmNetProfit, tvNoFinances;
    private RecyclerView rvFinanceBreakdown;
    private FinanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finances);

        dbHelper = new DatabaseHelper(this);
        farmManager = new FarmManager(this);

        tvFarmTotalIncome = findViewById(R.id.tvFarmTotalIncome);
        tvFarmTotalExpenses = findViewById(R.id.tvFarmTotalExpenses);
        tvFarmNetProfit = findViewById(R.id.tvFarmNetProfit);
        tvNoFinances = findViewById(R.id.tvNoFinances);
        rvFinanceBreakdown = findViewById(R.id.rvFinanceBreakdown);

        rvFinanceBreakdown.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FinanceAdapter(new java.util.ArrayList<>());
        rvFinanceBreakdown.setAdapter(adapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        BottomNavHelper.setup(bottomNav, this, R.id.nav_finances);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFinances();
    }

    private void loadFinances() {
        long farmId = farmManager.getCurrentFarmId();

        double totalIncome = dbHelper.getTotalIncomeAllCrops(farmId);
        double totalExpenses = dbHelper.getTotalExpensesAllCrops(farmId);
        double netProfit = totalIncome - totalExpenses;

        tvFarmTotalIncome.setText(String.format(Locale.getDefault(), "KES %.2f", totalIncome));
        tvFarmTotalExpenses.setText(String.format(Locale.getDefault(), "KES %.2f", totalExpenses));
        tvFarmNetProfit.setText(String.format(Locale.getDefault(), "KES %.2f", netProfit));
        tvFarmNetProfit.setTextColor(getResources().getColor(netProfit >= 0 ? R.color.green_primary : R.color.red_overdue));

        List<FinanceEntry> breakdown = dbHelper.getFinanceBreakdown(farmId);
        adapter.updateData(breakdown);

        if (breakdown.isEmpty()) {
            tvNoFinances.setVisibility(View.VISIBLE);
            rvFinanceBreakdown.setVisibility(View.GONE);
        } else {
            tvNoFinances.setVisibility(View.GONE);
            rvFinanceBreakdown.setVisibility(View.VISIBLE);
        }
    }
}