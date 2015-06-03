package com.app.AppBabySH.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

/**
 * @author yangyu
 *	功能描述：ViewPager適配器，用來綁定數據和view
 */
public class WelcomeViewPagerAdapter extends PagerAdapter {

    //界面列表
    private ArrayList<View> views;

    public WelcomeViewPagerAdapter(ArrayList<View> views){
        this.views = views;
    }

    /**
     * 獲得當前界面數
     */
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    /**
     * 初始化position位置的界面
     */
    @Override
    public Object instantiateItem(View view, int position) {

        ((ViewPager) view).addView(views.get(position), 0);

        return views.get(position);
    }

    /**
     * 判斷是否由對象生成界面
     */
    @Override
    public boolean isViewFromObject(View view, Object arg1) {
        return (view == arg1);
    }

    /**
     * 銷毀position位置的界面
     */
    @Override
    public void destroyItem(View view, int position, Object arg2) {
        ((ViewPager) view).removeView(views.get(position));
    }
}
