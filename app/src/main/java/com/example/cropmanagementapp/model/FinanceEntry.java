package com.example.cropmanagementapp.model;

/** One row in the farm-wide finance breakdown: a crop's totals. */
public class FinanceEntry {

    private String cropName;
    private String plotName;
    private double totalIncome;
    private double totalExpense;

    public FinanceEntry(String cropName, String plotName, double totalIncome, double totalExpense) {
        this.cropName = cropName;
        this.plotName = plotName;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public String getCropName() { return cropName; }
    public String getPlotName() { return plotName; }
    public double getTotalIncome() { return totalIncome; }
    public double getTotalExpense() { return totalExpense; }
    public double getNetProfit() { return totalIncome - totalExpense; }
}