package com.app.AppBabySH;

import android.os.Bundle;
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

public class SetAccountAboutFragment extends BaseFragment {
    final private String TAG = "setAboutF";
    private MainTabActivity main;
    private View rootView;
    private SetAccountAboutFragment thisFragment;

    private ImageButton mImgbBack;
    private TextView mTxtVersion;
    private Button mBtnCheck;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
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

    private void initView() {
        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetAccAboutBack);
        mTxtVersion = (TextView) rootView.findViewById(R.id.txtSetAccAboutVersion);
        mBtnCheck = (Button) rootView.findViewById(R.id.btnSetAccAboutCheck);
        mTxtVersion.setText("版本号 ： v" + ComFun.getVersionName(getActivity()));
        mImgbBack.setOnClickListener(new onClick());
        mBtnCheck.setOnClickListener(new onClick());
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbSetAccAboutBack:
                    main.RemoveBottomNotAddTab(thisFragment);
                    break;
                case R.id.btnSetAccAboutCheck:
                    DisplayToast("功能尚未啟用");
                    break;
            }
        }
    }
}