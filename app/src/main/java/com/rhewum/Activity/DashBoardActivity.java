package com.rhewum.Activity;
import static com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType.WORM;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rhewum.Activity.Adapter.HomeDashBoardAdapter;
import com.rhewum.Activity.Adapter.SliderAdapter;
import com.rhewum.Activity.data.ItemData;
import com.rhewum.Activity.model.SliderItem;
import com.rhewum.DrawerBaseActivity;
import com.rhewum.R;
import com.rhewum.databinding.ActivityDashBoardBinding;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

public class DashBoardActivity extends DrawerBaseActivity implements HomeDashBoardAdapter.ItemClickListener {
    ImageView img_newsBackword, img_newsForwad, img_viewPagerBackword, img_viewPagerForwad;
    RecyclerView recyclerView;
    List<ItemData> dataList;
    HomeDashBoardAdapter homeDashBoardAdapter;
    private FirebaseFirestore db;
    private SliderAdapter sliderAdapter;
    private List<SliderItem> sliderItems = new ArrayList<>();
    private int currentIndex = 0; // Start from the first item
    private SliderView sliderView;

    private static final String PREFS_NAME = "BadgePrefs";
    private static final String BADGE_COUNT_KEY = "badge_count";

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    ActivityDashBoardBinding activityDashBoardBinding;
    private BadgeDrawerArrowDrawable badgeDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestNotificationPermission();
        }
        FirebaseApp.initializeApp(this);
//       setContentView(R.layout.activity_dash_board);
      activityDashBoardBinding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(activityDashBoardBinding.getRoot());
        initObjects();


        db = FirebaseFirestore.getInstance();


        fetchSliderData();
        // setting the adapter
        homeDashBoardAdapter = new HomeDashBoardAdapter(DashBoardActivity.this, dataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        activityDashBoardBinding.recyclerView.setLayoutManager(layoutManager);
        HomeDashBoardAdapter.ViewHolder.setClickListener(this);
        activityDashBoardBinding.recyclerView.setAdapter(homeDashBoardAdapter);

        // Set click listeners for forward and backward navigation
        img_newsBackword.setOnClickListener(view -> navigateBackward());
        img_newsForwad.setOnClickListener(view -> navigateForward());

        // Click on forward
        img_viewPagerForwad.setOnClickListener(view -> {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int itemCount = homeDashBoardAdapter.getItemCount();
            if (firstVisibleItemPosition + 1 < itemCount) {
                activityDashBoardBinding.recyclerView.smoothScrollToPosition(firstVisibleItemPosition + 1);
            }
        });

        // Click on backward
        img_viewPagerBackword.setOnClickListener(view -> {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItemPosition - 1 >= 0) {
                activityDashBoardBinding.recyclerView.smoothScrollToPosition(firstVisibleItemPosition - 1);
            }
        });


    // click on fawad
        img_viewPagerForwad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int itemCount = homeDashBoardAdapter.getItemCount();
                if (firstVisibleItemPosition + 1 < itemCount) {
                    activityDashBoardBinding.recyclerView.smoothScrollToPosition(firstVisibleItemPosition + 1);
                }
            }
        });

        // click on backward
        img_viewPagerBackword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition - 1 >= 0) {
                    activityDashBoardBinding.recyclerView.smoothScrollToPosition(firstVisibleItemPosition - 1);
                }
            }

        });


    }

