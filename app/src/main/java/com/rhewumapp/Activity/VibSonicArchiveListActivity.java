package com.rhewumapp.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.rhewumapp.Activity.Adapter.VibSonicArchiveListAdapter;
import com.rhewumapp.Activity.MeshConveterData.ResponsiveAndroidBars;
import com.rhewumapp.Activity.MeshConveterData.Utils;
import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.Activity.interfaces.DeleteDialog;
import com.rhewumapp.Activity.interfaces.DeleteListner;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;

import java.util.ArrayList;

public class VibSonicArchiveListActivity extends DrawerBaseActivity implements View.OnClickListener, DeleteListner, DeleteDialog {
    private RelativeLayout backLayout;
    private LinearLayout bottomLayout;
    private RhewumDbHelper dbHelper;
    private TextView delete;
    private RelativeLayout edit;
    private boolean isChecked = false;
    private boolean isEditClicked = false;
    private boolean isSelectAll = false;
    private ListView listView;
    private VibSonicArchiveListAdapter mAdapter;
    private ArrayList<MeasurementDao> measureListNew = new ArrayList<>();
    private ArrayList<MeasurementDao> measurementList = new ArrayList<>();
    private TextView selectAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vib_sonic_archive_list);

        ResponsiveAndroidBars.setNotificationBarColor(this, getResources().getColor(R.color.header_backgrounds), false);
        ResponsiveAndroidBars.setNavigationBarColor(this, getResources().getColor(R.color.grey_background), false, false);



        setUpViews();
        getHelper();
        this.backLayout.setOnClickListener(this);
        this.selectAll.setOnClickListener(this);
        this.delete.setOnClickListener(this);
        this.edit.setOnClickListener(this);

    }
    private void setUpViews() {
        this.backLayout = (RelativeLayout) findViewById(R.id.activity_vib_sonic_archive_list_back_layout);
        this.edit = (RelativeLayout) findViewById(R.id.activity_vib_sonic_archive_list_edit_layout);
        this.listView = (ListView) findViewById(R.id.activity_vib_sonic_archive_list_listView);
        this.bottomLayout = (LinearLayout) findViewById(R.id.activity_vib_sonic_archive_list_bottom_layout);
        this.selectAll = (TextView) findViewById(R.id.activity_vib_sonic_archive_list_select_tv);
        this.delete = (TextView) findViewById(R.id.activity_vib_sonic_archive_list_delete_tv);
    }

    public void onClick(View view) {
        if (view.equals(this.backLayout)) {
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        } else if (view.equals(this.edit)) {
            if (this.measurementList.size() <= 0) {
                Log.e("Summery List","ccccc"+measurementList.size());
                return;
            }
            if (!this.isEditClicked) {
                this.bottomLayout.setVisibility(View.VISIBLE);
                this.isEditClicked = true;
                this.mAdapter.isEditClicked = true;
                this.mAdapter.notifyDataSetChanged();
                return;
            }
            this.bottomLayout.setVisibility(View.GONE);
            this.isEditClicked = false;
            this.mAdapter.isEditClicked = false;
            this.mAdapter.isSelectAll = false;
            this.isSelectAll = false;
            this.mAdapter.notifyDataSetChanged();

        } else if (view.equals(this.selectAll)) {
            if (!this.isSelectAll) {
                this.measureListNew.clear();
                this.mAdapter.isSelectAll = true;
                this.isSelectAll = true;
                this.isChecked = true;
            } else {
                this.mAdapter.isSelectAll = false;
                this.isSelectAll = false;
                this.isChecked = false;
            }
            this.mAdapter.notifyDataSetChanged();
        } else if (!view.equals(this.delete)) {
        } else {
            if (this.isSelectAll) {
                Utils.showLog("Select All True");
                if (!this.isChecked) {
                    Utils.showAlert(this, getResources().getString(R.string.select_measurement));
                } else {
                    deleteList(this.measurementList);
                }
            } else {
                Utils.showLog("Select All False");
                if (this.measureListNew.isEmpty()) {
                    Utils.showAlert(this, getResources().getString(R.string.select_measurement));
                } else {
                    deleteList(this.measureListNew);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.measurementList = this.dbHelper.getMeasurementList();
        VibSonicArchiveListAdapter vibSonicArchiveListAdapter = new VibSonicArchiveListAdapter(this, this, this.measurementList);
        this.mAdapter = vibSonicArchiveListAdapter;
        this.listView.setAdapter(vibSonicArchiveListAdapter);
        this.bottomLayout.setVisibility(8);
        this.isEditClicked = false;
        this.mAdapter.isEditClicked = false;
        this.mAdapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private RhewumDbHelper getHelper() {
        if (this.dbHelper == null) {
            this.dbHelper = (RhewumDbHelper) OpenHelperManager.getHelper(this, RhewumDbHelper.class);
        }
        return this.dbHelper;
    }

    public void onDelete(ArrayList<MeasurementDao> arrayList, boolean z, boolean z2) {
        Utils.showLog("Delete List Size: " + arrayList.size());
        this.measureListNew = arrayList;
        this.isChecked = z;
        if (!z2) {
            this.isSelectAll = false;
        }
    }

    private void deleteList(ArrayList<MeasurementDao> arrayList) {
        Utils.showAlertToDelete(this, this, arrayList);
    }

    public void onDeleteYesNo(ArrayList<MeasurementDao> arrayList, boolean z) {
        if (z) {
            this.dbHelper.deleteMeasurementList(arrayList);
            this.measurementList.clear();
            this.measureListNew.clear();
            this.measurementList = this.dbHelper.getMeasurementList();
            VibSonicArchiveListAdapter vibSonicArchiveListAdapter = new VibSonicArchiveListAdapter(this, this, this.measurementList);
            this.mAdapter = vibSonicArchiveListAdapter;
            this.listView.setAdapter(vibSonicArchiveListAdapter);
            this.bottomLayout.setVisibility(View.GONE);
            this.isEditClicked = false;
            this.mAdapter.isEditClicked = false;
            this.isChecked = false;
            this.isSelectAll = false;
            this.mAdapter.notifyDataSetChanged();
        }
    }
}