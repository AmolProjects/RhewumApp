package com.rhewumapp.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.rhewumapp.Activity.Adapter.VibCheckerArchiveAdapter;
import com.rhewumapp.Activity.Adapter.VibSonicArchiveListAdapter;
import com.rhewumapp.Activity.MeshConveterData.ResponsiveAndroidBars;
import com.rhewumapp.Activity.MeshConveterData.Utils;
import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.Activity.database.VibCheckerSummaryDao;
import com.rhewumapp.Activity.interfaces.DeleteListner;
import com.rhewumapp.Activity.interfaces.VibCheckerDeleteListner;
import com.rhewumapp.Activity.interfaces.VibChekerDeleteDialog;
import com.rhewumapp.R;

import java.util.ArrayList;

public class VibChekerArchiveActivity extends AppCompatActivity implements View.OnClickListener,VibChekerDeleteDialog,VibCheckerDeleteListner{
    private ImageView backLayout;
    private LinearLayout bottomLayout;
    private RhewumDbHelper dbHelper;
    private TextView delete,back;
    private TextView edits;
    private boolean isChecked = false;
    private boolean isEditClicked = false;
    private boolean isSelectAll = false;
    private ListView listView;
    private TextView selectAll;
    private VibCheckerArchiveAdapter mAdapter;
    private ArrayList<VibCheckerSummaryDao> SummeryListNew = new ArrayList<>();
    private ArrayList<VibCheckerSummaryDao> SummeryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vib_cheker_archive);
        ResponsiveAndroidBars.setNotificationBarColor(this, getResources().getColor(R.color.header_backgrounds), false);
        ResponsiveAndroidBars.setNavigationBarColor(this, getResources().getColor(R.color.grey_background), false, false);
        setUpViews();
        getHelper();
        this.backLayout.setOnClickListener(this);
        this.back.setOnClickListener(this);
        this.selectAll.setOnClickListener(this);
        this.delete.setOnClickListener(this);
        this.edits.setOnClickListener(this);

    }
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
    private void setUpViews() {
        this.backLayout = (ImageView) findViewById(R.id.activity_vib_sonic_archive_list_back_iv);
        this.back=(TextView)findViewById(R.id.activity_vib_sonic_archive_list_back_tv);
        this.edits = (TextView) findViewById(R.id.editss);
        this.listView = (ListView) findViewById(R.id.activity_vib_sonic_archive_list_listView);
        this.bottomLayout = (LinearLayout) findViewById(R.id.activity_vib_sonic_archive_list_bottom_layout);
        this.selectAll = (TextView) findViewById(R.id.activity_vib_sonic_archive_list_select_tv);
        this.delete = (TextView) findViewById(R.id.activity_vib_sonic_archive_list_delete_tv);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        SummeryList = this.dbHelper.getVibCheckerAcc();
        VibCheckerArchiveAdapter vibCheckerArchiveAdapter=new VibCheckerArchiveAdapter(VibChekerArchiveActivity.this,this,SummeryList);
        this.mAdapter = vibCheckerArchiveAdapter;
        this.listView.setAdapter(vibCheckerArchiveAdapter);
       // this.bottomLayout.setVisibility(8);
        this.isEditClicked = false;
        this.mAdapter.isEditClicked = false;
        this.mAdapter.notifyDataSetChanged();
    }
    public void onClick(View view) {
        if (view.equals(this.backLayout)) {
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        } if (view.equals(this.back)) {
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
        else if (view.equals(this.edits)) {
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();

            if (this.SummeryList.size() <= 0) {
                Log.e("Summery List","ccccc"+SummeryList.size());
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
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        } else if (view.equals(this.selectAll)) {
            if (!this.isSelectAll) {
                this.SummeryListNew.clear();
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
                    deleteList(this.SummeryList);
                }
            } else {
                Utils.showLog("Select All False");
                if (this.SummeryListNew.isEmpty()) {
                    Utils.showAlert(this, getResources().getString(R.string.select_measurement));
                } else {
                    deleteList(this.SummeryListNew);
                }
            }
        }
    }
    private RhewumDbHelper getHelper() {
        if (this.dbHelper == null) {
            this.dbHelper = (RhewumDbHelper) OpenHelperManager.getHelper(this, RhewumDbHelper.class);
        }
        return this.dbHelper;
    }
    public void onDelete(ArrayList<VibCheckerSummaryDao> arrayList, boolean z, boolean z2) {
        Utils.showLog("Delete List Size: " + arrayList.size());
        this.SummeryList = arrayList;
        this.isChecked = z;
        if (!z2) {
            this.isSelectAll = false;
        }
    }
    private void deleteList(ArrayList<VibCheckerSummaryDao> arrayList) {
        Utils.showAlertToDeleteSummary((Context) this,this, arrayList);
    }

    public void onDeleteYesNo(ArrayList<VibCheckerSummaryDao> arrayList, boolean z) {
        if (z) {
            this.dbHelper.deleteMeasurementListSummery(arrayList);
            this.SummeryList.clear();
            this.SummeryListNew.clear();
            this.SummeryList = this.dbHelper.getVibCheckerAcc();
            VibCheckerArchiveAdapter vibCheckerArchiveAdapter = new VibCheckerArchiveAdapter(this, this, this.SummeryList);
            this.mAdapter = vibCheckerArchiveAdapter;
            this.listView.setAdapter(vibCheckerArchiveAdapter);
            this.bottomLayout.setVisibility(View.GONE);
            this.isEditClicked = false;
            this.mAdapter.isEditClicked = false;
            this.isChecked = false;
            this.isSelectAll = false;
            this.mAdapter.notifyDataSetChanged();
        }
    }

}