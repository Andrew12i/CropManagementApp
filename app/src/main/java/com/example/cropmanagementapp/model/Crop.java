package com.example.cropmanagementapp.model;

/**
 * Represents a single crop record kept by the farmer.
 */
public class Crop {

    private long id;
    private String cropName;
    private String plotName;
    private String plantingDate;        // stored as yyyy-MM-dd
    private String expectedHarvestDate; // stored as yyyy-MM-dd
    private String areaPlanted;         // free text, e.g. "2 acres"

    public Crop() {
    }

    public Crop(long id, String cropName, String plotName, String plantingDate,
                String expectedHarvestDate, String areaPlanted) {
        this.id = id;
        this.cropName = cropName;
        this.plotName = plotName;
        this.plantingDate = plantingDate;
        this.expectedHarvestDate = expectedHarvestDate;
        this.areaPlanted = areaPlanted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public String getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(String plantingDate) {
        this.plantingDate = plantingDate;
    }

    public String getExpectedHarvestDate() {
        return expectedHarvestDate;
    }

    public void setExpectedHarvestDate(String expectedHarvestDate) {
        this.expectedHarvestDate = expectedHarvestDate;
    }

    public String getAreaPlanted() {
        return areaPlanted;
    }

    public void setAreaPlanted(String areaPlanted) {
        this.areaPlanted = areaPlanted;
    }
}