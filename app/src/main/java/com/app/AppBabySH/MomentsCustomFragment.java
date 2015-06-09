package com.app.AppBabySH;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.adapter.MomentsAdapter;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.MyAlertDialog;
import com.app.AppBabySH.adapter.MomentsCustomAdapter;
import com.app.AppBabySH.item.MomentsImageItem;
import com.app.AppBabySH.item.MomentsItem;
import com.app.Common.PullDownView;
import com.app.Common.ScrollOverListView;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;

import java.util.ArrayList;

import com.app.Common.ImageLoader;

/**
 * Created by ray on 2015/5/22.
 */
public class MomentsCustomFragment extends BaseFragment {
    final private String TAG = "MomentsCustomFragment";
    //  初始必須帶入資料
    public String USER_ID, Class_ID, NIC_NAME, USER_AVATAR;
    //
    private View rootView;
    private MainTabActivity main;
    private MomentsCustomFragment thisFragment;
    //
    private ViewGroup viewG;
    private PullDownView pullDownView;
    private ImageButton mImgBBack;
    private TextView mTxtTitle;
    private ScrollOverListView mSlvContent;
    private MomentsCustomAdapter adapter;
    private ArrayList<MomentsItem> momentslist;
    private int listPosition = 0;
    private boolean isInit = true;
    private LayoutInflater _inflater;
    private Toast toast;
    private int MOMENTS_COUNT;
    private int pageIndex;

    private Handler hdrMain;

    public CallBack onCallBack;

    public interface CallBack {
        public void onBack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onCallBack != null) onCallBack.onBack();
        main.AddTabHost();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        _inflater = inflater;
        rootView = inflater.inflate(R.layout.moments_custom_frag, container, false);
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        initView();
        return rootView;
    }

    private void initView() {
        Log.v(TAG, "Moments-Personal-Init");
        isInit = true;
        pageIndex = 1;

        mImgBBack = (ImageButton) rootView.findViewById(R.id.imgbtnMomentsPersonalBack);
        mImgBBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.RemoveBottom(thisFragment);
            }
        });
        mTxtTitle = (TextView) rootView.findViewById(R.id.txtMomentsPersonalTitle);
        if (NIC_NAME.equals("MomentsNews")) {
            mTxtTitle.setText("MomentsNews");
        } else {
            mTxtTitle.setText(NIC_NAME + "的班級圈");
        }

        //ListView Ready
        pullDownView = (PullDownView) rootView.findViewById(R.id.pdvMomentsPersonalContent);
        pullDownView.enableAutoFetchMore(true, 0);
        pullDownView.setOnPullDownListener(new PullDownView.OnPullDownListener() {

            @Override
            public void onRefresh() {//Refresh
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Log.v(TAG, "Moments-Personal-Refresh");
                        refreshListView();
                    }
                }, 100);
            }

            @Override
            public void onLoadMore() {//LoadMore
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Log.v(TAG, "Moments-Personal-Refresh");
                        pageIndex++;
                        getData();
                        pullDownView.notifyDidLoadMore(momentslist.isEmpty());
                    }
                }, 1000);
            }
        });


        mSlvContent = pullDownView.getListView();
        momentslist = new ArrayList<MomentsItem>();
        adapter = new MomentsCustomAdapter(getActivity(), momentslist);
        adapter.onCallBack = new AdapterCallBack();
        mSlvContent.setAdapter(adapter);


        //ListView Add Header
        hdrMain = new Handler();
        hdrMain.post(runInit);
    }

    private Runnable runInit = new Runnable() {
        @Override
        public void run() {
            //ListView Add Header
            ViewGroup header = (ViewGroup) _inflater.inflate(R.layout.moments_header, mSlvContent, false);
            ImageView headerIMG = (ImageView) header.findViewById(R.id.imgMomentsHeaderUserHead);
            ((ViewGroup.MarginLayoutParams) headerIMG.getLayoutParams()).topMargin = 50;
            if (USER_AVATAR.equals("")) {
                headerIMG.setVisibility(View.GONE);
            } else {
                ImageLoader.getInstance().DisplayRoundedCornerImage(USER_AVATAR, headerIMG);
            }
            TextView mTxtNews = (TextView) header.findViewById(R.id.txtMomentsHeaderNews);
            mTxtNews.setVisibility(View.GONE);
            mSlvContent.addHeaderView(header, null, false);

            //Create ListView
            getData();
            hdrMain.removeCallbacks(runInit);
        }
    };

    //  取得 Web 資料
    private void getData() {
        showLoadingDiaLog(getActivity(), "Personal Loading...");
        if (NIC_NAME.equals("MomentsNews")) {
            WebService.GetCircleListNotRead(null, USER_ID, Class_ID, String.valueOf(pageIndex), "3", new webCallBack());
        } else {
            WebService.GetCircleListPersonal(null, USER_ID, Class_ID, String.valueOf(pageIndex), "3", new webCallBack());
        }
    }

    class webCallBack implements WebService.WebCallback {
        @Override
        public void CompleteCallback(String id, Object obj) {
            cancleDiaLog();
            if (obj == null) {
                showOKDiaLog(getActivity(), "Personal Error!");
                return;
            }
            JSONArray json = (JSONArray) obj;
            JSONArray infoAry = json.optJSONObject(0).optJSONArray("CIRCLE_INFO");
            JSONArray atchAry = json.optJSONObject(0).optJSONArray("CIRCLE_ATCH");
            JSONArray replyAry = json.optJSONObject(0).optJSONArray("CIRCLE_REPLY");
            JSONArray goodAry = json.optJSONObject(0).optJSONArray("CIRCLE_GOOD");
            if (infoAry.length() == 0) {
                showOKDiaLog(getActivity(), "Personal Empty!");
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
            refreshPullView();
        }
    }

    //  刷新列表
    private void refreshListView() {
        pageIndex = 1;
        momentslist.clear();
        ImageLoader.getInstance().clearCache();
        System.gc();
        getData();
    }

    //  刷新外框View
    private void refreshPullView() {
        if (isInit) {
            isInit = false;
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
            pullDownView.notifyDidRefresh(momentslist.isEmpty());
        }

        if (momentslist.size() == MOMENTS_COUNT) {
            pullDownView.notifyDidDataLoad(true);
        } else {
            pullDownView.notifyDidDataLoad(false);
        }
    }

    // MomentsAdapterCallBack 班級圈各個 Item 的功能(刪除、按讚、收藏、回覆、圖示放大瀏覽)
    class AdapterCallBack implements MomentsCustomAdapter.CallBack {

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
            momentsImageFragment.openFunBar = false;
            momentsImageFragment.SEQ = $item.SEQ;
            //  MomentsImageFragmentCallBack
            momentsImageFragment.onCallBack = new MomentsImageFragment.imgCallBack() {
                @Override
                public void onBack(MomentsItem $item, String $actionType) {
                    main.RemoveTab();
                }
            };
            main.OpenBottom(momentsImageFragment);
        }
    }
}
