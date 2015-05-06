package com.app.AppBabySH.UIBase;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;

public class MyAlertDialog {
	
	
	/**CallBack 設定*/
	private static Builder _singleSpinner;
	
	private static Callback _callBack;
	public  interface Callback {
		public abstract void SingleSpinnerClick(int position);
		public abstract void SingleSpinnerCancle(int position);
		
	}	
	/** 多選 設定***/
	private static Builder _mustSpinner;
	private static boolean[] _boolary;
	private static MustCallBack _mCallback;
	public interface MustCallBack {
		public abstract void MustSpinnerOK(boolean[] _ary);
		public abstract void MustSpinnerCancle(int position);
	}
	/****************/
	
	private static ProgressDialog pd;
	
	public static void Show(Context context, String message) {
		Show(context, message, "检查", "确定");
	}

	public static void Show(Context context, String message, String title,
			String button) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton(button, null).show();
	}

	public static ProgressDialog ShowProgress(Context context,
			String message) {
		return ShowProgress(context, message, true, true);
	}

	public static ProgressDialog ShowProgress(Context context,
			String message, boolean indeterminate, boolean cancel) {
		if (pd!=null){
			pd.dismiss();
			pd=null;
		}
		if (context==null)return null;
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage(message);
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		return pd;
	}

	public static void Dismiss(){
		if (pd!=null){
			pd.dismiss();
			pd=null;
		}
	}

	/**
	 * 單選/下拉式選單 String[]
	 * @param context
	 * @param data
	 * @param spinnerback
	 * @return
	 */
	public static AlertDialog SingleSpinner(Context context,String[] data,Callback spinnerback){
		_callBack=spinnerback;
		if (_singleSpinner==null){
			_singleSpinner = new AlertDialog.Builder(context);
			_singleSpinner.setTitle("请选择");
			_singleSpinner.setItems(data, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					_callBack.SingleSpinnerClick(which);
					dialog.dismiss();
					_singleSpinner=null;
				}
			});
			_singleSpinner.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							_callBack.SingleSpinnerCancle(which);
							dialog.dismiss();
							_singleSpinner=null;
						}
					});
			AlertDialog alert = _singleSpinner.create();
			alert.show();
			return alert;
		}else{
			return null;
		}
	}

	/**
	 * 多選/下拉式選單
	 * @param context
	 * @param data
	 * @param checkdata
	 * @param spinnerback
	 * @return
	 */
	public static AlertDialog MustSpinner(Context context,String[] data,boolean[] checkdata,MustCallBack spinnerback){
		if (_mustSpinner==null){
			_mCallback=spinnerback;
			_boolary=checkdata;
			_mustSpinner = new AlertDialog.Builder(context);
			_mustSpinner.setTitle("请选择");
			_mustSpinner.setMultiChoiceItems(data, checkdata,new OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					_boolary[which]=isChecked;
				}
			});
			_mustSpinner.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					_mCallback.MustSpinnerOK(_boolary);
					dialog.dismiss();
					_mustSpinner=null;
				}
			});
			_mustSpinner.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					_mCallback.MustSpinnerCancle(which);
					dialog.dismiss();
					_mustSpinner=null;					
				}
			});
			AlertDialog alert = _mustSpinner.create();
			alert.show();			
			return alert;
		}else{
			return null;
		}
	}
	
	
	 
	
	
}
