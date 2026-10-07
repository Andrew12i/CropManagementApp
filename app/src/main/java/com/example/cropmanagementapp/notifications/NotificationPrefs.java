package com.example.cropmanagementapp.notifications;

import android.content.Context;
import android.content.SharedPreferences;

/** Stores whether the farmer wants harvest reminder notifications. Enabled by default. */
public class NotificationPrefs {

    private static final String PREFS_NAME = "crop_notification_prefs";
    private static final String KEY_ENABLED = "reminders_enabled";

    private final SharedPreferences prefs;

    public NotificationPrefs(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isEnabled() {
        return prefs.getBoolean(KEY_ENABLED, true);
    }

    public void setEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply();
    }
}