package com.ssaczkowski.earthquakemonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntegerRes;

public class EqDbHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "earthquakes.db";

    public EqDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String EARTHQUAKE_DATABASE = "CREATE TABLE " + EqContract.EqColumns.TABLE_NAME + " (" +
                EqContract.EqColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EqContract.EqColumns.MAGNITUDE + " TEXT NOT NULL," +
                EqContract.EqColumns.PLACE + " TEXT NOT NULL," +
                EqContract.EqColumns.LONGITUDE + " TEXT NOT NULL, " +
                EqContract.EqColumns.LATITUDE + " TEXT NOT NULL, " +
                EqContract.EqColumns.TIMESTAMP + " TEXT NOT NULL" +
                 ")";

        db.execSQL(EARTHQUAKE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}
