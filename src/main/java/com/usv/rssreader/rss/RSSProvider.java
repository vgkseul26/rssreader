package com.usv.rssreader.rss;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class RSSProvider extends ContentProvider {
    final String LOG = "myLogs";

    static final String DB_NAME = "rss";
    static final int DB_VERSION = 2;

    static final String RSS_TABLE = "rss_list";
    static final String RSS_ID = "id";
    static final String RSS_TITLE = "title";
    static final String RSS_DESCRIPTION = "description";
    static final String RSS_LINK = "link";

    static final String DB_CREATE = "create table " + RSS_TABLE + "("
            + RSS_ID + " integer primary key autoincrement, "
            + RSS_TITLE + " text, " + RSS_DESCRIPTION + " text, " + RSS_LINK + " text" + ");";
    static final String AUTHORITY = "com.usv.rssreader.provider.RSSRepository";
    static final String RSS_PATH = "rss_list";
    public static final Uri RSS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RSS_PATH);
    static final String RSS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + RSS_PATH;
    static final String RSS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + RSS_PATH;
    static final int URI_RSS = 1;
    static final int URI_RSS_ID = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, RSS_PATH, URI_RSS);
        uriMatcher.addURI(AUTHORITY, RSS_PATH + "/#", URI_RSS_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    public RSSProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_RSS:
                Log.d(LOG, "URI_RSS");
                break;
            case URI_RSS_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG, "URI_RSS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = RSS_ID + " = " + id;
                } else {
                    selection = selection + " AND " + RSS_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(RSS_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(LOG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_RSS:
                return RSS_CONTENT_TYPE;
            case URI_RSS_ID:
                return RSS_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_RSS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(RSS_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(RSS_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public boolean onCreate() {
        Log.d(LOG, "Create database rss");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(LOG, "query, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_RSS:
                Log.d(LOG, "URI_RSS");
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = RSS_TITLE + " ASC";
                }
                break;
            case URI_RSS_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG, "URI_RSS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = RSS_ID + " = " + id;
                } else {
                    selection = selection + " AND " + RSS_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(RSS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), RSS_CONTENT_URI);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_RSS:
                Log.d(LOG, "URI_RSS");
                break;
            case URI_RSS_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG, "URI_RSS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = RSS_ID + " = " + id;
                } else {
                    selection = selection + " AND " + RSS_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(RSS_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
