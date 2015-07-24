package com.app.AppBabySH;

import android.os.Bundle;
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
import com.app.Common.UserMstr;
import com.app.Common.WebService;

/**
 * Created by ray on 2015/5/29.
 */
public class AccountRegistHowToGetFragment extends BaseFragment {
    final private String TAG = "AccountRegistHowToGetF";
    public String TYPE;
    private View rootView;
    private AccountRegistHowToGetFragment thisFragment;
    private LoginActivity loginA;
    private MainTabActivity mainA;
    private String actName;

    private Button mBtnFeedBack;
    private ImageButton mBtnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.login_regist_howtoget_fragment, container, false);
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
        mBtnBack = (ImageButton) rootView.findViewById(R.id.imgbRegHowToGetBack);
        mBtnFeedBack = (Button) rootView.findViewById(R.id.btnRegHowToGetFeedBack);
        mBtnBack.setOnClickListener(new HowtoGetonClick());
        mBtnFeedBack.setOnClickListener(new HowtoGetonClick());
    }

    class HowtoGetonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbRegHowToGetBack:
                    onBack();
                    break;
                case R.id.btnRegHowToGetFeedBack:
                    AccountRegistFeedbackFragment feedbackF = new AccountRegistFeedbackFragment();
                    feedbackF.TYPE = "activation";
                    if (actName.equals("MainTabActivity")) {
                        mainA.OpenBottom(feedbackF);
                    } else {
                        loginA.OpenBottom(feedbackF);
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
}
