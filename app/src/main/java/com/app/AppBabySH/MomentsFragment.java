package com.app.AppBabySH;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.AppBabySH.UIBase.MyAlertDialog;
import com.app.AppBabySH.adapter.MomentsAdapter;
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

import lazylist.ImageLoader;

import static com.app.Common.LogUtils.logd;
import static com.app.Common.LogUtils.logi;

public class MomentsFragment extends Fragment {
    private static final String TAG = "MomentsFragment";
    private static final String ForTestClassID = "C201504000002";
    private MainTabActivity main;
    private View rootView;
    private ViewGroup viewG;
    private PullDownView pullDownView;
    private ScrollOverListView momentslistView;
    private MomentsAdapter adapter;
    private ProgressDialog pd;
    private AlertDialog.Builder alertD;
    private ArrayList<MomentsItem> momentslist;
    private int listPosition = 0;
    private boolean isInit = true;
    private LayoutInflater _inflater;
    public ImageLoader imageLoader;
    private MomentsItem callBackItem;
    private Toast toast;

    private Handler hdrMain;

    //About Reply
    private View mVReply;
    private LinearLayout mLyReply;
    private EditText mEdtReplyTo;
    private Button mBtnReplySend;
    private String strReplySN;
    private String strReplyName;
    private Integer intReplyPosition;
    private Integer intListViewH;
    private Integer intAdjustH;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
        imageLoader = new ImageLoader();
        _inflater = inflater;
        rootView = inflater.inflate(R.layout.moments_fragment, container, false);

