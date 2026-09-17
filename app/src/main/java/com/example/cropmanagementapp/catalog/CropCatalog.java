package com.example.cropmanagementapp.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CropCatalog {

    public static final String CATEGORY_CEREALS = "Cereals";
    public static final String CATEGORY_PULSES = "Pulses";
    public static final String CATEGORY_VEGETABLES = "Vegetables";
    public static final String CATEGORY_FRUITS = "Fruits";
    public static final String CATEGORY_CASH_CROPS = "Cash Crops";
    public static final String CATEGORY_OTHER = "Other";

    public static List<String> getCategories() {
        return Arrays.asList(CATEGORY_CEREALS, CATEGORY_PULSES, CATEGORY_VEGETABLES,
                CATEGORY_FRUITS, CATEGORY_CASH_CROPS, CATEGORY_OTHER);
    }

    public static List<CropCatalogItem> getDefaultItems() {
        List<CropCatalogItem> items = new ArrayList<>();

        String[] cereals = {"Maize", "Wheat", "Rice (Paddy)", "Sorghum", "Millet", "Barley"};
        for (String name : cereals) items.add(new CropCatalogItem(name, CATEGORY_CEREALS, false, null));

        String[] pulses = {"Beans", "Green Grams", "Cowpeas", "Pigeon Peas", "Soybean", "Groundnuts"};
        for (String name : pulses) items.add(new CropCatalogItem(name, CATEGORY_PULSES, false, null));

        String[] vegetables = {"Tomato", "Onion", "Cabbage", "Kale (Sukuma Wiki)", "Spinach",
                "Carrot", "Capsicum / Pepper", "Pumpkin"};
        for (String name : vegetables) items.add(new CropCatalogItem(name, CATEGORY_VEGETABLES, false, null));

        String[] fruits = {"Banana", "Avocado", "Mango", "Watermelon", "Passion Fruit", "Pineapple"};
        for (String name : fruits) items.add(new CropCatalogItem(name, CATEGORY_FRUITS, false, null));

        String[] cashCrops = {"Coffee", "Tea", "Macadamia", "Sugarcane", "Cotton", "Sunflower"};
        for (String name : cashCrops) items.add(new CropCatalogItem(name, CATEGORY_CASH_CROPS, false, null));

        String[] other = {"Irish Potato", "Sweet Potato", "Cassava", "Napier Grass"};
        for (String name : other) items.add(new CropCatalogItem(name, CATEGORY_OTHER, false, null));

        return items;
    }
}