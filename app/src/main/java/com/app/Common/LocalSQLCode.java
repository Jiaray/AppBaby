package com.app.Common;


import android.util.Log;

public class LocalSQLCode {
    private static final String TAG = "LocalSQLCode";
    private static final String KEY_ID = "_id";        //�q��Key

    /**
     * 創建會員表
     *
     * @return Creat MSTR
     */
    public static String SQLite_CreatMSTR() {
        // ASC_MSTR --> �򥻸��  ( KEY_ID (����)  | USER_NAME | USER_PSWD | USER_PMS<�v��>  )
        String sql = "CREATE TABLE IF NOT EXISTS ASC_MSTR " + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                " USER_NAME TEXT, " +
                " USER_PSWD TEXT,  " +
                " USER_PMS TEXT) ";
        Log.v(TAG, "組合 創建 本地資料庫SQL:" + sql);
        return sql;
    }

    /**
     * ��s��ƪ�
     * @param _table       (��ƪ�W��)
     * @param _idfield     (�������)
     * @param _id          (���ޭ�)
     * @param _changefield (�������)
     * @param _changevaule (���ܭ�)
     * @return
     */
    public static String SQLite_UpdateTableData(String _table, String _idfield, String _id, String _changefield, String _changevaule) {
        String sql = "UPDATE " + _table + " " +
                "SET " + _changefield + " = " +
                "'" + _changevaule + "'" +
                " WHERE " + _idfield +
                " = '" + _id + "'";
        return sql;
    }

    /**
     * 移除資料表
     *
     * @return
     */
    public static String SQLite_RemoveTable(String _name) {
        String sql = "Delete From " + _name;
        Log.v(TAG, "組合 刪除 本地資料庫SQL:" + sql);
        return sql;
    }

    /**
     * 新增欄位至資料表中
     *
     * @param _id
     * @param _psd
     * @param _pms
     * @return
     */
    public static String SQLite_InsertMSTR(String _id, String _psd, String _pms) {
        String sql = "Insert Into ASC_MSTR (   USER_NAME, USER_PSWD , USER_PMS ) Values ( " +
                "'" + _id + "'" + " , " +
                "'" + _psd + "'" + " , " +
                "'" + _pms + "'" + " ) ";
        Log.v(TAG, "組合 新增 本地資料庫SQL:" + sql);
        return sql;
    }

    /**
     * ��o  ASC_MSTR �M�� ��O  �ϥΪ�ID �ϥΪ̱K�X  �ϥΪ��v��
     */
    public static String SQLite_GetMSTRList() {
        String sql = "Select  USER_NAME, USER_PSWD, USER_PMS From ASC_MSTR ";
        return sql;
    }

    public static String SQLite_GetTableExist(String _tablename) {
        String sql = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name=" + _tablename;
        return sql;
    }
}
