package com.example.cropmanagementapp.model;

/**
 * Represents a single farm activity (fertilizer application, weeding,
 * irrigation, pesticide spraying, etc.) logged against a crop.
 */
public class ActivityLog {

    private long id;
    private long cropId;
    private String activityType;
    private String activityDate; // stored as yyyy-MM-dd
    private String notes;

    public ActivityLog() {
    }

    public ActivityLog(long id, long cropId, String activityType, String activityDate, String notes) {
        this.id = id;
        this.cropId = cropId;
        this.activityType = activityType;
        this.activityDate = activityDate;
        this.notes = notes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCropId() {
        return cropId;
    }

    public void setCropId(long cropId) {
        this.cropId = cropId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}