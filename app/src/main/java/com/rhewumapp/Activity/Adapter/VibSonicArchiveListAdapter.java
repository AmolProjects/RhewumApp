package com.rhewumapp.Activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.itextpdf.text.pdf.security.SecurityConstants;
import com.rhewumapp.Activity.VibSonicArchiveActivity;
import com.rhewumapp.Activity.VibSonicArchiveListActivity;
import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.interfaces.DeleteListner;
import com.rhewumapp.R;

import org.apache.commons.lang3.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class VibSonicArchiveListAdapter extends BaseAdapter {
    /* access modifiers changed from: private */
    public boolean checkSelected = false;
    public boolean isEditClicked = false;
    public boolean isSelectAll = false;
    /* access modifiers changed from: private */
    public VibSonicArchiveListActivity mContext;
    /* access modifiers changed from: private */
    public DeleteListner mListner;
    /* access modifiers changed from: private */
    public ArrayList<MeasurementDao> measureListNew;
    /* access modifiers changed from: private */
    public ArrayList<MeasurementDao> measurementList;

    public long getItemId(int i) {
        return (long) i;
    }

    public VibSonicArchiveListAdapter(VibSonicArchiveListActivity vibSonicArchiveListActivity, DeleteListner deleteListner, ArrayList<MeasurementDao> arrayList) {
        ArrayList<MeasurementDao> arrayList2 = new ArrayList<>();
        this.measureListNew = arrayList2;
        this.mContext = vibSonicArchiveListActivity;
        this.measurementList = arrayList;
        this.mListner = deleteListner;
        arrayList2.clear();
    }

    public int getCount() {
        return this.measurementList.size();
    }

    public Object getItem(int i) {
        return this.measurementList.get(i);
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
            view = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_archive_list, (ViewGroup) null);
            viewholder = new viewHolder();
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
            this.measureListNew.remove(this.measurementList.get(i));
            this.mListner.onDelete(this.measureListNew, true, this.isSelectAll);
        } else {
            viewholder.cb.setChecked(true);
            this.measureListNew.add(this.measurementList.get(i));
            this.mListner.onDelete(this.measureListNew, true, this.isSelectAll);
        }
        String replace = new SimpleDateFormat("MMM dd yyyy, hh:mm:ss aa").format(this.measurementList.get(i).measurementDate).replace("AM", "am").replace("PM", "pm");
        String replace2 = this.measurementList.get(i).meanLevelTotal.replace("dB(A)", "");
        viewholder.date_tv.setText(replace);
        TextView textView = viewholder.measurement_tv;
        textView.setText(this.mContext.getResources().getString(R.string.measurement_time) + StringUtils.SPACE + this.measurementList.get(i).measurementTotalTime);
        TextView textView2 = viewholder.mean_level_tv;
        textView2.setText(this.mContext.getResources().getString(R.string.mean_level_total) + StringUtils.SPACE + replace2);
        viewholder.cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (viewholder.cb.isChecked()) {
                    VibSonicArchiveListAdapter.this.measureListNew.add((MeasurementDao) VibSonicArchiveListAdapter.this.measurementList.get(i));
                    VibSonicArchiveListAdapter.this.mListner.onDelete(VibSonicArchiveListAdapter.this.measureListNew, true, false);
                    boolean unused = VibSonicArchiveListAdapter.this.checkSelected = true;
                    return;
                }
                VibSonicArchiveListAdapter.this.measureListNew.remove(VibSonicArchiveListAdapter.this.measurementList.get(i));
                VibSonicArchiveListAdapter.this.mListner.onDelete(VibSonicArchiveListAdapter.this.measureListNew, true, false);
                boolean unused2 = VibSonicArchiveListAdapter.this.checkSelected = false;
            }
        });
        if (this.isEditClicked) {
            viewholder.rl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!VibSonicArchiveListAdapter.this.checkSelected) {
                        viewholder.cb.setChecked(true);
                        boolean unused = VibSonicArchiveListAdapter.this.checkSelected = true;
                        VibSonicArchiveListAdapter.this.measureListNew.add((MeasurementDao) VibSonicArchiveListAdapter.this.measurementList.get(i));
                        VibSonicArchiveListAdapter.this.mListner.onDelete(VibSonicArchiveListAdapter.this.measureListNew, true, false);
                        return;
                    }
                    viewholder.cb.setChecked(false);
                    boolean unused2 = VibSonicArchiveListAdapter.this.checkSelected = false;
                    VibSonicArchiveListAdapter.this.measureListNew.remove(VibSonicArchiveListAdapter.this.measurementList.get(i));
                    VibSonicArchiveListAdapter.this.mListner.onDelete(VibSonicArchiveListAdapter.this.measureListNew, true, false);
                }
            });
        } else {
            viewholder.rl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(VibSonicArchiveListAdapter.this.mContext, VibSonicArchiveActivity.class);
                    intent.putExtra("JumpFrom", "ArchieveList");
                    intent.putExtra(SecurityConstants.Id, ((MeasurementDao) VibSonicArchiveListAdapter.this.measurementList.get(i)).measurementId);
                    VibSonicArchiveListAdapter.this.mContext.startActivity(intent);
                    VibSonicArchiveListAdapter.this.mContext.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }
        return view;
    }
}
