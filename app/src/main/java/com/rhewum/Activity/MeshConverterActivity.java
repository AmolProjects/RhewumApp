package com.rhewum.Activity;

import static org.apache.commons.lang3.StringUtils.isNumeric;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import com.rhewum.Activity.MeshConveterData.Constants;
import com.rhewum.Activity.MeshConveterData.InputFilterMinMax;
import com.rhewum.Activity.MeshConveterData.ResponsiveAndroidBars;
import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.DrawerBaseActivity;
import com.rhewum.R;
import com.rhewum.databinding.ActivityDashBoardBinding;
import com.rhewum.databinding.ActivityMeshConverterBinding;

public class MeshConverterActivity extends DrawerBaseActivity implements View.OnClickListener{
    /* access modifiers changed from: private */
    public EditText angleEt;
    /* access modifiers changed from: private */
    public String angleEtStr;
    private EditText astm;
    private RadioButton automaticRadio;
    /* access modifiers changed from: private */
    public RelativeLayout customAlertDialog;
    private Dialog dialog;
    private EditText din_1;
    private EditText din_2;
    private RadioButton inchRadio;
    private RelativeLayout info;
    /* access modifiers changed from: private */
    public boolean isAlertShown = false;
    private boolean isAstm_Clicked = false;
    private boolean isDin1_Clicked = false;
    private boolean isDin2_Clicked = false;
    private boolean isTyler_Clicked = false;
    private int mainIndex = 0;
    private RadioButton manualRadio;
    /* access modifiers changed from: private */
    public EditText meshOpeningEt;
    /* access modifiers changed from: private */
    public String meshOpeningEtStr;
    private RadioButton mmRadio;
    private ScrollView parentScrollView;
    private int pickerAstmIndex = 0;
    /* access modifiers changed from: private */
    public int pickerIndex = 0;
    private int pickerTylerIndex = 0;
    /* access modifiers changed from: private */
    public TextView resultTv,meshBackTv,meshInfoTv;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;
    private TextView subTitle_angle;
    private DecimalFormat twoDigitForm = new DecimalFormat("##.##");
    private EditText tyler;
    private ImageView back,meshInfoIv;
    ActivityMeshConverterBinding activityMeshConverterBinding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Lock orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Utils.setFontFamily("fonts/heebo.ttf");
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mesh_converter);
        activityMeshConverterBinding = ActivityMeshConverterBinding.inflate(getLayoutInflater());
        setContentView(activityMeshConverterBinding.getRoot());
        setUpViews();

        ResponsiveAndroidBars.setNotificationBarColor(this, getResources().getColor(R.color.header_backgrounds), false);
        if (Utils.isTablet(this)) {
            ResponsiveAndroidBars.setNavigationBarColor(this, getResources().getColor(R.color.header_backgrounds), true, false);
        } else {
            ResponsiveAndroidBars.setNavigationBarColor(this, getResources().getColor(R.color.grey_background), false, false);
        }


        this.back.setOnClickListener(this);
        this.meshBackTv.setOnClickListener(this);
        this.meshInfoIv.setOnClickListener(this);
        this.meshInfoTv.setOnClickListener(this);
        this.din_1.setOnClickListener(this);
        this.astm.setOnClickListener(this);
        this.tyler.setOnClickListener(this);
        this.din_2.setOnClickListener(this);
        this.angleEt.setOnClickListener(this);
        this.automaticRadio.setOnClickListener(this);
        this.manualRadio.setOnClickListener(this);
        this.inchRadio.setOnClickListener(this);
        this.mmRadio.setOnClickListener(this);
        this.customAlertDialog = (RelativeLayout) findViewById(R.id.activity_mesh_layout_alert);
        this.parentScrollView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MeshConverterActivity.this.meshOpeningEt.setFocusableInTouchMode(true);
                MeshConverterActivity.this.angleEt.setFocusableInTouchMode(true);
                return false;
            }
        });

        this.meshOpeningEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String input = charSequence.toString().trim();

                // Flag to track whether input is valid for further processing
                boolean isValidInput = true;

                // Check for both comma and dot in the input (only one allowed)
                if (input.indexOf(",") != input.lastIndexOf(",") || input.indexOf(".") != input.lastIndexOf(".") ||
                        (input.contains(",") && input.contains("."))) {
                    // If more than one comma or dot, or both comma and dot are present, invalidate the input
                    isValidInput = false;
                } else if (input.contains(",") || input.contains(".")) {
                    // Split on either comma or dot
                    String[] parts = input.split("[,.]");

                    // If there are digits after the comma or dot
                    if (parts.length > 1 && parts[1].length() > 2) {
                        // Trim to allow only 2 digits after the comma or dot
                        input = parts[0] + (input.contains(",") ? "," : ".") + parts[1].substring(0, 2);
                        MeshConverterActivity.this.meshOpeningEt.setText(input);
                        MeshConverterActivity.this.meshOpeningEt.setSelection(input.length()); // Set cursor at the end
                    }
                }

                // Handle the mesh opening input based on whether input is valid
                if (!isValidInput) {
                    MeshConverterActivity.this.resultTv.setText("Invalid input"); // Optional: show error
                    return;
                }

                // Proceed if the input is valid and not empty
                if (MeshConverterActivity.this.meshOpeningEt.getText().toString().trim().equals("")
                        || MeshConverterActivity.this.angleEt.getText().toString().trim().equals("")) {
                    MeshConverterActivity.this.meshOpeningEtStr = input;
                    MeshConverterActivity.this.resultTv.setText("");
                    return;
                }

                // Assign the valid input to meshOpeningEtStr
                MeshConverterActivity.this.meshOpeningEtStr = input;

                // Perform the calculation
                MeshConverterActivity.this.calculateProjectedOpening();
            }
        });
        this.angleEt.setFilters(new InputFilter[]{new InputFilterMinMax("0", "45")});

        this.angleEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//                if (MeshConverterActivity.this.meshOpeningEt.getText().toString().trim().equals("") || MeshConverterActivity.this.angleEt.getText().toString().trim().equals("")) {
