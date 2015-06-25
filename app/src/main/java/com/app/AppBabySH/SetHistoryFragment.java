package com.app.AppBabySH;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.ComFun;
import com.app.Common.WebService;

import org.json.JSONArray;
import org.json.JSONObject;

public class SetHistoryFragment extends BaseFragment {
    final private String TAG = "setHistoryF";
    public String School_ID, Student_ID;
    private MainTabActivity main;
    private View rootView;
    private SetHistoryFragment thisFragment;
    private LayoutInflater _inflater;

    private ImageButton mImgbBack;
    private TableLayout mTblCurr, mTblOld;

    @Override
    public void onDestroy() {
        super.onDestroy();
        main.AddTabHost();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        _inflater = inflater;
        rootView = inflater.inflate(R.layout.profile_set_history_fragment, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initView();
        //  判斷網路
        if (WebService.isConnected(getActivity())) getData();
        return rootView;
    }

    private void initView() {
        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetHistoryBack);
        mTblCurr = (TableLayout) rootView.findViewById(R.id.tblCurrState);
        mTblOld = (TableLayout) rootView.findViewById(R.id.tblOldState);
        mImgbBack.setOnClickListener(new onClick());
    }

    private void getData() {
        Log.i(TAG, "SchoolID:" + School_ID + " StudentID:" + Student_ID);
        DisplayLoadingDiaLog("资料读取中，请稍后...");
        WebService.GetStudentHistory(null, School_ID, Student_ID, new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                CancelDiaLog();
                if (obj == null) {
                    DisplayOKDiaLog("取得资讯错误！");
                    return;
                } else if (obj.equals("Success! No Data Return!")) {
                    DisplayOKDiaLog("没有任何资讯！");
                    return;
                }
                Log.i(TAG,"obj : "+obj);
                JSONArray json = (JSONArray) obj;
                if (json.length() == 0) {
                    DisplayOKDiaLog("没有任何资讯！");
                    return;
                }
                JSONObject tmpObj;
                int i = -1;
                while (++i < json.length()) {
                    tmpObj = json.optJSONObject(i);
                    View mVHistoryItem = _inflater.inflate(R.layout.profile_personalhistory_item, null);
                    TextView mTxtSchoolName = (TextView) mVHistoryItem.findViewById(R.id.txtProfileHistorySchoolName);
                    TextView mTxtClassName = (TextView) mVHistoryItem.findViewById(R.id.txtProfileHistoryClassName);
                    TextView mTxtStudentName = (TextView) mVHistoryItem.findViewById(R.id.txtProfileHistoryStudentName);
                    TextView mTxtTime = (TextView) mVHistoryItem.findViewById(R.id.txtProfileHistoryTime);
                    mTxtSchoolName.setText(tmpObj.optString("SCHOOL_NAME"));
                    mTxtClassName.setText(tmpObj.optString("CLASS_NAME"));
                    mTxtStudentName.setText(tmpObj.optString("STUDENT_NAME"));
                    mTxtTime.setText(ComFun.date2UserSee(tmpObj.optString("CLASS_ST_DATE"), "/") + " - " + ComFun.date2UserSee(tmpObj.optString("CLASS_END_DATE"), "/"));

                    if (Integer.valueOf(tmpObj.optString("CLASS_END_DATE")) < Integer.valueOf(ComFun.GetNowDate())) {
                        //if(Integer.valueOf(tmpObj.optString("CLASS_END_DATE")) < 20160101){
                        mTblOld.addView(mVHistoryItem);
                    } else {
                        mTblCurr.addView(mVHistoryItem);
                    }

                }
            }
        });
    }


    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbSetHistoryBack:
                    main.RemoveBottom(thisFragment);
                    break;
            }
        }
    }
}