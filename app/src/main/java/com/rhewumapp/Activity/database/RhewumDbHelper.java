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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RhewumDbHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = Constants.DbName;
    private static final int DATABASE_VERSION = 11;
    private Dao<MeasurementDao, Integer> frequencyDao;
    private Dao<VibCheckerSummaryDao, Integer> accelerometerDao;
    private Dao<PsdSummaryDao,Integer>displacementDao;
    private Dao<RawDao,Integer>rawDao;
   // private Dao<RawSensor,Integer>rawSensors;
    private Dao<RawSensorDao,Integer>rawSensorDao;

    public RhewumDbHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
    }

    // create a table
    public void onCreate(SQLiteDatabase sQLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, MeasurementDao.class);
            TableUtils.createTable(connectionSource, VibCheckerSummaryDao.class);
            TableUtils.createTable(connectionSource, PsdSummaryDao.class);
            TableUtils.createTable(connectionSource, RawDao.class);
           // TableUtils.createTable(connectionSource, RawSensor.class);
            TableUtils.createTable(connectionSource, RawSensorDao.class);

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
            TableUtils.dropTable(connectionSource, RawDao.class,true);
          //  TableUtils.dropTable(connectionSource, RawSensor.class,true);
            TableUtils.dropTable(connectionSource, RawSensorDao.class,true);
            onCreate(sQLiteDatabase, connectionSource);
        } catch (SQLException e) {
            String name = RhewumDbHelper.class.getName();
            Log.e(name, "Unable to upgrade database from version " + i + " to new " + i2, e);
            e.getMessage();
        }
    }
    @Override
    public void close() {
        rawDao = null;
        super.close();
    }

    // create Raw data
    public Dao<RawDao,Integer>getRawData()throws SQLException{
        if(this.rawDao==null){
            this.rawDao = getDao(RawDao.class);
        }
        return this.rawDao;
    }
    // create model class for vibSonic data

    public Dao<MeasurementDao, Integer> getMeasurementDao() throws SQLException {
        if (this.frequencyDao == null) {
            this.frequencyDao = getDao(MeasurementDao.class);
        }
        return this.frequencyDao;
    }

    //added new for vibchecker25-11
    public Dao<VibCheckerSummaryDao, Integer> getaccelerometerDao() throws SQLException {
        if (this.accelerometerDao == null) {
            this.accelerometerDao = getDao(VibCheckerSummaryDao.class);
        }
        return this.accelerometerDao;
    }

    public Dao<RawSensorDao, Integer> getRawSensorDao() throws SQLException {
        if (this.rawSensorDao == null) {
            this.rawSensorDao = getDao(RawSensorDao.class);
        }
        return this.rawSensorDao;
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

    public ArrayList<MeasurementDao> getMeasurementList() {
        try {
            QueryBuilder<MeasurementDao, Integer> queryBuilder = getMeasurementDao().queryBuilder();

            // Assuming there is a field "timestamp" or "id" to indicate order of insertion
            queryBuilder.orderBy("id", false); // 'false' for descending order

            Utils.showLog("Query Executed: " + queryBuilder.prepareStatementString());

            try {
                return (ArrayList<MeasurementDao>) getMeasurementDao().query(queryBuilder.prepare());
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
            return null;
        }
    }
    // fetching the data for the vib-checker for accelerometer
    public ArrayList<VibCheckerSummaryDao> getVibCheckerAcc() {
        try {
            QueryBuilder<VibCheckerSummaryDao, Integer> queryBuilder = getAccelerometerDao().queryBuilder();
            queryBuilder.orderBy("id", false); // 'false' for descending order

            Utils.showLog("Query Executed: " + queryBuilder.prepareStatementString());
            try {
               /* ArrayList<VibCheckerSummaryDao> results = (ArrayList<VibCheckerSummaryDao>) getAccelerometerDao().query(queryBuilder.prepare());
                Utils.showLog("Data retrieved: " + results.size() + " records");*/
                return  (ArrayList<VibCheckerSummaryDao>) getAccelerometerDao().query(queryBuilder.prepare());
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

    // delete the vibsonic list
    public void deleteMeasurementListSummery(ArrayList<VibCheckerSummaryDao> arrayList) {
        Iterator<VibCheckerSummaryDao> it = arrayList.iterator();
        while (it.hasNext()) {
            try {
                getAccelerometerDao().delete(it.next());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertSensorsData(String dateTime,Long time,Float accX, Float accY,Float accZ,int counter) {
        try {
            RawSensorDao rawSensorDao = new RawSensorDao();
            rawSensorDao.setDateTime(dateTime);
            rawSensorDao.setTime(time);
            rawSensorDao.setxAxisRawValue(accX);
            rawSensorDao.setyAxisRawValue(accY);
            rawSensorDao.setzAxisRawValue(accZ);
            rawSensorDao.setCounter(counter);
            getRawSensorDao().create(rawSensorDao);
        } catch (Exception e) {
            Utils.showLog("Error inserting data: " + e.getMessage());
        }
    }

    public List<RawSensorDao> fetchSensorDataById(int recordId) {
        List<RawSensorDao> rawVal = new ArrayList<>();
        try {
            // QueryBuilder to filter rows by ID
            QueryBuilder<RawSensorDao, Integer> queryBuilder = getRawSensorDao().queryBuilder();
            queryBuilder.where().eq("counter", recordId); // Apply filter by ID

            rawVal = getRawSensorDao().query(queryBuilder.prepare()); // Fetch filtered results

            for (RawSensorDao dao : rawVal) {
                Log.d("DB_INSERT", "RD:Fetched Data: xRawValue=" + dao.getxAxisRawValue() +
                        ", yRawValue=" + dao.getyAxisRawValue() +
                        ", zRawValue=" + dao.getzAxisRawValue() +", counter=" + dao.getCounter());
            }
        } catch (SQLException e) {
            Utils.showLog("Error fetching data: " + e.getMessage());
        }
        return rawVal;
    }

    public List<RawSensorDao> fetchSensorRawData() {
        List<RawSensorDao> rawVal = new ArrayList<>();
        try {
            // QueryBuilder to filter rows by ID
            QueryBuilder<RawSensorDao, Integer> queryBuilder = getRawSensorDao().queryBuilder();
            rawVal = getRawSensorDao().query(queryBuilder.prepare()); // Fetch filtered results

            for (RawSensorDao dao : rawVal) {
                Log.d("DB_INSERT", "RD:Fetched Data: xRawValue=" + dao.getxAxisRawValue() +
                        ", yRawValue=" + dao.getyAxisRawValue() +
                        ", zRawValue=" + dao.getzAxisRawValue() +", counter=" + dao.getCounter());
            }
        } catch (SQLException e) {
            Utils.showLog("Error fetching data: " + e.getMessage());
        }
        return rawVal;
    }


    //added new 29Nov
    //added new for buffer data base
    public void insertVibCheckerData(float accX, float accY, float accZ, float dominantFrX, float dominantFrY, float dominantFrZ, float ampX, float ampY, float ampZ, int measurementTotalTime, int delay, String measurementDate, byte[] sensorData,RawSensorDao rawSensorDao1) {
        try {
            VibCheckerSummaryDao data = new VibCheckerSummaryDao();
            // Insert accelerometer data
            data.xAxis = accX;
            data.yAxis = accY;
            data.zAxis = accZ;
            data.dominantFrequencyX = dominantFrX;
            data.dominantFrequencyY = dominantFrY;
            data.dominantFrequencyZ = dominantFrZ;
            data.peakAmplitudeX = ampX;
            data.peakAmplitudeY = ampY;
            data.peakAmplitudeZ = ampZ;
            data.measurementTotalTime = measurementTotalTime;
            data.delay = delay;
            data.measurementDate = new Date();
            data.sensorData = sensorData;

            // Insert into database
            getAccelerometerDao().create(data);
            Utils.showLog("Data inserted with buffer: " + Arrays.toString(sensorData));
        } catch (Exception e) {
            Utils.showLog("Error inserting data: " + e.getMessage());
        }
    }


    public List<RawDao> getLatestRawValues(int limit) {
        List<RawDao> rawValues = new ArrayList<>();
        try {
            // Ensure the limit is greater than 0
           // if (limit <= 0) {
             //   return rawValues;
           // }
            // Create a query builder for VibCheckerSummaryDao
            QueryBuilder<RawDao, Integer> queryBuilder = getRawDao().queryBuilder();

            // Order by ID in descending order to fetch the latest entries
          //  queryBuilder.orderBy("id", false); // 'false' for descending order

            List<RawDao> vibCheckerList = queryBuilder.query();

            for (RawDao dao : vibCheckerList) {
                Log.d("DB_INSERT", "RD:Fetched Data: xRawValue=" + dao.xRawValues +
                        ", yRawValue=" + dao.yRawValues +
                        ", zRawValue=" + dao.zRawValues);
            }

            // Limit the number of results
          //  queryBuilder.limit((long) limit);

            // Log the query to verify correctness (Optional)
            String query = queryBuilder.prepareStatementString();
            System.out.println("Generated Query: " + query);

            // Execute the query and retrieve the results
            rawValues = getRawDao().query(queryBuilder.prepare());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rawValues;
    }

    public Dao<RawDao, Integer> getRawDao() {
        try {
            // Retrieve the DAO for VibCheckerSummaryDao
            return getDao(RawDao.class);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



}