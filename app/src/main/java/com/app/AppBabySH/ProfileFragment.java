package com.app.AppBabySH;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.app.AppBabySH.activity.LoginActivity;
import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.ImageLoader;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;

public class ProfileFragment extends BaseFragment {
    final private String TAG = "profile";
    private MainTabActivity main;
    private View rootView;
    private GlobalVar centerV;
    private LayoutInflater _inflater;
    private AlertDialog.Builder alertD;

    private LinearLayout mLyRegOther, mLySet, mLyFavChannel;
    private ImageView mImgPic;
    private TableLayout mTblPersonalInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _inflater = inflater;
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        if(UserMstr.userData.getUserName().equals("guest")){
            final AlertDialog mutiItemDialogLogin = createLoginDialog();
            mutiItemDialogLogin.show();
        }else{
            initView();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //共用宣告
        centerV = (GlobalVar) rootView.getContext().getApplicationContext();
        main = (MainTabActivity) getActivity();
        main.setSoftInputMode("adjustPan");
        //  判斷網路
        if (WebService.isConnected(getActivity()) && !UserMstr.userData.getUserName().equals("guest")) getData();
    }


    //  初始化
    private void initView() {
        mTblPersonalInfo = (TableLayout) rootView.findViewById(R.id.tblProfilePersonalInfo);
        mTblPersonalInfo.removeAllViews();
        mLyRegOther = (LinearLayout) rootView.findViewById(R.id.lyProfileRegOther);
        mLySet = (LinearLayout) rootView.findViewById(R.id.lyProfileSet);
        mLyFavChannel = (LinearLayout) rootView.findViewById(R.id.lyProfileFavChannel);
        mImgPic = (ImageView) rootView.findViewById(R.id.imgProfilePic);

        mLyRegOther.setOnClickListener(new onClick());
        mLySet.setOnClickListener(new onClick());
        mLyFavChannel.setOnClickListener(new onClick());
        ImageLoader.getInstance().DisplayRoundedCornerImage(
                UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_AVATAR"), mImgPic);
    }

    //  提示登入選單
    public AlertDialog createLoginDialog() {
        final String[] items = {"我已经有帐户，我需要登录", "没有帐户，需要注册", "取消"};
        alertD = new AlertDialog.Builder(getActivity());
        alertD.setTitle("当前您并未登录宝贝通，部分功能需要登录后可用。");
        //設定對話框內的項目
        alertD.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.mTabHost.setCurrentTab(1);
                switch (which) {
                    case 0:
                        centerV.loginAgain = true;
                        UserMstr.userData = null;
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        AccountRegistFragment regF = new AccountRegistFragment();
                        main.OpenBottom(regF);
                        break;
                    case 2:

                        break;
                }

            }
        });
        return alertD.create();
    }

    //  取得數據
    private void getData() {
        DisplayLoadingDiaLog("资料读取中，请稍后...");
        if (UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_TYPE").equals("P")) {
            WebService.GetParentChilds(null, UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_TYPE_ID"), new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    CancelDiaLog();
                    try {
                        if (obj == null) {
                            DisplayOKDiaLog("取得资讯错误！");
                            return;
                        }else if (obj.equals("Success! No Data Return!")) {
                            DisplayOKDiaLog("读取完成，无资料返回!");
                            return;
                        }
                        JSONArray json = (JSONArray) obj;
                        if (json.length() == 0) {
                            DisplayOKDiaLog("没有任何资讯！");
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
                            mVPersonalInfoItem.setOnClickListener(new onPersonalItemClick(json.optJSONObject(i).optString("SCHOOL_ID"), json.optJSONObject(i).optString("STUDENT_ID")));
                        }
                    } catch (Exception e) {
                        DisplayOKDiaLog("GetParentChilds 取得资讯失败！e:" + e);
                        e.printStackTrace();
                    }
                }
            });
        } else {
            WebService.GetTeacherTeaching(null, UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_TYPE_ID"), new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    CancelDiaLog();
                    try {
                        if (obj == null) {
                            DisplayOKDiaLog("取得资讯错误！");
                            return;
                        }else if (obj.equals("Success! No Data Return!")) {
                            DisplayOKDiaLog("读取完成，无资料返回!");
                            return;
                        }
                        JSONArray json = (JSONArray) obj;
                        if (json.length() == 0) {
                            DisplayOKDiaLog("没有任何资讯！");
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
                    } catch (Exception e) {
                        DisplayOKDiaLog("GetTeacherTeaching 取得资讯失败！e:" + e);
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //  判斷網路
            if (!WebService.isConnected(getActivity())) return;
            switch (v.getId()) {
                case R.id.lyProfileRegOther:
                    AccountRegistFragment regF = new AccountRegistFragment();
                    main.OpenBottom(regF);
                    break;
                case R.id.lyProfileSet:
                    SetMenuFragment setF = new SetMenuFragment();
                    setF.onCallBack = new SetMenuFragment.CallBack() {
                        @Override
                        public void onBack() {
                            try {
                                ImageLoader.getInstance().DisplayRoundedCornerImage(
                                        UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_AVATAR"), mImgPic);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    main.OpenBottom(setF);
                    break;
                case R.id.lyProfileFavChannel:
                    SetFavChannelFragment setFCF = new SetFavChannelFragment();
                    main.OpenBottom(setFCF);
                    break;
            }
        }
    }

    private class onPersonalItemClick implements View.OnClickListener {
        private String schollID, studentID;

        public onPersonalItemClick(String $schollID, String $studentID) {
            schollID = $schollID;
            studentID = $studentID;
        }

        @Override
        public void onClick(View v) {
            //  判斷網路
            if (!WebService.isConnected(getActivity())) return;
            SetHistoryFragment setHistoryF = new SetHistoryFragment();
            setHistoryF.School_ID = schollID;
            setHistoryF.Student_ID = studentID;
            main.OpenBottom(setHistoryF);
        }
    }
}