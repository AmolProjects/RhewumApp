package com.rhewum.Activity.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rhewum.Activity.MeshConveterData.Constants;
import com.rhewum.Activity.MeshConveterData.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RhewumDbHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = Constants.DbName;
    private static final int DATABASE_VERSION = 2;
    private Dao<MeasurementDao, Integer> frequencyDao;
    private Dao<VibCheckerSummaryDao, Integer> accelerometerDao;

    public RhewumDbHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable((ConnectionSource) this.connectionSource, MeasurementDao.class);
            TableUtils.createTable(connectionSource, VibCheckerSummaryDao.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, MeasurementDao.class, true);
            TableUtils.dropTable(connectionSource, VibCheckerSummaryDao.class, true);
            onCreate(sQLiteDatabase, connectionSource);
        } catch (SQLException e) {
            String name = RhewumDbHelper.class.getName();
            Log.e(name, "Unable to upgrade database from version " + i + " to new " + i2, e);
            e.printStackTrace();
        }
    }

    public Dao<MeasurementDao, Integer> getMeasurementDao() throws SQLException {
        if (this.frequencyDao == null) {
            this.frequencyDao = getDao(MeasurementDao.class);
        }
        return this.frequencyDao;
    }

    public Dao<VibCheckerSummaryDao, Integer> getAccelerometerDao() throws SQLException {
        if (this.accelerometerDao == null) {
            this.accelerometerDao = getDao(VibCheckerSummaryDao.class);
        }
        return this.accelerometerDao;
    }

    public ArrayList<MeasurementDao> getMeasurementList() {
        try {
            QueryBuilder<MeasurementDao, Integer> queryBuilder = getMeasurementDao().queryBuilder();
            Utils.showLog("Query Executed: " + queryBuilder.prepareStatementString());
            try {
                return (ArrayList) getMeasurementDao().query(queryBuilder.prepare());
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public void deleteMeasurementListById(ArrayList<MeasurementDao> arrayList) {
        Iterator<MeasurementDao> it = arrayList.iterator();
        while (it.hasNext()) {
            MeasurementDao next = it.next();
            try {
                DeleteBuilder<MeasurementDao, Integer> deleteBuilder = getMeasurementDao().deleteBuilder();
                deleteBuilder.where().eq("measurementId", Integer.valueOf(next.measurementId));
                Utils.showLog("Query: " + deleteBuilder.prepareStatementString());
                getMeasurementDao().delete(deleteBuilder.prepare());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteMeasurementList(ArrayList<MeasurementDao> arrayList) {
        Iterator<MeasurementDao> it = arrayList.iterator();
        while (it.hasNext()) {
            try {
                getMeasurementDao().delete(it.next());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getLastId() {
        List<MeasurementDao> arrayList = new ArrayList<>();
        try {
            Dao<MeasurementDao, Integer> measurementDao = getMeasurementDao();
            arrayList = measurementDao.query(measurementDao.queryBuilder().orderBy("id", false).limit(1L).prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (arrayList.size() == 0) {
            return 0;
        }
        return arrayList.get(0).measurementId;
    }

    public ArrayList getListById(int i) {
        ArrayList<MeasurementDao> arrayList = new ArrayList<>();
        try {
            QueryBuilder<MeasurementDao, Integer> queryBuilder = getMeasurementDao().queryBuilder();
            try {
                queryBuilder.where().eq("id", Integer.valueOf(i));
                return (ArrayList) getMeasurementDao().query(queryBuilder.prepare());
            } catch (SQLException e) {
                e.printStackTrace();
                return arrayList;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
            return arrayList;
        }
    }

    public void addNewRecord(Context context, String str, String str2, String str3, HashMap<Integer, Double> hashMap) {
        MeasurementDao measurementDao = new MeasurementDao();
        measurementDao.measurementDate = new Date();
        measurementDao.measurementTotalTime = str2;
        measurementDao.graphImage = str;
        measurementDao.meanLevelTotal = str3;
        measurementDao.decibleForFreq32 = hashMap.get(0).doubleValue();
        measurementDao.decibleForFreq63 = hashMap.get(1).doubleValue();
        measurementDao.decibleForFreq125 = hashMap.get(2).doubleValue();
        measurementDao.decibleForFreq250 = hashMap.get(3).doubleValue();
        measurementDao.decibleForFreq500 = hashMap.get(4).doubleValue();
        measurementDao.decibleForFreq1k = hashMap.get(5).doubleValue();
        measurementDao.decibleForFreq2k = hashMap.get(6).doubleValue();
        measurementDao.decibleForFreq4k = hashMap.get(7).doubleValue();
        measurementDao.decibleForFreq8k = hashMap.get(8).doubleValue();
        measurementDao.decibleForFreq16k = hashMap.get(9).doubleValue();
        try {
            getMeasurementDao().create(measurementDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAccelerometerData(List<float[]> accelerometerData) {
        try {
            for (float[] data : accelerometerData) {
                VibCheckerSummaryDao accelerometer = new VibCheckerSummaryDao();
                accelerometer.xAxis = data[0];
                accelerometer.yAxis = data[1];
                accelerometer.zAxis = data[2];
                getAccelerometerDao().create(accelerometer);
                Utils.showLog("Data inserted: " + data[0] + ", " + data[1] + ", " + data[2]);

            }
        } catch (SQLException e) {
            Utils.showLog("Error inserting data: " + e.getMessage());
        }
    }
    public ArrayList<VibCheckerSummaryDao> getVibCheckerAcc() {
        try {
            QueryBuilder<VibCheckerSummaryDao, Integer> queryBuilder = getAccelerometerDao().queryBuilder();
            Utils.showLog("Query Executed: " + queryBuilder.prepareStatementString());
            try {
                ArrayList<VibCheckerSummaryDao> results = (ArrayList<VibCheckerSummaryDao>) getAccelerometerDao().query(queryBuilder.prepare());
                Utils.showLog("Data retrieved: " + results.size() + " records");
                return results;
            } catch (SQLException e) {
                Utils.showLog("Error querying data: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } catch (SQLException e2) {
            Utils.showLog("Error preparing query: " + e2.getMessage());
            e2.printStackTrace();
            return null;
        }
    }
}