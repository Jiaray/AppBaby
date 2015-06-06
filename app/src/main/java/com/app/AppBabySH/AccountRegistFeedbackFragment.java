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
import com.app.Common.SQLite.LocalFun;
import com.app.Common.SQLite.LocalSQLCode;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ray on 2015/5/29.
 */
public class AccountRegistFeedbackFragment extends BaseFragment {
    final private String TAG = "AccountRegistFeedbackFragment";
    public String TYPE;
    private View rootView;
    private AccountRegistFeedbackFragment thisFragment;
    private LoginActivity loginA;
    private MainTabActivity mainA;
    private String actName;

    private EditText mEdtNickName, mEdtPhone, mEdtEmail, mEdtSchool, mEdtClass, mEdtStudent, mEdtActivation, mEdtContent;
    private Button mBtnCommit;
    private ImageButton mBtnBack;

    private String strSource, strNickName, strPhone, strEmail, strSchool, strClass, strStudent, strActivation, strContent, strUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.login_regist_feedback_fragment, container, false);
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
        mEdtNickName = (EditText) rootView.findViewById(R.id.edtRegFeedbackNickName);
        mEdtPhone = (EditText) rootView.findViewById(R.id.edtRegFeedbackPhone);
        mEdtEmail = (EditText) rootView.findViewById(R.id.edtRegFeedbackEmail);
        mEdtSchool = (EditText) rootView.findViewById(R.id.edtRegFeedbackSchool);
        mEdtClass = (EditText) rootView.findViewById(R.id.edtRegFeedbackClass);
        mEdtStudent = (EditText) rootView.findViewById(R.id.edtRegFeedbackStudent);
        mEdtActivation = (EditText) rootView.findViewById(R.id.edtRegFeedbackActivation);
        mEdtContent = (EditText) rootView.findViewById(R.id.edtRegFeedbackContent);
        mBtnBack = (ImageButton) rootView.findViewById(R.id.imgbRegFeedBackBack);
        mBtnCommit = (Button) rootView.findViewById(R.id.btnRegFeedBackCommit);
        mBtnBack.setOnClickListener(new FeedbackonClick());
        mBtnCommit.setOnClickListener(new FeedbackonClick());
        if (TYPE.equals("activation")) {
            mEdtActivation.setVisibility(View.VISIBLE);
        } else if (TYPE.equals("other")) {
            mEdtActivation.setVisibility(View.GONE);
        }
    }

    class FeedbackonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbRegFeedBackBack:
                    onBack();
                    break;
                case R.id.btnRegFeedBackCommit:
                    strSource = "激活碼無法註冊";
                    strNickName = mEdtNickName.getText().toString();
                    strPhone = mEdtPhone.getText().toString();
                    strSchool = mEdtSchool.getText().toString();
                    strClass = mEdtClass.getText().toString();
                    strEmail = mEdtEmail.getText().toString();
                    strStudent = mEdtStudent.getText().toString();
                    strActivation = TYPE.equals("activation") ? mEdtActivation.getText().toString() : "";
                    strContent = mEdtContent.getText().toString();
                    strUserID = UserMstr.userData.getUserID();
                    if (strNickName.equals("")) {
                        DisplayToast("称呼未填写!");
                        return;
                    } else if (strPhone.equals("")) {
                        DisplayToast("手机号码未填写!");
                        return;
                    } else if (strEmail.equals("")) {
                        DisplayToast("Email 未填写!");
                        return;
                    } else if (strStudent.equals("")) {
                        DisplayToast("学生未填写!");
                        return;
                    } else if (TYPE.equals("activation") && strActivation.equals("")) {
                        DisplayToast("激活码未填写!");
                        return;
                    } else if (strContent.equals("")) {
                        DisplayToast("意见内容未填写!");
                        return;
                    } else if (strSchool.equals("")) {
                        DisplayToast("学校未填写!");
                        return;
                    } else if (strClass.equals("")) {
                        DisplayToast("班级未填写!");
                        return;
                    } else {
                        sendFeedback();
                    }
                    break;
            }
        }
    }

    private void onBack() {
        if (actName.equals("MainTabActivity")) {
            mainA.RemoveBottomNotAddTab(thisFragment);
        } else {
            loginA.RemoveBottom(thisFragment);
        }
    }

    //  激活碼驗證
    private void sendFeedback() {
        showLoadingDiaLog(getActivity(), "提交中,请稍后!");
        WebService.SetFeedback(null,
                strSource, strNickName, strPhone,
                strSchool, strClass, strEmail,
                strStudent, strActivation, strContent, strUserID,
                new WebService.WebCallback() {

                    @Override
                    public void CompleteCallback(String id, Object obj) {
                        cancleDiaLog();

                        // TODO Auto-generated method stub
                        if (obj == null || !obj.equals("1")) {
                            showOKDiaLog(getActivity(), "提交失败！");
                            return;
                        } else {
                            showOKDiaLog(getActivity(), "提交完成");
                            onBack();
                        }
                    }
                });
    }
}
