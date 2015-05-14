package com.app.AppBabySH;


import java.util.List;

import com.app.AppBabySH.UIBase.BaseFragment;
import com.app.Common.UserMstr;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

//for git test
public class MainTabActivity extends FragmentActivity {
    private static final String TAG = "MainTabActivity";
    private boolean isActive = false;
    private CenterVariable centerV;
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    private ViewGroup mTabHostParent;
    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {MomentsFragment.class, ChatFragment.class, NewsMainFragment.class, GrowthFragment.class, ProfileFragment.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_moments, R.drawable.tab_chat, R.drawable.tab_news,
            R.drawable.tab_growth, R.drawable.tab_profile};

    //Tab选项卡的文字
    private String mTextviewArray[] = {"Moments", "Chat", "News", "Growth", "Profile"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintab_layout);
        Log.v(TAG, "onCreate");

        //取得螢幕寬高
        centerV = (CenterVariable) getApplicationContext();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        centerV.windowHeight = metrics.heightPixels;
        centerV.windowWidth = metrics.widthPixels;
        initView();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (UserMstr.userData == null) {
            Log.v(TAG, "onResume : (UserMstr.userData = null)");
            Intent intent = new Intent(MainTabActivity.this, LoginActivity.class);
            startActivityForResult(intent, 0);// 打开新界面无法使用动画
        }
        if (!isActive) {
            Log.v(TAG, "onResume : (isActive = false) app 从后台唤醒，进入前台");
            //app 从后台唤醒，进入前台
            isActive = true;
            Intent intent = new Intent(MainTabActivity.this, WelcomeActivity.class);
            startActivity(intent);// 打开新界面无法使用动画
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = isBackground(this);
        Log.v(TAG, "onStop : isActive : " + isActive);
    }


    /**
     * 初始化组件
     */
    private void initView() {
        Log.v(TAG, "initView : 初始化组件");
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHostParent = (ViewGroup) mTabHost.getParent();
        //得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_background);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.maintab_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    /**
     * 開啟子頁面*
     */
    public void OpenBottom(BaseFragment subFrm) {
        CloseInput();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
//        transaction.setCustomAnimations(R.anim.in_from_right,
//                android.R.anim.fade_out);
        transaction.add(R.id.realtabcontent, subFrm);
        transaction.addToBackStack(subFrm.getClass().getName());
        transaction.commitAllowingStateLoss();
        if (mTabHostParent != null) {
            mTabHostParent.removeView(mTabHost);
        }
    }

    /**
     * 移除子頁面*
     */
    public void RemoveBottom(BaseFragment subFrm) {
        CloseInput();
        getSupportFragmentManager().popBackStack();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in,
                R.anim.out_to_bottom);
        transaction.remove(subFrm);
        transaction.commitAllowingStateLoss();
        if (mTabHostParent != null) {
            mTabHostParent.addView(mTabHost);
        }
    }

    /**
     * 關閉鍵盤
     */
    public void CloseInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //判断是否后台
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }
}
