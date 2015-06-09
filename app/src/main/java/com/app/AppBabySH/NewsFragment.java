package com.app.AppBabySH;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.AppBabySH.adapter.NewsAdapter;
import com.app.AppBabySH.item.NewsItem;
import com.app.Common.PullDownView;
import com.app.Common.ScrollOverListView;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;

import java.util.ArrayList;

public class NewsFragment extends BaseFragment {
    private static final String TAG = "NewsF";
    private View rootView;
    private MainTabActivity main;
    private ArrayList<NewsItem> newslist;

    private PullDownView pullDownView;
    private ScrollOverListView mSlvNewsContent;
    private NewsAdapter adapter;

    private boolean isInit = true;//是否首次進入
    private int pageIndex;
    private int NEWS_COUNT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main = (MainTabActivity) getActivity();
        rootView = inflater.inflate(R.layout.news_fragment, container, false);
        isInit = true;
        initView();
        getData();
        return rootView;
    }

    private void initView() {
        pageIndex = 1;
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        pullDownView = (PullDownView) rootView.findViewById(R.id.pdwNewsContent);
        pullDownView.enableAutoFetchMore(true, 0);
        pullDownView.setOnPullDownListener(new PullDownView.OnPullDownListener() {

            @Override
            public void onRefresh() {//刷新
                refreshListView();
            }

            @Override
            public void onLoadMore() {//加載更多
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Log.i(TAG, "頻道加載更多");
                        pageIndex++;
                        getData();
                        pullDownView.notifyDidLoadMore(newslist.isEmpty());
                    }
                }, 1000);
            }
        });

        mSlvNewsContent = pullDownView.getListView();
        newslist = new ArrayList<NewsItem>();
        adapter = new NewsAdapter(getActivity(), newslist);
        mSlvNewsContent.setAdapter(adapter);
        mSlvNewsContent.setOnItemClickListener(new clickTheme());
    }

    private void getData() {
        showLoadingDiaLog(getActivity(), "資料讀取中...");
        WebService.GetNews(null, UserMstr.userData.getUserID(), "P", String.valueOf(pageIndex), "3", new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                cancleDiaLog();
                if (obj == null) {
                    showOKDiaLog(getActivity(), "取得資訊錯誤！");
                    return;
                } else if (obj.equals("Success! No Data Return!")) {
                    /*showOKDiaLog(getActivity(), "!");
                    return;*/
                }
                Log.i(TAG,"obj:"+obj);
                JSONArray json = (JSONArray) obj;

                if (json.length() == 0) {
                    showOKDiaLog(getActivity(), "沒有任何資訊！");
                    return;
                }
                Log.i(TAG, "頻道資料取得成功 json:" + json.toString());
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
                NEWS_COUNT = Integer.valueOf(json.optJSONObject(0).optString("COUNT"));
                refreshPullView();
            }
        });
    }

    private void refreshPullView() {
        if (isInit) {
            isInit = false;
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
            pullDownView.notifyDidRefresh(newslist.isEmpty());
        }

        if (newslist.size() == NEWS_COUNT) {
            pullDownView.notifyDidDataLoad(true);
        } else {
            pullDownView.notifyDidDataLoad(false);
        }
        //Log.i(TAG, "頻道畫面產生完畢!");
    }

    private void refreshListView(){
        Log.i(TAG, "頻道刷新");
        pageIndex = 1;
        newslist.clear();
        getData();
    }

    //  開啟主題
    private class clickTheme implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NewsItem _item = newslist.get(position);
            NewsChannelFragment newsItemFragment = new NewsChannelFragment();
            newsItemFragment.onCallBack = new NewsChannelFragment.itmeCallBack() {
                @Override
                public void onBack() {refreshListView();
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
            Log.i(TAG, "SetFavChannelFragment 點擊頻道 : " + _item.CHANNEL_ID.toString());
        }
    }
}