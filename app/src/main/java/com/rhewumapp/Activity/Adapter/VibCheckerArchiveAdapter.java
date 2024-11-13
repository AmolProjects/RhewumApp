package com.rhewumapp.Activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itextpdf.text.pdf.security.SecurityConstants;
import com.rhewumapp.Activity.VibChekerArchiveActivity;
import com.rhewumapp.Activity.VibSonicArchiveActivity;
import com.rhewumapp.Activity.VibSonicArchiveListActivity;
import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.database.VibCheckerSummaryDao;
import com.rhewumapp.Activity.interfaces.DeleteListner;
import com.rhewumapp.Activity.interfaces.VibCheckerDeleteListner;
import com.rhewumapp.R;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        this.mListner = vibChekerArchiveActivity;
        arrayList2.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy, hh:mm:ss a", Locale.US);
        dateValue =dateFormat.format(calendar.getTime());

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
        TextView mean_level_tv;
        TextView measurement_tv;
        RelativeLayout rl;

        viewHolder() {
        }
    }


    @SuppressLint("SetTextI18n")
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final viewHolder viewholder;
        if (view == null) {
            view = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.vibcheckerarchiveadapter, (ViewGroup) null);
            viewholder =new viewHolder();
            viewholder.cb = (CheckBox) view.findViewById(R.id.row_archive_cb);
            viewholder.date_tv = (TextView) view.findViewById(R.id.row_archive_date_tv);
            viewholder.measurement_tv = (TextView) view.findViewById(R.id.row_archive_m_time_tv);
            viewholder.mean_level_tv = (TextView) view.findViewById(R.id.row_archive_mean_level_total_tv);
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
        viewholder.date_tv.setText(dateValue);
        TextView textView = viewholder.measurement_tv;
        textView.setText(this.mContext.getResources().getString(R.string.measurement_time) + StringUtils.SPACE + this.SummeryList.get(i).measurementTotalTime);
        TextView textView2 = viewholder.mean_level_tv;
        textView2.setText(this.mContext.getResources().getString(R.string.peak_acceleration) + StringUtils.SPACE + replace2);
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
                        VibCheckerArchiveAdapter.this.SummeryListNew.add((VibCheckerSummaryDao) VibCheckerArchiveAdapter.this.SummeryList.get(i));
                        VibCheckerArchiveAdapter.this.mListner.onDelete(VibCheckerArchiveAdapter.this.SummeryListNew, true, false);
                        return;
                    }
                    viewholder.cb.setChecked(false);
                    boolean unused2 = VibCheckerArchiveAdapter.this.checkSelected = false;
                    VibCheckerArchiveAdapter.this.SummeryListNew.remove(VibCheckerArchiveAdapter.this.SummeryList.get(i));
                    VibCheckerArchiveAdapter.this.mListner.onDelete(VibCheckerArchiveAdapter.this.SummeryListNew, true, false);
                }
            });
        } else {
            viewholder.rl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(VibCheckerArchiveAdapter.this.mContext, VibSonicArchiveActivity.class);
                    intent.putExtra(SecurityConstants.Id, ((VibCheckerSummaryDao) VibCheckerArchiveAdapter.this.SummeryList.get(i)).id);
                    VibCheckerArchiveAdapter.this.mContext.startActivity(intent);
                    VibCheckerArchiveAdapter.this.mContext.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }
        return view;
    }
}
