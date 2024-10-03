package com.rhewumapp.Activity.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "accelerometer_data")
public class VibCheckerSummaryDao implements Serializable {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public float xAxis;
    @DatabaseField
    public float yAxis;
    @DatabaseField
    public float zAxis;
    @DatabaseField
    public float dominantFrequencyX;
    @DatabaseField
    public float dominantFrequencyY;
    @DatabaseField
    public float dominantFrequencyZ;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public MeasurementDao measurement;
}
