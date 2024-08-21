package com.rhewum.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rhewum.R;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = NotificationService.class.getName();
    private static final String CHANNEL_ID = "my_channel_id";
    private static final String NOTIFICATION_COUNT_KEY = "notification_count";
    private static final String PREFERENCES_FILE_NAME = "my_preferences";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            incrementNotificationCount();
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        int count = prefs.getInt(NOTIFICATION_COUNT_KEY, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.rhewumt) // Replace with your app icon
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setNumber(count) // This sets the badge number on supported launchers
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL); // Set the badge icon type

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void incrementNotificationCount() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        int count = prefs.getInt(NOTIFICATION_COUNT_KEY, 0);
        prefs.edit().putInt(NOTIFICATION_COUNT_KEY, count).apply();
    }

    private void resetNotificationCount() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(NOTIFICATION_COUNT_KEY, 0).apply();
    }

}