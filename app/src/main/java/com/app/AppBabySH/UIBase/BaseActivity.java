package com.app.AppBabySH.UIBase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class BaseActivity extends Activity {
	private Toast toast;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * @param Msg
	 */
	protected void DisplayToast(String Msg) {
		if (toast == null) {
			toast = Toast.makeText(this, Msg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setText(Msg);
		}
		toast.show();
	}
	
	protected  void CloseInput() {
		View view = getWindow().peekDecorView();
		
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	
	

	@Override
	public void onPause() {
		if (this.getCurrentFocus() != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(
					this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		
	}
	
	
}