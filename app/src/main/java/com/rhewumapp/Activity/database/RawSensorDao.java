package com.rhewumapp.Activity.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
@DatabaseTable(tableName = "sensor_value")
public class RawSensorDao implements Serializable {
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "DateTime")
    private String DateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public Long getTime() {
        return Time;
    }

    public void setTime(Long time) {
        Time = time;
    }

    @DatabaseField(columnName = "Time")
    private Long Time;

    @DatabaseField(columnName = "xAxisRawValue")
    private float xAxisRawValue;

    @DatabaseField(columnName = "yAxisRawValue")
    private float yAxisRawValue;

    @DatabaseField(columnName = "zAxisRawValue")
    private float zAxisRawValue;

    @DatabaseField(columnName = "counter")
    private int counter;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    // Constructors, Getters, and Setters
    public RawSensorDao() {
    }

    public float getxAxisRawValue() {
        return xAxisRawValue;
    }

    public void setxAxisRawValue(float xAxisRawValue) {
        this.xAxisRawValue = xAxisRawValue;
    }

    public float getyAxisRawValue() {
        return yAxisRawValue;
    }

    public void setyAxisRawValue(float yAxisRawValue) {
        this.yAxisRawValue = yAxisRawValue;
    }

    public float getzAxisRawValue() {
        return zAxisRawValue;
    }

    public void setzAxisRawValue(float zAxisRawValue) {
        this.zAxisRawValue = zAxisRawValue;
    }
}
