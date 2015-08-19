package com.app.AppBabySH;

import android.app.Application;

/**
 * Created by ray on 2015/5/14.
 */
public class GlobalVar extends Application {
    public Integer windowHeight,windowWidth;
    public final double[] momentsImageSizeAry={0.26,0.38,0.5};
    public Boolean loginAgain = false;
    public String apn = "P";
    public String momentPushNum, chatPushNum, newsPushNum, growthPushNum;
    public boolean selectClass = false;
}
