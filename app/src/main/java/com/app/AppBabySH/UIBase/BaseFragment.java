package com.app.AppBabySH.UIBase;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.app.Common.ComFun;
import java.util.Calendar;

public class BaseFragment extends Fragment {
	private Toast toast;
	protected ProgressDialog progressDialog;
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onActivityCreated(savedInstanceState);
    }
	protected void DisplayToast(String Msg){
		if (toast==null) {
			if (getActivity()!=null ){
				toast = Toast.makeText(getActivity(), Msg, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
			}else{
				return;
			}
			
		}else{
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setText(Msg);
		}
		toast.show();
	}
	
	
	/**
	 * 讀取中對話視窗
	 * @param context
	 * @param msg
	 */
	protected void showLoadingDiaLog(Context context,String msg){
		//progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//ProgressDialog.show(context,"", msg, true);
		cancleDiaLog();
		progressDialog = MyAlertDialog.ShowProgress(context, msg);	
		if (progressDialog==null)return;
		progressDialog.show();
		if (!ComFun.checkNetworkState(context)) {
			MyAlertDialog.Show(context, "当前网络不可用，请设置后重试！");
			progressDialog.cancel();
			return;
		}
	}
	
	/**
	 * 關閉對話視窗
	 */
	protected void cancleDiaLog(){
		if (progressDialog!=null){
			progressDialog.cancel();
			progressDialog.dismiss();
			progressDialog=null;
		}
	}
	
	
	
	
	/**
	 * 展示日期 yyyy-MM-dd
	 * @param _returnView
	 */
	protected void showDate(final TextView _returnView ,Context _context){
		Calendar c = Calendar.getInstance(); //
		
		new DatePickerDialog(_context,
				// 绑定监听器  
				new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {  
				String month = String.valueOf(monthOfYear + 1);
				String date = String.valueOf(dayOfMonth);
				if ((monthOfYear+1)<10){
					month = "0"+month;
				}
				if (dayOfMonth<10){
					date = "0"+date;
				}
				_returnView.setText(year + "-"+month+"-"+date);
				_returnView.setTag(String.valueOf(year)+month+date);
						
			}  
		}  
		// 设置初始日期  
		, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
		.get(Calendar.DAY_OF_MONTH)).show();
	}

	
	/**
	 * 取得日期時間  Text(yyyy-MM-dd  24:00)  Tag(yyyyMMDDHHmm)
	 * @param _returnView
	 * @param _context
	 */
	protected void showDateTime(final TextView _returnView, final Context _context){
		final String[] getDateTime = new String[2];
		Calendar calendar = Calendar.getInstance(); //
	    int year = calendar.get(Calendar.YEAR);
	    int monthOfYear = calendar.get(Calendar.MONTH) ;
	    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
	    final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
	    final int minute = calendar.get(Calendar.MINUTE);
	    
		new DatePickerDialog(_context,
				// 绑定监听器  
				new DatePickerDialog.OnDateSetListener() {
			private boolean isShow= false;
			private String month,date;
			@Override
			public void onDateSet(DatePicker view, final int year,
					final int monthOfYear, final int dayOfMonth) {  
				month = String.valueOf(monthOfYear + 1);
				date = String.valueOf(dayOfMonth);
				if ((monthOfYear+1)<10)month = "0"+month;
				if (dayOfMonth<10)date = "0"+date;
				getDateTime[0]=year + "-"+month+"-"+date;		
				if (!isShow){
					isShow=true;
				new TimePickerDialog(_context, new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							String hh= String.valueOf(hourOfDay);
							String mm= String.valueOf(minute);
							if (hourOfDay<10)hh = "0"+hh;
							if (minute<10)mm = "0"+mm;
							getDateTime[1]=	hh+":"+mm;
							_returnView.setText(getDateTime[0]+"  "+getDateTime[1]);
							_returnView.setTag(String.valueOf(year)+month+date+
									";"+hh+mm);
						}
					},hourOfDay, minute,true ).show();
				}
				
			}  
		}  
		// 设置初始日期  
		, year,monthOfYear, dayOfMonth).show(); 
		
	}
	
	/**
	 * 展示狀態
	 * @param _returnView
	 */
	protected void showDialogList(final TextView _returnView ,final String[] _showList ,Context _context){
		AlertDialog.Builder builder= new AlertDialog.Builder(_context);
		builder.setTitle("选择");
		builder.setItems(_showList, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							_returnView.setText(_showList[which]);
							_returnView.setTag(which-1);
						}
					});
		builder.show();
	}

	public interface DialogCallBack{
		public void onEnter();
		public void onCancle();
	}
	/**
	 * 打開對話框
	 * @param title
	 * @param msg
	 * @param yesMsg
	 * @param noMsg
	 * @param context
	 * @param onCallbck
	 */
	protected void showDialog(String title ,String msg,String yesMsg,String noMsg,
			Context context,final DialogCallBack onCallbck){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setPositiveButton(noMsg, new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		    	   dialog.cancel();
		    	   onCallbck.onCancle();
		       }
		   });
		builder.setNegativeButton(yesMsg, new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		    	   onCallbck.onEnter();
		       }
		   });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public interface MoreCheckCallBack{
		public void onEnter(boolean[] boolary);
	}
	/**
	 * 多选选單
	 * @param context
	 * @param data
	 * @param checkdata
	 * @param callback
	 */
	protected void showMoreCheckDialog(Context context,String[] data,boolean[] checkdata
			,final MoreCheckCallBack callback){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final boolean[] _boolary=checkdata;
		builder.setTitle("请选择");
		builder.setMultiChoiceItems(data, checkdata,new OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				_boolary[which]=isChecked;
			}
		});
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onEnter(_boolary);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();			
			}
		});
		AlertDialog alert = builder.create();
		alert.show();			
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();	
		if (toast!=null){
			toast.cancel();
			toast=null;
		}
		if (progressDialog!=null){
			progressDialog.cancel();
			progressDialog.dismiss();
		}		
	}
	
	
}
