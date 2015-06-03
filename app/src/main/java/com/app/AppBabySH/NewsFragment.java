package com.app.AppBabySH;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.AppBabySH.UIBase.BaseFragment;
import com.app.AppBabySH.adapter.NewsAdapter;
import com.app.AppBabySH.item.NewsItem;
import com.app.AppBabySH.UIBase.MyAlertDialog;
import com.app.Common.PullDownView;
import com.app.Common.ScrollOverListView;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;

import java.util.ArrayList;

public class NewsFragment extends BaseFragment {
    private static final String TAG = "NewsFragment";
    private View rootView;
    private PullDownView pullDownView;
    private ScrollOverListView newslistView;
    private NewsAdapter adapter;
    private ProgressDialog pd;
    private MainTabActivity main;
    private ArrayList<NewsItem> newslist;
    private int listPosition = 0;//目前定位
    private boolean isLoading = false;//是否加載中
    private boolean isInit = true;//是否首次進入

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main = (MainTabActivity) getActivity();
        rootView = inflater.inflate(R.layout.news_fragment, container, false);
        pullDownView = (PullDownView) rootView.findViewById(R.id.pdwNewsContent);
        pullDownView.enableAutoFetchMore(true, 0);
        newslistView = pullDownView.getListView();
        isInit = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "頻道初始化");
                initListView();
            }
        }, 500);

        pullDownView.setOnPullDownListener(new PullDownView.OnPullDownListener() {

            @Override
            public void onRefresh() {//刷新
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Log.i(TAG, "頻道刷新");
                        initListView();
                    }
                }, 100);
            }

            @Override
            public void onLoadMore() {//加載更多
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Log.i(TAG, "頻道加載更多");
                        adapter.notifyDataSetChanged();
                        pullDownView.notifyDidLoadMore(newslist.isEmpty());
                    }
                }, 1000);
            }
        });

        isLoading = false;
        return rootView;
    }

    private void initListView() {
        if (newslist == null) {
            newslist = new ArrayList<NewsItem>();
        }
        clearList();
        getData();
    }


    private void getData() {
        pd = MyAlertDialog.ShowProgress(getActivity(), "資料讀取中...");
        pd.show();
        WebService.GetNews(null, UserMstr.userData.getUserID(), "P", new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                pd.cancel();
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "取得資訊錯誤！");
                    return;
                }
                JSONArray json = (JSONArray) obj;

                if (json.length() == 0) {
                    MyAlertDialog.Show(getActivity(), "沒有任何資訊！");
                    return;
                }
                Log.i(TAG, "頻道資料取得成功 json:" + json.toString());

                // TODO 塞值進陣列中
                if (newslist == null) {
                    newslist = new ArrayList<NewsItem>();
                }
                Integer i = -1;
                while (++i < json.length()) {
                    NewsItem item = new NewsItem();
                    item.CHANNEL_ID = json.optJSONObject(i).optString("CHANNEL_ID");//"CHANNEL_ID":"C201504090003"
                    item.CHANNEL_TITLE = json.optJSONObject(i).optString("CHANNEL_TITLE");//"CHANNEL_TITLE":"Channel 3"
                    item.THUMB_URL = json.optJSONObject(i).optString("THUMB_URL");//"THUMB_URL":"http://img.appbaby.net/test_skichannel.jpg"
                    item.MEDIA_TYPE = json.optJSONObject(i).optString("MEDIA_TYPE");//"MEDIA_TYPE":"U"
                    item.MEDIA_CONTENT = json.optJSONObject(i).optString("MEDIA_CONTENT");//"MEDIA_CONTENT":"http://www.apple.com"
                    item.GOOD_CNT = json.optJSONObject(i).optString("GOOD_CNT");//"GOOD_CNT":2
                    item.FAVORITE_CNT = json.optJSONObject(i).optString("FAVORITE_CNT");//"FAVORITE_CNT":2
                    newslist.add(item);
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
        newslistView = pullDownView.getListView();
        adapter.onCallBack = new NewsAdapter.CallBack() {

            @Override
            public void onClick(NewsItem _item) {
                clickTheme(_item);
            }
        };
        adapter.notifyDataSetChanged();
        newslistView.setAdapter(adapter);
        newslistView.setSelection(listPosition);
        parent.addView(rootView);
        setPullView();
        Log.i(TAG, "頻道畫面產生完畢!");
    }

    private void setPullView(){
        if(isInit){
            isInit = false;
            adapter.notifyDataSetChanged();
            pullDownView.notifyDidDataLoad(false);
        }else{
            adapter.notifyDataSetChanged();
            pullDownView.notifyDidRefresh(newslist.isEmpty());
        }
    }

    /**
     * 清除列表
     */
    private void clearList() {
        if (adapter != null) {
            //adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
        if (newslist != null) {
            newslist.clear();
        }
        newslistView.setAdapter(null);
        adapter = new NewsAdapter(getActivity(), newslist);
    }

    /**
     * 開啟主題
     *
     * @param _item
     */
    private void clickTheme(final NewsItem _item) {
        if (isLoading) return;
        isLoading = true;
        NewsChannelFragment newsItemFragment = new NewsChannelFragment();
        newsItemFragment.onCallBack = new NewsChannelFragment.itmeCallBack() {
            @Override
            public void onBack() {
                clearList();
                isLoading = false;
                getData();
            }
        };
        newsItemFragment.CHANNEL_ID = _item.CHANNEL_ID;
        newsItemFragment.CHANNEL_TITLE = _item.CHANNEL_TITLE;
        newsItemFragment.THUMB_URL = _item.THUMB_URL;
        newsItemFragment.MEDIA_TYPE = _item.MEDIA_TYPE;
        newsItemFragment.MEDIA_CONTENT = _item.MEDIA_CONTENT;
        newsItemFragment.GOOD_CNT = _item.GOOD_CNT;
        newsItemFragment.FAVORITE_CNT = _item.FAVORITE_CNT;
        main.OpenBottom(newsItemFragment);
        Log.i(TAG, "點擊頻道 :" + _item.CHANNEL_ID.toString());
    }
}