package com.rhewum.Activity;
import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.DrawerBaseActivity;
import com.rhewum.R;
import com.rhewum.databinding.ActivityCapacityCheckerBinding;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CapacityCheckerActivity extends DrawerBaseActivity {
    private static final String TAG=CapacityCheckerActivity.class.getName();
    TextView txtBack,activity_mesh_trennschnitt_result;
    Spinner spinner;
    ImageView imgBack,img_test,textMachineTypeInfo,textScreenWidthInfo,textScreenAngleInfo,imgMaterialDenInfo,imgLayerHeightInfo;
    EditText edt_ScreenWidth,editTexScreenAngle,editTextMaterialDensity,editTextLayerHeight;
    RadioGroup radioGroup_screenWidth,radioGroup_Height;
    RadioButton radio_screenWidth_m,radio_screenWidth_ft,Radioheight_cm,Radioheight_inch;
    private List<String> spinnerItems;
    private ArrayAdapter<String> adapter;
   private String globalFlowVelocity,editTextWidth,editTextHeightValue;
    Button bt_submit;
    ActivityCapacityCheckerBinding activityCapacityCheckerBinding;


    Handler uiHandler;
    // Declare the executor service at the class level
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setFontFamily("fonts/heebo.ttf");
//        setContentView(R.layout.activity_capacity_checker);
        activityCapacityCheckerBinding = ActivityCapacityCheckerBinding.inflate(getLayoutInflater());
        setContentView(activityCapacityCheckerBinding.getRoot());
        initObjects();
        selectedItemSpinner();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });
        txtBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });


        textMachineTypeInfo.setOnClickListener(view -> showAlert("Read the machine type off your machines type plate"));
        textScreenWidthInfo.setOnClickListener(view -> showAlert("Measure the inside width or read the required parameters off your machines type plate"));
        textScreenAngleInfo.setOnClickListener(view -> showAlert("Please enter screen angle"));
        imgMaterialDenInfo.setOnClickListener(view -> showAlert("Give an estimation of the material density in the required unit"));
        imgLayerHeightInfo.setOnClickListener(view -> showAlert("While the machine is running, measure or estimate the height of the material layer directly at the infeed of the machine. It should be measured before material has fallen through the screen."));


        bt_submit.setOnClickListener(new View.OnClickListener() {
            double widthScreenValueM, layerHeightValueMM,MaterialDensityFt;
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onClick(View view) {
                String selectedItem = spinner.getSelectedItem().toString();
                Log.e("CapacityChecker","CapacityChecker"+selectedItem);
                int selectedWidth = radioGroup_screenWidth.getCheckedRadioButtonId();
                int selectedHeight = radioGroup_Height.getCheckedRadioButtonId();
                if (selectedItem.equals("Select one…")) {
                    // No valid option selected
                    Toast.makeText(CapacityCheckerActivity.this, "Please select Machine type", Toast.LENGTH_SHORT).show();
                } else if (selectedWidth == -1) {
                    // No radio button is selected
                    Toast.makeText(CapacityCheckerActivity.this, "Please select Screen width", Toast.LENGTH_SHORT).show();
                } else if (edt_ScreenWidth.getText().toString().isEmpty()) {
                    edt_ScreenWidth.setError("Please select Screen width");
                } else if (editTexScreenAngle.getText().toString().isEmpty()) {
                    editTexScreenAngle.setError("Please select Screen angle");
                } else if (editTextMaterialDensity.getText().toString().isEmpty()) {
                    editTextMaterialDensity.setError("Please select Material density");
                } else if (selectedHeight == -1) {
                    Toast.makeText(CapacityCheckerActivity.this, "Please select Layer height", Toast.LENGTH_SHORT).show();
                } else if (editTextLayerHeight.getText().toString().isEmpty()) {
                    editTextLayerHeight.setError("Please select Layer height");
                } else {
                    // Extract the value of height and width according to radio button selected by user
                    RadioButton selectedRadioButton = findViewById(selectedWidth);
                    String selectedOptionWidth = selectedRadioButton.getText().toString();

                    // for layer height radio button id
                    RadioButton selectedLayerHeight=findViewById(selectedHeight);
                    String selectedOptionHeight = selectedLayerHeight.getText().toString();

                    if(selectedOptionWidth.equals("m")){
                        widthScreenValueM = Double.parseDouble(edt_ScreenWidth.getText().toString());
                        MaterialDensityFt=Double.parseDouble(editTextMaterialDensity.getText().toString());
                        Log.e("Width screen","MaterialDensityFt M..."+MaterialDensityFt);
                    }else{
                        widthScreenValueM = Double.parseDouble(edt_ScreenWidth.getText().toString());
                        MaterialDensityFt=Double.parseDouble(editTextMaterialDensity.getText().toString())*0.0283;
                        Log.e("Width screen","MaterialDensityFt FT..."+MaterialDensityFt);
                    } if(selectedOptionHeight.equals("cm")){
                        layerHeightValueMM = (Double.parseDouble(editTextLayerHeight.getText().toString()))*10;
                        Log.e("Height screen","layerHeightValue CM TO MM..."+layerHeightValueMM);
                    } else {
                        layerHeightValueMM=(layerHeightValueMM)*(25.4);
                        Log.e("Height screen","layerHeightValue Inch TO  MM..."+layerHeightValueMM);
                    }

                    double rheTypeValue = Double.parseDouble(globalFlowVelocity);
                    // Use executor service to perform the calculations in a background thread
                    Future<Double> future = executorService.submit(() -> {
                        // Perform calculations
                        return (rheTypeValue)*(layerHeightValueMM)*(MaterialDensityFt)*(widthScreenValueM)*(0.0036);
                    });

                    // Update the UI with the result
                    try {
                        double layerHeight = future.get();
                        edt_ScreenWidth.setText("");
                        editTexScreenAngle.setText("");
                        editTextMaterialDensity.setText("");
                        editTextLayerHeight.setText("");
                        activity_mesh_trennschnitt_result.setText("");
                        runOnUiThread(() -> activity_mesh_trennschnitt_result.setText(String.format("%.2f", layerHeight)+" "+"mm"));
                        Log.e("Radio Width in m", selectedOptionWidth);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(CapacityCheckerActivity.this, "Error in calculation", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void initObjects(){
        txtBack=findViewById(R.id.txtBack);
        bt_submit=findViewById(R.id.bt_submit);
        textMachineTypeInfo=findViewById(R.id.textMachineTypeInfo);
        textScreenWidthInfo=findViewById(R.id.textScreenWidthInfo);
        textScreenAngleInfo=findViewById(R.id.textScreenAngleInfo);
        imgMaterialDenInfo=findViewById(R.id.imgMaterialDenInfo);
        imgLayerHeightInfo=findViewById(R.id.imgLayerHeightInfo);
        imgBack=findViewById(R.id.imgBack);
        img_test=findViewById(R.id.img_test);
        spinner=findViewById(R.id.spinner);
        edt_ScreenWidth=findViewById(R.id.edt_ScreenWidth);
        editTexScreenAngle=findViewById(R.id.editTexScreenAngle);
        editTextMaterialDensity=findViewById(R.id.editTextMaterialDensity);
        editTextLayerHeight=findViewById(R.id.editTextLayerHeight);
        radioGroup_screenWidth=findViewById(R.id.radioGroup_screenWidth);
        radioGroup_Height=findViewById(R.id.radioGroup_Height);
        radio_screenWidth_m=findViewById(R.id.radio_screenWidth_m);
        radio_screenWidth_ft=findViewById(R.id.radio_screenWidth_ft);
        Radioheight_cm=findViewById(R.id.Radioheight_cm);
        Radioheight_inch=findViewById(R.id.Radioheight_inch);
        activity_mesh_trennschnitt_result=findViewById(R.id.activity_mesh_trennschnitt_result);
        uiHandler = new Handler(Looper.getMainLooper());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.header_backgrounds));
    }
    private void selectedItemSpinner(){
        uiHandler.post(() -> {
            // Initialize the spinner items list
            spinnerItems = new ArrayList<>();
            String[] initialItems = getResources().getStringArray(R.array.spinner_items);
            spinnerItems.addAll(Arrays.asList(initialItems));
            // Create an ArrayAdapter using the spinner items list and a default spinner layout
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);

            // Set an item selected listener
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item
                    String selectedItem = spinnerItems.get(position);
                    if (selectedItem.equals("RHEsono(formely WA)") || selectedItem.equals("RHEmoto(formely WAU)")) {
                        globalFlowVelocity = String.valueOf(1);
                    } else if (selectedItem.equals("RHEstack(formely MDS)")) {
                        globalFlowVelocity = String.valueOf(0.08);
                    } else if (selectedItem.equals("RHEflex(formely RIUS)") || selectedItem.equals("RHEtrans(formely RIU)") || selectedItem
                            .equals("RHEfeed(formely RIM)")) {
                        globalFlowVelocity = String.valueOf(0.25);
                    } else if (selectedItem.equals("RHEsonox(formely WAF)") || selectedItem.equals("RHEmotox(formely WAUF)") || selectedItem.equals("RHEside/RHEmid(formely SV/AV)")) {
                        globalFlowVelocity = String.valueOf(0.3);
                    } else if (selectedItem.equals("RHEduo(formely DF)") || selectedItem.equals("RHEduox(formely DFM)")) {
                        globalFlowVelocity = String.valueOf(0.8);
                    } else if (selectedItem.equals("RHEox(formely UG)")) {
                        globalFlowVelocity = String.valueOf(0.4);
                    } else {
                        globalFlowVelocity = String.valueOf(0);
                    }

                    // Display a Toast message indicating the selected item
                    // Toast.makeText(CapacityCheckerActivity.this, "Selected: " + globalFlowVelocity, Toast.LENGTH_SHORT).show();

                    // Notify the adapter of the data change
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(CapacityCheckerActivity.this, "Please selected machine type value: " + globalFlowVelocity, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}