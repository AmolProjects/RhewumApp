package com.rhewum.Activity.interfaces;



import com.rhewum.Activity.database.MeasurementDao;

import java.util.ArrayList;

public interface DeleteDialog {
    void onDeleteYesNo(ArrayList<MeasurementDao> arrayList, boolean z);
}