//                    String unused = MeshConverterActivity.this.angleEtStr = charSequence.toString().trim();
//                    MeshConverterActivity.this.resultTv.setText("");
//                    return;
//                }
//                String unused2 = MeshConverterActivity.this.angleEtStr = charSequence.toString().trim();
//                MeshConverterActivity.this.calculateProjectedOpening();

                String input = charSequence.toString().trim();

                if (MeshConverterActivity.this.meshOpeningEt.getText().toString().trim().equals("")
                        || MeshConverterActivity.this.angleEt.getText().toString().trim().equals("")) {
                    MeshConverterActivity.this.angleEtStr = input;
                    MeshConverterActivity.this.resultTv.setText("");
                    return;
                }
                MeshConverterActivity.this.angleEtStr = input;
                MeshConverterActivity.this.calculateProjectedOpening();
            }
        });
    }
    private void setUpViews() {
        this.back=(ImageView) findViewById(R.id.activity_mesh_back_iv);
        this.meshInfoIv=(ImageView) findViewById(R.id.activity_mesh_info_iv);
        this.info = (RelativeLayout) findViewById(R.id.activity_mesh_info_layout);
        this.din_1 = (EditText) findViewById(R.id.activity_mesh_din1_et);
        this.astm = (EditText) findViewById(R.id.activity_mesh_astm_et);
        this.tyler = (EditText) findViewById(R.id.activity_mesh_tyler_et);
        this.din_2 = (EditText) findViewById(R.id.activity_mesh_din2_et);
        this.parentScrollView = (ScrollView) findViewById(R.id.activity_mesh_parent_scrollView);
        this.resultTv = (TextView) findViewById(R.id.activity_mesh_trennschnitt_result);
        this.meshBackTv = (TextView) findViewById(R.id.activity_mesh_back_tv);
        this.meshInfoTv = (TextView) findViewById(R.id.activity_mesh_info_tv);
        this.mmRadio = (RadioButton) findViewById(R.id.radio_screenWidth_m);
        this.inchRadio = (RadioButton) findViewById(R.id.radio_screenWidth_ft);
        this.manualRadio = (RadioButton) findViewById(R.id.activity_mesh_trennschnitt_manual_radio);
        this.automaticRadio = (RadioButton) findViewById(R.id.activity_mesh_trennschnitt_automatic_radio);
        this.meshOpeningEt = (EditText) findViewById(R.id.activity_mesh_trennschnitt_et_1);
        this.angleEt = (EditText) findViewById(R.id.activity_mesh_trennschnitt_et_2);
        TextView textView = (TextView) findViewById(R.id.activity_mesh_trennschnitt_sub_title_3);
        this.subTitle_angle = textView;
        String htmlText = "&nbsp;Angle range is between 0&deg; and 45&deg;.";
        textView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));

    }

    public void onClick(View view) {
        if (view.equals(this.back)) {
            unRegisterSensorListner();
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

        else if (view.equals(this.meshBackTv)) {
            unRegisterSensorListner();
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

        else if (view.equals(this.meshInfoIv)) {
            unRegisterSensorListner();
            startActivity(new Intent(this, MeshInfoActivity.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }

        else if (view.equals(this.meshInfoTv)) {
            unRegisterSensorListner();
            startActivity(new Intent(this, MeshInfoActivity.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }

        else if (view.equals(this.info)) {
            unRegisterSensorListner();
            startActivity(new Intent(this, MeshInfoActivity.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        } else if (view.equals(this.din_1)) {
            this.isDin1_Clicked = true;
            this.isAstm_Clicked = false;
            this.isTyler_Clicked = false;
            this.isDin2_Clicked = false;
            hideSoftInput(this.din_1);
            openDialog("din1");
        } else if (view.equals(this.astm)) {
            this.isDin1_Clicked = false;
            this.isAstm_Clicked = true;
            this.isTyler_Clicked = false;
            this.isDin2_Clicked = false;
            hideSoftInput(this.astm);
            openDialog("astm");
        } else if (view.equals(this.tyler)) {
            this.isDin1_Clicked = false;
            this.isAstm_Clicked = false;
            this.isTyler_Clicked = true;
            this.isDin2_Clicked = false;
            hideSoftInput(this.tyler);
            openDialog("tyler");
        } else if (view.equals(this.din_2)) {
            this.isDin1_Clicked = false;
            this.isAstm_Clicked = false;
            this.isTyler_Clicked = false;
            this.isDin2_Clicked = true;
            hideSoftInput(this.din_2);
            openDialog("din2");
        } else if (view.equals(this.angleEt)) {
            if (this.automaticRadio.isChecked()) {
                hideSoftInput(this.angleEt);
            }
        } else if (view.equals(this.automaticRadio)) {
            this.angleEt.setCursorVisible(false);
            calculateAngle();
        } else if (view.equals(this.manualRadio)) {
            this.angleEt.setCursorVisible(true);
            unRegisterSensorListner();
        } else if (view.equals(this.inchRadio)) {
            if (!this.meshOpeningEt.getText().toString().trim().equals("") && !this.angleEt.getText().toString().trim().equals("")) {
                calculateProjectedOpening();
            }
        } else if (view.equals(this.mmRadio) && !this.meshOpeningEt.getText().toString().trim().equals("") && !this.angleEt.getText().toString().trim().equals("")) {
            calculateProjectedOpening();
        }
    }

    /* access modifiers changed from: private */
    /*public void calculateProjectedOpening() {
        try {
            if (this.meshOpeningEtStr != null && !this.meshOpeningEtStr.trim().isEmpty() && isNumeric(this.meshOpeningEtStr));

            double parseDouble = Double.parseDouble(this.meshOpeningEtStr);
            double parseDouble2 = Double.parseDouble(this.angleEtStr);
            if (this.inchRadio.isChecked()) {
                double cos = parseDouble * Math.cos(Math.toRadians(parseDouble2));
                TextView textView = this.resultTv;
                textView.setText(this.twoDigitForm.format(cos) + " inch");
                return;
            }
            double cos2 = parseDouble * Math.cos(Math.toRadians(parseDouble2));
            TextView textView2 = this.resultTv;
            textView2.setText(this.twoDigitForm.format(cos2) + " mm");
        }
        catch (NumberFormatException e){
            Toast.makeText(this, "Enter valid mesh opening input", Toast.LENGTH_SHORT).show();
        }

    }*/

//    private void calculateProjectedOpening() {
//        try {
//            if (meshOpeningEtStr != null && !meshOpeningEtStr.trim().isEmpty() && isNumeric(meshOpeningEtStr)) {
//                double meshOpening = Double.parseDouble(convertToDecimal(meshOpeningEtStr));
//                double angle = Double.parseDouble(convertToDecimal(angleEtStr));
//
//                double result;
//                if (inchRadio.isChecked()) {
//                    result = meshOpening * Math.cos(Math.toRadians(angle));
//                    resultTv.setText(twoDigitForm.format(result) + " inch");
//                } else {
//                    result = meshOpening * Math.cos(Math.toRadians(angle));
//                    resultTv.setText(twoDigitForm.format(result) + " mm");
//                }
//            }
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Enter valid mesh opening input", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void calculateProjectedOpening() {
        try {
            // Log the raw inputs
            Log.d("MeshConverter", "Raw meshOpeningEtStr: " + meshOpeningEtStr);
            Log.d("MeshConverter", "Raw angleEtStr: " + angleEtStr);

            String meshOpeningStr = convertToDecimal(meshOpeningEtStr);
            String angleStr = convertToDecimal(angleEtStr);

            // Log the converted inputs
            Log.d("MeshConverter", "Converted meshOpening: " + meshOpeningStr);
            Log.d("MeshConverter", "Converted angle: " + angleStr);

            if (meshOpeningStr != null && !meshOpeningStr.trim().isEmpty() && isNumeric(meshOpeningStr)) {
                double meshOpening = Double.parseDouble(meshOpeningStr);
                double angle = Double.parseDouble(angleStr);

                double result = meshOpening * Math.cos(Math.toRadians(angle));
                if (inchRadio.isChecked()) {
                    resultTv.setText(twoDigitForm.format(result) + " inch");
                } else {
                    resultTv.setText(twoDigitForm.format(result) + " mm");
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid mesh opening input", Toast.LENGTH_SHORT).show();
        }
    }




    private String convertToDecimal(String input) {
        return input.replace(",", ".");
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // Adjust regex if needed
    }
//    private void calculateAngle() {
//        SensorManager sensorManager2 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        this.sensorManager = sensorManager2;
//        this.sensor = sensorManager2.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        SensorEventListener r0 = new SensorEventListener() {
//            public void onAccuracyChanged(Sensor sensor, int i) {
//            }
//
//            public void onSensorChanged(SensorEvent sensorEvent) {
//                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                    float f = sensorEvent.values[0]; // X-axis
//                    float f2 = sensorEvent.values[1]; // Y-axis
//                    float f3 = sensorEvent.values[2]; // Z-axis
//
//                    // Calculate the magnitude of the acceleration vector
//                    float magnitude = (float) Math.sqrt(f * f + f2 * f2 + f3 * f3);
//
//                    // Normalize the accelerometer values
//                    float normalizedF = f / magnitude;
//                    float normalizedF2 = f2 / magnitude;
//                    float normalizedF3 = f3 / magnitude;
//
//                    // Calculate the angle with respect to the Z-axis
//                    int angle = (int) Math.toDegrees(Math.acos(normalizedF3));
//                    int absAngle = Math.abs(angle);
//
//                    angleEt.setText(String.valueOf(absAngle));
//                    if (absAngle > 45) {
//                        showAlert(); // Show alert if the angle exceeds 45 degrees
//                    } else {
//                        customAlertDialog.setVisibility(View.GONE);
//                        isAlertShown = false;
//                        angleEt.setText(String.valueOf(absAngle)); // Display the angle
//                    }
//
//                    // Check if the device is flat on the surface
//                    boolean isFlat = Math.abs(f3) > 9.0 && Math.abs(f) < 0 && Math.abs(f2) < 0;
//                    if (isFlat) {
//                        angleEt.setText(""); // Clear the angle display
//                        customAlertDialog.setVisibility(View.GONE);
//                        isAlertShown = false;
//                    }
//                }
//            }
//        };
//        this.sensorEventListener = r0;
//        this.sensorManager.registerListener(r0, this.sensor, SensorManager.SENSOR_DELAY_NORMAL);
//    }

    private void calculateAngle() {
        SensorManager sensorManager2 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.sensorManager = sensorManager2;
        this.sensor = sensorManager2.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener r0 = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int i) {
            }

            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float f = sensorEvent.values[0]; // X-axis
                    float f2 = sensorEvent.values[1]; // Y-axis
                    float f3 = sensorEvent.values[2]; // Z-axis

                    // Calculate the magnitude of the acceleration vector
                    float magnitude = (float) Math.sqrt(f * f + f2 * f2 + f3 * f3);

                    // Normalize the accelerometer values
                    float normalizedF = f / magnitude;
                    float normalizedF2 = f2 / magnitude;
                    float normalizedF3 = f3 / magnitude;

                    // Determine if tilt is along X or Z axis
                    if (Math.abs(normalizedF) > Math.abs(normalizedF3)) {
                        // Tilt is along the X-axis
                        int angleX = (int) Math.toDegrees(Math.acos(normalizedF));
                        angleEt.setText(String.valueOf(Math.abs(angleX)));

                        if (Math.abs(angleX) > 45) {
                            showAlert(); // Show alert if angle exceeds 45 degrees
                        } else {
                            customAlertDialog.setVisibility(View.GONE);
                            isAlertShown = false;
                        }
                    } else {
                        // Tilt is along the Z-axis
                        int angleZ = (int) Math.toDegrees(Math.acos(normalizedF3));
                        angleEt.setText(String.valueOf(Math.abs(angleZ)));

                        if (Math.abs(angleZ) > 45) {
                            showAlert(); // Show alert if angle exceeds 45 degrees
                        } else {
                            customAlertDialog.setVisibility(View.GONE);
                            isAlertShown = false;
                        }
                    }

                    // Check if the device is flat on the surface
                    boolean isFlat = Math.abs(f3) > 9.0 && Math.abs(f) < 0 && Math.abs(f2) < 0;
                    if (isFlat) {
                        angleEt.setText(""); // Clear the angle display
                        customAlertDialog.setVisibility(View.GONE);
                        isAlertShown = false;
                    }
                }
            }
        };
        this.sensorEventListener = r0;
        this.sensorManager.registerListener(r0, this.sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    /* access modifiers changed from: private */
    public void showAlert() {
        this.angleEt.setText("");
        findViewById(R.id.layout_custom_alert_ok_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MeshConverterActivity.this.customAlertDialog.setVisibility(View.GONE);
                isAlertShown = true;
            }
        });
        if (!isAlertShown) {
            customAlertDialog.setVisibility(View.VISIBLE);
            isAlertShown = true;
        }
    }

    private void unRegisterSensorListner() {
        SensorManager sensorManager2 = this.sensorManager;
        if (sensorManager2 != null || this.sensorEventListener != null) {
            assert sensorManager2 != null;
            sensorManager2.unregisterListener(this.sensorEventListener);
        }
    }

    private void openDialog(String str) {
        Dialog dialog2 = new Dialog(this);
        this.dialog = dialog2;
        dialog2.requestWindowFeature(1);
        this.dialog.setContentView(R.layout.number_picker_dialog);

        final NumberPicker numberPicker = (NumberPicker) this.dialog.findViewById(R.id.numberPicker);
        insertValuesToPicker(str, numberPicker);

        if (this.isDin1_Clicked) {

            int indexOf = Arrays.asList(Constants.din_1_Values).indexOf(this.din_1.getText().toString());
            this.mainIndex = indexOf;


            if (indexOf < 0) {
                numberPicker.setValue(0);
                this.din_1.setText(Constants.din_1_Values[0]);
                this.astm.setText(Constants.astm_Values[0]);
                this.tyler.setText(Constants.tyler_Values[0]);
                this.din_2.setText(Constants.din_2_Values[0]);
            } else {
                numberPicker.setValue(indexOf);

            }
        } else if (this.isAstm_Clicked) {
            this.mainIndex = Arrays.asList(Constants.astm_ValuesNew).indexOf(this.astm.getText().toString());
            if (this.astm.getText().toString().equals("-")) {
                numberPicker.setValue(1);
                this.din_1.setText(Constants.din_1_Values[1]);
                this.astm.setText(Constants.astm_Values[1]);
                this.tyler.setText(Constants.tyler_Values[1]);
                this.din_2.setText(Constants.din_2_Values[1]);
            } else {
                int i = this.mainIndex;
                if (i < 0) {
                    this.din_1.setText(Constants.din_1_Values[0]);
                    this.astm.setText(Constants.astm_Values[0]);
                    this.tyler.setText(Constants.tyler_Values[0]);
                    this.din_2.setText(Constants.din_2_Values[0]);
                } else {
                    numberPicker.setValue(i);
                }
            }
        } else if (this.isTyler_Clicked) {
            int indexOf2 = Arrays.asList(Constants.tyler_ValuesNew).indexOf(this.tyler.getText().toString());
            this.mainIndex = indexOf2;
            if (indexOf2 < 0) {
                numberPicker.setValue(0);
                this.din_1.setText(Constants.din_1_Values[0]);
                this.astm.setText(Constants.astm_Values[0]);
                this.tyler.setText(Constants.tyler_Values[0]);
                this.din_2.setText(Constants.din_2_Values[0]);
            } else {
                numberPicker.setValue(indexOf2);
            }
        } else if (this.isDin2_Clicked) {
            int indexOf3 = Arrays.asList(Constants.din_2_Values).indexOf(this.din_2.getText().toString());
            this.mainIndex = indexOf3;
            if (indexOf3 < 0) {
                numberPicker.setValue(0);
                this.din_1.setText(Constants.din_1_Values[0]);
                this.astm.setText(Constants.astm_Values[0]);
                this.tyler.setText(Constants.tyler_Values[0]);
                this.din_2.setText(Constants.din_2_Values[0]);
            } else {
                numberPicker.setValue(indexOf3);
            }
        }
//        NumberPicker.mListner = this;
//        numberPicker.setFocusable(false);
//        numberPicker.setFocusableInTouchMode(false);

        // Set an OnClickListener to dismiss the dialog when the NumberPicker is clicked
        numberPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();  // Dismiss the dialog
            }
        });


        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                int unused = MeshConverterActivity.this.pickerIndex = numberPicker.getValue();
                MeshConverterActivity meshActivity = MeshConverterActivity.this;
                meshActivity.insertUpdatedDataToPicker(meshActivity.pickerIndex);
            }
        });
        this.dialog.show();
    }

    /* access modifiers changed from: private */
    public void insertUpdatedDataToPicker(int i) {
        String str = Constants.din_1_Values[i];
        String str2 = Constants.astm_Values[i];
        String str3 = Constants.tyler_Values[i];
        String str4 = Constants.din_2_Values[i];
        if (this.isDin1_Clicked || this.isDin2_Clicked) {
            this.din_1.setText(str);
            this.astm.setText(str2);
            this.tyler.setText(str3);
            this.din_2.setText(str4);
        } else if (this.isAstm_Clicked) {
            String str5 = Constants.astm_ValuesNew[i];
            this.pickerAstmIndex = Arrays.asList(Constants.astm_Values).indexOf(str5);
            String str6 = Constants.din_1_Values[this.pickerAstmIndex];
            String str7 = Constants.tyler_Values[this.pickerAstmIndex];
            String str8 = Constants.din_2_Values[this.pickerAstmIndex];
            this.din_1.setText(str6);
            this.astm.setText(str5);
            this.tyler.setText(str7);
            this.din_2.setText(str8);
        } else if (this.isTyler_Clicked) {
            String str9 = Constants.tyler_ValuesNew[i];
            this.pickerTylerIndex = Arrays.asList(Constants.tyler_Values).indexOf(str9);
            String str10 = Constants.din_1_Values[this.pickerTylerIndex];
            String str11 = Constants.astm_Values[this.pickerTylerIndex];
            String str12 = Constants.din_2_Values[this.pickerTylerIndex];
            this.din_1.setText(str10);
            this.astm.setText(str11);
            this.tyler.setText(str9);
            this.din_2.setText(str12);
        }
    }


    private void insertValuesToPicker(String str, NumberPicker numberPicker) {
        if (str.equals("din1")) {

            numberPicker.setMaxValue(Constants.din_1_Values.length - 1);
            numberPicker.setDisplayedValues(Constants.din_1_Values);
            return;
        }
        int i = 0;
        if (str.equals("astm")) {
            ArrayList arrayList = new ArrayList();
            while (i < Constants.astm_Values.length) {
                if (!Constants.astm_Values[i].equals("-")) {
                    arrayList.add(Constants.astm_Values[i]);
                }
                i++;
            }
            Constants.astm_ValuesNew = new String[arrayList.size()];
            Constants.astm_ValuesNew = (String[]) arrayList.toArray(Constants.astm_ValuesNew);
            numberPicker.setDisplayedValues(Constants.astm_ValuesNew);
            numberPicker.setMaxValue(Constants.astm_ValuesNew.length - 1);
        } else if (str.equals("tyler")) {
            ArrayList arrayList2 = new ArrayList();
            while (i < Constants.tyler_Values.length) {
                if (!Constants.tyler_Values[i].equals("-")) {
                    arrayList2.add(Constants.tyler_Values[i]);
                }
                i++;
            }
            Constants.tyler_ValuesNew = new String[arrayList2.size()];
            Constants.tyler_ValuesNew = (String[]) arrayList2.toArray(Constants.tyler_ValuesNew);
            numberPicker.setDisplayedValues(Constants.tyler_ValuesNew);
            numberPicker.setMaxValue(Constants.tyler_ValuesNew.length - 1);
        } else if (str.equals("din2")) {
            numberPicker.setMaxValue(Constants.din_2_Values.length - 1);
            numberPicker.setDisplayedValues(Constants.din_2_Values);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        unRegisterSensorListner();
        this.manualRadio.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unRegisterSensorListner();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void hideSoftInput(EditText editText) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    public void onClick(boolean z) {
        if (z) {
            this.dialog.dismiss();
        }
    }
}