package com.rhewumapp.Activity.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

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

    @DatabaseField
    public float peakAmplitudeX;
    @DatabaseField
    public float peakAmplitudeY;
    @DatabaseField
    public float peakAmplitudeZ;

    @DatabaseField(columnName = "sensor_data", dataType = DataType.BYTE_ARRAY)
    public byte[] sensorData;

//    @DatabaseField
//    public float xRawValue;
//
//    @DatabaseField
//    public float yRawValue;
//
//    @DatabaseField
//    public float zRawValue;



    @DatabaseField(columnName = "measurement_total_time")
    public int measurementTotalTime;

    @DatabaseField(columnName = "measurement_date")
    public Date measurementDate;

    @DatabaseField(columnName = "graph_image")
    public String graphImage;

    @DatabaseField
    public int  delay;

}
