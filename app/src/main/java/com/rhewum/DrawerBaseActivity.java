package com.rhewum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.rhewum.Activity.DashBoardActivity;
import com.rhewum.Activity.InfoActivity;
import com.rhewum.Activity.NewsActivity;
import com.rhewum.Activity.WebsiteActivity;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private ImageView cancelIcon;

    private TextView activityTitleTextView;
    private Button backButton;



    @Override
    public void setContentView(View view) {

        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base,null);
        FrameLayout container =drawerLayout.findViewById(R.id.activityContainer);

        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar =drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Inflate the header view and find the ImageViewCancel
        View headerView = navigationView.getHeaderView(0);
        ImageView imageViewCancel = headerView.findViewById(R.id.imageViewCancel);

        // Set click listener for the ImageViewCancel
        imageViewCancel.setOnClickListener(v -> {
            Toast.makeText(this, "Cancel clicked", Toast.LENGTH_SHORT).show();
            // You can add more actions here, e.g., closing the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.menu_drawer_open,R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(Color.BLACK); // Change to your desired color
        toggle.syncState();

        // Find and set up the activity title and back button
        activityTitleTextView = findViewById(R.id.activityTitle);
        backButton = findViewById(R.id.backButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        if (id == R.id.nav_home){
            startActivity(new Intent(this, DashBoardActivity.class));
        }
        else if (id == R.id.nav_news) {
            startActivity(new Intent(this, NewsActivity.class));
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
}