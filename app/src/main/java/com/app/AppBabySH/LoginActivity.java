package com.app.AppBabySH;


import com.app.AppBabySH.UIBase.BaseActivity;
import com.app.AppBabySH.UIBase.MyAlertDialog;
import com.app.Common.ComFun;
import com.app.Common.LocalFun;
import com.app.Common.LocalSQLCode;
import com.app.Common.UserData;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "LoginActivity";
    private EditText idTxt, pwTxt;
    private Button loginBtn;
    private ProgressDialog pd;

    // Sql使用
    private LocalFun sqlLocal = new LocalFun(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.login_activity);
        InitView();
        CheckLogin();
    }

    private void InitView() {
        idTxt = (EditText) findViewById(R.id.l_idTxt);
        pwTxt = (EditText) findViewById(R.id.l_pwTxt);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
    }

    /**
     * 判斷登入
     */
    private void CheckLogin() {
        pd = MyAlertDialog.ShowProgress(this, "資料確認中...");
        pd.show();
        File dbDatabase = getApplicationContext().getDatabasePath("appbaby.db");
        boolean getCheckDB = sqlLocal.CheckDB(dbDatabase);
        boolean getCheckBaseTable = false;
        if (getCheckDB) {
            // 判斷基本資料表是否存在
            getCheckBaseTable = sqlLocal.CheckTable("ASC_MSTR");
            if (getCheckBaseTable) {
                // 將 Local 存的資料抓出來
                if (Display_Local_Data_To_Screen()) {
                    // 自動登錄
                    checkLoginData();
                } else {
                    pd.cancel();
                    DisplayToast("0筆");
                    return;
                }
            } else {
                pd.cancel();
                DisplayToast("沒找到表");
                return;
            }
        } else {// 沒找到DB 等待輸入
            pd.cancel();
            DisplayToast("首次開啟，尚無資料");
            return;
        }
    }

    /**
     * 將 Local 存的資料抓出來
     *
     * @return
     */
    private boolean Display_Local_Data_To_Screen() {
        // TODO 自動產生的方法 Stub
        List<Map<String, String>> wLocal_Data = sqlLocal.RunSqlDataTable(LocalSQLCode.SQLite_GetMSTRList());
        if (wLocal_Data != null) {
            Log.v(TAG, "取得本機資料 ID = " + wLocal_Data.get(0).get("USER_NAME") + "||PW = " + wLocal_Data.get(0).get("USER_PSWD"));
            UserMstr.userData = new UserData();
            UserMstr.userData.setUserName(wLocal_Data.get(0).get("USER_NAME"));
            UserMstr.userData.setUserPW(wLocal_Data.get(0).get("USER_PSWD"));
            idTxt.setText(UserMstr.userData.getUserName());
            pwTxt.setText(UserMstr.userData.getUserPW());
            if (UserMstr.userData.getUserPW().equals("")) {
                return false;
            } else {
                return true;
            }
        } else {
            DisplayToast("null");
            return false;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.loginBtn:
                Log.v(TAG, "點擊登入按鈕");
                checkLoginData();
                overridePendingTransition(android.R.anim.fade_in, R.anim.out_to_bottom);
                break;
        }
    }

    private void checkLoginData() {
        Log.v(TAG, "初步確認輸入的資料");
        if (idTxt.getText().toString().equals("")) {
            DisplayToast("请输入用戶名称");
            return;
        }
        if (pwTxt.getText().toString().equals("")) {
            DisplayToast("请输入密码");
            return;
        }
        pd = MyAlertDialog.ShowProgress(this, "登陆中...");
        pd.show();

        if (UserMstr.userData == null) UserMstr.userData = new UserData();
        UserMstr.userData.setUserName(idTxt.getText().toString());
        UserMstr.userData.setUserPW(pwTxt.getText().toString());
        connectWebLogin();
    }

    private void connectWebLogin() {
        Log.v(TAG, "連結至網路確認登入資料");
        /** 判斷網路 */
        if (!ComFun.checkNetworkState(this)) {
            MyAlertDialog.Show(this, "当前网络不可用，请设置后重试！");
            pd.cancel();
            return;
        }

        WebService.Login(null, UserMstr.userData.getUserName(),
                UserMstr.userData.getUserPW(), "Android", "1234", "1234", "1234", "1234", new WebService.WebCallback() {

                    @Override
                    public void CompleteCallback(String id, Object obj) {
                        // TODO Auto-generated method stub
                        if (obj == null) {
                            MyAlertDialog.Show(LoginActivity.this, "账号或密码错误！");
                            pd.cancel();
                            return;
                        }
                        JSONArray json = (JSONArray) obj;

                        if (json.length() == 0) {
                            MyAlertDialog.Show(LoginActivity.this, "无登录权限！");
                            pd.cancel();
                            return;
                        }
                        Log.v(TAG, "登入成功，取得會員 json:" + json.toString());
                        UserMstr.userData.setUserID(json.optJSONObject(0).optString("USER_ID"));
                        getUserInfo();
                    }
                });
    }

    private void getUserInfo() {
        WebService.GetUserInfo(null, UserMstr.userData.getUserName(),
                UserMstr.userData.getUserID(), new WebService.WebCallback() {

                    @Override
                    public void CompleteCallback(String id, Object obj) {
                        Log.v(TAG, "obj:" + obj);
                        // TODO Auto-generated method stub
                        if (obj == null) {
                            MyAlertDialog.Show(LoginActivity.this, "账号或密码错误！");
                            pd.cancel();
                            return;
                        }
                        JSONArray json = (JSONArray) obj;

                        if (json.length() == 0) {
                            MyAlertDialog.Show(LoginActivity.this, "无登录权限！");
                            pd.cancel();
                            return;
                        }
                        UserMstr.userData.setBaseInfoAry(json.optJSONObject(0).optJSONArray("BASE_INFO"));
                        UserMstr.userData.setChildinfoAry(json.optJSONObject(0).optJSONArray("CHILD_INFO"));
                        UserMstr.userData.setClassInfoAry(json.optJSONObject(0).optJSONArray("CLASS_INFO"));
                        UserMstr.userData.setPushInfoAry(json.optJSONObject(0).optJSONArray("PUSH_INFO"));
                        GetUserDataSuccess();
                    }
                });
    }

    /**
     * 取得使用者資訊成功
     */
    private void GetUserDataSuccess() {
        Log.v(TAG, "登入成功，將資料寫入本地資料庫!");
        /**
         * 回存SQL
         */
        if (sqlLocal.CheckTable("ASC_MSTR")) {
            sqlLocal.RunSqlNoQuery(LocalSQLCode.SQLite_RemoveTable("ASC_MSTR"));
        }
        sqlLocal.RunSqlNoQuery(LocalSQLCode.SQLite_CreatMSTR());
        sqlLocal.RunSqlNoQuery(
                LocalSQLCode.SQLite_InsertMSTR(
                        UserMstr.userData.getUserName(),
                        UserMstr.userData.getUserPW(),
                        UserMstr.userData.getUserID()));
        pd.cancel();
        finish();
    }
}