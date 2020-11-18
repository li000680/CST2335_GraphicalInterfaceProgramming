package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "MessageDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "resultTable";
    public final static String COL_COUNTRY = "country";
    public final static String COL_PROVOINCE = "province/state";
    public final static String COL_CASE = "case number";
    public final static String COL_DATE = "date";
    public final static String COL_ID = "_id";

    /**
     *
     */
    public MyOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_COUNTRY + " text,"
                + COL_PROVOINCE + " text,"
                + COL_CASE + " text,"
                + COL_DATE  + " text);");  // add or remove columns
    }
    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create the new table:
        onCreate(db);
    }
    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create the new table:
        onCreate(db);
    }
}

