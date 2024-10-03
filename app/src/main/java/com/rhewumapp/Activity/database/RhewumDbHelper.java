package com.rhewumapp.Activity.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rhewumapp.Activity.MeshConveterData.Constants;
import com.rhewumapp.Activity.MeshConveterData.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RhewumDbHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = Constants.DbName;
    private static final int DATABASE_VERSION = 3;
    private Dao<MeasurementDao, Integer> frequencyDao;
    private Dao<VibCheckerSummaryDao, Integer> accelerometerDao;
    private Dao<PsdSummaryDao,Integer>displacementDao;

    public RhewumDbHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
    }

    // create a table
    public void onCreate(SQLiteDatabase sQLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, MeasurementDao.class);
            TableUtils.createTable(connectionSource, VibCheckerSummaryDao.class);
            TableUtils.createTable(connectionSource, PsdSummaryDao.class);

        } catch (SQLException e) {
            e.getMessage();
        }
    }

    // upgrade the table regarding the data
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, MeasurementDao.class, true);
            TableUtils.dropTable(connectionSource, VibCheckerSummaryDao.class, true);
            TableUtils.dropTable(connectionSource, PsdSummaryDao.class,true);
            onCreate(sQLiteDatabase, connectionSource);
        } catch (SQLException e) {
            String name = RhewumDbHelper.class.getName();
            Log.e(name, "Unable to upgrade database from version " + i + " to new " + i2, e);
            e.getMessage();
        }
    }
    // create model class for vibSonic data

    public Dao<MeasurementDao, Integer> getMeasurementDao() throws SQLException {
        if (this.frequencyDao == null) {
            this.frequencyDao = getDao(MeasurementDao.class);
        }
        return this.frequencyDao;
    }
    // create model class for acceleration data

    public Dao<VibCheckerSummaryDao, Integer> getAccelerometerDao() throws SQLException {
        if (this.accelerometerDao == null) {
            this.accelerometerDao = getDao(VibCheckerSummaryDao.class);
        }
        return this.accelerometerDao;
    }
    // create model class for displacement data
    public Dao<PsdSummaryDao, Integer> getDisplacementDao() throws SQLException {
        if (this.displacementDao == null) {
            this.displacementDao = getDao(PsdSummaryDao.class);
        }
        return this.displacementDao;
    }
    // create model class for frequency data
    public Dao<PsdSummaryDao, Integer> getFrequencyDao() throws SQLException {
        if (this.displacementDao == null) {
            this.displacementDao = getDao(PsdSummaryDao.class);
        }
        return this.displacementDao;
    }
    // getting the vibsonic data

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
    // delete the vibsonic data according to vibsonic id
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
    // delete the vibsonic list
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
    // getting the data according to last id
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

    // insert the record of vibsoinc data within the database

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

    // insert the record of Max acceleration data within the database

    public void maxAccelerometerData(float maxAccX,float maxAccY,float maxAccZ){
        try {
            VibCheckerSummaryDao accelerometer = new VibCheckerSummaryDao();
            accelerometer.xAxis = maxAccX;
            accelerometer.yAxis = maxAccY;
            accelerometer.zAxis = maxAccZ;
            getAccelerometerDao().create(accelerometer);
            Utils.showLog("Data inserted Max Acceleration: " + maxAccX + ", " + maxAccY + ", " + maxAccZ);
        }catch (SQLException e){
            Utils.showLog("Error inserting data: " + e.getMessage());
        }
    }
    // insert the record of Max dominant frequency data within the database
    public void maxDominantFrequencyData(float maxdominantFrX,float maxdominantFrY,float maxdominantFrZ){
        try {
            VibCheckerSummaryDao accelerometer = new VibCheckerSummaryDao();
            accelerometer.dominantFrequencyX = maxdominantFrX;
            accelerometer.dominantFrequencyY = maxdominantFrY;
            accelerometer.dominantFrequencyZ = maxdominantFrZ;
            getAccelerometerDao().create(accelerometer);
            Utils.showLog("Data inserted Dominant Frequency: " + maxdominantFrX + ", " + maxdominantFrY + ", " + maxdominantFrZ);
        }catch (SQLException e){
            Utils.showLog("Error inserting data: " + e.getMessage());
        }
    }
    // insert the record of displacement data within the database
    public void saveDisplacementData(List<Float> xDisplacement, List<Float> yDisplacement, List<Float> zDisplacement) throws SQLException {
        for (int i = 0; i < xDisplacement.size(); i++) {
            PsdSummaryDao psdSummaryDao = new PsdSummaryDao();
            psdSummaryDao.xDisplacement= xDisplacement.get(i);
            psdSummaryDao.yDisplacement= yDisplacement.get(i);
            psdSummaryDao.zDisplacement= zDisplacement.get(i);
            getDisplacementDao().create(psdSummaryDao);

        }
    }

    // insert the record of fft frequency data within the database
    public void saveFrequencyMagnitudeData(List<Float> xFrequencyMagnitude, List<Float> yFrequencyMagnitude, List<Float> zFrequencyMagnitude) {
        try {
            for (int i = 0; i < xFrequencyMagnitude.size(); i++) {
                PsdSummaryDao psdSummaryDao = new PsdSummaryDao();
                psdSummaryDao.xFrequencyMagnitude= xFrequencyMagnitude.get(i);
                psdSummaryDao.yFrequencyMagnitude= yFrequencyMagnitude.get(i);
                psdSummaryDao.zFrequencyMagnitude= zFrequencyMagnitude.get(i);
                getDisplacementDao().create(psdSummaryDao);
            }
        } catch (SQLException e) {
            Utils.showLog("Error inserting frequency magnitude data: " + e.getMessage());
        }
    }

    // fetching the data for the vib-checker for accelerometer
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

    // Method to fetch the latest Displacement data based on timestamp
    public List<PsdSummaryDao> getLatestDisplacementsByListSize(int limit) {
        List<PsdSummaryDao> displacement = new ArrayList<>();
        try {
            // Ensure the limit is greater than 0
            if (limit <= 0) {
                return displacement;
            }
            QueryBuilder<PsdSummaryDao, Integer> queryBuilder = getDisplacementDao().queryBuilder();
            // Order by ID in descending order to get the latest entries
            queryBuilder.orderBy("id", false); // 'false' for descending order
            // Limit the number of results
            queryBuilder.limit((long) limit);
            // Log the query to check correctness (Optional)
            String query = queryBuilder.prepareStatementString();
            System.out.println("Generated Query: " + query);
            // Execute the query and retrieve the results
            displacement = getDisplacementDao().query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return displacement;
    }

}