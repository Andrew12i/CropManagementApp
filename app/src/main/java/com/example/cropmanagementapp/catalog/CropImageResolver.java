package com.example.cropmanagementapp.catalog;

import android.content.Context;

import com.example.cropmanagementapp.R;

/**
 * Resolves which image to show for a given crop. Looks first for a real
 * photo the developer may have added later (a drawable resource named
 * "crop_" + a sanitized version of the crop name, e.g. "crop_maize" for
 * "Maize"). If none exists yet, falls back to a colour-coded icon for
 * the crop's category, so every crop always shows something.
 */
public class CropImageResolver {

    public static int resolve(Context context, String cropName, String category) {
        String sanitized = sanitize(cropName);
        int photoResId = context.getResources().getIdentifier(
                "crop_" + sanitized, "drawable", context.getPackageName());
        if (photoResId != 0) {
            return photoResId;
        }
        return categoryIcon(category);
    }

    public static int categoryIcon(String category) {
        if (category == null) return R.drawable.ic_cat_other;
        switch (category) {
            case CropCatalog.CATEGORY_CEREALS: return R.drawable.ic_cat_cereal;
            case CropCatalog.CATEGORY_PULSES: return R.drawable.ic_cat_pulses;
            case CropCatalog.CATEGORY_VEGETABLES: return R.drawable.ic_cat_vegetable;
            case CropCatalog.CATEGORY_FRUITS: return R.drawable.ic_cat_fruit;
            case CropCatalog.CATEGORY_CASH_CROPS: return R.drawable.ic_cat_cash_crop;
            default: return R.drawable.ic_cat_other;
        }
    }

    /** Turns "Kale (Sukuma Wiki)" into "kale_sukuma_wiki" for resource lookup. */
    private static String sanitize(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }
}