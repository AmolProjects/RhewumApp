package com.rhewum.Activity.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

@DatabaseTable(tableName = "displacements_data")
public class PsdSummaryDao implements Serializable {

    @DatabaseField(generatedId = true)
    public int id;
/*    @DatabaseField
    public float xAcceleration;
    @DatabaseField
    public float yAcceleration;
    @DatabaseField
    public float zAcceleration;
    @DatabaseField
    public float dominantsFrequencyX;
    @DatabaseField
    public float dominantsFrequencyY;
    @DatabaseField
    public float dominantsFrequencyZ;*/

    @DatabaseField
    public float xDisplacement;

    @DatabaseField
    public float yDisplacement;

    @DatabaseField
    public float zDisplacement;

    @DatabaseField
    public float xFrequencyMagnitude;

    @DatabaseField
    public float yFrequencyMagnitude;

    @DatabaseField
    public float zFrequencyMagnitude;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public PsdSummaryDao psdSummaryDao;
}
