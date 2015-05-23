package com.app.Common;

import android.util.Log;

public class LogUtils {

	/**
	 * 是否显示    false 表示上线阶段，true 表示开发阶段
	 */
	private static final boolean isShow = true;
	
	public static void logw(String msg){
		if(isShow){
			Log.w("Ray", msg);
		}
	}
	public static void logi(String tag,String msg){
		if(isShow){
			Log.i(tag, msg);
		}
	}
	public static void logi(String msg){
		if(isShow){
			Log.i("Ray", msg);
		}
	}
	
	public static void logd(String tag,String msg){
		if(isShow){
			Log.d(tag, msg);
		}
	}
	public static void logd(String msg){
		if(isShow){
			Log.d("Ray", msg);
		}
	}
	
	public static void loge(String tag,String msg){
		if(isShow){
			Log.e(tag, msg);
		}
	}
	public static void loge(String msg){
		if(isShow){
			Log.e("Ray", msg);
		}
	}
	
}
