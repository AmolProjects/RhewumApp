package com.rhewumapp.Activity;
import static com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType.WORM;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.rhewumapp.Activity.Adapter.HomeDashBoardAdapter;
import com.rhewumapp.Activity.Adapter.SliderAdapter;
import com.rhewumapp.Activity.Pojo.MainJsonNews;
import com.rhewumapp.Activity.Pojo.SubArrayNews;
import com.rhewumapp.Activity.data.ItemData;
import com.rhewumapp.Apis.ApiServices;
import com.rhewumapp.Apis.RetrofitInstance;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;
import com.rhewumapp.Utils.DeviceUtils;
import com.rhewumapp.databinding.ActivityDashBoardBinding;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardActivity extends DrawerBaseActivity implements HomeDashBoardAdapter.ItemClickListener {
    ImageView img_newsBackword, img_newsForwad, img_viewPagerBackword, img_viewPagerForwad;
    RecyclerView recyclerView;
    List<ItemData> dataList;
    HomeDashBoardAdapter homeDashBoardAdapter;
    private FirebaseFirestore db;
    private SliderAdapter sliderAdapter;
    private int currentIndex = 0; // Start from the first item
    private SliderView sliderView;
    public ArrayList<MainJsonNews>mainJsonNewsArrayList;
    public ArrayList<SubArrayNews>subArrayNews;
    private static final String PREFS_NAME = "BadgePrefs";
    private static final String BADGE_COUNT_KEY = "badge_count";

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    ActivityDashBoardBinding activityDashBoardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeApiCallWithExecutor();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestNotificationPermission();
        }
        // FirebaseApp.initializeApp(this);
//       setContentView(R.layout.activity_dash_board);
        activityDashBoardBinding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(activityDashBoardBinding.getRoot());
        initObjects();

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
                    recyclerView.smoothScrollToPosition(firstVisibleItemPosition + 1);
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
                    recyclerView.smoothScrollToPosition(firstVisibleItemPosition - 1);
                }
            }

        });


    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
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
        if (sliderView != null && sliderAdapter != null) {
            int nextIndex = sliderView.getCurrentPagePosition() + 1;
            if (nextIndex < sliderAdapter.getCount()) {
                sliderView.setCurrentPagePosition(nextIndex);
            } else {
                // Optionally handle if reached the end
                // Toast.makeText(this, "You are at the last item.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateBackward() {
        if (sliderView != null && sliderAdapter != null) {
            int nextIndex = sliderView.getCurrentPagePosition() -1;
            if (nextIndex < sliderAdapter.getCount()) {
                sliderView.setCurrentPagePosition(nextIndex);
            } else {
                // Optionally handle if reached the end
                //   Toast.makeText(this, "You are at the last item.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateSlider() {
        if (sliderAdapter != null) {
            sliderAdapter.setCurrentPosition(currentIndex);

        }

    }

    private void setupSlider(ArrayList<SubArrayNews> sliderItems) {
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
        Window window = getWindow();
        mainJsonNewsArrayList=new ArrayList<>();
        subArrayNews=new ArrayList<>();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.header_backgrounds));
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
        // Retrieve device ID and Android ID

        String androidId = DeviceUtils.getAndroidId(this);
        Log.e("androidId ","androidId:"+androidId);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(this, MeshConverterActivity.class);
                startActivity(intent);
                break;
            case 1:
                Toast.makeText(this,"Work in Progress..",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Intent intent1 = new Intent(this, VibFlashActivity.class);
                startActivity(intent1);
                break;
            case 3:
                Toast.makeText(this,"Work in Progress..",Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Intent intent4 = new Intent(this, CapacityCheckerActivity.class);
                startActivity(intent4);
                break;
        }

    }

/*
    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(this, MeshConverterActivity.class);
                startActivity(intent);
                break;
            case 1:
                Intent intent2 = new Intent(this, VibSonicActivity.class);
                startActivity(intent2);
                break;
            case 2:
                Intent intent3 = new Intent(this, VibFlashActivity.class);
                startActivity(intent3);
                break;
            case 3:
                Intent intent4 = new Intent(this, VibCheckerAccelerometer2Activity.class);
                startActivity(intent4);
                break;
            case 4:
                Intent intent5 = new Intent(this, CapacityCheckerActivity.class);
                startActivity(intent5);
                break;
        }

    }*/
    @Override
    protected void onResume() {
        super.onResume();

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
    // create makeApiCallWithExecutor
    public void makeApiCallWithExecutor() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                apiCall();
            }
        });
    }
    public void apiCall(){
        ApiServices apiServices= RetrofitInstance.getRetrofit().create(ApiServices.class);
        Call<MainJsonNews> call=apiServices.getNews(1,"pUFUoSFjTLQng8hFbSs4tAI9LwZmJBiOApWItqPLzMwVUAsRQf");
        call.enqueue(new Callback<MainJsonNews>() {
            @Override
            public void onResponse(Call<MainJsonNews> call, Response<MainJsonNews> response) {
                assert response.body() != null;
//                ArrayList<SubArrayNews>subArrayNews=response.body().getData();
//                setupSlider(subArrayNews);
                List<SubArrayNews> subArrayNews = response.body().getData();
                // Limit the size of the list and convert it to ArrayList
                ArrayList<SubArrayNews> limitedNews = new ArrayList<>(subArrayNews.subList(0, Math.min(subArrayNews.size(), 5)));
                setupSlider(limitedNews);

            }

            @Override
            public void onFailure(Call<MainJsonNews> call, Throwable t) {

            }
        });
    }

}