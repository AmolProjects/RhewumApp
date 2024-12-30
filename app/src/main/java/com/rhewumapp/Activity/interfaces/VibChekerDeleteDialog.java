package com.rhewumapp.Activity.interfaces;

import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.database.VibCheckerSummaryDao;

import java.util.ArrayList;

public interface VibChekerDeleteDialog {
    void onDeleteYesNo(ArrayList<VibCheckerSummaryDao> arrayList, boolean z);

}
