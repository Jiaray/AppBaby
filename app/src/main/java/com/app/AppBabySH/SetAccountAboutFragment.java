package com.app.AppBabySH;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.ComFun;
import com.app.Common.WebService;

import org.json.JSONArray;
import org.json.JSONObject;

public class SetAccountAboutFragment extends BaseFragment {
    final private String TAG = "setAboutF";
    private GlobalVar centerV;
    private MainTabActivity main;
    private View rootView;
    private SetAccountAboutFragment thisFragment;

    private ImageButton mImgbBack;
    private TextView mTxtVersion, mTxtTitle;
    private Button mBtnCheck;

    private Integer testCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisFragment = this;
        rootView = inflater.inflate(R.layout.profile_set_acc_about_fragment, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //共用宣告
        centerV = (GlobalVar) getActivity().getApplicationContext();
        main = (MainTabActivity) getActivity();
        setVersion();
    }


    private void initView() {
        testCount = 0;
        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetAccAboutBack);
        mTxtVersion = (TextView) rootView.findViewById(R.id.txtSetAccAboutVersion);
        mTxtTitle = (TextView) rootView.findViewById(R.id.txtSetAccAboutTitle);
        mBtnCheck = (Button) rootView.findViewById(R.id.btnSetAccAboutCheck);

        mImgbBack.setOnClickListener(new onClick());
        mBtnCheck.setOnClickListener(new onClick());
        mTxtTitle.setOnClickListener(new onClick());
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbSetAccAboutBack:
                    main.RemoveBottomNotAddTab(thisFragment);
                    break;
                case R.id.txtSetAccAboutTitle:
                    testCount++;
                    if (testCount >= 5) {
                        testCount = 0;
                        if (centerV.apn.equals("P")) {
                            centerV.apn = "E";
                        } else if (centerV.apn.equals("E")) {
                            centerV.apn = "D";
                        } else if (centerV.apn.equals("D")) {
                            centerV.apn = "P";
                        }

                        setVersion();
                    }
                    break;
                case R.id.btnSetAccAboutCheck:
                    //  判斷網路
                    if (!WebService.isConnected(getActivity())) return;

                    WebService.GetVersion(null, new WebService.WebCallback() {

                        @Override
                        public void CompleteCallback(String id, Object obj) {
                            CancelDiaLog();
                            try {
                                if (obj == null) {
                                    DisplayOKDiaLog("取得版本资讯错误！");
                                    return;
                                } else if (obj.equals("Success! No Data Return!")) {
                                    DisplayOKDiaLog("没有任何资讯！");
                                    return;
                                }
                                Log.i(TAG, "GetStudentHistory obj : " + obj);
                                JSONArray json = (JSONArray) obj;
                                if (json.length() == 0) {
                                    DisplayOKDiaLog("没有任何版本资讯！");
                                    return;
                                }
                                JSONObject tmpObj = json.optJSONObject(0);
                                if (ComFun.getVersionName(getActivity()).equals(tmpObj.optString("VERSION_SN"))) {
                                    DisplayToast("目前为最新版本");
                                } else {
                                    DisplayToast("最新版本为:" + tmpObj.optString("VERSION_SN"));
                                }
                            } catch (NumberFormatException e) {
                                DisplayOKDiaLog("GetStudentHistory 取得版本资讯失败！e:" + e);
                                e.printStackTrace();
                            }
                        }
                    });

                    break;
            }
        }
    }

    private void setVersion() {
        if (!centerV.apn.equals("P")) {
            mTxtVersion.setText("版本号 ： v" + ComFun.getVersionName(getActivity()) + " APN : " + centerV.apn);
        } else {
            mTxtVersion.setText("版本号 ： v" + ComFun.getVersionName(getActivity()));
        }
    }
}