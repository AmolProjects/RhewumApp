package com.rhewumapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rhewumapp.Activity.BadgeDrawerArrowDrawable;
import com.rhewumapp.Activity.CapacityCheckerActivity;
import com.rhewumapp.Activity.DashBoardActivity;
import com.rhewumapp.Activity.InfoActivity;

import com.rhewumapp.Activity.MeshConverterActivity;
import com.rhewumapp.Activity.NewsActivity;

import com.rhewumapp.Activity.VibFlashActivity;
import com.rhewumapp.Activity.WebsiteActivity;
import com.rhewumapp.Activity.data.CustomTypefaceSpan;

import java.util.Objects;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PREFS_NAME = "BadgePrefs";
    private static final String BADGE_COUNT_KEY = "badge_count";
    private static final String NOTIFICATION_COUNT_KEY = "notification_count";
    private static final String PREFERENCES_FILE_NAME = "my_preferences";
    DrawerLayout drawerLayout;
    private ImageView cancelIcon;
    private NavigationView navigationView;
    private TextView activityTitleTextView;
    private Button backButton;
    private BadgeDrawerArrowDrawable badgeDrawable;
    private ActionBarDrawerToggle toggle;
    private TextView newsBadgeTextView;
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
            } else {
                Log.d("BadgeUpdate", "Broadcast received with no badge count.");
            }
        }
    };

    @Override
    public void setContentView(View view) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        setCustomFontForMenuItems(navigationView.getMenu());

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
        toggle.syncState();

    }

    private void setCustomFontForMenuItems(Menu menu) {
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/handle_go.ttf");

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            View itemView = navigationView.getMenu().findItem(menuItem.getItemId()).getActionView();

            if (itemView == null) {
                // Create a new TextView if the action view is not set
                TextView textView = new TextView(this);
                textView.setText(menuItem.getTitle());
                textView.setTypeface(customFont); // Set the custom font
                textView.setTextSize(16); // Set desired text size
                textView.setTextColor(Color.BLACK); // Set text color

                // Set the layout parameters for the TextView
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params);
                itemView = textView;
            } else {
                // Set the custom font if an action view exists
//                TextView textView = itemView.findViewById(R.id.textViewMenu); // Replace with actual ID if necessary
//                if (textView != null) {
//                    textView.setTypeface(customFont);
//                }
            }

            // Set the item view back to the menu item
            menuItem.setActionView(itemView);
        }
    }

    private void setNavigationDrawerMenuTitles(Menu menu) {

        // Load the custom font
        Typeface handleGoFont = ResourcesCompat.getFont(this, R.font.handle_go);

//        MenuItem navHome = menu.findItem(R.id.nav_home);
        MenuItem navNews = menu.findItem(R.id.nav_news);
        MenuItem navInfo = menu.findItem(R.id.nav_info);
        MenuItem navWebsite = menu.findItem(R.id.nav_website);
        MenuItem nav_mesh_converter = menu.findItem(R.id.nav_mesh_converter);
        MenuItem nav_vibsonic = menu.findItem(R.id.nav_vibsonic);
        MenuItem nav_vib_flash = menu.findItem(R.id.nav_vib_flash);
        MenuItem nav_vib_checker = menu.findItem(R.id.nav_vib_checker);
        MenuItem nav_capacity_checker = menu.findItem(R.id.nav_capacity_checker);

        MenuItem navHome = menu.findItem(R.id.nav_home);

        // Set titles based on item IDs
        // Increase the text size using SpannableString
        increaseMenuItemTitleSize(navHome, "Home", 18, handleGoFont);
        increaseMenuItemTitleSize(navNews, "News", 16,handleGoFont);
        increaseMenuItemTitleSize(navInfo, "Info", 16,handleGoFont);
        increaseMenuItemTitleSize(navWebsite, "Website", 16,handleGoFont);
        increaseMenuItemTitleSize(nav_mesh_converter, "Mesh Converter", 16,handleGoFont);
        increaseMenuItemTitleSize(nav_vibsonic, "VibSonic", 16,handleGoFont);
        increaseMenuItemTitleSize(nav_vib_flash, "VibFlash", 16,handleGoFont);
        increaseMenuItemTitleSize(nav_vib_checker, "VibChecker", 16,handleGoFont);
        increaseMenuItemTitleSize(nav_capacity_checker, "Capacity Checker", 16,handleGoFont);


        // susheel
// Define the desired icon size (width and height in pixels)
        int iconSize = (int) getResources().getDimension(R.dimen.icon_size); // Defined in dimens.xml

// Resize and set icons for each menu item
        resizeAndSetIcon(nav_mesh_converter, R.drawable.menu_circle1, iconSize);
        resizeAndSetIcon(nav_vibsonic, R.drawable.menu_circle1, iconSize);
        resizeAndSetIcon(nav_vib_flash, R.drawable.menu_circle1, iconSize);
        resizeAndSetIcon(nav_vib_checker, R.drawable.menu_circle1, iconSize);
        // resizeAndSetIcon(navHome, R.drawable.baseline_home_24, iconSize);

        // Set icons for each menu item
        nav_mesh_converter.setIcon(R.drawable.menu_circle1);    // Replace with your own icon resource
        nav_vibsonic.setIcon(R.drawable.menu_circle1);    // Replace with your own icon resource
        nav_vib_flash.setIcon(R.drawable.menu_circle1);    // Replace with your own icon resource
        nav_vib_checker.setIcon(R.drawable.menu_circle1);
        nav_capacity_checker.setIcon(R.drawable.menu_circle1);
        navHome.setIcon(R.drawable.home);
        navNews.setIcon(R.drawable.news);
        navInfo.setIcon(R.drawable.info);
        navWebsite.setIcon(R.drawable.globe);

        // Set color for icons
        int color = ContextCompat.getColor(this, R.color.header_backgrounds); // Define your color in res/colors.xml

// Change icon colors using a color filter
        Drawable homeIcon = nav_mesh_converter.getIcon();
        if (homeIcon != null) {
            homeIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        Drawable navHomes = navHome.getIcon();
        if (navHomes != null) {
            navHomes.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        Drawable nav_News = navNews.getIcon();
        if (nav_News != null) {
            nav_News.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        Drawable nav_Info = navInfo.getIcon();
        if (nav_Info != null) {
            nav_Info.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        Drawable nav_Website = navWebsite.getIcon();
        if (nav_Website != null) {
            nav_Website.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        Drawable newsIcon = nav_vibsonic.getIcon();
        if (newsIcon != null) {
            newsIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        Drawable infoIcon = nav_vib_flash.getIcon();
        if (infoIcon != null) {
            infoIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        Drawable websiteIcon = nav_vib_checker.getIcon();
        if (websiteIcon != null) {
            websiteIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }


    }


    private void increaseMenuItemTitleSize(MenuItem menuItem, String title, int textSizeInSp, Typeface typeface) {
        SpannableString spannableTitle = new SpannableString(title);

        // Set text size in SP (scale-independent pixels)
        spannableTitle.setSpan(new AbsoluteSizeSpan(textSizeInSp, true), 0, spannableTitle.length(), 0);

        // Set custom font (TypefaceSpan requires API 28 or custom span workaround)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            spannableTitle.setSpan(new TypefaceSpan(typeface), 0, spannableTitle.length(), 0);
        } else {
            // For older versions, create a custom TypefaceSpan if needed (workaround for lower API levels)
            spannableTitle.setSpan(new CustomTypefaceSpan(typeface), 0, spannableTitle.length(), 0);
        }

        // Set the title with the modified text size and font
        menuItem.setTitle(spannableTitle);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            startActivity(new Intent(this, DashBoardActivity.class));
        } else if (id == R.id.nav_mesh_converter) {
            startActivity(new Intent(this, MeshConverterActivity.class));
        } /*else if (id == R.id.nav_vibsonic) {
            startActivity(new Intent(this, VibSonicActivity.class));
        }*/ else if (id == R.id.nav_vib_flash) {
            startActivity(new Intent(this, VibFlashActivity.class));
        }/* else if (id == R.id.nav_vib_checker) {
            startActivity(new Intent(this, VibCheckerAccelerometer2Activity.class));
        }*/ else if (id == R.id.nav_capacity_checker) {
            startActivity(new Intent(this, CapacityCheckerActivity.class));
        } else if (id == R.id.nav_news) {
            startActivity(new Intent(this, NewsActivity.class));
            resetNotificationCount();
            updateBadge(0);  // Clear the badge in the drawer
            updateNewsBadge(0);  // Clear the badge in the News menu item

        } else if (id == R.id.nav_info) {
            startActivity(new Intent(this, InfoActivity.class));
        } else if (id == R.id.nav_website) {
            startActivity(new Intent(this, WebsiteActivity.class));
        }
        return false;
    }

    protected void allocateActivityTitle(String titleString) {
        if (getSupportActionBar() != null) {
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
        // suscribe the topic
        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        //Log.d(TAG, msg);
                        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(badgeUpdateReceiver);
    }

    // Helper method to resize and set icon
    private void resizeAndSetIcon(MenuItem menuItem, int drawableResId, int size) {
        Drawable icon = ContextCompat.getDrawable(this, drawableResId);
        if (icon != null) {
            // Convert the drawable into a bitmap
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();

            // Scale the bitmap to the desired size
            Drawable scaledIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));

            // Set the scaled drawable as the icon
            menuItem.setIcon(scaledIcon);
        }
    }

    // Helper method to set larger text size for MenuItem title
    private void increaseMenuItemTitleSize(MenuItem menuItem, String title, int textSizeInSp) {
        SpannableString spannableTitle = new SpannableString(title);

        // Set text size in SP (scale-independent pixels)
        spannableTitle.setSpan(new AbsoluteSizeSpan(textSizeInSp, true), 0, spannableTitle.length(), 0);

        // Set the title with the modified text size
        menuItem.setTitle(spannableTitle);
    }


}