package com.rhewum.Activity.interfaces;

import com.rhewum.Activity.database.MeasurementDao;

import java.util.ArrayList;

public interface DeleteListner {
    void onDelete(ArrayList<MeasurementDao> arrayList, boolean z, boolean z2);
}
