package com.app.AppBabySH;

import com.app.AppBabySH.adapter.WelcomeViewPagerAdapter;
import com.app.Common.LocalDB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import lazylist.FileCache;

public class WelcomeActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    final private String TAG = "WelcomeA";
    private ImageView mImgFirstImg;
    private Button mBtnSkip, mBtnLogin, mBtnGuest;
    private ViewPager mVpStepPic;
    private LinearLayout mLyStepBall;

    private WelcomeViewPagerAdapter vpAdapter;//定義ViewPager適配器
    private ArrayList<View> views; //定義一個ArrayList來存放View
    private static final int[] pics = {R.drawable.nav_1, R.drawable.nav_2, R.drawable.nav_3};//引導圖片資源
    private ImageView[] points;//底部小點的圖片
    private int currentIndex;//記錄當前選中位置

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        new LocalDB(this);
        initView();
        initData();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (FileCache.getInstance().checkFileExistsByPath(FileCache.getInstance().getJsonPath() + "FirstIn.txt")) {
                    finish();
                } else {
                    mImgFirstImg.setVisibility(View.GONE);
                    mLyStepBall.setVisibility(View.VISIBLE);
                    mVpStepPic.setVisibility(View.VISIBLE);
                    mBtnSkip.setVisibility(View.VISIBLE);
                    Log.i("Ray", "進入引導動畫");
                }
            }
        };
        new Handler().postDelayed(r, 2000);// 2秒
    }

    /**
     * 初始化組件
     */
    private void initView() {
        mBtnLogin = (Button) findViewById(R.id.btnWelcomeLogin);
        mBtnGuest = (Button) findViewById(R.id.btnWelcomeGuest);
        mBtnSkip = (Button) findViewById(R.id.btnWelcomeSkip);
        mImgFirstImg = (ImageView) findViewById(R.id.imgWelcomeImg);
        mLyStepBall = (LinearLayout) findViewById(R.id.lyWelcomeStepBall);
        mBtnLogin.setVisibility(View.GONE);
        mBtnGuest.setVisibility(View.GONE);
        mBtnSkip.setVisibility(View.GONE);
        mImgFirstImg.setVisibility(View.VISIBLE);
        mLyStepBall.setVisibility(View.GONE);

        views = new ArrayList<View>();//實例化ArrayList對象
        mVpStepPic = (ViewPager) findViewById(R.id.vpWelcomeStepPic);//實例化ViewPager
        vpAdapter = new WelcomeViewPagerAdapter(views);//實例化ViewPager適配器
        mVpStepPic.setVisibility(View.GONE);

        mBtnLogin.setOnClickListener(this);
        mBtnGuest.setOnClickListener(this);
        mBtnSkip.setOnClickListener(this);
    }

    /**
     * 初始化數據
     */
    private void initData() {
        //定義一個布局並設置參數
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);

        //初始化引導圖片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            views.add(iv);
        }

        //設置數據
        mVpStepPic.setAdapter(vpAdapter);
        //設置監聽
        mVpStepPic.setOnPageChangeListener(this);

        //初始化底部小點
        initPoint();
    }

    //  初始化底部小點
    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lyWelcomeStepBall);
        points = new ImageView[pics.length];
        //循環取得小點圖片
        for (int i = 0; i < pics.length; i++) {
            //得到一個LinearLayout下面的每一個子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            //默認都設為灰色
            points[i].setEnabled(true);
            //給每個小點設置監聽
            points[i].setOnClickListener(this);
            //設置位置tag，方便取出與當前位置對應
            points[i].setTag(i);
        }

        //設置當面默認的位置
        currentIndex = 0;
        //設置為白色，即選中狀態
        points[currentIndex].setEnabled(false);
    }

    //  當滑動狀態改變時調用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    //  當當前頁面被滑動時調用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    //  當新的頁面被選中時調用
    @Override
    public void onPageSelected(int position) {
        //設置底部小點選中狀態
        setCurDot(position);
        if (position == 2) {
            mBtnLogin.setVisibility(View.VISIBLE);
            mBtnGuest.setVisibility(View.VISIBLE);
        } else {
            mBtnLogin.setVisibility(View.GONE);
            mBtnGuest.setVisibility(View.GONE);
        }
    }

    //  通過點擊事件來切換當前的頁面
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWelcomeGuest:
                Log.v(TAG, "遊客身分進入");
                enterLogin();
                break;
            case R.id.btnWelcomeLogin:
            case R.id.btnWelcomeSkip:
                Log.v(TAG,"登入");
                enterLogin();
                break;
            default:
                int position = (Integer) v.getTag();
                setCurView(position);
                setCurDot(position);
                break;
        }
    }

    //  設置當前頁面的位置
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        mVpStepPic.setCurrentItem(position);
    }

    //  設置當前的小點的位置
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);
        currentIndex = positon;
    }

    //  進入登入畫面
    private void enterLogin(){
        Intent intent = new Intent();
        intent.setClass(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        //设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }
}
