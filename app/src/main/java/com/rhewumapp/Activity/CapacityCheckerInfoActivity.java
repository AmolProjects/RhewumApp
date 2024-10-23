package com.rhewumapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rhewumapp.Activity.Adapter.MaterialAdapter;
import com.rhewumapp.R;
import com.rhewumapp.Utils.Material;
import com.rhewumapp.Utils.MaterialList;

import java.util.List;

public class CapacityCheckerInfoActivity extends AppCompatActivity {
 RecyclerView resCapacity;
 ImageView back_button;
 TextView txtBack;
    private MaterialAdapter materialAdapter;
    private List<Material> materialList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capacity_checker_info);
        initObjects();
        resCapacity.setLayoutManager(new LinearLayoutManager(this));
        MaterialList materialListObj = new MaterialList();
        materialList = materialListObj.getMaterials();

        materialAdapter = new MaterialAdapter(this, materialList);
        resCapacity.setAdapter(materialAdapter);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    private  void initObjects(){
        resCapacity=findViewById(R.id.resCapacity);
        back_button=findViewById(R.id.back_button);
        txtBack=findViewById(R.id.txtBack);

    }
}