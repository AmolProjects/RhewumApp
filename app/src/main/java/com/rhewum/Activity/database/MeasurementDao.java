package com.rhewum.Activity.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "measurementdao")
public class MeasurementDao implements Serializable {
    private static final long serialVersionUID = -1667460018464649054L;
    @DatabaseField(columnName = "decible3")
    public double decibleForFreq125;
    @DatabaseField(columnName = "decible10")
    public double decibleForFreq16k;
    @DatabaseField(columnName = "decible6")
    public double decibleForFreq1k;
    @DatabaseField(columnName = "decible4")
    public double decibleForFreq250;
    @DatabaseField(columnName = "decible7")
    public double decibleForFreq2k;
    @DatabaseField(columnName = "decible1")
    public double decibleForFreq32;
    @DatabaseField(columnName = "decible8")
    public double decibleForFreq4k;
    @DatabaseField(columnName = "decible5")
    public double decibleForFreq500;
    @DatabaseField(columnName = "decible2")
    public double decibleForFreq63;
    @DatabaseField(columnName = "decible9")
    public double decibleForFreq8k;
    @DatabaseField(columnName = "graph_image")
    public String graphImage;
    @DatabaseField(columnName = "mean_level_total")
    public String meanLevelTotal;
    @DatabaseField(columnName = "measurement_date")
    public Date measurementDate;
    @DatabaseField(columnName = "id", generatedId = true)
    public int measurementId;
    @DatabaseField(columnName = "measurement_total_time")
    public String measurementTotalTime;
}