//    private void updateBadge(int badgeCount) {
//        ImageView badgeImageView = findViewById(R.id.toolBar); // Replace with your actual ImageView ID
//        if (badgeImageView != null) {
//            if (badgeCount > 0) {
//                badgeDrawable.setText(String.valueOf(badgeCount));
//                badgeImageView.setImageDrawable(badgeDrawable);
//            } else {
//                badgeDrawable.setText(""); // Clear badge text if no notifications
//                badgeImageView.setImageDrawable(null); // Hide the badge if no notifications
//            }
//            badgeDrawable.invalidateSelf(); // Force redraw
//        }
//    }

    private void checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            // Show an explanation to the user
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                // You can show your custom dialog or UI here
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                // No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void navigateForward() {
//        if (sliderItems.size() == 0) {
//            Toast.makeText(this, "No items to display", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (currentIndex < sliderItems.size() - 1) {
//            currentIndex++;
//            sliderAdapter.setCurrentPosition(currentIndex);
//            sliderAdapter.notifyDataSetChanged(); // Update the adapter to show the new item
//            Toast.makeText(this, "Forward Clicked", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "End of List", Toast.LENGTH_SHORT).show();
//        }
        if (sliderView != null && sliderAdapter != null) {
            int nextIndex = sliderView.getCurrentPagePosition() + 1;
            if (nextIndex < sliderAdapter.getCount()) {
                sliderView.setCurrentPagePosition(nextIndex);
            } else {
                // Optionally handle if reached the end
                Toast.makeText(this, "You are at the last item.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateBackward() {
//        if (sliderItems.size() == 0) {
//            Toast.makeText(this, "No items to display", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (currentIndex > 0) {
//            currentIndex--;
//            sliderAdapter.setCurrentPosition(currentIndex);
//            sliderAdapter.notifyDataSetChanged(); // Update the adapter to show the new item
//            Toast.makeText(this, "Backward Clicked", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Start of List", Toast.LENGTH_SHORT).show();
//        }

        if (sliderView != null && sliderAdapter != null) {
            int nextIndex = sliderView.getCurrentPagePosition() -1;
            if (nextIndex < sliderAdapter.getCount()) {
                sliderView.setCurrentPagePosition(nextIndex);
            } else {
                // Optionally handle if reached the end
                Toast.makeText(this, "You are at the last item.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateSlider() {
        if (sliderAdapter != null) {
            sliderAdapter.setCurrentPosition(currentIndex);

        }

    }


    private void fetchSliderData() {
        db.collection("news")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error listening for updates: ", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        List<SliderItem> sliderItems = new ArrayList<>();
                        for (QueryDocumentSnapshot document : snapshots) {
                            SliderItem item = document.toObject(SliderItem.class);
                            sliderItems.add(item);
                        }
                        setupSlider(sliderItems);
                    }
                });
    }


    private void setupSlider(List<SliderItem> sliderItems) {
        sliderView = findViewById(R.id.img_test);
        sliderAdapter = new SliderAdapter(this, sliderItems);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setScrollTimeInSec(5);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorAnimation(WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        if (sliderItems.isEmpty()) {
            Toast.makeText(this, "No items available for slider", Toast.LENGTH_SHORT).show();
        }
    }



    private void initObjects() {
        img_newsBackword = findViewById(R.id.img_newsBackword);
        img_newsForwad = findViewById(R.id.img_newsForwad);
        recyclerView = findViewById(R.id.recyclerView);
        img_viewPagerBackword = findViewById(R.id.img_viewPagerBackword);
        img_viewPagerForwad = findViewById(R.id.img_viewPagerForwad);


        // Sample data
        dataList = new ArrayList<>();
        int[] images = {
                R.drawable.mesh, R.drawable.vibsonic, R.drawable.vibflash,
                R.drawable.vibchecker, R.drawable.capacitychecker
        };

        String[] titles = {
                "MeshConverter", "VibSonic", "VibFlash",
                "VibChecker", "CapacityChecker"
        };

        for (int i = 0; i < images.length; i++) {
            dataList.add(new ItemData(titles[i], images[i]));
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        // Determine which activity to open based on the position
        Class<?> activityClass;
        switch (position) {
            case 0:
                activityClass = MeshConverterActivity.class;
                break;
            case 1:
                activityClass = VibSonicActivity.class;
                break;
            case 2:
                activityClass = VibFlashActivity.class;
                break;
            case 3:
                activityClass = VibCheckerAccelerometer2Activity.class;
                break;
            case 4:
                activityClass = CapacityCheckerActivity.class;
                break;
            // Add more cases as needed
            default:
                activityClass = MeshConverterActivity.class;
                break;
        }
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();


//        LocalBroadcastManager.getInstance(this).registerReceiver(badgeUpdateReceiver,
//                new IntentFilter("com.rhewum.UPDATE_BADGE"));
    }
    private void clearBadgeCount() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(BADGE_COUNT_KEY, 0).apply();
        ShortcutBadger.applyCount(this, 0); // for 1.1.4+
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with displaying notifications
            } else {
                // Permission denied, show a message to the user
            }
        }
    }

//    private final BroadcastReceiver badgeUpdateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d("BadgeUpdate", "Broadcast received. Intent: " + intent);
//            if (intent != null && intent.hasExtra("badge_count")) {
//                int badgeCount = intent.getIntExtra("badge_count", 0);
//                Log.d("BadgeUpdate", "Badge count: " + badgeCount);
////                updateBadge(badgeCount);
//            } else {
//                Log.d("BadgeUpdate", "Broadcast received with no badge count.");
//            }
//        }
//    };

//    private void updateBadge(int badgeCount) {
//        ImageView badgeImageView = findViewById(R.id.toolBar); // Ensure this ID matches your toolbar
//        if (badgeImageView != null) {
//            if (badgeCount > 0) {
//                badgeDrawable.setText(""); // Red dot only, no text
//                badgeImageView.setImageDrawable(badgeDrawable);
//            } else {
//                badgeImageView.setImageDrawable(null); // Hide the badge if no notifications
//            }
//            badgeDrawable.invalidateSelf(); // Force redraw
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(badgeUpdateReceiver);
    }
}