package com.app.Common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDB extends SQLiteOpenHelper {
    // 資料庫名稱
	private static final String DB_NAME = "appbaby.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
	private static final int DB_VERSION = 1;
	private static SQLiteOpenHelper mSQLiteOpenHelper;
   
	public LocalDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mSQLiteOpenHelper=this;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		mSQLiteOpenHelper=this;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public  static SQLiteOpenHelper GetOpenHelper (){
		return mSQLiteOpenHelper;
	}

}
