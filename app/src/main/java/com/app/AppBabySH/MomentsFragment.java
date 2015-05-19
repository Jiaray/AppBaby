package com.app.AppBabySH;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

import lazylist.ImageLoader;

public class MomentsFragment extends Fragment {
    private static final String TAG = "MomentsFragment";
    private View rootView;
    private PullDownView pullDownView;
    private ScrollOverListView momentslistView;
    private MomentsAdapter adapter;
    private ProgressDialog pd;
    private ArrayList<MomentsItem> momentslist;
    private int listPosition = 0;
    private boolean isInit = true;
    private LayoutInflater _inflater;
    public ImageLoader imageLoader;
    private MomentsItem callBackItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _inflater = inflater;
        rootView = inflater.inflate(R.layout.moments_fragment, container, false);
        pullDownView = (PullDownView) rootView.findViewById(R.id.moments_pulldownview);
        pullDownView.enableAutoFetchMore(true, 0);
        momentslistView = pullDownView.getListView();
        imageLoader = new ImageLoader(container.getContext().getApplicationContext());
        ViewGroup header = (ViewGroup) _inflater.inflate(R.layout.moments_header, momentslistView, false);
        ImageView headerIMG = (ImageView) header.findViewById(R.id.momentsheader_userIMG);
        imageLoader.DisplayRoundedCornerImage(UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_AVATAR")
                , headerIMG);

        momentslistView.addHeaderView(header, null, false);

        isInit = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.v(TAG, "Moments-Init");
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

        return rootView;
    }

    private void initListView() {
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
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
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
                } else if($actiontype.equals("good")){
                    goodCircle();
                } else if($actiontype.equals("fav")){
                    favCircle();
                } else if($actiontype.equals("comment")){
                    commentCircle();
                }
            }
        };

        parent.addView(rootView);
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

    ///刪除班級圈
    private void delCircle() {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity()); //創建訊息方塊
        ad.setTitle("Delete current Circle!");
        ad.setMessage("are you Sure?");
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() { //按"是",則退出應用程式
            public void onClick(DialogInterface dialog, int i) {
                WebService.SetCircleDelete(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
                    @Override
                    public void CompleteCallback(String id, Object obj) {
                        initListView();
                    }
                });
            }
        });

        ad.setNegativeButton("No", new DialogInterface.OnClickListener() { //按"否",則不執行任何操作
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad.show();//顯示訊息視窗
    }
    private void goodCircle(){
        WebService.GetCircleHadGood(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "Error!");
                    return;
                }
                JSONObject json = (JSONObject) obj;
                if (json.optString("GOOD_CNT").equals("1")){
                    Log.v(TAG,"goodCircle : 已按過讚!");
                }else{
                    WebService.SetCircleGood(null, callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
                        @Override
                        public void CompleteCallback(String id, Object obj) {
                           Log.v(TAG,"obj:"+obj);
                        }
                    });
                }
            }
        });
    }
    private void favCircle(){

    }
    private void commentCircle(){

    }

}
