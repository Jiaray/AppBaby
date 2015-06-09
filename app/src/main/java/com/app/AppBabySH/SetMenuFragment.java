package com.app.AppBabySH;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.app.AppBabySH.activity.LoginActivity;
import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.ImageLoader;
import com.app.Common.SQLite.LocalFun;
import com.app.Common.SQLite.LocalSQLCode;
import com.app.Common.UserMstr;

import com.app.Common.FileCache;

public class SetMenuFragment extends BaseFragment {
    final private String TAG = "setF";
    private MainTabActivity main;
    private View rootView;
    private SetMenuFragment thisFragment;

    private ImageButton mImgbBack;
    private Button mBtnLogout;
    private LinearLayout mLySetAcc, mLyClearCache, mLyFeedback, mLyAbout;

    public CallBack onCallBack;
    public interface CallBack {
        public void onBack();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        onCallBack.onBack();
        main.AddTabHost();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        rootView = inflater.inflate(R.layout.profile_set_menu_fragment, container, false);
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
        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetMenuBack);
        mBtnLogout = (Button) rootView.findViewById(R.id.btnSetAccHeadCommit);
        mLySetAcc = (LinearLayout) rootView.findViewById(R.id.lySetMenuSetAcc);
        mLyClearCache = (LinearLayout) rootView.findViewById(R.id.lySetMenuClearCache);
        mLyFeedback = (LinearLayout) rootView.findViewById(R.id.lySetMenuFeedback);
        mLyAbout = (LinearLayout) rootView.findViewById(R.id.lySetMenuAbout);

        mImgbBack.setOnClickListener(new onClick());
        mBtnLogout.setOnClickListener(new onClick());
        mLySetAcc.setOnClickListener(new onClick());
        mLyClearCache.setOnClickListener(new onClick());
        mLyFeedback.setOnClickListener(new onClick());
        mLyAbout.setOnClickListener(new onClick());
    }
    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbSetMenuBack:
                    main.RemoveBottom(thisFragment);
                    break;
                case R.id.btnSetAccHeadCommit:
                    showDialog("确认", "确定要退出登录此帐号？", "确定", "取消", getActivity(), new DialogCallBack() {
                        @Override
                        public void onEnter() {
                            LocalFun.getInstance().RunSqlNoQuery(LocalSQLCode.SQLite_RemoveTable("ASC_MSTR"));
                            //Switch to config page
                            UserMstr.userData = null;
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                    break;
                case R.id.lySetMenuSetAcc:
                    SetAccountMenuFragment setAccF = new SetAccountMenuFragment();
                    main.OpenBottom(setAccF);
                    break;
                case R.id.lySetMenuClearCache:
                    ImageLoader.getInstance().clearCache();
                    if (FileCache.getInstance().clearAllData() == 1) {
                        DisplayToast("清除緩存!");
                    }
                    break;
                case R.id.lySetMenuFeedback:
                    AccountRegistFeedbackFragment feedbackF = new AccountRegistFeedbackFragment();
                    feedbackF.TYPE = "other";
                    main.OpenBottom(feedbackF);
                    break;
                case R.id.lySetMenuAbout:
                    SetAccountAboutFragment setAboutF = new SetAccountAboutFragment();
                    main.OpenBottom(setAboutF);
                    break;
            }
        }
    }
}