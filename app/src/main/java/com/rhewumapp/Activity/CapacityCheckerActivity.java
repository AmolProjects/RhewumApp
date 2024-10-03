package com.rhewumapp.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;
import com.rhewumapp.databinding.ActivityCapacityCheckerBinding;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CapacityCheckerActivity extends DrawerBaseActivity {
    private static final String TAG = CapacityCheckerActivity.class.getName();
    TextView txtBack, activity_mesh_trennschnitt_result;
    Spinner spinner;
    ImageView img_test, textMachineTypeInfo, textScreenWidthInfo, textScreenAngleInfo, imgMaterialDenInfo, imgLayerHeightInfo;
    ImageView imges_back;
    EditText edt_ScreenWidth, editTextMaterialDensity, editTextLayerHeight;
    RadioGroup radioGroup_screenWidth, radioGroup_MaterialDensity, radioGroup_Height;
    RadioButton radio_screenWidth_m, radio_screenWidth_ft, radio_Material_DensityM, radio_Material_DensityLb, Radioheight_cm, Radioheight_inch;
    Button bt_submit;
    ActivityCapacityCheckerBinding activityCapacityCheckerBinding;

    Handler uiHandler;
    private List<String> spinnerItems;
    private ArrayAdapter<String> adapter;
    private String globalFlowVelocity, editTextWidth, editTextHeightValue;
    // Declare the executor service at the class level
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCapacityCheckerBinding = ActivityCapacityCheckerBinding.inflate(getLayoutInflater());
        setContentView(activityCapacityCheckerBinding.getRoot());
        initObjects();
        selectedItemSpinner();

        View rootView = activityCapacityCheckerBinding.getRoot(); // This is the root view of your activity layout
        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View view = getCurrentFocus(); // Get the currently focused view (usually an EditText)
                if (view instanceof EditText) {
                    int[] location = {0, 0};
                    view.getLocationOnScreen(location);
                    float x = event.getRawX() + view.getLeft() - location[0];
                    float y = event.getRawY() + view.getTop() - location[1];
                    if (x < 0 || x > view.getWidth() || y < 0 || y > view.getHeight()) {
                        closeKeyboard(view); // Close keyboard if touch is outside the EditText
                    } else {
                        view.performClick(); // Call performClick() for accessibility
                    }
                }
            }
            return false; // Return false to allow other events to be handled
        });


        CardView cardView = findViewById(R.id.cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(v); // Close keyboard if touch is outside the EditText

            }
        });
        // Add TextWatcher to EditText fields
        edt_ScreenWidth.addTextChangedListener(new InputTextWatcher());
        editTextMaterialDensity.addTextChangedListener(new InputTextWatcher());
        editTextLayerHeight.addTextChangedListener(new InputTextWatcher());
        radioGroup_screenWidth.setOnCheckedChangeListener(new RadioGroupChangeListener());
        radioGroup_MaterialDensity.setOnCheckedChangeListener(new RadioGroupChangeListener());
        radioGroup_Height.setOnCheckedChangeListener(new RadioGroupChangeListener());


        imges_back.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });
        txtBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });

        textMachineTypeInfo.setOnClickListener(view -> showAlert("Read the machine's type off your machines type plate"));
        textScreenWidthInfo.setOnClickListener(view -> showAlert("Measure the inside width or read the required parameters off your machine's type plate"));
        imgMaterialDenInfo.setOnClickListener(view -> showAlert("Give an estimation of the material density in the required unit"));
        imgLayerHeightInfo.setOnClickListener(view -> showAlert("While the machine is running, measure or estimate the height of the material layer directly at the infeed of the machine. It should be measured before material has fallen through the screen."));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void initObjects() {
        txtBack = findViewById(R.id.txtBack);
        // bt_submit = findViewById(R.id.bt_submit);
        textMachineTypeInfo = findViewById(R.id.textMachineTypeInfo);
        textScreenWidthInfo = findViewById(R.id.textScreenWidthInfo);
        imgMaterialDenInfo = findViewById(R.id.imgMaterialDenInfo);
        imgLayerHeightInfo = findViewById(R.id.imgLayerHeightInfo);
        imges_back = findViewById(R.id.back_button);
        img_test = findViewById(R.id.img_test);
        spinner = findViewById(R.id.spinner);
        edt_ScreenWidth = findViewById(R.id.edt_ScreenWidth);
        editTextMaterialDensity = findViewById(R.id.editTextMaterialDensity);
        editTextLayerHeight = findViewById(R.id.editTextLayerHeight);
        radioGroup_screenWidth = findViewById(R.id.radioGroup_screenWidth);
        radioGroup_MaterialDensity = findViewById(R.id.radioGroup_MaterialDensity);
        radioGroup_Height = findViewById(R.id.radioGroup_Height);
        radio_screenWidth_m = findViewById(R.id.radio_screenWidth_m);
        radio_screenWidth_ft = findViewById(R.id.radio_screenWidth_ft);
        radio_Material_DensityM = findViewById(R.id.radio_Material_DensityM);
        radio_Material_DensityLb = findViewById(R.id.radio_Material_DensityLb);
        Radioheight_cm = findViewById(R.id.Radioheight_cm);
        Radioheight_inch = findViewById(R.id.Radioheight_inch);
        activity_mesh_trennschnitt_result = findViewById(R.id.activity_mesh_trennschnitt_result);
        uiHandler = new Handler(Looper.getMainLooper());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.header_backgrounds));
    }


    private class InputTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Trigger calculation when text changes
            calculateAndUpdateResult();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }


    }

    private class RadioGroupChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // Trigger calculation when radio button changes
            calculateAndUpdateResult();
        }
    }


    private void calculateAndUpdateResult() {
        // Replace commas with dots in EditText values to handle German decimal input
        String screenWidthText = edt_ScreenWidth.getText().toString().replace(",", ".");
        String materialDensityText = editTextMaterialDensity.getText().toString().replace(",", ".");
        String layerHeightText = editTextLayerHeight.getText().toString().replace(",", ".");

        // Ensure all inputs are valid
        if (validateInputs(screenWidthText, materialDensityText, layerHeightText)) {
            double widthScreenValueM, layerHeightValueMM, MaterialDensityFt;
            try {
                String selectedItem = spinner.getSelectedItem().toString();
                int selectedWidth = radioGroup_screenWidth.getCheckedRadioButtonId();
                int selectedKgM = radioGroup_MaterialDensity.getCheckedRadioButtonId();
                int selectedHeight = radioGroup_Height.getCheckedRadioButtonId();

                RadioButton selectedRadioButton = findViewById(selectedWidth);
                String selectedOptionWidth = selectedRadioButton.getText().toString();

                RadioButton selectedKgm = findViewById(selectedKgM);
                String selectedOptionKg = selectedKgm.getText().toString();

                RadioButton selectedLayerHeight = findViewById(selectedHeight);
                String selectedOptionHeight = selectedLayerHeight.getText().toString();

                // Convert width
                if (selectedOptionWidth.equals("m")) {
                    widthScreenValueM = Double.parseDouble(screenWidthText);
                } else {
                    widthScreenValueM = Double.parseDouble(screenWidthText) * 0.304;
                }

                // Convert density
                if (selectedOptionKg.equals("kg/m3")) {
                    MaterialDensityFt = Double.parseDouble(materialDensityText);
                } else {
                    MaterialDensityFt = Double.parseDouble(materialDensityText) * 16.018;
                }

                // Convert height
                if (selectedOptionHeight.equals("cm")) {
                    layerHeightValueMM = Double.parseDouble(layerHeightText) * 10;
                } else {
                    layerHeightValueMM = Double.parseDouble(layerHeightText) * 25.4;
                }

                double rheTypeValue = Double.parseDouble(globalFlowVelocity);

                Future<Double> future = executorService.submit(() -> {
                    return (rheTypeValue) * (layerHeightValueMM) * (MaterialDensityFt) * (widthScreenValueM) * (0.0036);
                });

                try {
                    double layerHeight = future.get();
                    runOnUiThread(() -> activity_mesh_trennschnitt_result.setText(String.format("%.2f", layerHeight) + " t/h"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CapacityCheckerActivity.this, "Error in calculation", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(CapacityCheckerActivity.this, "Enter valid input", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean validateInputs(String screenWidth, String materialDensity, String layerHeight) {
        String decimalPattern = "^[0-9]*\\.?[0-9]+$";

        return !screenWidth.isEmpty() && screenWidth.matches(decimalPattern) &&
                !materialDensity.isEmpty() && materialDensity.matches(decimalPattern) &&
                !layerHeight.isEmpty() && layerHeight.matches(decimalPattern) &&
                radioGroup_screenWidth.getCheckedRadioButtonId() != -1 &&
                radioGroup_MaterialDensity.getCheckedRadioButtonId() != -1 &&
                radioGroup_Height.getCheckedRadioButtonId() != -1 &&
                spinner.getSelectedItemPosition() != -1;
    }


    private void selectedItemSpinner() {
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

                    // Update the globalFlowVelocity based on the selected item
                    switch (selectedItem) {
                        case "RHEsono(formely WA)":
                        case "RHEmoto(formely WAU)":
                            globalFlowVelocity = String.valueOf(1);
                            break;
                        case "RHEstack(formely MDS)":
                            globalFlowVelocity = String.valueOf(0.08);
                            break;
                        case "RHEflex(formely RIUS)":
                        case "RHEtrans(formely RIU)":
                        case "RHEfeed(formely RIM)":
                            globalFlowVelocity = String.valueOf(0.25);
                            break;
                        case "RHEsonox(formely WAF)":
                        case "RHEmotox(formely WAUF)":
                        case "RHEside/RHEmid(formely SV/AV)":
                            globalFlowVelocity = String.valueOf(0.3);
                            break;
                        case "RHEduo(formely DF)":
                        case "RHEduox(formely DFM)":
                            globalFlowVelocity = String.valueOf(0.8);
                            break;
                        case "RHEox(formely UG)":
                            globalFlowVelocity = String.valueOf(0.4);
                            break;
                        default:
                            globalFlowVelocity = String.valueOf(0);
                            break;
                    }

                    // Notify the adapter of the data change
                    adapter.notifyDataSetChanged();

                    // Trigger recalculation
                    calculateAndUpdateResult();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(CapacityCheckerActivity.this, "Please select a machine type", Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                int[] location = {0, 0};
                view.getLocationOnScreen(location);
                float x = event.getRawX() + view.getLeft() - location[0];
                float y = event.getRawY() + view.getTop() - location[1];
                if (x < 0 || x > view.getWidth() || y < 0 || y > view.getHeight()) {
                    closeKeyboard(view);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void closeKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

}