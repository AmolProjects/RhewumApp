package com.rhewumapp.Activity.interfaces;

import com.rhewumapp.Activity.database.MeasurementDao;

import java.util.ArrayList;

public interface DeleteListner {
    void onDelete(ArrayList<MeasurementDao> arrayList, boolean z, boolean z2);
}
