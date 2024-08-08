package com.rhewum.Activity;
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
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CapacityCheckerActivity extends AppCompatActivity {
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
    Handler uiHandler;
    // Declare the executor service at the class level
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setFontFamily("fonts/heebo.ttf");
        setContentView(R.layout.activity_capacity_checker);
        initObjects();
        selectedItemSpinner();

        imgBack.setOnClickListener(view ->{
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });
        txtBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });
        textMachineTypeInfo.setOnClickListener(view -> showAlert("Please provide your information machine type"));
        textScreenWidthInfo.setOnClickListener(view -> showAlert("Please provide your information screen type"));
        textScreenAngleInfo.setOnClickListener(view -> showAlert("Please provide your information screen angle"));
        imgMaterialDenInfo.setOnClickListener(view -> showAlert("Please provide your information material density"));
        imgLayerHeightInfo.setOnClickListener(view -> showAlert("Please provide your information layer height"));


        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedItem = spinner.getSelectedItem().toString();
                int selectedWidth = radioGroup_screenWidth.getCheckedRadioButtonId();
                int selectedHeight = radioGroup_Height.getCheckedRadioButtonId();

                if (selectedItem.equals("Select one...")) {
                    // No valid option selected
                    Toast.makeText(CapacityCheckerActivity.this, "Please select a valid option Machine type", Toast.LENGTH_SHORT).show();
                } else if (selectedWidth == -1) {
                    // No radio button is selected
                    Toast.makeText(CapacityCheckerActivity.this, "Please select an option Screen width", Toast.LENGTH_SHORT).show();
                } else if (edt_ScreenWidth.getText().toString().isEmpty()) {
                    edt_ScreenWidth.setError("Please select an option Screen width");
                } else if (editTexScreenAngle.getText().toString().isEmpty()) {
                    editTexScreenAngle.setError("Please select an option Screen angle");
                } else if (editTextMaterialDensity.getText().toString().isEmpty()) {
                    editTextMaterialDensity.setError("Please select an option Material density");
                } else if (selectedHeight == -1) {
                    Toast.makeText(CapacityCheckerActivity.this, "Please select an option Layer height", Toast.LENGTH_SHORT).show();
                } else if (editTextLayerHeight.getText().toString().isEmpty()) {
                    editTextLayerHeight.setError("Please select an option Layer height");
                } else {
                    // Extract necessary values
                    RadioButton selectedRadioButton = findViewById(selectedWidth);
                    String selectedOptionWidth = selectedRadioButton.getText().toString();

                    double rheTypeValue = Double.parseDouble(globalFlowVelocity);
                    double widthScreenValueM = Double.parseDouble(edt_ScreenWidth.getText().toString());
                    double screenValueAngle = Double.parseDouble(editTexScreenAngle.getText().toString());
                    double layerHeightValueCm = Double.parseDouble(editTextLayerHeight.getText().toString());
                    double materialDensityValueM = Double.parseDouble(editTextMaterialDensity.getText().toString());

                    // Use executor service to perform the calculations in a background thread
                    Future<Double> future = executorService.submit(() -> {
                        // Perform calculations
                        double upperResult = (rheTypeValue) * (widthScreenValueM) * (screenValueAngle);
                        double upperResult1 = (upperResult / layerHeightValueCm);
                        double lowerResult = (materialDensityValueM) * (widthScreenValueM * rheTypeValue) * (3.6);
                        return (upperResult1) / (lowerResult);
                    });

                    // Update the UI with the result
                    try {
                        double layerHeight = future.get();
                        runOnUiThread(() -> activity_mesh_trennschnitt_result.setText(String.format("%.3f", layerHeight)));
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
                    // Do nothing
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