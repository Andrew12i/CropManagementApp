package com.example.cropmanagementapp.catalog;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.cropmanagementapp.R;

import java.io.File;

/**
 * Decides what image to show for a crop: an explicit dev-added photo for
 * known crop types (see typeIcon), a custom crop's own gallery photo, or
 * a colour-coded category icon as the final fallback.
 */
public class CropImageResolver {

    /** Applies the correct image straight onto an ImageView. */
    public static void applyCropImage(ImageView imageView, Context context, String cropName,
                                      String category, String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            File file = new File(imagePath);
            if (file.exists()) {
                imageView.setImageURI(Uri.fromFile(file));
                return;
            }
        }
        imageView.setImageResource(resolve(context, cropName, category));
    }

    /**
     * Explicit photo mapping for the built-in crop catalog. Add a matching
     * drawable (e.g. res/drawable/maize.png) for each crop you have a real
     * photo for — the ones not yet added here simply fall through to the
     * naming-convention lookup, then the category icon.
     */
    public static int typeIcon(String cropName) {
        if (cropName == null) return -1;

        switch (cropName) {
            case "Maize": return R.drawable.maize;
            case "Wheat": return R.drawable.wheat;
            case "Rice (Paddy)": return R.drawable.rice_paddy;
            case "Sorghum": return R.drawable.sorghum;
            case "Millet": return R.drawable.millet;
            case "Barley": return R.drawable.barley;

            case "Beans": return R.drawable.beans;
            case "Green Grams": return R.drawable.green_grams;
            case "Cowpeas": return R.drawable.cowpeas;
            case "Pigeon Peas": return R.drawable.pigeon_peas;
            case "Soybean": return R.drawable.soybean;
            case "Groundnuts": return R.drawable.groundnuts;

            case "Tomato": return R.drawable.tomato;
            case "Onion": return R.drawable.onion;
            case "Cabbage": return R.drawable.cabbage;
            case "Kale (Sukuma Wiki)": return R.drawable.kales;
            case "Spinach": return R.drawable.spinach;
            case "Carrot": return R.drawable.carrot;
            case "Capsicum / Pepper": return R.drawable.capsicum_pepper;
            case "Pumpkin": return R.drawable.pumpkin;

            case "Banana": return R.drawable.banana;
            case "Avocado": return R.drawable.avocado;
            case "Mango": return R.drawable.mangoes;
            case "Watermelon": return R.drawable.mellon;
            case "Passion Fruit": return R.drawable.passion;
            case "Pineapple": return R.drawable.pineapple;

            case "Coffee": return R.drawable.coffee;
            case "Tea": return R.drawable.tea;
            case "Macadamia": return R.drawable.macadamia;
            case "Sugarcane": return R.drawable.sugarcane;
            case "Cotton": return R.drawable.cotton;
            case "Sunflower": return R.drawable.sunflower;

            case "Irish Potato": return R.drawable.irish_potatoes;
            case "Sweet Potato": return R.drawable.sweet_potatoes;
            case "Cassava": return R.drawable.cassava;
            case "Napier Grass": return R.drawable.nappier;

            default:
                return -1; // no explicit photo mapped — fall through
        }
    }

    /**
     * Resolution order: (1) an explicit photo mapped in typeIcon, (2) a
     * drawable named "crop_<sanitized name>" for custom crops added later
     * some other way, (3) the category's colour-coded icon.
     */
    public static int resolve(Context context, String cropName, String category) {
        int explicit = typeIcon(cropName);
        if (explicit != -1) {
            return explicit;
        }

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

    private static String sanitize(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }
}