package com.example.k4ycer.geoalarm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLUtilities extends SQLiteOpenHelper {
    String sql = "CREATE TABLE Alarm(idAlarm INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "descrition TEXT, " +
            "latitude REAL NOT NULL," +
            "longitude REAL NOT NULL," +
            "status Boolean NOT NULL)";
    public SQLUtilities(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ALARM");
        onCreate(db);
    }
}
