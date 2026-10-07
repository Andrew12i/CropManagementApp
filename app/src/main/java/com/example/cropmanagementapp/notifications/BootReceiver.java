package com.example.cropmanagementapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Re-schedules the daily alarm after the device reboots (AlarmManager alarms don't survive a reboot). */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            CropAlarmScheduler.scheduleDaily(context);
        }
    }
}