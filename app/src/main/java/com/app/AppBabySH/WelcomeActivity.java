package com.app.AppBabySH;


import com.app.AppBabySH.R;
import com.app.Common.LocalDB;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class WelcomeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_activity);
        new LocalDB(this);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
				
			}
		};
		new Handler().postDelayed(r, 2000);// 2秒
	
	}
}
