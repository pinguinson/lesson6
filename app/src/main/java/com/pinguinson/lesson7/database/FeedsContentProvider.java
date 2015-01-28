package com.pinguinson.lesson7.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class FeedsContentProvider extends ContentProvider {
    private static final String FEEDS_TABLE = "Feeds";
    private static final String ENTRIES_TABLE = "FeedEntries";

    private DBHelper helper;

    private static final int SINGLE_FEED = 1;
    private static final int ENTRIES = 4;
    private static final int ENTRY = 2;
    private static final int FEEDS = 3;

    private static final String AUTHORITY = "ru.ifmo.mobdev.rss";
    private static final String PATH_ENTRIES = "FeedEntries";

    private static final String PATH_FEEDS = "Feeds";
    public static final Uri CONTENT_URI_FEEDS =
            Uri.parse("content://" + AUTHORITY + "/" + PATH_FEEDS);

    public static final Uri CONTENT_URI_ENTRIES =
            Uri.parse("content://" + AUTHORITY + "/" + PATH_ENTRIES);

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_FEEDS, FEEDS);
        uriMatcher.addURI(AUTHORITY, PATH_ENTRIES, ENTRIES);
        uriMatcher.addURI(AUTHORITY, PATH_ENTRIES + "/#", ENTRY);
        uriMatcher.addURI(AUTHORITY, PATH_FEEDS + "/#", SINGLE_FEED);
    }


    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case ENTRIES:
                queryBuilder.setTables(ENTRIES_TABLE);
                break;
            case FEEDS:
                queryBuilder.setTables(FEEDS_TABLE);
                break;
            case SINGLE_FEED:
                queryBuilder.setTables(FEEDS_TABLE);
                queryBuilder.appendWhere(FeedsTable._ID + "=" + uri.getLastPathSegment());
                break;
            case ENTRY:
                queryBuilder.setTables(ENTRIES_TABLE);
                queryBuilder.appendWhere("feed_id" + "=" + uri.getLastPathSegment());
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long id;
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                id = db.insert(FeedsTable.TABLE_NAME, null, contentValues);
                break;
            case ENTRY:
                contentValues.put(EntriesTable.ENTRY_FEED_ID_COLUMN, uri.getLastPathSegment());
                id = db.insert(EntriesTable.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int removed;
        switch (uriMatcher.match(uri)) {
            case ENTRIES:
                removed = db.delete(EntriesTable.TABLE_NAME, selection, selectionArgs);
                break;
            case FEEDS:
                removed = db.delete(FeedsTable.TABLE_NAME, selection, selectionArgs);
                break;
            case SINGLE_FEED:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    removed = db.delete(FeedsTable.TABLE_NAME, FeedsTable._ID + "=" + id, selectionArgs);
                } else {
                    removed = db.delete(FeedsTable.TABLE_NAME, FeedsTable._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case ENTRY:
                String feedId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    removed = db.delete(EntriesTable.TABLE_NAME, EntriesTable.ENTRY_FEED_ID_COLUMN + "=" + feedId, selectionArgs);
                } else {
                    removed = db.delete(EntriesTable.TABLE_NAME, EntriesTable.ENTRY_FEED_ID_COLUMN + "=" + feedId + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return removed;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int updated;
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                updated = db.update(FeedsTable.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case SINGLE_FEED:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    updated = db.update(FeedsTable.TABLE_NAME, contentValues, FeedsTable._ID + "=" + id, selectionArgs);
                } else {
                    updated = db.update(FeedsTable.TABLE_NAME, contentValues, FeedsTable._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case ENTRY:
                String feedId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    updated = db.update(EntriesTable.TABLE_NAME, contentValues, EntriesTable.ENTRY_FEED_ID_COLUMN + "=" + feedId, selectionArgs);
                } else {
                    updated = db.update(EntriesTable.TABLE_NAME, contentValues, EntriesTable.ENTRY_FEED_ID_COLUMN + "=" + feedId + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updated;
    }
}
