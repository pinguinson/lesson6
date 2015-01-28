package com.pinguinson.lesson7.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class FeedsTable implements BaseColumns {
    public static final String TABLE_NAME = "Feeds";

    public static final String FEED_TITLE_COLUMN = "title";
    public static final String FEED_URL_COLUMN = "url";

    public static void onCreate(SQLiteDatabase db){
        String DB_CREATE = "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                BaseColumns._ID + " integer PRIMARY KEY autoincrement, " +
                FEED_TITLE_COLUMN + " text not null, " +
                FEED_URL_COLUMN + " text nit null );";
        db.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME);
        onCreate(db);
    }

}
