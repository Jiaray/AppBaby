package com.app.AppBabySH;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.AppBabySH.activity.LoginActivity;
import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.AppBabySH.adapter.MomentsAdapter;
import com.app.AppBabySH.item.ClassItem;
import com.app.AppBabySH.item.MomentsImageItem;
import com.app.AppBabySH.item.MomentsItem;
import com.app.Common.PullDownView;
import com.app.Common.ScrollOverListView;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.app.Common.ImageLoader;

public class MomentsFragment extends BaseFragment {
    private static final String TAG = "MomentsF";
    private MomentsCommonFun comF;
    public MomentsAdapter adapter;
    private AlertDialog.Builder alertD;
    public ArrayList<MomentsItem> momentslist;
    private Handler hdrMain;

    //Main Obj
    private LayoutInflater _inflater;
    private MainTabActivity main;
    private View rootView;
    private ViewGroup viewG;
    private PullDownView pullDownView;
    private ScrollOverListView mSlvContent;
    private ImageButton mImgBFilter, mImgBAddNew;
    private MomentsFragment thisFragment;

    //values
    public String CurrClassID;
    private int MOMENTS_COUNT;
    private int pageIndex;
    private boolean isInit = true;
    public MomentsItem callBackItem;
    private GlobalVar centerV;

    //About Reply
    public View mVReply;
    private LinearLayout mLyReply;
    private EditText mEdtReplyTo;
    private Button mBtnReplySend;
    private String strReplySN;
    private String strReplyName;
    private Integer intReplyPosition;
    private Integer intListViewH;
    private Integer intAdjustH;

    private TextView mTxtHeaderNews;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        centerV = (GlobalVar) getActivity().getApplicationContext();
        _inflater = inflater;
        rootView = inflater.inflate(R.layout.moments_fragment, container, false);

        if (UserMstr.userData.getUserName().equals("guest")) {
            final AlertDialog mutiItemDialogLogin = createLoginDialog();
            mutiItemDialogLogin.show();
        } else {
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
        main.setSoftInputMode("adjustResize");
        comF = new MomentsCommonFun(this, rootView);

        if (!UserMstr.userData.getUserName().equals("guest")) {
            //ListView Add Header
            hdrMain = new Handler();
            hdrMain.post(runInit);
        }

    }


    private void initView() {
        Log.v(TAG, "Moments-Init");
        if (UserMstr.userData == null) return;
        isInit = true;
        CurrClassID = UserMstr.userData.ClassAryList.get(0).CLASS_ID;
        pageIndex = 1;

        //ListView Ready
        pullDownView = (PullDownView) rootView.findViewById(R.id.pdvMomentsContent);
        pullDownView.enableAutoFetchMore(true, 0);
        pullDownView.setOnPullDownListener(new PullDownView.OnPullDownListener() {

            @Override
            public void onRefresh() {//Refresh
                //  判斷網路
                if (!WebService.isConnected(getActivity())) {
                    refreshPullView();
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Log.v(TAG, "Moments-Refresh");
                            refreshListView();
                        }
                    }, 100);
                }
            }

