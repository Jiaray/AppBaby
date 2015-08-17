package com.app.Common.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.AppBabySH.R;

public class LocalDB extends SQLiteOpenHelper {
    // 資料庫名稱
    public static final String DB_NAME = "AppBabySH";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    //private static final int DB_VERSION = 1;
    private static SQLiteOpenHelper mSQLiteOpenHelper;

    public LocalDB(Context context) {
        super(context, DB_NAME, null, 1);
        mSQLiteOpenHelper = this;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        mSQLiteOpenHelper = this;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static SQLiteOpenHelper GetOpenHelper() {
        return mSQLiteOpenHelper;
    }

}
