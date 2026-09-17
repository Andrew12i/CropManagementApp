package com.example.cropmanagementapp.model;

public class IncomeLog {

    private long id;
    private long cropId;
    private String incomeDate;
    private String quantity;
    private String unit;
    private String rate;
    private String totalAmount;
    private String buyerName;
    private String receivedStatus; // "Fully received" or "Pending"
    private String notes;

    public IncomeLog() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCropId() { return cropId; }
    public void setCropId(long cropId) { this.cropId = cropId; }

    public String getIncomeDate() { return incomeDate; }
    public void setIncomeDate(String incomeDate) { this.incomeDate = incomeDate; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getRate() { return rate; }
    public void setRate(String rate) { this.rate = rate; }

    public String getTotalAmount() { return totalAmount; }
    public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getReceivedStatus() { return receivedStatus; }
    public void setReceivedStatus(String receivedStatus) { this.receivedStatus = receivedStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}