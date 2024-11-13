package com.rhewumapp.Activity.interfaces;

import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.database.VibCheckerSummaryDao;

import java.util.ArrayList;

public interface VibCheckerDeleteListner {
    void onDelete(ArrayList<VibCheckerSummaryDao> arrayList, boolean z, boolean z2);

}
