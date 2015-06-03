package com.app.AppBabySH;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.Common.LocalFun;
import com.app.Common.LocalSQLCode;
import com.app.Common.UserData;
import com.app.Common.UserMstr;

import lazylist.FileCache;
import lazylist.ImageLoader;

public class ProfileFragment extends Fragment{
	private MainTabActivity main;
	private View rootView;

	private Toast toast;

	private Button mBtnLogout,mBtnClear;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		//共用宣告
		main = (MainTabActivity) getActivity();
		rootView = inflater.inflate(R.layout.profile_fragment, container, false);
		initView();
		return rootView;
	}

	private void initView() {
		mBtnLogout = (Button) rootView.findViewById(R.id.btnProfileLogout);
		mBtnClear = (Button) rootView.findViewById(R.id.btnProfileClearCache);

		mBtnLogout.setOnClickListener(new onClick());
		mBtnClear.setOnClickListener(new onClick());
	}

	class onClick implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.btnProfileLogout:
					LocalFun.getInstance().RunSqlNoQuery(LocalSQLCode.SQLite_RemoveTable("ASC_MSTR"));
					//getActivity().setContentView(R.layout.login_activity);
					//Switch to config page
					UserMstr.userData = null;
					Intent intent = new Intent();
					intent.setClass(getActivity(), LoginActivity.class);
					startActivity(intent);
					break;
				case R.id.btnProfileClearCache:
					if(FileCache.getInstance().clearAllData() == 1){
						DisplayToast("清除緩存!");
					}
					break;
			}
		}
	}

	protected void DisplayToast(String Msg) {
		if (toast == null) {
			toast = Toast.makeText(getActivity(), Msg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setText(Msg);
		}
		toast.show();
	}
}