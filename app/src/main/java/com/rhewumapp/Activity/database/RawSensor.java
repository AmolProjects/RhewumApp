package com.rhewumapp.Activity.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.ArrayList;


@DatabaseTable(tableName = "sensor_data")
public class RawSensor implements Serializable {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "xRawVal", dataType = DataType.SERIALIZABLE)
    public ArrayList<Float> xRawVal;

    @DatabaseField(columnName = "yRawVal", dataType = DataType.SERIALIZABLE)
    public ArrayList<Float> yRawVal;

    @DatabaseField(columnName = "zRawVal", dataType = DataType.SERIALIZABLE)
    public ArrayList<Float> zRawVal;
}



