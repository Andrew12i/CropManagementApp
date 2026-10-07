package com.example.cropmanagementapp.notifications;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.Crop;
import com.example.cropmanagementapp.model.Farm;

import java.util.List;

/**
 * Runs the daily (and app-open) check across every farm's active crops,
 * notifying about ones due within 3 days or already overdue.
 */
public class CropCheckReceiver extends BroadcastReceiver {

    private static final int DUE_SOON_WINDOW_DAYS = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        runCheck(context);
    }

    public static void runCheck(Context context) {
        NotificationPrefs prefs = new NotificationPrefs(context);
        if (!prefs.isEnabled()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationHelper.createChannel(context);

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<Farm> farms = dbHelper.getAllFarms();

        for (Farm farm : farms) {
            List<Crop> activeCrops = dbHelper.getAllCrops(null, farm.getId());
            for (Crop crop : activeCrops) {
                int daysLeft = DateUtils.daysUntil(crop.getExpectedHarvestDate());
                if (daysLeft == Integer.MIN_VALUE) continue;

                String cropLabel = crop.getCropName() +
                        (crop.getVariety() != null && !crop.getVariety().isEmpty() ? " (" + crop.getVariety() + ")" : "");

                if (daysLeft < 0) {
                    String message = cropLabel + " on " + crop.getPlotName() + " (" + farm.getName() +
                            ") is overdue for harvest by " + Math.abs(daysLeft) + " day(s).";
                    NotificationHelper.showCropReminder(context, crop.getId(), "Harvest overdue", message);
                } else if (daysLeft <= DUE_SOON_WINDOW_DAYS) {
                    String when = (daysLeft == 0) ? "today" : "in " + daysLeft + " day(s)";
                    String message = cropLabel + " on " + crop.getPlotName() + " (" + farm.getName() +
                            ") is due for harvest " + when + ".";
                    NotificationHelper.showCropReminder(context, crop.getId(), "Harvest coming up", message);
                }
            }
        }
    }
}