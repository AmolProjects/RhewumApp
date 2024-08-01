package com.rhewum.Activity;

import android.annotation.SuppressLint;
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
import com.rhewum.R;

public class MeshConverterActivity extends AppCompatActivity implements View.OnClickListener{
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setFontFamily("fonts/heebo.ttf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesh_converter);
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
        this.angleEt.setFilters(new InputFilter[]{new InputFilterMinMax("0", "45")});
        this.meshOpeningEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (MeshConverterActivity.this.meshOpeningEt.getText().toString().trim().equals("")
                        || MeshConverterActivity.this.angleEt.getText().toString().trim().equals("")) {
                    String unused = MeshConverterActivity.this.meshOpeningEtStr = charSequence.toString().trim();
                    MeshConverterActivity.this.resultTv.setText("");
                    return;
                }
                String unused2 = MeshConverterActivity.this.meshOpeningEtStr = charSequence.toString().trim();
                MeshConverterActivity.this.calculateProjectedOpening();
            }
        });
        this.angleEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (MeshConverterActivity.this.meshOpeningEt.getText().toString().trim().equals("") || MeshConverterActivity.this.angleEt.getText().toString().trim().equals("")) {
                    String unused = MeshConverterActivity.this.angleEtStr = charSequence.toString().trim();
                    MeshConverterActivity.this.resultTv.setText("");
                    return;
                }
                String unused2 = MeshConverterActivity.this.angleEtStr = charSequence.toString().trim();
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
        textView.setText(Html.fromHtml("Angle range is between 0&deg; to 45&deg;."));
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
    public void calculateProjectedOpening() {
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

    private void calculateAngle() {
        SensorManager sensorManager2 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.sensorManager = sensorManager2;
        this.sensor = sensorManager2.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener r0 = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int i) {
            }

//            public void onSensorChanged(SensorEvent sensorEvent) {
//                if (sensorEvent.sensor.getType() == 1) {
//                    float f = sensorEvent.values[0] / 9.8f;
//                    float f2 = sensorEvent.values[1] / 9.8f;
//                    float f3 = sensorEvent.values[2] / 9.8f;
//                    if (f == 0.0f) {
//                        f = 0.01f;
//                    }
//                    if (f2 == 0.0f) {
//
//                        f2 = 0.01f;
//                    }
//                    if (f3 == 0.0f) {
//                        f3 = 0.01f;
//                    }
//                    int atan = (int) ((Math.atan((double) (f2 / f)) * 180.0d) / 3.141592653589793d);
//                    int abs1 = Math.abs(atan);
//
//                    if (Math.abs(f3) >= 0.9f) {
//                        angleEt.setText("");
//                    } else if ((f2 < 0.0f && atan < 0) || (f < 0.0f && f2 < 0.0f)) {
//                        showAlert();
//                    } else if (abs1 > 45) {
//                        showAlert();
//                    } else {
//                        customAlertDialog.setVisibility(View.GONE);
//                        isAlertShown = false;
////                     angleEt.setText(Integer.toString(abs1));
//                        angleEt.setText(String.valueOf(abs1));
////                     angleEt.setText(abs1);
////                        angleEt.setText(String.format(Locale.getDefault(), "%d", abs1));
//                    }
//                }
//            }

            //new
//            public void onSensorChanged(SensorEvent sensorEvent) {
//                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                    float f = sensorEvent.values[0] / 9.8f; // X-axis
//                    float f2 = sensorEvent.values[1] / 9.8f; // Y-axis
//                    float f3 = sensorEvent.values[2] / 9.8f; // Z-axis
//
//                    if (f2 == 0.0f) {
//                        f2 = 0.01f;
//                    }
//                    if (f3 == 0.0f) {
//                        f3 = 0.01f;
//                    }
//
//                    // Calculate the angle between the Y and Z axes
//                    int angle = (int) ((Math.atan((double) (f/ f3)) * 180.0d) / Math.PI);
//                    int absAngle = Math.abs(angle);
//
//                    if (Math.abs(f2) >= 0.9f) { // Device held flat (X-axis mostly affected)
//                        angleEt.setText(""); // Clear angle display
//                    }
////                    else if ((f3 < 0.0f && angle < 0) || (f2 < 0.0f && f3 < 0.0f)) {
////                        showAlert(); // Handle specific case if needed
////                    }
//
//                    else if (absAngle > 45) { // Arbitrary threshold, can be adjusted
//                        showAlert(); // Show alert if angle is steep
//                    } else {
//                        customAlertDialog.setVisibility(View.GONE);
//                        isAlertShown = false;
//                        angleEt.setText(String.valueOf(absAngle)); // Display the angle
//                    }
//                }
//            }
            //new close

            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float f = sensorEvent.values[0]; // X-axis
                    float f2 = sensorEvent.values[1]; // Y-axis
                    float f3 = sensorEvent.values[2]; // Z-axis

                    // Normalize the accelerometer values to the range -1 to 1
                    float normalizedF = f / 9.8f;
                    float normalizedF2 = f2 / 9.8f;
                    float normalizedF3 = f3 / 9.8f;

                    // Avoid zero values to prevent division by zero
                    if (normalizedF2 == 0.0f) normalizedF2 = 0.01f;

                    // Determine if the device is in a vertical position against a wall
                    boolean isVertical = Math.abs(normalizedF2) > 0.7 && Math.abs(normalizedF3) < 0.3;

                    if (isVertical) {
                        // Calculate the angle between the X and Y axes (rotation around Z-axis)
                        int angle = (int) ((Math.atan2(normalizedF, normalizedF2) * 180.0d) / Math.PI);
                        int absAngle = Math.abs(angle);

                        angleEt.setText(String.valueOf(absAngle));
                        if (absAngle > 46) {
                            showAlert(); // Show alert if the angle exceeds 45 degrees
                        } else if (absAngle <= 45) {
                            customAlertDialog.setVisibility(View.GONE);
                            isAlertShown = false;
                            angleEt.setText(String.valueOf(absAngle)); // Display the angle
                        } else {
                            angleEt.setText("");
                            showAlert(); // Show alert if the angle exceeds 45 degrees
                        }
                    }

                    else {
                        // Clear the angle display when the device is not vertical
                        angleEt.setText("");
                        customAlertDialog.setVisibility(View.GONE);
                        isAlertShown = false;
                    }
                }
            }



        };
        this.sensorEventListener = r0;
        this.sensorManager.registerListener(r0, this.sensor, 3);
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
        numberPicker.setFocusable(true);
        numberPicker.setFocusableInTouchMode(true);
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