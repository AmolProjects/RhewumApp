package com.rhewumapp.Activity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rhewumapp.R;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = NotificationService.class.getName();
    private static final String CHANNEL_ID = "my_channel_id";
    private static final String NOTIFICATION_COUNT_KEY = "notification_count";
    private static final String PREFERENCES_FILE_NAME = "my_preferences";

    private static final String PREFS_NAME = "BadgePrefs";
    private static final String BADGE_COUNT_KEY = "badge_count";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Consolidate data and notification payload handling
        if (!remoteMessage.getData().isEmpty() || remoteMessage.getNotification() != null) {
            incrementNotificationCount();
            String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "Default Title";
            String body = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "Default Message";
            sendNotification(title, body);
            broadcastBadgeCount();
        }

    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e("NotificationService:","Token is:"+token);
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from_notification", true);  // Add extra to identify notification click
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Retrieve the updated badge count
//        int badgeCount = getBadgeCount();

        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        int count = prefs.getInt(BADGE_COUNT_KEY, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.rhewum) // Replace with your app icon
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
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentCount = prefs.getInt(BADGE_COUNT_KEY, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BADGE_COUNT_KEY, currentCount + 1);
        editor.apply();

    }


    private int getBadgeCount() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(BADGE_COUNT_KEY, 0);

    }

    private void broadcastBadgeCount() {
        int badgeCount = getBadgeCount();
        Intent intent = new Intent("com.rhewum.UPDATE_BADGE");
        intent.putExtra("badge_count", badgeCount);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d("BroadcastTest", "Broadcast sent for badge update with count: " + badgeCount);
    }


}