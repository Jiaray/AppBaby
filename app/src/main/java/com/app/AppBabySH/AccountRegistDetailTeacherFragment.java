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

import com.app.AppBabySH.activity.LoginActivity;
import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.WebService;

/**
 * Created by ray on 2015/5/29.
 */
public class AccountRegistDetailTeacherFragment extends BaseFragment {
    final private String TAG = "AccountRegistDetailTeacherFragment";
    public String Activity_No, Validate_No;

    private View rootView;
    private AccountRegistDetailTeacherFragment thisFragment;
    private LoginActivity loginA;
    private MainTabActivity mainA;
    private String actName;

    private EditText mEdtNickname, mEdtPW, mEdtCheckPW;
    private Button mBtnCommit;
    private ImageButton mBtnBack;

    private String strNick, strPw, strCheckPw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.login_regist_detail_t_fragment, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        thisFragment = this;
        initView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity().getLocalClassName().equals("activity.LoginActivity")) {
            loginA = (LoginActivity) getActivity();
            actName = "LoginActivity";
        } else {
            mainA = (MainTabActivity) getActivity();
            actName = "MainTabActivity";
        }
    }


    private void initView() {
        mEdtNickname = (EditText) rootView.findViewById(R.id.edtRegInfoTNickname);
        mEdtPW = (EditText) rootView.findViewById(R.id.edtRegInfoTPW);
        mEdtCheckPW = (EditText) rootView.findViewById(R.id.edtRegInfoTCheckPW);
        mEdtPW.setTransformationMethod(PasswordTransformationMethod.getInstance());//隱藏密碼
        mEdtCheckPW.setTransformationMethod(PasswordTransformationMethod.getInstance());//隱藏密碼

        mBtnBack = (ImageButton) rootView.findViewById(R.id.imgbRegInfoTBack);
        mBtnCommit = (Button) rootView.findViewById(R.id.btnRegInfoTCommit);
        mBtnBack.setOnClickListener(new DetailonClick());
        mBtnCommit.setOnClickListener(new DetailonClick());

        //mEdtCheckPW.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//顯示密碼
    }

    class DetailonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbRegInfoTBack:
                    onBack();
                    break;
                case R.id.btnRegInfoTCommit:
                    //  判斷網路
                    if (!WebService.isConnected(getActivity())) return;
                    commitData();
                    break;
            }
        }
    }

    //  提交
    private void commitData() {
        strNick = mEdtNickname.getText().toString();
        strPw = mEdtPW.getText().toString();
        strCheckPw = mEdtCheckPW.getText().toString();
        if (strNick.equals("") || strPw.equals("") || strCheckPw.equals("")) {
            DisplayToast("尚有资料未填写完成");
            return;
        } else if (!strPw.equals(strCheckPw)) {
            DisplayToast("密码不一致");
            return;
        } else {
            DisplayLoadingDiaLog("提交中,请稍后!");
            WebService.GetValidate(null, Activity_No, Validate_No, "T", strPw, strNick, new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    CancelDiaLog();
                    Log.i(TAG, "GetValidate obj:" + obj);
                    try {
                        if (obj == null) {
                            DisplayOKDiaLog("提交失败！");
                            return;
                        }

                        DisplayOKDiaLog("提交完成");
                    } catch (Exception e) {
                        DisplayOKDiaLog("提交失败！ e:" + e);
                        e.printStackTrace();
                    }
                    onBack();
                }
            });
        }
    }

    private void onBack() {
        if (actName.equals("MainTabActivity")) {
            mainA.RemoveBottomNotAddTab(thisFragment);
        } else {
            loginA.RemoveBottom(thisFragment);
        }
    }
}
