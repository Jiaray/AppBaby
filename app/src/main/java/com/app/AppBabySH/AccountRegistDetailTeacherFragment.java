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
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.MyAlertDialog;
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
        loginA = (LoginActivity) getActivity();
        thisFragment = this;
        initView();
        return rootView;
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

    class  DetailonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbRegInfoTBack:
                    loginA.RemoveBottom(thisFragment);
                    break;
                case R.id.btnRegInfoTCommit:
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
            showLoadingDiaLog(getActivity(), "提交中,请稍后!");
            WebService.GetValidate(null, Activity_No, Validate_No, "T", strPw, strNick, new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    cancleDiaLog();
                    Log.i(TAG, "obj:" + obj);
                    // TODO Auto-generated method stub
                    if (obj == null) {
                        MyAlertDialog.Show(getActivity(), "提交失败！");
                        return;
                    }

                    MyAlertDialog.Show(getActivity(), "提交完成");
                    loginA.RemoveBottom(thisFragment);
                }
            });
        }
    }
}
