package com.rhewumapp.Activity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rhewumapp.Activity.Pojo.Notifications;
import com.rhewumapp.Apis.ApiServices;
import com.rhewumapp.Apis.RetrofitInstance;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends DrawerBaseActivity {
    private boolean isAskPermission = false;
    private static final int AUDIO_PERMISSION_REQUEST_CODE = 201;
    private static final String TAG=SplashActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_splash);
      //  getNotificationApi();
        askPermission();
    }
    // resume the screen
    @Override
    protected void onResume() {
        super.onResume();
        //  initObject();
        askPermission();
    }
    // initilize the init object
    private void openNextPage() {

        new Handler().postDelayed(this::openNextPages, 2000);
    }
    private void askPermission() {
        if (!isAskPermission) {
            isAskPermission = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.e(TAG, "SDK version: " + Build.VERSION.SDK_INT);
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)!=PackageManager.PERMISSION_GRANTED)
                {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                    android.Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES,
                                    Manifest.permission.READ_MEDIA_VIDEO},
                            AUDIO_PERMISSION_REQUEST_CODE);
                } else {
                    openNextPage();
                }
            } else {
                Log.e(TAG, "SDK version: " + Build.VERSION.SDK_INT);
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||

                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.MODIFY_AUDIO_SETTINGS},
                            AUDIO_PERMISSION_REQUEST_CODE);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.CAMERA},
                            AUDIO_PERMISSION_REQUEST_CODE);
                } else {
                    openNextPage();
                }
            }
        }
    }

    private void openNextPages() {
        Intent loginIntent = new Intent(SplashActivity.this, DashBoardActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: ");
        if (requestCode == AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with your task
                openNextPage();
            } else {
                // Permissions denied, inform the user
                Toast.makeText(this, "Audio permissions denied", Toast.LENGTH_SHORT).show();
//                askPermission();
            }
        }
    }

    private void getNotificationApi(){
        ApiServices apiServices= RetrofitInstance.getRetrofit().create(ApiServices.class);
        // Create request body
        Notifications requestBody = new Notifications();
        Call<Notifications>call=apiServices.postNotificationData("vo5agsgDG36FYO1c58vCfL4gEZ0Jg4Dqr9ZlKqHPCT8QKcz2MpyJA3CQhDqD",requestBody);
        call.enqueue(new Callback<Notifications>() {
            @Override
            public void onResponse(@NonNull Call<Notifications> call, @NonNull Response<Notifications> response) {
                Log.e("Notification Response","Notification Response is::"+response.body());

            }

            @Override
            public void onFailure(@NonNull Call<Notifications> call, @NonNull Throwable t) {

            }
        });
    }
}