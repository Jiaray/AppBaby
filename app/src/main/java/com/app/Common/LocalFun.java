package com.app.Common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalFun {
	private SQLiteDatabase Database;
	private Cursor cursor;
	private Handler mHandler = new Handler();
	private Context ctx = null;

	public LocalFun(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * @param ����SQL��O
	 *            -��o��@���e�ϥ�
	 * @return ��^��@String
	 */
	public String RunSqlOnlyOne(String sql) {
		Database = LocalDB.GetOpenHelper().getWritableDatabase();
		String RS = "";
		try {
			cursor = Database.rawQuery(sql, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				RS = cursor.getString(0);
			}
			closeDB();
		} catch (Exception e) {
			Log.v("zyo", "RunSqlOnlyOne : SqlError:" + sql);
		}
		if (RS == null)
			RS = "";
		return RS;
	}

	/**
	 * 
	 * @param ����SQL��O
	 *            �L�^��
	 * 
	 */
	public void RunSqlNoQuery(String sql) {
		Database = LocalDB.GetOpenHelper().getWritableDatabase();
		try {
			Database.execSQL(sql);
			closeDB();
		} catch (Exception e) {
			Log.v("zyo", "ExecuteNoQuery : SqlError:" + sql);
		}
	}

	/**
	 * 
	 * @param �P�_��Ʈw�O�_�s�b
	 * @return True/False
	 */
	public boolean CheckDB(File dataBase) {
		Log.v("zyo", "LocalFun : CheckDB");
		return dataBase.exists();
	}

	/**
	 * 
	 * @param �P�_��O�_�s�b
	 * @return True/False
	 */
	public boolean CheckTable(String _tablename) {
		boolean result = false;
		if (_tablename == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = LocalDB.GetOpenHelper().getWritableDatabase();
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
					+ _tablename.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;

	}

	/**
	 * @param ����SQL��O
	 *            -��o��ƪ�ϥ�
	 * @return ��^List<Map<Integer,String>>
	 */
	public List<Map<String, String>> RunSqlDataTable(String sql) {
		Database = LocalDB.GetOpenHelper().getWritableDatabase();
		List<Map<String, String>> list = null;
		try {
			cursor = Database.rawQuery(sql, null);
			if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
				list = new ArrayList<Map<String, String>>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					for (int j = 0; j < cursor.getColumnCount(); j++) {
						String name = cursor.getColumnName(j);
						map.put(name, cursor.getString(j));
					}
					list.add(map);
					if (!cursor.isLast()) {
						cursor.moveToNext();
					}
				}
				Log.v("zyo", "RunSqlDataTable:SDDD");
			}
			Log.v("zyo", "RunSqlDataTable:cursor:" + cursor.getCount());
			closeDB();
			return list;
		} catch (Exception e) {
			Log.v("zyo", "RunSqlDataTable:SqlError:" + e + " SQL=" + sql);
			return null;
		}
	}

	/**
	 * ������Ʈw����
	 */
	public void closeDB() {
		mHandler.removeCallbacks(runnable);
		mHandler.postDelayed(runnable, 3000);
	}

	final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (cursor != null) {
				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
			if (Database != null) {
				if (Database.isOpen()) {
					try {
						Database.close();
					} catch (Exception e) {
					}
				}
			}
		}
	};
}