        //Start
        hdrMain = new Handler();
        hdrMain.post(runInit);
        return rootView;
    }

    private Runnable runInit = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "Moments-Init");
            isInit = true;
            viewG = (ViewGroup) rootView.getParent();

            //ListView Ready
            pullDownView = (PullDownView) rootView.findViewById(R.id.pdvMomentsContent);
            pullDownView.enableAutoFetchMore(true, 0);
            pullDownView.setOnPullDownListener(new PullDownView.OnPullDownListener() {

                @Override
                public void onRefresh() {//Refresh
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Log.v(TAG, "Moments-Refresh");
                            initListView();
                        }
                    }, 100);
                }

                @Override
                public void onLoadMore() {//LoadMore
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Log.v(TAG, "Moments-LoadMore");
                            adapter.notifyDataSetChanged();
                            pullDownView.notifyDidLoadMore(momentslist.isEmpty());
                        }
                    }, 1000);
                }
            });

            //ListView Add Header
            momentslistView = pullDownView.getListView();
            ViewGroup mVgHeader = (ViewGroup) _inflater.inflate(R.layout.moments_header, momentslistView, false);
            ImageView mImgHeaderHeadImage = (ImageView) mVgHeader.findViewById(R.id.imgMomentsHeaderUserHead);
            TextView mTxtHeaderNews = (TextView) mVgHeader.findViewById(R.id.txtMomentsHeaderNews);
            mTxtHeaderNews.setText(main.momentPushNum+" 條新消息!");
            mTxtHeaderNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Handler tt=new Handler();
                    tt.post(new Runnable() {
                        @Override
                        public void run() {
                            customCircle("news");
                        }
                    });
                }
            });
            imageLoader.DisplayRoundedCornerImage(UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_AVATAR"), mImgHeaderHeadImage);
            momentslistView.addHeaderView(mVgHeader, null, false);

            //Create ListView
            initListView();

            //Cancel Reply Mode
            momentslistView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                        toViewMode();
                    }
                    return false;
                }

            });
            hdrMain.removeCallbacks(runInit);
        }
    };


    private void initListView() {
        if (mVReply != null) {
            viewG.removeView(mVReply);
            mVReply = null;
        }
        clearList();
        getData();
    }

    /**
     * 清除列表
     */
    private void clearList() {
        if (adapter != null) {
            //adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
        if (momentslist != null) {
            momentslist.clear();
        } else {
            momentslist = new ArrayList<MomentsItem>();
        }
        listPosition = 0;
        momentslistView.setAdapter(null);
        adapter = new MomentsAdapter(getActivity(), momentslist);
        System.gc();
    }

    //  取得 Web 資料
    private void getData() {
        pd = MyAlertDialog.ShowProgress(getActivity(), "Loading...");
        pd.show();
        WebService.GetCircleListClass(null, UserMstr.userData.getUserID(), ForTestClassID, new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                pd.cancel();
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "Error!");
                    return;
                }
                JSONArray json = (JSONArray) obj;
                JSONArray infoAry = json.optJSONObject(0).optJSONArray("CIRCLE_INFO");
                JSONArray atchAry = json.optJSONObject(0).optJSONArray("CIRCLE_ATCH");
                JSONArray replyAry = json.optJSONObject(0).optJSONArray("CIRCLE_REPLY");
                JSONArray goodAry = json.optJSONObject(0).optJSONArray("CIRCLE_GOOD");
                if (infoAry.length() == 0) {
                    MyAlertDialog.Show(getActivity(), "Empty!");
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
                createListView();
            }
        });
    }

    //  創建內容
    private void createListView() {
        // TODO 移除當前畫面
        Log.v(TAG, "ViewG : createListView : " + viewG);
        viewG.removeView(rootView);
        // TODO 依取得的資料創建 ListView
        momentslistView = pullDownView.getListView();
        momentslistView.setAdapter(adapter);
        momentslistView.setSelection(listPosition);
        adapter.onCallBack = new AdapterCallBack();
        viewG.addView(rootView);
        adapter.notifyDataSetChanged();
        if (isInit) {
            isInit = false;
            adapter.notifyDataSetChanged();
            pullDownView.notifyDidDataLoad(false);
        } else {
            adapter.notifyDataSetChanged();
            pullDownView.notifyDidRefresh(momentslist.isEmpty());
        }
        Log.v(TAG, "Moments Load Complete!");
    }

    // MomentsAdapterCallBack 班級圈各個 Item 的功能(刪除、按讚、收藏、回覆、圖示放大瀏覽)
    class AdapterCallBack implements MomentsAdapter.CallBack {

        //刪除、按讚、收藏功能
        @Override
        public void onClick(String $actiontype, MomentsItem $item) {
            callBackItem = $item;
            Log.v(TAG, "CIRCLE_ID:" + callBackItem.CIRCLE_ID);
            Log.v(TAG, "DESCRIPTION:" + callBackItem.DESCRIPTION);
            if ($actiontype.equals("del")) {
                delCircle();
            } else if ($actiontype.equals("good")) {
                goodCircle();
            } else if ($actiontype.equals("fav")) {
                favCircle();
            } else if ($actiontype.equals("personal")) {
                customCircle("personal");
            }
        }

        //  回覆功能
        @Override
        public void onCommentClick(MomentsItem $item, String $atReplySN, String $atReplyName) {
            callBackItem = $item;
            strReplySN = $atReplySN;
            strReplyName = $atReplyName;
            commentCircle();
        }

        //  進入瀏覽圖示的畫面
        @Override
        public void onImgAdapterClick(MomentsImageItem $item) {
            MomentsImageFragment momentsImageFragment = new MomentsImageFragment();
            int i = -1;
            while (++i < momentslist.size()) {
                if (momentslist.get(i).CIRCLE_ID.equals($item.CIRCLE_ID)) {
                    momentsImageFragment.CricleItem = momentslist.get(i);
                }
            }
            momentsImageFragment.openFunBar = true;
            momentsImageFragment.SEQ = $item.SEQ;
            //  MomentsImageFragmentCallBack
            momentsImageFragment.onCallBack = new MomentsImageFragment.imgCallBack() {
                @Override
                public void onBack(MomentsItem $item, String $actionType) {
                    if ($actionType.equals("comment")) {
                        onCommentClick($item, "", "");
                    } else {
                        //  取 ID 位置
                        int i = -1;
                        getI:
                        while (++i < momentslist.size()) {
                            if (momentslist.get(i).CIRCLE_ID.equals($item.CIRCLE_ID)) break getI;
                        }
                        //  清除畫面
                        clearList();

                        //  設定進入前的 ID 位置
                        listPosition = (i + 1);//有 header 所以+1
                        getData();
                    }
                }
            };
            main.OpenBottom(momentsImageFragment);
        }
    }

    //  條件顯示班級圈頁面
    private void customCircle(String $type) {
        MomentsCustomFragment personalF = new MomentsCustomFragment();
        personalF.Class_ID = ForTestClassID;
        if($type.equals("personal")){
            personalF.USER_ID = callBackItem.USER_ID;
            personalF.NIC_NAME = callBackItem.NIC_NAME;
            personalF.USER_AVATAR = callBackItem.USER_AVATAR;
        }else if($type.equals("news")){
            personalF.USER_ID = UserMstr.userData.getUserID();
            personalF.NIC_NAME = "MomentsNews";
            personalF.USER_AVATAR = "";
        }

        main.OpenBottom(personalF);
    }

    //  刪除班級圈
    private void delCircle() {
        alertD = new AlertDialog.Builder(getActivity()); //創建訊息方塊
        alertD.setTitle("Delete current Circle!");
        alertD.setMessage("are you Sure?");
        alertD.setPositiveButton("Yes", new DialogInterface.OnClickListener() { //按"是",則退出應用程式
            public void onClick(DialogInterface dialog, int i) {
                WebService.SetCircleDelete(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
                    @Override
                    public void CompleteCallback(String id, Object obj) {
                        alertD = new AlertDialog.Builder(getActivity());
                        alertD.setTitle("班級圈已刪除");
                        alertD.setCancelable(false);
                        alertD.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < momentslist.size(); i++) {
                                    if ("c".equals(momentslist.get(i))) {
                                        momentslist.remove(i);
                                    }
                                }
                                initListView();
                                adapter.notifyDataSetChanged();
                            }
                        });
                        alertD.show();
                    }
                });
            }
        });

        alertD.setNegativeButton("No", new DialogInterface.OnClickListener() { //按"否",則不執行任何操作
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        alertD.show();//顯示訊息視窗
    }

    //  對班級圈按讚
    private void goodCircle() {
        WebService.GetCircleHadGood(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "Error!");
                    return;
                }
                JSONObject json = (JSONObject) obj;
                if (json.optString("GOOD_CNT").equals("1")) {
                    DisplayToast("已按讚!");
                } else {
                    WebService.SetCircleGood(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
                        @Override
                        public void CompleteCallback(String id, Object obj) {
                            Map map = new HashMap();
                            map.put("CIRCLE_ID", callBackItem.CIRCLE_ID);
                            map.put("USER_ID", UserMstr.userData.getUserID());
                            map.put("NIC_NAME", UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("NIC_NAME"));
                            JSONObject newJsonObj = new JSONObject(map);
                            // 按下"收到"以後要做的事情
                            callBackItem.GOOD.add(newJsonObj);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    //  收藏班級圈
    private void favCircle() {
        WebService.GetCircleHadKeepToGrow(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "Error!");
                    return;
                }
                JSONObject json = (JSONObject) obj;
                if (json.optString("GROW_CNT").equals("1")) {
                    DisplayToast("已收藏!");
                } else {
                    WebService.SetCircleKeepToGrow(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
                        @Override
                        public void CompleteCallback(String id, Object obj) {
                            alertD = new AlertDialog.Builder(getActivity());
                            alertD.setTitle("檢查"); //設定dialog 的title顯示內容
                            alertD.setMessage("收藏成功!");
                            alertD.setCancelable(false); //關閉 Android 系統的主要功能鍵(menu,home等...)
                            alertD.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertD.show();
                        }
                    });
                }
            }
        });
    }

    //  回覆班級圈
    private void commentCircle() {
        main.mTabHost.setVisibility(View.GONE);
        if (mVReply == null) {
            mVReply = _inflater.inflate(R.layout.moments_reply, momentslistView, false);
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
                momentslistView.setSelection(intReplyPosition + 1);
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
                intAdjustH = intListViewH - momentslistView.getChildAt(0).getHeight() - mLyReply.getMeasuredHeight();
                momentslistView.setSelectionFromTop(intReplyPosition + 1, intAdjustH);
            }
        }
    };

    class SendReplyMsg implements View.OnClickListener {
        public void onClick(View v) {
            toViewMode();
            WebService.SetCircleReply(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), mEdtReplyTo.getText().toString(), strReplySN, new WebService.WebCallback() {
                @Override
                public void CompleteCallback(String id, Object obj) {
                    if (obj != null) {
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