            @Override
            public void onLoadMore() {//LoadMore
                //  判斷網路
                if (!WebService.isConnected(getActivity())) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            pullDownView.notifyDidLoadMore(momentslist.isEmpty());
                            refreshPullView();
                        }
                    }, 1000);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Log.v(TAG, "Moments-LoadMore");
                            pageIndex++;
                            getData();
                            pullDownView.notifyDidLoadMore(momentslist.isEmpty());
                        }
                    }, 1000);
                }
            }
        });


        mSlvContent = pullDownView.getListView();
        momentslist = new ArrayList<MomentsItem>();
        adapter = new MomentsAdapter(getActivity(), momentslist);
        adapter.onCallBack = new AdapterCallBack();
        mSlvContent.setAdapter(adapter);
        mSlvContent.setOnTouchListener(new View.OnTouchListener() {//AddEvent Cancel Reply Mode

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                    toViewMode();
                }
                return false;
            }

        });

        //Create Filter Menu
        mImgBFilter = (ImageButton) rootView.findViewById(R.id.imgbMomentsFilter);
        final AlertDialog mutiItemDialogFilter = createFilterDialog();
        mImgBFilter.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  判斷網路
                if (WebService.isConnected(getActivity())) {
                    mutiItemDialogFilter.show();
                }
            }
        });
        if(centerV.selectClass)mutiItemDialogFilter.show();

        //Create AddNew Menu
        mImgBAddNew = (ImageButton) rootView.findViewById(R.id.imgbMomentsAddNew);
        final AlertDialog mutiItemDialogAddNew = createAddNewDialog();
        mImgBAddNew.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  判斷網路
                if (WebService.isConnected(getActivity())) {
                    mutiItemDialogAddNew.show();
                }
            }
        });

    }

    private Runnable runInit = new Runnable() {
        @Override
        public void run() {

            viewG = (ViewGroup) rootView.getParent();
            ViewGroup mVgHeader = (ViewGroup) _inflater.inflate(R.layout.moments_header, mSlvContent, false);
            ImageView mImgHeaderHeadImage = (ImageView) mVgHeader.findViewById(R.id.imgMomentsHeaderUserHead);
            mTxtHeaderNews = (TextView) mVgHeader.findViewById(R.id.txtMomentsHeaderNews);
            mTxtHeaderNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Handler tt = new Handler();
                    tt.post(new Runnable() {
                        @Override
                        public void run() {
                            //  判斷網路
                            if (WebService.isConnected(getActivity())) {
                                comF.customCircle("news");
                            }
                        }
                    });
                }
            });

            ImageLoader.getInstance().DisplayRoundedCornerImage(UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_AVATAR"), mImgHeaderHeadImage);
            mSlvContent.addHeaderView(mVgHeader, null, false);
            //  判斷網路
            if (!WebService.isConnected(getActivity())) {
                refreshPullView();
                return;
            } else {
                getData();
            }
            //removeCallbacks
            hdrMain.removeCallbacks(runInit);
        }
    };

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

    //  過濾選單
    public AlertDialog createFilterDialog() {
        final String[] items = new String[UserMstr.userData.ClassAryList.size() + 1];
        int i = -1;
        while (++i < UserMstr.userData.ClassAryList.size()) {
            ClassItem tmpClassitem = UserMstr.userData.ClassAryList.get(i);
            if (tmpClassitem.SCHOOL_NAME.equals("")) {
                items[i] = tmpClassitem.CLASS_NAME;
            } else {
                items[i] = tmpClassitem.STUDENT_NAME + "(" + tmpClassitem.SCHOOL_NAME + "-" + tmpClassitem.CLASS_NAME + ")";
            }
        }
        items[UserMstr.userData.ClassAryList.size()] = "取消";
        alertD = new AlertDialog.Builder(getActivity());
        alertD.setTitle("选择班级");
        //設定對話框內的項目
        alertD.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which != (UserMstr.userData.ClassAryList.size())) {
                    CurrClassID = UserMstr.userData.ClassAryList.get(which).CLASS_ID;
                    refreshListView();
                }
                centerV.selectClass = false;
            }
        });
        return alertD.create();
    }

    //  新增選單
    public AlertDialog createAddNewDialog() {
        final String[] items = {"从手机相册选择", "拍照", "取消"};
        alertD = new AlertDialog.Builder(getActivity());
        alertD.setTitle("选择内容来源!");
        //設定對話框內的項目
        alertD.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which != 2) {
                    clearGridView();
                    MomentsAddNewFragment addF = new MomentsAddNewFragment();
                    addF.Class_ID = CurrClassID;
                    addF.Add_Type = which == 0 ? "albums" : "camera";
                    addF.onCallBack = new MomentsAddNewFragment.CallBack() {
                        @Override
                        public void onBack() {
                            refreshListView();
                        }
                    };
                    main.OpenBottom(addF);
                }
            }
        });
        return alertD.create();
    }

    private void clearGridView() {
        ImageLoader.getInstance().clearCache();
        momentslist.clear();
        ImageLoader.getInstance().clearCache();
        System.gc();
        adapter.notifyDataSetChanged();
        System.gc();
    }

    //  取得 Web 資料
    private void getData() {
        DisplayLoadingDiaLog("资料读取中，请稍后...");
        WebService.GetCircleListClass(null, UserMstr.userData.getUserID(), CurrClassID, String.valueOf(pageIndex), "5", new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                CancelDiaLog();
                Log.i(TAG, "GetCircleListClass obj : " + obj);
                try {
                    if (obj == null) {
                        DisplayOKDiaLog("Error!");
                        return;
                    } else if (obj.equals("Success! No Data Return!")) {
                        DisplayOKDiaLog("读取完成，无资料返回!");
                        pullDownView.notifyDidDataLoad(true);
                        return;
                    }
                    JSONArray json = (JSONArray) obj;
                    JSONArray infoAry = json.optJSONObject(0).optJSONArray("CIRCLE_INFO");
                    JSONArray atchAry = json.optJSONObject(0).optJSONArray("CIRCLE_ATCH");
                    JSONArray replyAry = json.optJSONObject(0).optJSONArray("CIRCLE_REPLY");
                    JSONArray goodAry = json.optJSONObject(0).optJSONArray("CIRCLE_GOOD");
                    if (infoAry.length() == 0) {
                        DisplayOKDiaLog("Empty!");
                        return;
                    }

                    // TODO 塞值進陣列中
                    Integer i = -1, j;
                    //i = 0;
                    while (++i < infoAry.length()) {
                        MomentsItem item = new MomentsItem();
                        item.CIRCLE_ID = infoAry.optJSONObject(i).optString("CIRCLE_ID");//"C201504120015"
                        item.USER_ID = infoAry.optJSONObject(i).optString("USER_ID");//"U201504000002"
                        item.NIC_NAME = infoAry.optJSONObject(i).optString("NIC_NAME");//"張老師"
                        item.USER_AVATAR = infoAry.optJSONObject(i).optString("USER_AVATAR");//"http://img.appbaby.net/test_icon.png"
                        item.CLASS_ID = infoAry.optJSONObject(i).optString("CLASS_ID");//"C201504000002"
                        item.CLASS_NAME = infoAry.optJSONObject(i).optString("CLASS_NAME");//"B班"
                        item.SCHOOL_NAME = infoAry.optJSONObject(i).optString("SCHOOL_NAME");//"APP測試園一"
                        item.DESCRIPTION = infoAry.optJSONObject(i).optString("DESCRIPTION");//"但凡有?儿"
                        item.CIRCLE_TYPE = infoAry.optJSONObject(i).optString("CIRCLE_TYPE");//"T"
                        item.LATITUDE = infoAry.optJSONObject(i).optString("LATITUDE");//""
                        item.LONGITUDE = infoAry.optJSONObject(i).optString("LONGITUDE");//""
                        item.ENTRY_DATE = infoAry.optJSONObject(i).optString("ENTRY_DATE");//"20150511"
                        item.ENTRY_TIME = infoAry.optJSONObject(i).optString("ENTRY_TIME");//"161700"
                        j = -1;
                        while (++j < atchAry.length()) {
                            if (atchAry.optJSONObject(j).optString("CIRCLE_ID").equals(item.CIRCLE_ID)) {
                                item.ATCH.add(atchAry.optJSONObject(j));
                            }
                        }
                        j = -1;
                        while (++j < replyAry.length()) {
                            if (replyAry.optJSONObject(j).optString("CIRCLE_ID").equals(item.CIRCLE_ID)) {
                                item.REPLY.add(replyAry.optJSONObject(j));
                            }
                        }
                        j = -1;
                        while (++j < goodAry.length()) {
                            if (goodAry.optJSONObject(j).optString("CIRCLE_ID").equals(item.CIRCLE_ID)) {
                                item.GOOD.add(goodAry.optJSONObject(j));
                            }
                        }
                        momentslist.add(item);
                    }
                    MOMENTS_COUNT = Integer.valueOf(infoAry.optJSONObject(0).optString("COUNT"));
                    adapter.notifyDataSetChanged();
                    refreshPullView();
                } catch (NumberFormatException e) {
                    DisplayOKDiaLog("GetCircleListClass Error! e:" + e);
                    e.printStackTrace();
                }
            }
        });
    }

    //  刷新列表
    public void refreshListView() {
        pageIndex = 1;
        if (mVReply != null) {
            viewG.removeView(mVReply);
            mVReply = null;
        }
        momentslist.clear();
        ImageLoader.getInstance().clearCache();
        System.gc();
        adapter.notifyDataSetChanged();
        getData();
    }

    //  刷新外框View
    private void refreshPullView() {
        if (centerV.momentPushNum.equals("0")) {
            mTxtHeaderNews.setVisibility(View.GONE);
        } else {
            mTxtHeaderNews.setVisibility(View.VISIBLE);
            mTxtHeaderNews.setText(centerV.momentPushNum + " 條新消息!");
        }
        if (isInit) {
            isInit = false;
        } else {
            pullDownView.notifyDidRefresh(momentslist.isEmpty());
        }
        adapter.notifyDataSetChanged();

        if (momentslist.size() == MOMENTS_COUNT) {
            pullDownView.notifyDidDataLoad(true);
        } else {
            pullDownView.notifyDidDataLoad(false);
        }
        //Log.i(TAG, "班級圈畫面產生完畢!");
    }

    // MomentsAdapterCallBack 班級圈各個 Item 的功能(刪除、按讚、收藏、回覆、圖示放大瀏覽)
    class AdapterCallBack implements MomentsAdapter.CallBack {

        //刪除、按讚、收藏功能
        @Override
        public void onClick(String $actiontype, MomentsItem $item) {
            //  判斷網路
            if (!WebService.isConnected(getActivity())) {
                return;
            }
            callBackItem = $item;
            Log.v(TAG, "CIRCLE_ID:" + callBackItem.CIRCLE_ID);
            Log.v(TAG, "DESCRIPTION:" + callBackItem.DESCRIPTION);
            if ($actiontype.equals("del")) {
                comF.delCircle();
            } else if ($actiontype.equals("good")) {
                comF.goodCircle();
            } else if ($actiontype.equals("fav")) {
                comF.favCircle();
            } else if ($actiontype.equals("personal")) {
                comF.customCircle("personal");
            }
        }

        //  回覆功能
        @Override
        public void onCommentClick(MomentsItem $item, String $atReplySN, String $atReplyName) {
            //  判斷網路
            if (!WebService.isConnected(getActivity())) {
                return;
            }
            callBackItem = $item;
            strReplySN = $atReplySN;
            strReplyName = $atReplyName;
            commentCircle();
        }

        //  進入瀏覽圖示的畫面
        @Override
        public void onImgAdapterClick(MomentsImageItem $item) {
            //  判斷網路
            if (!WebService.isConnected(getActivity())) {
                return;
            }
            MomentsImageFragment momentsImageFragment = new MomentsImageFragment();
            int i = -1;
            while (++i < momentslist.size()) {
                if (momentslist.get(i).CIRCLE_ID.equals($item.CIRCLE_ID)) {
                    momentsImageFragment.CricleItem = momentslist.get(i);
                }
            }
            momentsImageFragment.openFunBar = true;
            momentsImageFragment.SEQ = $item.SEQ;
            momentsImageFragment.onCallBack = new MomentsImageFragment.imgCallBack() {
                @Override
                public void onBack(MomentsItem $item, String $actionType) {
                    if ($actionType != null && $actionType.equals("comment")) {
                        onCommentClick($item, "", "");
                    }
                }
            };
            main.OpenBottom(momentsImageFragment);
        }
    }


    //  回覆班級圈
    private void commentCircle() {
        main.mTabHost.setVisibility(View.GONE);
        if (mVReply == null) {
            mVReply = _inflater.inflate(R.layout.moments_reply, mSlvContent, false);
            viewG.addView(mVReply);
            mEdtReplyTo = (EditText) mVReply.findViewById(R.id.txtMomentsReplyTo);
            mBtnReplySend = (Button) mVReply.findViewById(R.id.btnMomentsReplySend);
            mLyReply = (LinearLayout) mVReply.findViewById(R.id.lyMomentsReply);
            mBtnReplySend.setOnClickListener(new SendReplyMsg());
        } else {
            mVReply.setVisibility(View.VISIBLE);
        }
        mEdtReplyTo.setText("");
        mEdtReplyTo.setHint("回覆 " + strReplyName);
        mEdtReplyTo.requestFocus();


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int i = -1;
                getI:
                while (++i < momentslist.size()) {
                    if (momentslist.get(i).CIRCLE_ID.equals(callBackItem.CIRCLE_ID)) {
                        intReplyPosition = i;
                        break getI;
                    }
                }
                mSlvContent.setSelection(intReplyPosition + 1);
                intAdjustH = mVReply.getMeasuredHeight();
                main.OpenInput();
            }
        });

        hdrMain.post(checkValue);
    }

    private Runnable checkValue = new Runnable() {
        @Override
        public void run() {
            //確定鍵盤已展開
            if (intAdjustH == mVReply.getMeasuredHeight()) {
                hdrMain.postDelayed(checkValue, 100);
            } else {
                hdrMain.removeCallbacks(checkValue);
                intListViewH = pullDownView.getMeasuredHeight();
                intAdjustH = intListViewH - mSlvContent.getChildAt(0).getHeight() - mLyReply.getMeasuredHeight();
                mSlvContent.setSelectionFromTop(intReplyPosition + 1, intAdjustH);
            }
        }
    };

    class SendReplyMsg implements View.OnClickListener {
        public void onClick(View v) {
            toViewMode();
            Log.i(TAG, "SendReplyMsg\nAPN:" + centerV.apn + "\nCIRCLE_ID:" + callBackItem.CIRCLE_ID + "\ngetUserID:" + UserMstr.userData.getUserID() + "\nreplyTo:" + mEdtReplyTo.getText().toString().replace("\n", "") + "\nstrReplySN:" + strReplySN);
            WebService.SetCircleReply(null, centerV.apn, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), mEdtReplyTo.getText().toString().replace("\n", ""), strReplySN, new WebService.WebCallback() {
                @Override
                public void CompleteCallback(String id, Object obj) {
                    try {
                        if (obj != null) {
                            if (obj.equals("Success! No Data Return!")) {
                                DisplayOKDiaLog("读取完成，无资料返回!");
                                return;
                            }
                            Map map = new HashMap();
                            map.put("CIRCLE_ID", callBackItem.CIRCLE_ID);
                            map.put("REPLY_SN", "");
                            map.put("USER_ID", UserMstr.userData.getUserID());
                            map.put("NIC_NAME", UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("NIC_NAME"));
                            map.put("REPLY_DESC", mEdtReplyTo.getText().toString());
                            map.put("AT_REPLY_SN", strReplySN);
                            JSONObject newJsonObj = new JSONObject(map);
                            // 按下"收到"以後要做的事情
                            callBackItem.REPLY.add(newJsonObj);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        DisplayOKDiaLog("SetCircleReply Error! e:" + e);
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    //  切回瀏覽班級圈畫面
    private void toViewMode() {
        main.CloseInput();
        if (mVReply != null && mVReply.getVisibility() == View.VISIBLE) {
            mVReply.setVisibility(View.GONE);
            main.mTabHost.setVisibility(View.VISIBLE);
        }
    }

}
