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

import com.app.AppBabySH.UIBase.BaseFragment;
import com.app.AppBabySH.UIBase.MyAlertDialog;
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

    private Button mBtnCommit, mBtnSendCaptcha;
    private EditText mEdtPhone, mEdtCaptcha, mEdtNewPW, mEdtCheckPW;
    private ImageButton mBtnBack;

    private String validateNo,strPw, strCheckPw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.login_forgetpw_fragment, container, false);
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
    }

    class ForGetPwonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbForgetPWBack:
                    loginA.RemoveBottom(thisFragment);
                    break;
                case R.id.btnForgetPWSendCaptcha:
                    if (mEdtPhone.equals("")) {
                        DisplayToast("手机号码尚未填写!");
                        return;
                    }
                    getCaptcha();
                    break;

                case R.id.btnForgetPWCommit:
                    Log.i(TAG,"Commit ForgetPW");
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
                    }else{
                        checkCaptcha();
                    }
                    break;
            }
        }
    }

    //  取得驗證碼
    private void getCaptcha() {
        showLoadingDiaLog(getActivity(), "验证码短信发送中,请稍后");
        WebService.GetPasswordValidate(null, mEdtPhone.getText().toString(), new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                cancleDiaLog();
                // TODO Auto-generated method stub
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "发送失败！");
                    return;
                }
                JSONArray json = (JSONArray) obj;
                validateNo = json.optJSONObject(0).optString("VALID_NO");
                SendCaptcha();
            }
        });
    }

    //  傳送驗證碼
    private void SendCaptcha() {
        if (mEdtPhone.length() < 11) {
            cancleDiaLog();
            MyAlertDialog.Show(getActivity(), "<测试> 验证码为:" + validateNo);
        } else {
            WebService.SendMessage(null, mEdtPhone.getText().toString(), validateNo, new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    cancleDiaLog();
                    Log.v(TAG, "obj:" + obj);
                    // TODO Auto-generated method stub
                    if (obj == null) {
                        MyAlertDialog.Show(getActivity(), "短信接口错误！");
                        return;
                    }
                }
            });
        }
    }

    //  確認驗證碼
    private void checkCaptcha() {
        if (validateNo.equals(mEdtCaptcha.getText().toString())) {

            WebService.SetPasswordReset(null, mEdtPhone.getText().toString(), validateNo, strPw, new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    // TODO Auto-generated method stub
                    if (obj == null || !obj.equals("1")) {
                        MyAlertDialog.Show(getActivity(), "密码重置错误！");
                        return;
                    }else{
                        MyAlertDialog.Show(getActivity(), "密码重置完成！");
                        loginA.RemoveBottom(thisFragment);
                    }
                }
            });
        } else {
            MyAlertDialog.Show(getActivity(), "验证码錯誤！");
        }
    }
}
