package com.kics.kstudio.kgallery.Misc;

import android.database.Cursor;
import android.util.ArrayMap;

import java.util.HashMap;

public class ColumnIndexCache {

    private HashMap<String, Integer> mMap = new HashMap<>();

    public int getColumnIndex(Cursor cursor, String columnName) {
        if (!mMap.containsKey(columnName))
            mMap.put(columnName, cursor.getColumnIndex(columnName));
        return mMap.get(columnName);
    }

    public void clear() {
        mMap.clear();
    }
}
