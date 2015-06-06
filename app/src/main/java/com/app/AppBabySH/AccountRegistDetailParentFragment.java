package com.app.AppBabySH;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.AppBabySH.activity.LoginActivity;
import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.MyAlertDialog;
import com.app.Common.WebService;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by ray on 2015/5/29.
 */
public class AccountRegistDetailParentFragment extends BaseFragment {
    final private String TAG = "DetailP";
    public String Activity_No, Phone, SchoolName, Province, City, ClassName, BabyName;
    private Integer i;

    private View rootView;
    private AccountRegistDetailParentFragment thisFragment;
    private LoginActivity loginA;

    private LinearLayout mLyDetail;
    private TextView mTxtPhone, mTxtSchool, mTxtProvince, mTxtCity, mTxtClass, mTxtBabyName, mTxtRole;
    private EditText mEdtNickname, mEdtPW, mEdtCheckPW;
    private Button mBtnCommit, mBtnRelationship;
    private ImageButton mBtnBack;
    private MainTabActivity mainA;
    private String actName;

    private ArrayList<relationshipItem> relationshipAryList;
    private AlertDialog adRelationship;
    private AlertDialog.Builder alertD;
    private String relationshipID;

    private String strNick, strPw, strCheckPw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.login_regist_detail_p_fragment, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (getActivity().getLocalClassName().equals("activity.LoginActivity")) {
            loginA = (LoginActivity) getActivity();
            actName = "LoginActivity";
        } else {
            mainA = (MainTabActivity) getActivity();
            actName = "MainTabActivity";
        }
        thisFragment = this;
        getRelationshipData();
        initView();
        return rootView;
    }

    //Create Menu
    private void getRelationshipData() {
        WebService.GetParentRelation(null, new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                Log.i(TAG, "obj : " + obj);
                // TODO Auto-generated method stub
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "关系资讯取得错误！");
                    return;
                }
                JSONArray json = (JSONArray) obj;
                if (json.length() == 0) {

                } else {
                    relationshipAryList = new ArrayList<relationshipItem>();
                    i = -1;
                    while (++i < json.length()) {
                        relationshipItem ritem = new relationshipItem();
                        ritem.CODE = json.optJSONObject(i).optString("PARENTS_CLASS_CODE");
                        ritem.NAME = json.optJSONObject(i).optString("PARENTS_CLASS_NAME");
                        relationshipAryList.add(ritem);
                    }
                    adRelationship = getMenuDialog();
                }
            }
        });
    }

    private void initView() {
        mLyDetail = (LinearLayout) rootView.findViewById(R.id.lyRegInfoDetail);
        mTxtPhone = (TextView) rootView.findViewById(R.id.txtRegInfoPhone);
        mTxtSchool = (TextView) rootView.findViewById(R.id.txtRegInfoSchool);
        mTxtProvince = (TextView) rootView.findViewById(R.id.txtRegInfoProvince);
        mTxtCity = (TextView) rootView.findViewById(R.id.txtRegInfoCity);
        mTxtClass = (TextView) rootView.findViewById(R.id.txtRegInfoClass);
        mTxtBabyName = (TextView) rootView.findViewById(R.id.txtRegInfoBabyName);
        mTxtRole = (TextView) rootView.findViewById(R.id.txtRegInfoRole);
        mTxtPhone.setText("注册手机号码 : " + Phone);
        mTxtSchool.setText("学校名称 : " + SchoolName);
        mTxtProvince.setText("省份 : " + Province);
        mTxtCity.setText("城市 : " + City);
        mTxtClass.setText("所属班级 : " + ClassName);
        mTxtBabyName.setText("宝贝姓名 : " + BabyName);
        mTxtRole.setText("帐号身分 : 家长");

        mEdtNickname = (EditText) rootView.findViewById(R.id.edtRegInfoNickname);
        mEdtPW = (EditText) rootView.findViewById(R.id.edtRegInfoPW);
        mEdtCheckPW = (EditText) rootView.findViewById(R.id.edtRegInfoCheckPW);
        mEdtPW.setTransformationMethod(PasswordTransformationMethod.getInstance());//隱藏密碼
        mEdtCheckPW.setTransformationMethod(PasswordTransformationMethod.getInstance());//隱藏密碼
        mBtnRelationship = (Button) rootView.findViewById(R.id.btnRegInfoRelationship);
        mBtnRelationship.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                adRelationship.show();
            }
        });
        mBtnBack = (ImageButton) rootView.findViewById(R.id.imgbRegInfoBack);
        mBtnCommit = (Button) rootView.findViewById(R.id.btnRegInfoCommit);
        mBtnBack.setOnClickListener(new RegistInfoonClick());
        mBtnCommit.setOnClickListener(new RegistInfoonClick());
    }

    //  關係選單
    public AlertDialog getMenuDialog() {
        final String[] items = new String[relationshipAryList.size()];
        i = -1;
        while (++i < relationshipAryList.size()) {
            items[i] = relationshipAryList.get(i).NAME;
        }
        alertD = new AlertDialog.Builder(getActivity());
        alertD.setTitle("与宝贝的关系");
        //設定對話框內的項目
        alertD.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                i = -1;
                while (++i < relationshipAryList.size()) {
                    if (relationshipAryList.get(i).NAME.equals(items[which])) {
                        Log.i(TAG, "關係ID:" + relationshipAryList.get(i).CODE);
                        relationshipID = relationshipAryList.get(i).CODE;
                        mBtnRelationship.setHint(relationshipAryList.get(i).NAME);
                    }
                }
            }
        });
        return alertD.create();
    }

    class RegistInfoonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbRegInfoBack:
                    onBack();
                    break;
                case R.id.btnRegInfoCommit:
                    regist();
                    break;
            }
        }
    }

    private void regist() {
        strNick = mEdtNickname.getText().toString();
        strPw = mEdtPW.getText().toString();
        strCheckPw = mEdtCheckPW.getText().toString();
        if (strNick.equals("") || strPw.equals("") || strCheckPw.equals("")) {
            DisplayToast("尚有资料未填写完成");
            return;
        } else if (!strPw.equals(strCheckPw)) {
            DisplayToast("密码不一致");
            return;
        } else {
            WebService.SetRegister(null, Activity_No, relationshipID, strPw, strNick, new WebService.WebCallback() {

                @Override
                public void CompleteCallback(String id, Object obj) {
                    Log.i(TAG, "obj : " + obj);
                    // TODO Auto-generated method stub
                    if (obj == null) {
                        MyAlertDialog.Show(getActivity(), "关系资讯取得错误！");
                        return;
                    }
                }
            });
        }
    }

    static class relationshipItem {
        public String CODE, NAME;
    }

    private void onBack(){
        if(actName.equals("MainTabActivity")){
            mainA.RemoveBottomNotAddTab(thisFragment);
        }else {
            loginA.RemoveBottom(thisFragment);
        }
    }
}
