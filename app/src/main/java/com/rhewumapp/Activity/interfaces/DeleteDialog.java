package com.rhewumapp.Activity.interfaces;

import com.rhewumapp.Activity.database.MeasurementDao;

import java.util.ArrayList;

public interface DeleteDialog {
    void onDeleteYesNo(ArrayList<MeasurementDao> arrayList, boolean z);
}
