package com.example.cropmanagementapp.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

/** Schedules the daily harvest-check alarm (inexact, battery-friendly). */
public class CropAlarmScheduler {

    private static final long INTERVAL_MILLIS = AlarmManager.INTERVAL_DAY;
    private static final int REQUEST_CODE = 1001;

    public static void scheduleDaily(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, CropCheckReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags);

        long triggerAt = SystemClock.elapsedRealtime() + INTERVAL_MILLIS;
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                triggerAt, INTERVAL_MILLIS, pendingIntent);
    }

    public static void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, CropCheckReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags);
        alarmManager.cancel(pendingIntent);
    }
}