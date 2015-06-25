package com.app.AppBabySH;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.AppBabySH.activity.LoginActivity;
import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.SQLite.LocalFun;
import com.app.Common.SQLite.LocalSQLCode;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;

/**
 * Created by ray on 2015/5/29.
 */
public class AccountForgetPWFragment extends BaseFragment {
    final private String TAG = "accForgetPW";
    private View rootView;
    private AccountForgetPWFragment thisFragment;
    private LoginActivity loginA;
    private MainTabActivity mainA;
    private String actName;

    private LinearLayout mLyPhone, mLyCaptcha, mLySendCaptcha, mLyNewPw, mLyCheckNewPw;
    private TextView mTxtTitle;
    private Button mBtnCommit, mBtnSendCaptcha;
    private EditText mEdtPhone, mEdtCaptcha, mEdtNewPW, mEdtCheckPW;
    private ImageButton mBtnBack;

    private String validateNo, strPw, strCheckPw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.login_forgetpw_fragment, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (getActivity().getLocalClassName().equals("activity.LoginActivity")) {
            loginA = (LoginActivity) getActivity();
            actName = "LoginActivity";
        } else {
            mainA = (MainTabActivity) getActivity();
            actName = "MainTabActivity";
        }
        thisFragment = this;
        initView();
        return rootView;
    }

    private void initView() {
        mLyPhone = (LinearLayout) rootView.findViewById(R.id.lyForgetPWPhone);
        mLyCaptcha = (LinearLayout) rootView.findViewById(R.id.lyForgetPWCaptcha);
        mLySendCaptcha = (LinearLayout) rootView.findViewById(R.id.lyForgetPWSendCaptcha);
        mLyNewPw = (LinearLayout) rootView.findViewById(R.id.lyForgetPWNewPw);
        mLyCheckNewPw = (LinearLayout) rootView.findViewById(R.id.lyForgetPWCheckNewPw);
        mTxtTitle = (TextView) rootView.findViewById(R.id.txtForgetPWTitle);
        mEdtPhone = (EditText) rootView.findViewById(R.id.edtForgetPWPhone);
        mEdtCaptcha = (EditText) rootView.findViewById(R.id.edtForgetPWCaptcha);
        mEdtNewPW = (EditText) rootView.findViewById(R.id.edtForgetPWNewPw);
        mEdtCheckPW = (EditText) rootView.findViewById(R.id.edtForgetPWCheckNewPw);
        mEdtNewPW.setTransformationMethod(PasswordTransformationMethod.getInstance());//隱藏密碼
        mEdtCheckPW.setTransformationMethod(PasswordTransformationMethod.getInstance());//隱藏密碼
        mBtnBack = (ImageButton) rootView.findViewById(R.id.imgbForgetPWBack);
        mBtnSendCaptcha = (Button) rootView.findViewById(R.id.btnForgetPWSendCaptcha);
        mBtnCommit = (Button) rootView.findViewById(R.id.btnForgetPWCommit);
        mBtnBack.setOnClickListener(new ForGetPwonClick());
        mBtnSendCaptcha.setOnClickListener(new ForGetPwonClick());
        mBtnCommit.setOnClickListener(new ForGetPwonClick());
        if (actName.equals("LoginActivity")) {
            mTxtTitle.setText("忘记密码");
            mLyPhone.setVisibility(View.VISIBLE);
            mLyCaptcha.setVisibility(View.VISIBLE);
            mLySendCaptcha.setVisibility(View.VISIBLE);
        } else {
            mTxtTitle.setText("设置新密码");
            mEdtPhone.setText(UserMstr.userData.getUserName());
            mLyPhone.setVisibility(View.GONE);
            mLyCaptcha.setVisibility(View.GONE);
            mLySendCaptcha.setVisibility(View.GONE);
        }
    }

    class ForGetPwonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbForgetPWBack:
                    if (actName.equals("MainTabActivity")) {
                        mainA.RemoveBottomNotAddTab(thisFragment);
                    } else {
                        loginA.RemoveBottom(thisFragment);
                    }
                    break;
                case R.id.btnForgetPWSendCaptcha:
                    //  判斷網路
                    if (!WebService.isConnected(getActivity())) return;
                    if (mEdtPhone.equals("")) {
                        DisplayToast("手机号码尚未填写!");
                        return;
                    }
                    getCaptcha();
                    break;

                case R.id.btnForgetPWCommit:
                    //  判斷網路
                    if (!WebService.isConnected(getActivity())) return;
                    Log.i(TAG, "Commit ForgetPW");
                    strPw = mEdtNewPW.getText().toString();
                    strCheckPw = mEdtCheckPW.getText().toString();
                    if (mEdtCaptcha.getText().equals("")) {
                        DisplayToast("验证码尚未填写!");
                        return;
                    } else if (strPw.equals("")) {
                        DisplayToast("密码尚未填写!");
                    } else if (!strPw.equals(strCheckPw)) {
                        DisplayToast("密码不一致!");
                        return;
                    } else {
                        if (actName.equals("LoginActivity")) {
                            checkCaptcha();
                        } else {
                            getCaptcha();
                        }
                    }
                    break;
            }
        }
    }

    //  取得驗證碼
    private void getCaptcha() {
        if (actName.equals("LoginActivity")) {
            DisplayLoadingDiaLog("验证码短信发送中,请稍后...");
        } else {
            DisplayLoadingDiaLog("设置新密码中,请稍后...");
        }
        WebService.GetPasswordValidate(null, mEdtPhone.getText().toString(), new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                if (actName.equals("LoginActivity")) CancelDiaLog();
                // TODO Auto-generated method stub
                if (obj == null) {
                    DisplayOKDiaLog("取得验证码失败！");
                    return;
                }
                JSONArray json = (JSONArray) obj;
                validateNo = json.optJSONObject(0).optString("VALID_NO");
                if (actName.equals("LoginActivity")) {
                    SendCaptcha();
                } else {
                    checkCaptcha();
                }
            }
        });
    }

    //  傳送驗證碼
    private void SendCaptcha() {
        if (mEdtPhone.length() < 11) {
            CancelDiaLog();
            DisplayOKDiaLog("<测试> 验证码为:" + validateNo);
        } else {
            WebService.SendMessage(null, mEdtPhone.getText().toString(), validateNo, new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    CancelDiaLog();
                    Log.v(TAG, "obj:" + obj);
                    // TODO Auto-generated method stub
                    if (obj == null) {
                        DisplayOKDiaLog("短信接口错误！");
                        return;
                    }
                }
            });
        }
    }

    //  確認驗證碼
    private void checkCaptcha() {
        if (validateNo.equals(mEdtCaptcha.getText().toString()) || !actName.equals("LoginActivity")) {
            WebService.SetPasswordReset(null, mEdtPhone.getText().toString(), validateNo, strPw, new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    // TODO Auto-generated method stub
                    if (obj == null || !obj.equals("1")) {
                        DisplayOKDiaLog("密码重置错误！");
                        return;
                    } else {
                        DisplayOKDiaLog("密码重置完成！");
                        LocalFun.getInstance().RunSqlNoQuery(
                                LocalSQLCode.SQLite_UpdateTableData(
                                        "ASC_MSTR",
                                        "USER_PSWD", UserMstr.userData.getUserPW(),
                                        "USER_PSWD", strPw));

                        if (actName.equals("MainTabActivity")) {
                            mainA.RemoveBottomNotAddTab(thisFragment);
                        } else {
                            loginA.RemoveBottom(thisFragment);
                        }
                    }
                }
            });
        } else {
            DisplayOKDiaLog("验证码錯誤！");
        }
    }
}
