package com.app.AppBabySH;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.MyAlertDialog;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;

public class ProfileFragment extends BaseFragment {
    final private String TAG = "profile";
    private MainTabActivity main;
    private View rootView;
    private LayoutInflater _inflater;

    private LinearLayout mLyRegOther, mLySet, mLyFavChannel;
    private TableLayout mTblPersonalInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
        _inflater = inflater;
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        initView();
        getData();
        return rootView;
    }
    //  初始化
    private void initView() {
        mTblPersonalInfo = (TableLayout) rootView.findViewById(R.id.tblProfilePersonalInfo);
        mTblPersonalInfo.removeAllViews();
        mLyRegOther = (LinearLayout) rootView.findViewById(R.id.lyProfileRegOther);
        mLySet = (LinearLayout) rootView.findViewById(R.id.lyProfileSet);
        mLyFavChannel = (LinearLayout) rootView.findViewById(R.id.lyProfileFavChannel);

        mLyRegOther.setOnClickListener(new onClick());
        mLySet.setOnClickListener(new onClick());
        mLyFavChannel.setOnClickListener(new onClick());
    }
    //  取得數據
    private void getData() {
        showLoadingDiaLog(getActivity(), "资料读取中，请稍后...");
        if (UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_TYPE").equals("P")) {
            WebService.GetParentChilds(null, UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_TYPE_ID"), new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    cancleDiaLog();
                    if (obj == null) {
                        MyAlertDialog.Show(getActivity(), "取得资讯错误！");
                        return;
                    }
                    JSONArray json = (JSONArray) obj;
                    if (json.length() == 0) {
                        MyAlertDialog.Show(getActivity(), "没有任何资讯！");
                        return;
                    }
                    int i = -1;
                    while (++i < json.length()) {
                        View mVPersonalInfoItem = _inflater.inflate(R.layout.profile_personalinfo_item, null);
                        TextView mTxtRelationship = (TextView) mVPersonalInfoItem.findViewById(R.id.txtProfilPersonalRelationship);
                        TextView mTxtSchool = (TextView) mVPersonalInfoItem.findViewById(R.id.txtProfilPersonalSchool);
                        mTxtRelationship.setText(json.optJSONObject(i).optString("STUDENT_NAME") + " 的 " + json.optJSONObject(i).optString("STUDENT_RELATION"));
                        mTxtSchool.setText(json.optJSONObject(i).optString("SCHOOL_NAME"));
                        mTblPersonalInfo.addView(mVPersonalInfoItem);
                        mVPersonalInfoItem.setOnClickListener(new onPersonalItemClick(json.optJSONObject(i).optString("SCHOOL_ID"),json.optJSONObject(i).optString("STUDENT_ID")));
                    }
                }
            });
        }else{
            WebService.GetTeacherTeaching(null, UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_TYPE_ID"), new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    cancleDiaLog();
                    if (obj == null) {
                        MyAlertDialog.Show(getActivity(), "取得资讯错误！");
                        return;
                    }
                    JSONArray json = (JSONArray) obj;
                    if (json.length() == 0) {
                        MyAlertDialog.Show(getActivity(), "没有任何资讯！");
                        return;
                    }
                    int i = -1;
                    while (++i < json.length()) {
                        View mVPersonalInfoItem = _inflater.inflate(R.layout.profile_personalinfo_item, null);
                        TextView mTxtRelationship = (TextView) mVPersonalInfoItem.findViewById(R.id.txtProfilPersonalRelationship);
                        TextView mTxtSchool = (TextView) mVPersonalInfoItem.findViewById(R.id.txtProfilPersonalSchool);
                        ImageView mImgNext = (ImageView) mVPersonalInfoItem.findViewById(R.id.imgProfilePersonalNext);
                        mTxtRelationship.setText(json.optJSONObject(i).optString("SCHOOL_NAME") + " - " + json.optJSONObject(i).optString("CLASS_NAME"));
                        mTxtSchool.setVisibility(View.GONE);
                        mImgNext.setVisibility(View.GONE);
                        mTblPersonalInfo.addView(mVPersonalInfoItem);
                    }
                }
            });
        }
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lyProfileRegOther:
                    AccountRegistFragment regF = new AccountRegistFragment();
                    main.OpenBottom(regF);
                    break;
                case R.id.lyProfileSet:
                    SetMenuFragment setF = new SetMenuFragment();
                    main.OpenBottom(setF);
                    break;
                case R.id.lyProfileFavChannel:
                    SetFavChannelFragment setFCF = new SetFavChannelFragment();
                    main.OpenBottom(setFCF);
                    break;
            }
        }
    }

    private class onPersonalItemClick implements View.OnClickListener{
        private String schollID,studentID;
        public onPersonalItemClick(String $schollID,String $studentID){
            schollID = $schollID;
            studentID = $studentID;
        }
        @Override
        public void onClick(View v) {
            SetHistoryFragment setHistoryF = new SetHistoryFragment();
            setHistoryF.School_ID = schollID;
            setHistoryF.Student_ID = studentID;
            main.OpenBottom(setHistoryF);
        }
    }
}