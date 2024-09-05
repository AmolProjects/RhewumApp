package com.rhewum;





import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.navigation.NavigationView;
import com.rhewum.Activity.BadgeDrawerArrowDrawable;
import com.rhewum.Activity.DashBoardActivity;
import com.rhewum.Activity.InfoActivity;

import com.rhewum.Activity.NewsActivity;

import com.rhewum.Activity.WebsiteActivity;

import java.util.Objects;


public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private ImageView cancelIcon;
    private NavigationView navigationView;

    private TextView activityTitleTextView;
    private Button backButton;

    private static final String PREFS_NAME = "BadgePrefs";
    private static final String BADGE_COUNT_KEY = "badge_count";
    private BadgeDrawerArrowDrawable badgeDrawable;
    private ActionBarDrawerToggle toggle;
    private TextView newsBadgeTextView;
    private static final String NOTIFICATION_COUNT_KEY = "notification_count";
    private static final String PREFERENCES_FILE_NAME = "my_preferences";

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base,null);
        FrameLayout container =drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar =drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setNavigationDrawerMenuTitles(navigationView.getMenu());



        // Set custom view for news menu item
        MenuItem newsMenuItem = navigationView.getMenu().findItem(R.id.nav_news);
        View actionView = getLayoutInflater().inflate(R.layout.menu_item_with_badge, null);
        newsMenuItem.setActionView(actionView);

        newsBadgeTextView = actionView.findViewById(R.id.menu_item_badge1);

        // Inflate the header view and find the ImageViewCancel
        View headerView = navigationView.getHeaderView(0);
        ImageView imageViewCancel = headerView.findViewById(R.id.imageViewCancel);

        // Set click listener for the ImageViewCancel
        imageViewCancel.setOnClickListener(v -> {

            // You can add more actions here, e.g., closing the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        badgeDrawable = new BadgeDrawerArrowDrawable(Objects.requireNonNull(getSupportActionBar()).getThemedContext());
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(Color.BLACK);
        // Change to your color
        toggle.syncState();
    }

    private void setNavigationDrawerMenuTitles(Menu menu) {

        MenuItem navHome = menu.findItem(R.id.nav_home);
        MenuItem navNews = menu.findItem(R.id.nav_news);
        MenuItem navInfo = menu.findItem(R.id.nav_info);
        MenuItem navWebsite = menu.findItem(R.id.nav_website);

        // Set titles based on item IDs
        navHome.setTitle("Home");
        navNews.setTitle("News");
        navInfo.setTitle("Info");
        navWebsite.setTitle("Website");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        if (id == R.id.nav_home){
            //testing for notification
            /*Intent intent = new Intent("com.rhewum.UPDATE_BADGE");
            intent.putExtra("badge_count", 1);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/
            startActivity(new Intent(this, DashBoardActivity.class));

        }
        else if (id == R.id.nav_news) {
            startActivity(new Intent(this, NewsActivity.class));
            resetNotificationCount();
            updateBadge(0);  // Clear the badge in the drawer
            updateNewsBadge(0);  // Clear the badge in the News menu item

        }

        else if (id == R.id.nav_info) {
            startActivity(new Intent(this, InfoActivity.class));
        }
        else if (id == R.id.nav_website) {
            startActivity(new Intent(this, WebsiteActivity.class));
        }
        return false;
    }

    protected void allocateActivityTitle(String titleString){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(titleString);
        }
    }

    protected void setNavigationButtonText(String rightButtonText, View.OnClickListener listener) {
        Button rightButton = findViewById(R.id.rightButton);
        rightButton.setText(rightButtonText);
        rightButton.setOnClickListener(listener);
    }

    protected void setActivityTitle(String title) {
        if (activityTitleTextView != null) {
            activityTitleTextView.setText(title);
        }
    }

    protected void showBackButton(boolean show) {
        if (backButton != null) {
            backButton.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    private final BroadcastReceiver badgeUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("badge_count")) {
                int badgeCount = intent.getIntExtra("badge_count", 1);

                Log.d("BadgeUpdate", "Broadcast received. Badge count: " + badgeCount);
                Log.d("BadgeUpdate", "Broadcast received. Badge count: " + badgeCount);
                runOnUiThread(() -> {
                    updateBadge(badgeCount);
                    updateNewsBadge(badgeCount);
                });
            }
            else {
                Log.d("BadgeUpdate", "Broadcast received with no badge count.");
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(badgeUpdateReceiver,
                new IntentFilter("com.rhewum.UPDATE_BADGE"));
        // Retrieve the badge count from SharedPreferences and update the UI
        updateBadgeFromPreferences();

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).registerReceiver(badgeUpdateReceiver, new IntentFilter("com.rhewum.UPDATE_BADGE"));

    }


    private void updateBadge(int badgeCount) {
        if (badgeDrawable != null) {
            badgeDrawable.setEnabled(badgeCount > 0);
            badgeDrawable.setText(""); // Show red dot only

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                if (toggle != null) {
                    toggle.setDrawerArrowDrawable(badgeDrawable);
                    toggle.setDrawerIndicatorEnabled(false);
                    toggle.setDrawerIndicatorEnabled(true);
                    toggle.syncState(); // Ensure UI is refreshed
                }
            } else {
                Log.e("BadgeUpdate", "ActionBar is null.");
            }
        } else {
            Log.e("BadgeUpdate", "badgeDrawable is null.");
        }
    }



    private void updateNewsBadge(int badgeCount) {
        if (newsBadgeTextView != null) {
            // Show the badge with the badge count if it's greater than 0, otherwise hide it
            if (badgeCount > 0) {
                newsBadgeTextView.setVisibility(View.VISIBLE);
                newsBadgeTextView.setText(String.valueOf(badgeCount));
            } else {
                newsBadgeTextView.setVisibility(View.GONE); // Hide the badge if count is 0
            }
        }
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);

        // Handle badge update if coming from a notification
        if (intent.hasExtra("from_notification")) {
            updateBadgeFromPreferences();
        }
    }

    private void updateBadgeFromPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int badgeCount = prefs.getInt(BADGE_COUNT_KEY, 0);
        updateBadge(badgeCount);
        updateNewsBadge(badgeCount);
    }

    private void resetNotificationCount() {
        // Reset the notification count in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BADGE_COUNT_KEY, 0);
        editor.apply();

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(badgeUpdateReceiver,
                new IntentFilter("com.rhewum.UPDATE_BADGE"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(badgeUpdateReceiver);
    }



}