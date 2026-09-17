package com.example.cropmanagementapp.catalog;

/**
 * One entry in the crop picker: a name, its category (for filtering and
 * a fallback icon), an optional real photo path, and whether it was
 * added by the farmer (custom) rather than being a built-in default.
 */
public class CropCatalogItem {

    private final String name;
    private final String category;
    private final boolean custom;
    private final String imagePath;

    public CropCatalogItem(String name, String category, boolean custom, String imagePath) {
        this.name = name;
        this.category = category;
        this.custom = custom;
        this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public boolean isCustom() { return custom; }
    public String getImagePath() { return imagePath; }
}