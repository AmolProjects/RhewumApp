package com.rhewumapp.Activity.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "raw_data")
public class RawDao implements Serializable {

    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(columnName = "xRawValues")
    public float xRawValues;

    @DatabaseField(columnName = "yRawValues")
    public float yRawValues;

    @DatabaseField(columnName = "zRawValues")
    public float zRawValues;
}

