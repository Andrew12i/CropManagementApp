package com.example.cropmanagementapp.model;

/**
 * Represents a single crop record kept by the farmer.
 */
public class Crop {

    private long id;
    private String cropName;
    private String variety;
    private String plotName;
    private String plantingDate;        // stored as yyyy-MM-dd
    private String expectedHarvestDate; // stored as yyyy-MM-dd
    private String areaPlanted;         // free text, e.g. "2 acres"
    private boolean harvested;
    private String yieldAmount;         // free text, e.g. "800 kg"
    private String harvestedDate;       // stored as yyyy-MM-dd
    private String category;            // e.g. "Cereals", "Vegetables"

    public Crop() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public String getPlotName() { return plotName; }
    public void setPlotName(String plotName) { this.plotName = plotName; }

    public String getPlantingDate() { return plantingDate; }
    public void setPlantingDate(String plantingDate) { this.plantingDate = plantingDate; }

    public String getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(String expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }

    public String getAreaPlanted() { return areaPlanted; }
    public void setAreaPlanted(String areaPlanted) { this.areaPlanted = areaPlanted; }

    public boolean isHarvested() { return harvested; }
    public void setHarvested(boolean harvested) { this.harvested = harvested; }

    public String getYieldAmount() { return yieldAmount; }
    public void setYieldAmount(String yieldAmount) { this.yieldAmount = yieldAmount; }

    public String getHarvestedDate() { return harvestedDate; }
    public void setHarvestedDate(String harvestedDate) { this.harvestedDate = harvestedDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}