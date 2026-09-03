package com.example.cropmanagementapp.model;

/**
 * Represents a single farm activity logged against a crop, with an
 * optional expense amount for tracking input costs.
 */
public class ActivityLog {

    private long id;
    private long cropId;
    private String activityType;
    private String activityDate; // stored as yyyy-MM-dd
    private String notes;
    private String expenseAmount; // free text numeric, e.g. "500" or "500.50"

    public ActivityLog() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCropId() { return cropId; }
    public void setCropId(long cropId) { this.cropId = cropId; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getActivityDate() { return activityDate; }
    public void setActivityDate(String activityDate) { this.activityDate = activityDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getExpenseAmount() { return expenseAmount; }
    public void setExpenseAmount(String expenseAmount) { this.expenseAmount = expenseAmount; }
}