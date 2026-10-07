package com.example.cropmanagementapp.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.model.Farm;

/**
 * Tracks which farm is currently selected, remembered across app
 * restarts. Self-heals if the stored farm was deleted or never set.
 */
public class FarmManager {

    private static final String PREFS_NAME = "crop_management_farm";
    private static final String KEY_FARM_ID = "current_farm_id";
    private static final String KEY_FARM_NAME = "current_farm_name";

    private final SharedPreferences prefs;
    private final DatabaseHelper dbHelper;

    public FarmManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        dbHelper = new DatabaseHelper(context);
    }

    public long getCurrentFarmId() {
        long farmId = prefs.getLong(KEY_FARM_ID, -1);
        if (farmId == -1 || dbHelper.getFarm(farmId) == null) {
            farmId = dbHelper.ensureDefaultFarm();
            Farm farm = dbHelper.getFarm(farmId);
            setCurrentFarm(farmId, farm != null ? farm.getName() : "My Farm");
        }
        return farmId;
    }

    public String getCurrentFarmName() {
        return prefs.getString(KEY_FARM_NAME, "My Farm");
    }

    public void setCurrentFarm(long farmId, String farmName) {
        prefs.edit()
                .putLong(KEY_FARM_ID, farmId)
                .putString(KEY_FARM_NAME, farmName)
                .apply();
    }
}