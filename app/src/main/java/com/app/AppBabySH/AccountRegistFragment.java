package com.app.AppBabySH;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.app.AppBabySH.activity.LoginActivity;
import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.MyAlertDialog;
import com.app.Common.WebService;

import org.json.JSONArray;

/**
 * Created by ray on 2015/5/29.
 */
public class AccountRegistFragment extends BaseFragment {
    final private String TAG = "AccountRegistFragment";
    private View rootView;
    private AccountRegistFragment thisFragment;
    private LoginActivity loginA;
    private MainTabActivity mainA;
    private String actName;

    private EditText mEdtActivation, mEdtPhone, mEdtCaptcha;
    private Button mBtnNext, mBtnSendCaptcha,mBtnHowtoGet;
    private ImageButton mBtnBack;

    private Boolean activated = false;
    private String userType, validateNo;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(actName.equals("MainTabActivity"))mainA.AddTabHost();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.login_regist_fragment, container, false);
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
        mEdtActivation = (EditText) rootView.findViewById(R.id.edtRegActivation);
        mEdtPhone = (EditText) rootView.findViewById(R.id.edtRegPhone);
        mEdtCaptcha = (EditText) rootView.findViewById(R.id.edtRegCaptcha);
        mBtnHowtoGet = (Button) rootView.findViewById(R.id.btnRegHowToGet);
        mBtnBack = (ImageButton) rootView.findViewById(R.id.imgbRegBack);
        mBtnNext = (Button) rootView.findViewById(R.id.btnRegNext);
        mBtnSendCaptcha = (Button) rootView.findViewById(R.id.btnRegSendCaptcha);
        mBtnHowtoGet.setOnClickListener(new RegistPonClick());
        mBtnBack.setOnClickListener(new RegistPonClick());
        mBtnNext.setOnClickListener(new RegistPonClick());
        mBtnSendCaptcha.setOnClickListener(new RegistPonClick());
    }

    class RegistPonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRegSendCaptcha:
                    if (mEdtActivation.getText().toString().equals("")) {
                        DisplayToast("请输入激活码");
                        return;
                    }
                    if (mEdtPhone.getText().toString().equals("")) {
                        DisplayToast("请输入手机号码");
                        return;
                    }
                    activated = false;
                    checkActivation();
                    break;
                case R.id.imgbRegBack:
                    if(actName.equals("MainTabActivity")){
                        mainA.RemoveBottom(thisFragment);
                    }else{
                        loginA.RemoveBottom(thisFragment);
                    }
                    break;
                case R.id.btnRegNext:
                    checkCaptcha();
                    break;
                case R.id.btnRegHowToGet:
                    AccountRegistHowToGetFragment feedbackF = new AccountRegistHowToGetFragment();
                    if (actName.equals("MainTabActivity")) {
                        mainA.OpenBottom(feedbackF);
                    } else {
                        loginA.OpenBottom(feedbackF);
                    }
                    break;
            }
        }
    }

    //  激活碼驗證
    private void checkActivation() {
        showLoadingDiaLog(getActivity(), "激活码验证中,请稍后!");
        WebService.GetActivate(null, mEdtActivation.getText().toString(), mEdtPhone.getText().toString(), new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                cancleDiaLog();
                // TODO Auto-generated method stub
                if (obj == null) {
                    showOKDiaLog(getActivity(), "激活失败！");
                    return;
                }
                JSONArray json = (JSONArray) obj;
                if (json.optJSONObject(0).optString("STATE").equals("Y")) {
                    showLoadingDiaLog(getActivity(), "短信发送中,请稍后!");
                    activated = true;
                    userType = json.optJSONObject(0).optString("USER_TYPE");
                    validateNo = json.optJSONObject(0).optString("VALIDATE_NO");
                    SendCaptcha();
                } else {
                    Integer STATE = Integer.valueOf(json.optJSONObject(0).optString("STATE"));
                    switch (STATE) {
                        case 1:
                            showOKDiaLog(getActivity(), "無效電話！");
                            break;
                        case 2:
                            showOKDiaLog(getActivity(), "無效激活！");
                            break;
                        case 3:
                            showOKDiaLog(getActivity(), "已激活！");
                            break;
                        case 4:
                            showOKDiaLog(getActivity(), "沒有建班級！");
                            break;
                    }
                }
            }
        });
    }

    //  傳送驗證碼
    private void SendCaptcha() {
        if (mEdtPhone.length() < 11) {
            cancleDiaLog();
            showOKDiaLog(getActivity(), "<测试> 验证码为:" + validateNo);
        } else {
            WebService.SendMessage(null, mEdtPhone.getText().toString(), validateNo, new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    cancleDiaLog();
                    Log.v(TAG, "obj:" + obj);
                    // TODO Auto-generated method stub
                    if (obj == null) {
                        showOKDiaLog(getActivity(), "短信接口错误！");
                        return;
                    }

                }
            });
        }
    }

    //  確認驗證碼
    private void checkCaptcha() {
        if (activated && validateNo.equals(mEdtCaptcha.getText().toString())) {
            if (userType.equals("P")) {// 家長的詳細資料填寫
                readyParentDetail();
            } else {//    老師
                loginA.RemoveBottom(thisFragment);
                AccountRegistDetailTeacherFragment regDTF = new AccountRegistDetailTeacherFragment();
                regDTF.Activity_No = mEdtActivation.getText().toString();
                regDTF.Validate_No = validateNo;
                loginA.OpenBottom(regDTF);
            }
        } else {
            if (!activated) {
                showOKDiaLog(getActivity(), "尚未获取验证码！");
            } else {
                showOKDiaLog(getActivity(), "验证码錯誤！");
            }
        }
    }

    //  準備家長詳細資料填寫的畫面
    private void readyParentDetail() {
        showLoadingDiaLog(getActivity(), "验证码确认中,请稍后!");
        WebService.GetValidate(null, mEdtActivation.getText().toString(), validateNo, "P", "", "", new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                cancleDiaLog();
                Log.i(TAG, "obj:" + obj);
                // TODO Auto-generated method stub
                if (obj == null) {
                    showOKDiaLog(getActivity(), "验证错误！");
                    return;
                }
                JSONArray json = (JSONArray) obj;
                loginA.RemoveBottom(thisFragment);
                AccountRegistDetailParentFragment regDPF = new AccountRegistDetailParentFragment();
                regDPF.Activity_No = mEdtActivation.getText().toString();
                regDPF.Phone = mEdtPhone.getText().toString();
                regDPF.SchoolName = json.optJSONObject(0).optString("SCHOOL_NAME");
                regDPF.Province = json.optJSONObject(0).optString("PROVINCE");
                regDPF.City = json.optJSONObject(0).optString("CITY");
                regDPF.ClassName = json.optJSONObject(0).optString("CLASS_ENAME");
                regDPF.BabyName = json.optJSONObject(0).optString("STUDENT_NAME");
                loginA.OpenBottom(regDPF);
            }
        });
    }
}
