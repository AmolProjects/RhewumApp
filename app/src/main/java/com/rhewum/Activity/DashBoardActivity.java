package com.rhewum.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rhewum.Activity.Adapter.HomeDashBoardAdapter;
import com.rhewum.Activity.data.ItemData;
import com.rhewum.R;

import java.util.ArrayList;
import java.util.List;

public class DashBoardActivity extends AppCompatActivity implements HomeDashBoardAdapter.ItemClickListener  {
    ImageView img_newsBackword,img_newsForwad,img_viewPagerBackword,img_viewPagerForwad;
    RecyclerView recyclerView;
    List<ItemData> dataList;
    HomeDashBoardAdapter homeDashBoardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        initObjects();
        // setting the adapter
        homeDashBoardAdapter = new HomeDashBoardAdapter(DashBoardActivity.this,dataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        HomeDashBoardAdapter.ViewHolder.setClickListener(this);
        recyclerView.setAdapter(homeDashBoardAdapter);

        // click on fawad
        img_viewPagerForwad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int itemCount = homeDashBoardAdapter.getItemCount();
                if (firstVisibleItemPosition + 1 < itemCount) {
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
                    recyclerView.smoothScrollToPosition(firstVisibleItemPosition - 1);
                }
            }

        });
    }
    private void initObjects() {
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


}