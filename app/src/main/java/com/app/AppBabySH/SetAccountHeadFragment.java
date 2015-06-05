package com.app.AppBabySH;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;

public class SetAccountHeadFragment extends BaseFragment {
    final private String TAG = "setAccountHeadF";
    private MainTabActivity main;
    private View rootView;
    private SetAccountHeadFragment thisFragment;

    private ImageButton mImgbBack;
    private ImageView mImgUserHead;
    private Button mBtnCommit,mBtnChoose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        rootView = inflater.inflate(R.layout.profile_set_acc_head_fragment, container, false);
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
        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetAccHeadBack);
        mBtnCommit = (Button) rootView.findViewById(R.id.btnSetAccHeadCommit);
        mBtnChoose = (Button) rootView.findViewById(R.id.btnSetAccHeadChoose);
        mImgUserHead = (ImageView) rootView.findViewById(R.id.imgMomentsHeaderUserHead);
        mImgbBack.setOnClickListener(new onClick());
        mBtnCommit.setOnClickListener(new onClick());
        mBtnChoose.setOnClickListener(new onClick());
    }
    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbSetAccHeadBack:
                    main.RemoveBottomNotAddTab(thisFragment);
                    break;
                case R.id.btnSetAccHeadChoose:
                    break;
                case R.id.btnSetAccHeadCommit:
                    break;
            }
        }
    }
}