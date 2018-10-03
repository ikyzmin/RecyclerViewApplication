package com.jaka.recyclerviewapplication.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.jaka.recyclerviewapplication.App;
import com.jaka.recyclerviewapplication.R;

import androidx.core.app.NotificationCompat;

public class ScheduleNotificationManager {

    private static final String CHANNEL_ID = "Test Channel";


    private static Notification createNotification(Context context, String title, String text, String bigText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_announcement_white)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText).setBigContentTitle(text));
        return builder.build();

    }

    public static void showNotification(Context context, int id, String title, String text, String bigText) {
        if (App.getInstance().isAppIsInBackground()) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            createChannel(context);
            notificationManager.notify(id, createNotification(context, title, text, bigText));
        }
    }

    private static void createChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }
}
