package com.media.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbHelper;

    private int openedConnections = 0;

    // ...
    public synchronized SQLiteDatabase getReadableDatabase() {
        openedConnections++;
        return super.getReadableDatabase();
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        openedConnections++;
        return super.getWritableDatabase();
    }

    public synchronized void close() {
        openedConnections--;
        if (openedConnections == 0) {
            super.close();
        }
    }

    public static DBHelper dbHelper() {
        return dbHelper;
    }

    public static void init(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
    }

    private DBHelper(Context context) {
        super(context, "account.db", null, 1);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE user (_id integer primary key, "
				+ "username varchar(20), name varchar(20),role varchar(10),"
				+ "tags varchar(200),userId varchar(50),key varchar(50))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS user");
		onCreate(db);
	}

}
