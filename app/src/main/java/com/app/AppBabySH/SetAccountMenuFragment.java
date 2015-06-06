package com.app.AppBabySH;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.UserMstr;

public class SetAccountMenuFragment extends BaseFragment {
    final private String TAG = "setAccountF";
    private MainTabActivity main;
    private View rootView;
    private SetAccountMenuFragment thisFragment;

    private ImageButton mImgbBack;
    private LinearLayout mLyNickName, mLyPW, mLyHead;
    private TextView mTxtCurrNickName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        rootView = inflater.inflate(R.layout.profile_set_acc_menu_fragment, container, false);
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
        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetAccBack);
        mLyNickName = (LinearLayout) rootView.findViewById(R.id.lySetAccNickName);
        mLyPW = (LinearLayout) rootView.findViewById(R.id.lySetAccPW);
        mLyHead = (LinearLayout) rootView.findViewById(R.id.lySetAccHead);
        mTxtCurrNickName = (TextView) rootView.findViewById(R.id.txtSetAccCurrNickName);
        mTxtCurrNickName.setText("" + UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("NIC_NAME"));
        mImgbBack.setOnClickListener(new onClick());
        mLyNickName.setOnClickListener(new onClick());
        mLyPW.setOnClickListener(new onClick());
        mLyHead.setOnClickListener(new onClick());
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbSetAccBack:
                    main.RemoveBottomNotAddTab(thisFragment);
                    break;
                case R.id.lySetAccNickName:
                    SetAccountNickNameFragment setNickNameF = new SetAccountNickNameFragment();
                    setNickNameF.onCallBack = new SetAccountNickNameFragment.CallBack() {
                        @Override
                        public void onBack() {
                            mTxtCurrNickName.setText("" + UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("NIC_NAME"));
                        }
                    };
                    main.OpenBottom(setNickNameF);
                    break;
                case R.id.lySetAccPW:
                    AccountForgetPWFragment accForgetPWF = new AccountForgetPWFragment();
                    main.OpenBottom(accForgetPWF);
                    break;
                case R.id.lySetAccHead:
                    SetAccountHeadFragment setHeadF = new SetAccountHeadFragment();
                    main.OpenBottom(setHeadF);
                    break;
            }
        }
    }
}