package com.example.cropmanagementapp.catalog;

/**
 * One entry in the crop picker: a name, its category (for filtering and
 * for picking a fallback icon), and whether it was added by the farmer
 * (custom) rather than being one of the built-in defaults.
 */
public class CropCatalogItem {

    private final String name;
    private final String category;
    private final boolean custom;

    public CropCatalogItem(String name, String category, boolean custom) {
        this.name = name;
        this.category = category;
        this.custom = custom;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public boolean isCustom() { return custom; }
}