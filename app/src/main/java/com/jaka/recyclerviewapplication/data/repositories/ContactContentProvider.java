package com.jaka.recyclerviewapplication.data.repositories;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class ContactContentProvider extends ContentProvider {

    static final int CONTACTS = 1;
    static final int CONTACTS_ID = 2;

    private static final String AUTHORITY = "com.jaka.recyclerviewapplication";
    private static final String PATH = "contacts";
    private static final String PATH_ID = "contacts/#";

    public static final String URL = "content://" + AUTHORITY + "/" + PATH;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static HashMap<String, String> CONTACTS_PROJECTION_MAP;

    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH, CONTACTS);
        uriMatcher.addURI(AUTHORITY, PATH_ID, CONTACTS_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Telephone";
    static final String CONTACTS_TABLE_NAME = "contacts";
    static final int DATABASE_VERSION = 1;
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String PHONE_NUMBER = "phonenumber";
    static final String ID = "id";
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + CONTACTS_TABLE_NAME +
                    " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIRSTNAME + " TEXT NOT NULL, " +
                    LASTNAME + "  TEXT NOT NULL, " + PHONE_NUMBER + " TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CONTACTS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                qb.setProjectionMap(CONTACTS_PROJECTION_MAP);
                break;
            case CONTACTS_ID:
                qb.appendWhere(ID + "=" + uri.getPathSegments().get(1));
                break;
        }

        if (s1 == null || s1 == "") {
            s1 = FIRSTNAME;
        }

        Cursor c = qb.query(db, strings, s,
                strings1, null, null, s1);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowId = db.insert(CONTACTS_TABLE_NAME, "", contentValues);

        if (rowId > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                count = db.delete(CONTACTS_TABLE_NAME, s, strings);
                break;

            case CONTACTS_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(CONTACTS_TABLE_NAME, ID + " = " + id +
                        (!TextUtils.isEmpty(s) ? " AND (" + s + ")" : ""), strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                count = db.update(CONTACTS_TABLE_NAME, contentValues, s, strings);
                break;

            case CONTACTS_ID:
                count = db.update(CONTACTS_TABLE_NAME, contentValues,
                        ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(s) ? " AND (" + s + ")" : ""), strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
