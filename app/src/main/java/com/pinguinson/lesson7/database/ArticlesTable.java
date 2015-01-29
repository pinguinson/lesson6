package com.pinguinson.lesson7.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class ArticlesTable implements BaseColumns {
    public static final String TABLE_NAME = "FeedEntries";

    public static final String ENTRY_TITLE_COLUMN = "title";
    public static final String ENTRY_URL_COLUMN = "url";
    public static final String ENTRY_FEED_ID_COLUMN = "feed_id";

    public static void onCreate(SQLiteDatabase db) {
        String DB_CREATE = "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                BaseColumns._ID + " integer PRIMARY KEY autoincrement, " +
                ENTRY_TITLE_COLUMN + " text not null, " +
                ENTRY_URL_COLUMN + " text not null, " +
                ENTRY_FEED_ID_COLUMN + " INTEGER REFERENCES Feeds(_ID) ON DELETE CASCADE); ";
        db.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}