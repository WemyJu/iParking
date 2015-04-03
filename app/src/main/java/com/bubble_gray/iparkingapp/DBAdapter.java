package com.bubble_gray.iparkingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter {
    private final static String TAG="HelloWorld";
    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        Log.v(TAG, "DB constructor");
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open()
    {
        Log.v(TAG,"DB open");
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        Log.v(TAG,"DB close");
        myDBHelper.close();
    }


    public static final String DATABASE_NAME = "ProjectDB";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "myID";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_IDNUM = "idnum";

    public static final String[] ALL_KEYS = new String[] {KEY_ROWID,KEY_IDNUM};
    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            Log.v(TAG,"DB helper oncreate");
            final String DATABASE_CREATE_SQL =
                    "create table " + DATABASE_TABLE
                            + " ("
                            + KEY_ROWID + " integer not null, "
                            + KEY_IDNUM + " integer not null "
                            + ");";

            _db.execSQL(DATABASE_CREATE_SQL);
            Log.v(TAG,"DB helper oncreate done");
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
    }

    // Add a new set of values to the database.
    public long insertID(int id)
    {
        Log.v(TAG,"DB insert ID");
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, 0);
        initialValues.put(KEY_IDNUM, id);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }
    // Get a specific row (by rowId)
    public Cursor getMyId()
    {
        Log.v(TAG,"DB get ID");
        String where = KEY_ROWID + "=0";
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    //delete data
    public boolean deleteDB() {
        String where = KEY_ROWID + "=0";
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }
}
