package com.app.AppBabySH;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.AppBabySH.item.NewsItem;
import com.app.Common.MyAlertDialog;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetAccountNickNameFragment extends BaseFragment {
    final private String TAG = "setAccountNickNameF";
    private MainTabActivity main;
    private View rootView;
    private SetAccountNickNameFragment thisFragment;

    private ImageButton mImgbBack;
    private Button mBtnCommit;
    private TextView mTxtCurr;
    private EditText mEdtNew;

    public CallBack onCallBack;
    public interface CallBack {
        public void onBack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onCallBack != null) onCallBack.onBack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        rootView = inflater.inflate(R.layout.profile_set_acc_nickname_fragment, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initView();
        return rootView;
    }

    private void initView() {
        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetAccNickNameBack);
        mBtnCommit = (Button) rootView.findViewById(R.id.btnSetAccNickNameCommit);
        mTxtCurr = (TextView) rootView.findViewById(R.id.txtSetAccNickNameCurr);
        mEdtNew = (EditText) rootView.findViewById(R.id.edtSetAccNickNameNew);
        mTxtCurr.setText("原来的昵称：" + UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("NIC_NAME"));
        mImgbBack.setOnClickListener(new onClick());
        mBtnCommit.setOnClickListener(new onClick());
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbSetAccNickNameBack:
                    main.RemoveBottomNotAddTab(thisFragment);
                    break;
                case R.id.btnSetAccNickNameCommit:
                    if (mEdtNew.getText().toString().equals("")) {
                        DisplayToast("尚未输入新的昵称");
                        return;
                    }
                    setNick();
                    break;
            }
        }
    }

    private void setNick() {
        showLoadingDiaLog(getActivity(), "昵称设置中，请稍后...");
        WebService.SetChangeNick(null, UserMstr.userData.getUserID(), UserMstr.userData.getUserName(), mEdtNew.getText().toString(), new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj)  {
                cancleDiaLog();
                if (obj == null) {
                    showOKDiaLog(getActivity(), "设置昵称错误！");
                    return;
                }
                if(obj.toString().equals("1")){
                    showOKDiaLog(getActivity(), "设置昵称完成！");
                    JSONObject tmpObj = UserMstr.userData.getBaseInfoAry().optJSONObject(0);
                    try {
                        tmpObj.put("NIC_NAME",mEdtNew.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    main.RemoveBottomNotAddTab(thisFragment);
                }else{
                    showOKDiaLog(getActivity(), "设置昵称失败！");
                }
            }
        });
    }
}