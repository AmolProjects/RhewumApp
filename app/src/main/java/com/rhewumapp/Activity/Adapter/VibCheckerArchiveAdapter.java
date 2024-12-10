package com.rhewumapp.Activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.itextpdf.text.pdf.security.SecurityConstants;
import com.rhewumapp.Activity.Fragments.SummeryFragment;
import com.rhewumapp.Activity.VibCheckerMainActivity;
import com.rhewumapp.Activity.VibChecker_ListActivity;
import com.rhewumapp.Activity.VibChekerArchiveActivity;


import com.rhewumapp.Activity.database.VibCheckerSummaryDao;
import com.rhewumapp.Activity.interfaces.DeleteListner;
import com.rhewumapp.Activity.interfaces.VibCheckerDeleteListner;
import com.rhewumapp.R;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class VibCheckerArchiveAdapter extends BaseAdapter {
    /* access modifiers changed from: private */
    public boolean checkSelected = false;
    public boolean isEditClicked = false;
    public boolean isSelectAll = false;
    /* access modifiers changed from: private */
    public VibChekerArchiveActivity mContext;
    /* access modifiers changed from: private */
    public VibCheckerDeleteListner mListner;
    /* access modifiers changed from: private */
    private ArrayList<VibCheckerSummaryDao> SummeryListNew;
    private ArrayList<VibCheckerSummaryDao> SummeryList;
    Calendar calendar = Calendar.getInstance();
    String dateValue;

    public VibCheckerArchiveAdapter(VibChekerArchiveActivity vibChekerArchiveActivity, VibCheckerDeleteListner vibCheckerDeleteListner, ArrayList<VibCheckerSummaryDao> summeryList) {
        ArrayList<VibCheckerSummaryDao> arrayList2 = new ArrayList<>();
        this.SummeryListNew = arrayList2;
        this.mContext = vibChekerArchiveActivity;
        this.SummeryList = summeryList;
        this.mListner = vibCheckerDeleteListner;
        arrayList2.clear();

    }

    public int getCount() {
        return this.SummeryList.size();
    }

    public Object getItem(int i) {
        return this.SummeryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    static class viewHolder {
        CheckBox cb;
        TextView date_tv;
        TextView mean_level_tv,yAcclereation_tv,delay;
        TextView measurement_tv;
        RelativeLayout rl;

        viewHolder() {
        }
    }

    //added new 29NOv
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final viewHolder viewholder;
        if (view == null) {
            view = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.vibcheckerarchiveadapter, (ViewGroup) null);
            viewholder =new viewHolder();
            viewholder.cb = (CheckBox) view.findViewById(R.id.row_archive_cb);
            viewholder.date_tv = (TextView) view.findViewById(R.id.row_archive_date_tv);
            viewholder.measurement_tv = (TextView) view.findViewById(R.id.row_archive_m_time_tv);
            viewholder.mean_level_tv = (TextView) view.findViewById(R.id.row_archive_mean_level_total_tv);
            viewholder.delay = (TextView) view.findViewById(R.id.delay);

            viewholder.rl = (RelativeLayout) view.findViewById(R.id.row_archive_list_layoutMain);
            view.setTag(viewholder);
        } else {
            viewholder = (viewHolder) view.getTag();
        }
        if (!this.isEditClicked) {
            viewholder.cb.setVisibility(View.INVISIBLE);
        } else {
            viewholder.cb.setVisibility(View.VISIBLE);
        }
        if (!this.isSelectAll) {
            viewholder.cb.setChecked(false);
            this.SummeryListNew.remove(this.SummeryList.get(i));
            this.mListner.onDelete(this.SummeryListNew, true, this.isSelectAll);
        } else {
            viewholder.cb.setChecked(true);
            this.SummeryListNew.add(this.SummeryList.get(i));
            this.mListner.onDelete(this.SummeryListNew, true, this.isSelectAll);
        }
        // String replace = new SimpleDateFormat("MMM dd yyyy, hh:mm:ss aa").format(this.measurementList.get(i).measurementDate).replace("AM", "am").replace("PM", "pm");
        float replace2 = this.SummeryList.get(i).xAxis;
        float valueYaxis = this.SummeryList.get(i).yAxis;
        float valueZaxis = this.SummeryList.get(i).zAxis ;
        float dominantFreqX = this.SummeryList.get(i).dominantFrequencyX;
        float dominantFreqY = this.SummeryList.get(i).dominantFrequencyY;
        float dominantFreqZ = this.SummeryList.get(i).dominantFrequencyZ;
        float amplitudeX = this.SummeryList.get(i).peakAmplitudeX;
        float amplitudeY = this.SummeryList.get(i).peakAmplitudeY;
        float amplitudeZ = this.SummeryList.get(i).peakAmplitudeZ;
        int delayTime = this.SummeryList.get(i).delay;
//        String newDate = this.SummeryList.get(i).measurementDate;

        String formattedValue = String.format(Locale.US, "%.1f", replace2).replace('.', ',');
        String formattedYValue = String.format(Locale.US, "%.1f", valueYaxis).replace('.', ',');
        String formattedZValue = String.format(Locale.US, "%.1f", valueZaxis).replace('.', ',');

        //added new
        if (this.SummeryList.get(i).measurementDate != null) {
            // Create a new local variable for the formatted date
            String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                    .format(this.SummeryList.get(i).measurementDate);
            viewholder.date_tv.setText(formattedDate);
            Log.d("DateVAlue", "ViewHolderDate: " + formattedDate);

            // Pass the formatted date to the Intent
            viewholder.rl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(VibCheckerArchiveAdapter.this.mContext, VibChecker_ListActivity.class);
                    // Pass other data as extras
                    intent.putExtra("replace2", replace2);
                    intent.putExtra("valueYaxis", valueYaxis);
                    intent.putExtra("valueZaxis", valueZaxis);
                    intent.putExtra("dominantFreqX", dominantFreqX);
                    intent.putExtra("dominantFreqY", dominantFreqY);
                    intent.putExtra("dominantFreqZ", dominantFreqZ);
                    intent.putExtra("amplitudeX", amplitudeX);
                    intent.putExtra("amplitudeY", amplitudeY);
                    intent.putExtra("amplitudeZ", amplitudeZ);
                    intent.putExtra("id",i+1);
                    // Deserialization of buffer
                    byte[] serializedBuffer = SummeryList.get(i).sensorData;
                    float[] buffer = new float[0]; // Default fallback buffer

                    //
                    if (serializedBuffer != null) {
                        Log.d("BufferCheck", "Starting deserialization for item at index " + i);
                        buffer = deserializeBuffer(serializedBuffer);
                        if (buffer.length > 0) {
                            Log.d("BufferCheck", "Buffer deserialized successfully. Size: " + buffer.length);
                        } else {
                            Log.e("BufferError", "Deserialized buffer is empty or null for item at index " + i);
                        }
                    } else {
                        Log.d("BufferError", "sensorData is null for item at index " + i);
                    }

                    // Log the buffer size before passing to Intent
                    Log.d("BufferCheck", "Buffer before passing to Intent, size: " + buffer.length);

                    //

                    //new method for graph 29
                    Log.d("BufferCheck", "Buffer before passing to Intent, size: " + buffer.length);
                    intent.putExtra("sensor_data", buffer);

                    //added for graph
//                    intent.putExtra("summaryId", VibCheckerArchiveAdapter.this.SummeryList.get(i).id );
//                    Log.d("DB_INSERT", "VA:Passing summaryId: " + SummeryList.get(i).id);


                    // Pass the local formattedDate
                    intent.putExtra("dateValue", formattedDate);
                    Log.d("DateVAlue", "PutIntent: " + formattedDate);

                    // Existing ID extra
                    intent.putExtra(SecurityConstants.Id, VibCheckerArchiveAdapter.this.SummeryList.get(i).id);
                    VibCheckerArchiveAdapter.this.mContext.startActivity(intent);
                    VibCheckerArchiveAdapter.this.mContext.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        } else {
            viewholder.date_tv.setText("N/A"); // Fallback for null dates
        }

        TextView textView = viewholder.delay;
        TextView measurement = viewholder.measurement_tv;

        textView.setText("Measurement Time: " + StringUtils.SPACE + this.SummeryList.get(i).measurementTotalTime + " sec");

        measurement.setText("Delay: " +delayTime + " sec");
        TextView textView2 = viewholder.mean_level_tv;
        TextView yAxisTv = viewholder.yAcclereation_tv;

        textView2.setText("Peak Acceleration - X: " +formattedValue + " "  +" "+ "Y: "+formattedYValue + " "+" "+"Z: " +formattedZValue);


        viewholder.cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (viewholder.cb.isChecked()) {
                    VibCheckerArchiveAdapter.this.SummeryListNew.add((VibCheckerSummaryDao) VibCheckerArchiveAdapter.this.SummeryList.get(i));
                    VibCheckerArchiveAdapter.this.mListner.onDelete(VibCheckerArchiveAdapter.this.SummeryListNew, true, false);
                    boolean unused = VibCheckerArchiveAdapter.this.checkSelected = true;
                    return;
                }
                VibCheckerArchiveAdapter.this.SummeryListNew.remove(VibCheckerArchiveAdapter.this.SummeryList.get(i));
                VibCheckerArchiveAdapter.this.mListner.onDelete(VibCheckerArchiveAdapter.this.SummeryListNew, true, false);
                boolean unused2 = VibCheckerArchiveAdapter.this.checkSelected = false;
            }
        });
        if (this.isEditClicked) {
            viewholder.rl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!VibCheckerArchiveAdapter.this.checkSelected) {
                        viewholder.cb.setChecked(true);
                        boolean unused = VibCheckerArchiveAdapter.this.checkSelected = true;
                        VibCheckerArchiveAdapter.this.SummeryList.add((VibCheckerSummaryDao) VibCheckerArchiveAdapter.this.SummeryList.get(i));
                        VibCheckerArchiveAdapter.this.mListner.onDelete(VibCheckerArchiveAdapter.this.SummeryListNew, true, false);
                        return;
                    }
                    viewholder.cb.setChecked(false);
                    boolean unused2 = VibCheckerArchiveAdapter.this.checkSelected = false;
                    VibCheckerArchiveAdapter.this.SummeryListNew.remove(VibCheckerArchiveAdapter.this.SummeryList.get(i));
                    VibCheckerArchiveAdapter.this.mListner.onDelete(VibCheckerArchiveAdapter.this.SummeryListNew, true, false);
                }
            });
        }

        else {
        }
        return view;
    }

    //

    private void notifyListener() {
        mListner.onDelete(SummeryListNew, !SummeryListNew.isEmpty(), isSelectAll);
    }


    //added new 29NOv
    //added buffer
    private float[] deserializeBuffer(byte[] serializedBuffer) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(serializedBuffer);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            // Log to check if deserialization is starting
            Log.d("Deserialize", "Starting deserialization. Buffer size: " + serializedBuffer.length);

            float[] buffer = (float[]) ois.readObject();

            // Log the deserialized buffer
            Log.d("Deserialize", "Deserialization successful. Buffer contents: " + Arrays.toString(buffer));

            return buffer;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("DeserializeError", "Error during deserialization: " + e.getMessage());
            return null;
        }
    }

}
