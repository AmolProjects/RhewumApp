package com.rhewum.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rhewum.R;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String PREFS_NAME = "BadgePrefs";
    private static final String BADGE_COUNT_KEY = "badge_count";

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // Handle the received message
        if (message.getNotification() != null) {
            incrementBadgeCount();
            showNotification(message.getNotification().getTitle(), message.getNotification().getBody());
        }
    }

    private void incrementBadgeCount() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int  badgeCount = prefs.getInt(BADGE_COUNT_KEY, 0);
        Log.d("BadgeCount", "Current badge count: " + badgeCount);
        badgeCount++;
        Log.d("BadgeCount", "New badge count: " + badgeCount);
        prefs.edit().putInt(BADGE_COUNT_KEY, badgeCount).apply();
        boolean success = ShortcutBadger.applyCount(this, badgeCount); // for 1.1.4+
        if (!success) {
            Log.e(TAG, "Failed to apply badge count.");
        } else {
            Log.d(TAG, "Badge count applied successfully.");
        }

    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "news_channel_id";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "News Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for news notifications");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(0, notificationBuilder.build());
    }


}
