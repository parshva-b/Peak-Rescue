package com.peakrescue.oximeter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String createOximeterDataTable =
            "CREATE TABLE oximeterData ("
                    + "id INTEGER PRIMARY KEY autoincrement, "
                    + "spo2 NUMERIC); ";

    public DatabaseHelper(Context context) {
        super(context, "UserData.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(createOximeterDataTable);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
    public void onUpgrade(SQLiteDatabase db, int i, int j) {

        db.execSQL("DROP TABLE IF EXISTS oximeterData");
        onCreate(db);
    }


}
