package com.app.AppBabySH.activity;


import com.app.AppBabySH.AccountAgreeFragment;
import com.app.AppBabySH.AccountForgetPWFragment;
import com.app.AppBabySH.AccountRegistFragment;
import com.app.AppBabySH.R;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.MyAlertDialog;
import com.app.AppBabySH.item.ClassItem;
import com.app.Common.ComFun;
import com.app.Common.SQLite.LocalFun;
import com.app.Common.SQLite.LocalSQLCode;
import com.app.Common.UserData;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.app.Common.FileCache;

public class LoginActivity extends FragmentActivity implements OnClickListener {
    private static final String TAG = "LoginA";
    private EditText mTxtName, mTxtPW;
    private Button mBtnLoginEnter, mBtnReg, mBtnForget, mBtnGuest;
    private Toast toast;
    private ProgressDialog pd;
    public Boolean Agreement = false;

    //  按兩次返回建退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0 && (keyCode == KeyEvent.KEYCODE_BACK)) {   //確定按下退出鍵
            ConfirmExit(); //呼叫ConfirmExit()函數
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void ConfirmExit() {
        AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this); //創建訊息方塊
        ad.setTitle("離開");
        ad.setMessage("確定要離開?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() { //按"是",則退出應用程式
            public void onClick(DialogInterface dialog, int i) {
                UserMstr.closeApp = true;
                UserMstr.userData = null;
                LoginActivity.this.finish();//關閉activity
            }
        });

        ad.setNegativeButton("否", new DialogInterface.OnClickListener() { //按"否",則不執行任何操作
            public void onClick(DialogInterface dialog, int i) {
            }
        });
        ad.show();//顯示訊息視窗
    }

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.login_activity);
        initView();
    }


    private void initView() {
        if (!FileCache.getInstance().checkFileExistsByPath(FileCache.getInstance().getJsonPath() + "FirstIn.txt")) {
            FileCache.getInstance().savaJsonData("FirstIn", "0", false);
        }
        mTxtName = (EditText) findViewById(R.id.edtLoginName);
        mTxtPW = (EditText) findViewById(R.id.edtLoginPW);
        mBtnLoginEnter = (Button) findViewById(R.id.btnLoginEnter);
        mBtnReg = (Button) findViewById(R.id.btnLoginReg);
        mBtnForget = (Button) findViewById(R.id.btnLoginForget);
        mBtnGuest = (Button) findViewById(R.id.btnLoginGuest);
        mBtnLoginEnter.setOnClickListener(this);
        mBtnReg.setOnClickListener(this);
        mBtnForget.setOnClickListener(this);
        mBtnGuest.setOnClickListener(this);
        getUserInfoByDB();
    }

    //  確認 DB 資料
    private void getUserInfoByDB() {
        pd = MyAlertDialog.ShowProgress(this, "資料確認中...");
        pd.show();
        String dbName = "ShBaby";
        File dbDatabase = getApplicationContext().getDatabasePath(dbName);
        boolean getCheckDB = LocalFun.getInstance().CheckDB(dbDatabase);
        boolean getCheckBaseTable = false;
        boolean getCheckTableList = false;
        if (getCheckDB) {
            // 判斷基本資料表是否存在
            getCheckBaseTable = LocalFun.getInstance().CheckTable("ASC_MSTR");
            getCheckTableList = getUserData();
            if (getCheckBaseTable && getCheckTableList) {// 自動登錄
                if (Agreement) {
                    checkLoginData();
                } else {
                    pd.cancel();
                }
            } else {
                pd.cancel();
                DisplayToast("沒找到资料");
                return;
            }
        } else {// 沒找到DB 等待輸入
            pd.cancel();
            DisplayToast("首次开启，尚无资料");
            return;
        }
    }

    //  將 Local 存的資料抓出來
    private boolean getUserData() {
        // TODO 自動產生的方法 Stub
        List<Map<String, String>> wLocal_Data = LocalFun.getInstance().RunSqlDataTable(LocalSQLCode.SQLite_GetMSTRList());
        if (wLocal_Data != null) {
            Log.i(TAG, "wLocal_Data = " + wLocal_Data.get(0));
            Agreement = wLocal_Data.get(0).get("AGREEMENT").toString().equals("1") ? true : false;
            if (Agreement) {
                UserMstr.userData = new UserData();
                UserMstr.userData.setUserName(wLocal_Data.get(0).get("USER_NAME"));
                UserMstr.userData.setUserPW(wLocal_Data.get(0).get("USER_PSWD"));
                mTxtName.setText(UserMstr.userData.getUserName());
                mTxtPW.setText(UserMstr.userData.getUserPW());
            } else {
                mTxtName.setText(wLocal_Data.get(0).get("USER_NAME"));
                mTxtPW.setText(wLocal_Data.get(0).get("USER_PSWD"));
            }
            return true;
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
            case R.id.btnLoginEnter:
                Log.i(TAG, "點擊登入按鈕");
                checkLoginData();
                overridePendingTransition(android.R.anim.fade_in, R.anim.out_to_bottom);
                break;
            case R.id.btnLoginReg:
                Log.i(TAG, "註冊!");
                AccountRegistFragment regPF = new AccountRegistFragment();
                OpenBottom(regPF);
                break;
            case R.id.btnLoginForget:
                Log.i(TAG, "忘記密碼!");
                AccountForgetPWFragment forgetF = new AccountForgetPWFragment();
                OpenBottom(forgetF);
                break;
            case R.id.btnLoginGuest:
                break;
        }
    }

    //  APP 確認輸入的資料
    private void checkLoginData() {
        Log.i(TAG, "checkLoginData : 確認輸入的資料");
        if (mTxtName.getText().toString().equals("")) {
            DisplayToast("请输入用戶名称");
            return;
        } else if (mTxtPW.getText().toString().equals("")) {
            DisplayToast("请输入密码");
            return;
        } else {
            Log.i(TAG, "User Name = " + mTxtName.getText());
            Log.i(TAG, "User PW = " + mTxtPW.getText());
            Log.i(TAG, "Agreement = " + Agreement);

            pd = MyAlertDialog.ShowProgress(this, "登陆中...");
            pd.show();

            if (UserMstr.userData == null) {//非透過 DB 自動登入
                UserMstr.userData = new UserData();
                UserMstr.userData.setUserName(mTxtName.getText().toString());
                UserMstr.userData.setUserPW(mTxtPW.getText().toString());
                Agreement = false;
            }

            connectWebLogin();
        }
    }

    //  WEB 確認登入資料
    private void connectWebLogin() {
        Log.i(TAG, "connectWebLogin : 連結至網路確認登入資料");
        /** 判斷網路 */
        if (!ComFun.checkNetworkState(this)) {
            MyAlertDialog.Show(this, "当前网络不可用，请设置后重试！");
            pd.cancel();
            return;
        }

        WebService.Login(null, mTxtName.getText().toString(), mTxtPW.getText().toString(),
                "Android", "1234", "1234", "1234", "1234", new WebService.WebCallback() {

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
                        UserMstr.userData.setUserName(mTxtName.getText().toString());
                        UserMstr.userData.setUserPW(mTxtPW.getText().toString());
                        UserMstr.userData.setUserID(json.optJSONObject(0).optString("USER_ID"));
                        Log.i(TAG, "User ID = " + UserMstr.userData.getUserID());
                        getUserInfo();
                    }
                });
    }

    //  WEB 取得使用者詳細資料
    private void getUserInfo() {
        WebService.GetUserInfo(null, UserMstr.userData.getUserName(),
                UserMstr.userData.getUserID(), new WebService.WebCallback() {

                    @Override
                    public void CompleteCallback(String id, Object obj) {
                        JSONArray json = (JSONArray) obj;
                        if (json.optJSONObject(0).optJSONArray("BASE_INFO").length() == 0) {
                            MyAlertDialog.Show(LoginActivity.this, "查无使用者基本资讯！");
                            pd.cancel();
                            return;
                        }
                        UserMstr.userData.setBaseInfoAry(json.optJSONObject(0).optJSONArray("BASE_INFO"));
                        UserMstr.userData.setPushInfoAry(json.optJSONObject(0).optJSONArray("PUSH_INFO"));

                        //  整理班級資訊
                        UserMstr.userData.ClassAryList = new ArrayList<ClassItem>();
                        JSONArray ClassInfoAry;
                        String userType, tSchoolID, tSchoolName, tClassID, tClassName, tStudentID, tStudentName, tmpUserClass;
                        String[] classCompose;
                        userType = UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_TYPE");
                        int i, j;
                        if (userType.equals("P")) {//   身分為家長的班級列表
                            ClassInfoAry = json.optJSONObject(0).optJSONArray("CHILD_INFO");
                            i = -1;
                            while (++i < ClassInfoAry.length()) {
                                tSchoolID = ClassInfoAry.optJSONObject(i).optString("SCHOOL_ID");
                                tSchoolName = ClassInfoAry.optJSONObject(i).optString("SCHOOL_NAME");
                                tStudentID = ClassInfoAry.optJSONObject(i).optString("STUDENT_ID");
                                tStudentName = ClassInfoAry.optJSONObject(i).optString("STUDENT_NAME");
                                tmpUserClass = ClassInfoAry.optJSONObject(i).optString("USER_CLASS");
                                classCompose = tmpUserClass.split("\\|");
                                j = -1;
                                while (++j < classCompose.length) {//"C201504000003|C班|"
                                    if ((j % 2) != 0) {
                                        tClassID = classCompose[j - 1];
                                        tClassName = classCompose[j];
                                        addClassList(tSchoolID, tSchoolName, tClassID, tClassName, tStudentID, tStudentName);
                                    }
                                }
                            }
                        } else {//    身分為老師的班級列表
                            ClassInfoAry = json.optJSONObject(0).optJSONArray("CLASS_INFO");
                            tSchoolID = tSchoolName = tStudentID = tStudentName = "";
                            i = -1;
                            while (++i < ClassInfoAry.length()) {
                                tClassID = ClassInfoAry.optJSONObject(i).optString("CLASS_ID");
                                tClassName = ClassInfoAry.optJSONObject(i).optString("CLASS_NAME");
                                addClassList(tSchoolID, tSchoolName, tClassID, tClassName, tStudentID, tStudentName);
                            }
                        }

                        GetUserDataSuccess();
                    }
                });
    }

    //  取得使用者資訊成功
    private void GetUserDataSuccess() {
        Log.i(TAG, "登入成功，將資料寫入本地資料庫!");
        saveUserInfo2DB();
        pd.cancel();
        if (!Agreement) {
            AccountAgreeFragment regAF = new AccountAgreeFragment();
            OpenBottom(regAF);
        } else {
            finish();
        }
    }

    //  儲存帳號資訊
    public void saveUserInfo2DB() {
        //  創新表
        if (LocalFun.getInstance().CheckTable("ASC_MSTR")) {
            LocalFun.getInstance().RunSqlNoQuery(LocalSQLCode.SQLite_RemoveTable("ASC_MSTR"));
        }
        LocalFun.getInstance().RunSqlNoQuery(LocalSQLCode.SQLite_CreatMSTR());

        //  新增唯一一筆使用者資訊
        LocalFun.getInstance().RunSqlNoQuery(
                LocalSQLCode.SQLite_InsertMSTR(
                        UserMstr.userData.getUserName(),
                        UserMstr.userData.getUserPW(),
                        Agreement ? "1" : "0",
                        UserMstr.userData.getUserID()));
    }

    //  確認該帳號同意協議
    public void changeData2DB() {
        LocalFun.getInstance().RunSqlNoQuery(
                LocalSQLCode.SQLite_UpdateTableData(
                        "ASC_MSTR",
                        "AGREEMENT", "0",
                        "AGREEMENT", "1"));
    }

    /**
     * 班級列表中加入班級項
     *
     * @param $schoolID
     * @param $schoolName
     * @param $classID
     * @param $className
     * @param $studentID
     * @param $studentName
     */
    private void addClassList(String $schoolID, String $schoolName, String $classID, String $className, String $studentID, String $studentName) {
        ClassItem classItem = new ClassItem();
        classItem.SCHOOL_ID = $schoolID;
        classItem.SCHOOL_NAME = $schoolName;
        classItem.CLASS_ID = $classID;
        classItem.CLASS_NAME = $className;
        classItem.STUDENT_ID = $studentID;
        classItem.STUDENT_NAME = $studentName;
        UserMstr.userData.ClassAryList.add(classItem);
    }

    //  開啟子頁面
    public void OpenBottom(BaseFragment subFrm) {
        CloseInput();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.in_from_right,android.R.anim.fade_out);
        transaction.add(R.id.flyLoginContent, subFrm);
        transaction.addToBackStack(subFrm.getClass().getName());
        transaction.commitAllowingStateLoss();
    }

    //  移除子頁面
    public void RemoveBottom(BaseFragment subFrm) {
        CloseInput();
        getSupportFragmentManager().popBackStack();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, R.anim.out_to_bottom);
        transaction.remove(subFrm);
        transaction.commitAllowingStateLoss();
    }

    //  關閉鍵盤
    public void CloseInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * @param Msg
     */
    protected void DisplayToast(String Msg) {
        if (toast == null) {
            toast = Toast.makeText(this, Msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(Msg);
        }
        toast.show();
    }
}