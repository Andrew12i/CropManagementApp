package com.example.cropmanagementapp.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.cropmanagementapp.CropDetailsActivity;
import com.example.cropmanagementapp.R;

/** Builds and posts harvest reminder notifications, and sets up the required channel. */
public class NotificationHelper {

    public static final String CHANNEL_ID = "harvest_reminders";

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Harvest Reminders", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Reminders for crops due or overdue for harvest");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /** Posts one notification for a single crop. notificationId should be unique per crop (we use the crop id). */
    public static void showCropReminder(Context context, long cropId, String title, String message) {
        Intent intent = new Intent(context, CropDetailsActivity.class);
        intent.putExtra("crop_id", cropId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, (int) cropId, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(0xFF1B5E20)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify((int) cropId, builder.build());
    }
}