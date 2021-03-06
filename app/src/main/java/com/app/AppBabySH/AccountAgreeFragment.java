package com.app.AppBabySH;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.app.AppBabySH.activity.LoginActivity;
import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.WebService;

/**
 * Created by ray on 2015/5/29.
 */
public class AccountAgreeFragment extends BaseFragment {
    private View rootView;
    private AccountAgreeFragment thisFragment;
    private LoginActivity loginA;

    private CheckBox mCkbOK;
    private Button mBtnOK;
    private ImageButton mBtnBack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.login_agree_fragment, container, false);
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
        loginA = (LoginActivity) getActivity();
    }

    private void initView() {
        mBtnBack = (ImageButton) rootView.findViewById(R.id.imgbAgreeBack);
        mBtnOK = (Button) rootView.findViewById(R.id.btnAgreeOK);
        mBtnOK.setVisibility(View.GONE);
        mCkbOK = (CheckBox) rootView.findViewById(R.id.ckbAgreeOK);
        mBtnBack.setOnClickListener(new AgreeonClick());
        mBtnOK.setOnClickListener(new AgreeonClick());
        mCkbOK.setOnClickListener(new AgreeonClick());
    }

    class AgreeonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbAgreeBack:
                    loginA.RemoveBottom(thisFragment);
                    break;
                case R.id.btnAgreeOK:
                    //  判斷網路
                    if (!WebService.isConnected(getActivity())) return;
                    if (mCkbOK.isChecked()) {
                        loginA.changeData2DB();
                        getActivity().finish();
                    }
                    break;
                case R.id.ckbAgreeOK:
                    mBtnOK.setVisibility(mCkbOK.isChecked() ? View.VISIBLE : View.GONE);
                    break;
            }
        }
    }
}
