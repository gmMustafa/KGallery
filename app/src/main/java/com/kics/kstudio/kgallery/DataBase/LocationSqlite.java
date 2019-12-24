package com.kics.kstudio.kgallery.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;


import com.kics.kstudio.kgallery.DataModels.MapMarkers;

import java.util.ArrayList;
import java.util.List;

public class LocationSqlite {

    private SQLiteDatabase database;
    private LocationsDB dbHelper;
    private Context context;
    private String[] allColumns = {LocationsDB.FIELD_ROW_ID,
            LocationsDB.FIELD_LAT, LocationsDB.FIELD_LNG, LocationsDB.FIELD_TITLE};

    public LocationSqlite(Context context) {
        dbHelper = new LocationsDB(context);
        this.context = context;

    }


    public long addRecord(Double lat, Double lng, String title) {

        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LocationsDB.FIELD_LAT, lat);
        values.put(LocationsDB.FIELD_LNG, lng);
        values.put(LocationsDB.FIELD_TITLE, title);

        if (duplicatecheck(lat, lng, title)) {
            Toast.makeText(context, " Failure: Marker ALREADY EXISTS in range of 100 M ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, " Marker added with range of 100 M", Toast.LENGTH_SHORT).show();
            long insertId = database.insert(LocationsDB.DATABASE_TABLE, null, values);
            return insertId;
        }
        return -1;
    }

    private boolean duplicatecheck(Double lat, Double lng, String zoom) {
        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(LocationsDB.DATABASE_TABLE,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {

                if (lat.equals(Double.parseDouble(cursor.getString(1))) && lng.equals(Double.parseDouble(cursor.getString(2)))) {

                    cursor.close();
                    return true;
                } else {
                    Location center = new Location("fused");
                    center.setLatitude(Double.parseDouble(cursor.getString(1)));
                    center.setLongitude(Double.parseDouble(cursor.getString(2)));

                    Location test = new Location("fused");
                    test.setLatitude(lat);
                    test.setLongitude(lng);

                    Float distanceInMeters = center.distanceTo(test);
                    boolean isWithin1km = distanceInMeters < 100;
                    cursor.close();
                    return isWithin1km;
                }
            }
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return false;
    }


    public long deleteRecord(int id) {
        database = dbHelper.getWritableDatabase();
        long deleteId = database.delete(LocationsDB.DATABASE_TABLE, LocationsDB.FIELD_ROW_ID + "=?", new String[]{String.valueOf(id)});
        return deleteId;
    }

    public List<MapMarkers> getAllRecord() {

        database = dbHelper.getWritableDatabase();
        List<MapMarkers> comments = new ArrayList<MapMarkers>();

        Cursor cursor = database.query(LocationsDB.DATABASE_TABLE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                MapMarkers comment = new MapMarkers();
                comment.setFIELD_ROW_ID(cursor.getInt(0));
                comment.setFIELD_LAT((cursor.getDouble(1)));
                comment.setFIELD_LNG((cursor.getDouble(2)));
                comment.setFIELD_TITLE(cursor.getString(3));
                comments.add(comment);
                cursor.moveToNext();
            }
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }


    class LocationsDB extends SQLiteOpenHelper {
        public final static String DATABASE_NAME = "locationmarkersqlite";
        public final static int DATABASE_VERSION = 1;
        public static final String FIELD_ROW_ID = "_id";
        public static final String FIELD_LAT = "lat";
        public static final String FIELD_LNG = "lng";
        public static final String FIELD_TITLE = "zom";
        public static final String DATABASE_TABLE = "locations";

        public LocationsDB(Context context) {
            super(context,
                    context.getExternalFilesDir(null).getAbsolutePath()
                            + "/" + DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {

            String sql = "create table " + DATABASE_TABLE + " ( " +
                    FIELD_ROW_ID + " integer primary key autoincrement , " +
                    FIELD_LNG + " double , " +
                    FIELD_LAT + " double , " +
                    FIELD_TITLE + " text " +
                    " ) ";
            database.execSQL(sql);
            Log.e("a", "onCreate");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("a",
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);

        }
    }
}
