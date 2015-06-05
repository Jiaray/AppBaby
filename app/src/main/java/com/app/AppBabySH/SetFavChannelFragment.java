package com.app.AppBabySH;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.MyAlertDialog;
import com.app.AppBabySH.adapter.SetFavChannelAdapter;
import com.app.AppBabySH.item.NewsItem;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONArray;

import java.util.ArrayList;

public class SetFavChannelFragment extends BaseFragment {
    final private String TAG = "setFavChannelF";
    private MainTabActivity main;
    private View rootView;
    private SetFavChannelFragment thisFragment;

    private SetFavChannelAdapter adapter;
    private ArrayList<NewsItem> favlist;

    private ImageButton mImgbBack;
    private ListView mLvContent;

    @Override
    public void onDestroy() {
        super.onDestroy();
        main.AddTabHost();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        rootView = inflater.inflate(R.layout.profile_set_favchannel_fragment, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initView();
        getData();
        return rootView;
    }

    private void initView() {
        favlist = new ArrayList<NewsItem>();
        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetFavChannelBack);
        mLvContent = (ListView) rootView.findViewById(R.id.lvSetFavChannelContent);
        adapter = new SetFavChannelAdapter(getActivity(), favlist);
        mLvContent.setAdapter(adapter);
        mImgbBack.setOnClickListener(new onClick());
    }

    private void getData() {
        showLoadingDiaLog(getActivity(), "资料读取中，请稍后...");
        WebService.GetFavoriteList(null, UserMstr.userData.getUserID(), new WebService.WebCallback() {

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
                    NewsItem item = new NewsItem();
                    item.CHANNEL_ID = json.optJSONObject(i).optString("CHANNEL_ID");
                    item.CHANNEL_TITLE = json.optJSONObject(i).optString("CHANNEL_TITLE");
                    item.THUMB_URL = json.optJSONObject(i).optString("THUMB_URL");
                    item.MEDIA_TYPE = json.optJSONObject(i).optString("MEDIA_TYPE");
                    item.MEDIA_CONTENT = json.optJSONObject(i).optString("MEDIA_CONTENT");
                    item.GOOD_CNT = "0";
                    item.FAVORITE_CNT = "0";
                    favlist.add(item);
                }
                mLvContent.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbSetFavChannelBack:
                    main.RemoveBottom(thisFragment);
                    break;
            }
        }
    }
}