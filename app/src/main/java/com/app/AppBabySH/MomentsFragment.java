package com.app.AppBabySH;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.AppBabySH.UIBase.MyAlertDialog;
import com.app.AppBabySH.adapter.MomentsAdapter;
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

public class MomentsFragment extends Fragment {
    private static final String TAG = "MomentsFragment";
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

    //About Reply
    private View mLyReply;
    private EditText edtReplyTo;
    private Button btnReplySend;
    private String strReplySN;
    private String strReplyName;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        main = (MainTabActivity) getActivity();
        _inflater = inflater;
        rootView = inflater.inflate(R.layout.moments_fragment, container, false);
        pullDownView = (PullDownView) rootView.findViewById(R.id.moments_pulldownview);
        pullDownView.enableAutoFetchMore(true, 0);
        momentslistView = pullDownView.getListView();
        imageLoader = new ImageLoader(container.getContext().getApplicationContext());
        ViewGroup header = (ViewGroup) _inflater.inflate(R.layout.moments_header, momentslistView, false);
        ImageView headerIMG = (ImageView) header.findViewById(R.id.momentsheader_userIMG);
        imageLoader.DisplayRoundedCornerImage(UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_AVATAR"), headerIMG);
        momentslistView.addHeaderView(header, null, false);
        isInit = true;


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.v(TAG, "Moments-Init");
                viewG = (ViewGroup) rootView.getParent();
                initListView();
            }
        }, 200);
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
        momentslistView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                    toViewMode();
                }
                return false;
            }

        });
        return rootView;
    }


    private void initListView() {
        if (mLyReply != null) {
            viewG.removeView(mLyReply);
            mLyReply = null;
        }
        clearList();
        getData();
    }

    /**
     * 清除列表
     */
    private void clearList() {
        if (adapter != null) {
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
        if (momentslist != null) {
            momentslist.clear();
        } else {
            momentslist = new ArrayList<MomentsItem>();
        }
        momentslistView.setAdapter(null);
        adapter = new MomentsAdapter(getActivity(), momentslist);
    }

    private void getData() {
        pd = MyAlertDialog.ShowProgress(getActivity(), "Loading...");
        pd.show();
        WebService.GetCircleListClass(null, UserMstr.userData.getUserID(), "C201504000002", new WebService.WebCallback() {

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

    private void createListView() {
        // TODO 移除當前畫面
        Log.v(TAG, "ViewG : createListView : " + viewG);
        viewG.removeView(rootView);
        // TODO 依取得的資料創建 ListView
        momentslistView = pullDownView.getListView();
        momentslistView.setAdapter(adapter);
        momentslistView.setSelection(listPosition);
        adapter.onCallBack = new MomentsAdapter.CallBack() {
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
                }
            }

            @Override
            public void onCommentClick(MomentsItem $item, String $atReplySN, String $atReplyName) {
                callBackItem = $item;
                strReplySN = $atReplySN;
                strReplyName = $atReplyName;
                //momentslistView.setScrollY(0);
                int i = -1;
                geti:while (++i < momentslist.size()) {
                    if (momentslist.get(i).CIRCLE_ID.equals($item.CIRCLE_ID)) {
                        if (android.os.Build.VERSION.SDK_INT >= 8) {
                            momentslistView.smoothScrollToPosition(i+1);
                        } else {
                            momentslistView.setSelection(i+1);
                        }
                        break geti;
                    }
                }
                commentCircle();
            }
        };

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
        if (mLyReply == null) {
            mLyReply = _inflater.inflate(R.layout.moments_reply, momentslistView, false);
            viewG.addView(mLyReply);
            edtReplyTo = (EditText) mLyReply.findViewById(R.id.txtMomentsReplyTo);
            btnReplySend = (Button) mLyReply.findViewById(R.id.btnMomentsReplySend);
            btnReplySend.setOnClickListener(new SendReplyMsg());
        } else {
            mLyReply.setVisibility(View.VISIBLE);
        }
        edtReplyTo.setText("");
        edtReplyTo.setHint("回覆 " + strReplyName);
        edtReplyTo.requestFocus();


        main.OpenInput();
    }

    class SendReplyMsg implements View.OnClickListener {
        public void onClick(View v) {
            toViewMode();
            WebService.SetCircleReply(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), edtReplyTo.getText().toString(), strReplySN, new WebService.WebCallback() {
                @Override
                public void CompleteCallback(String id, Object obj) {
                    if (obj != null) {
                        Map map = new HashMap();
                        map.put("CIRCLE_ID", callBackItem.CIRCLE_ID);
                        map.put("REPLY_SN", "");
                        map.put("USER_ID", UserMstr.userData.getUserID());
                        map.put("NIC_NAME", UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("NIC_NAME"));
                        map.put("REPLY_DESC", edtReplyTo.getText().toString());
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
        if (mLyReply != null && mLyReply.getVisibility() == View.VISIBLE) {
            mLyReply.setVisibility(View.GONE);
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
