package com.app.AppBabySH;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.AppBabySH.adapter.SetFavChannelAdapter;
import com.app.AppBabySH.item.NewsItem;
import com.app.Common.listviewswipemenu.SwipeMenu;
import com.app.Common.listviewswipemenu.SwipeMenuCreator;
import com.app.Common.listviewswipemenu.SwipeMenuItem;
import com.app.Common.listviewswipemenu.SwipeMenuListView;
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
    private SwipeMenuListView mLvContent;

    @Override
    public void onDestroy() {
        super.onDestroy();
        main.AddTabHost();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        rootView = inflater.inflate(R.layout.profile_set_favchannel_fragment, container, false);
        initView();
        getData();
        return rootView;
    }

    private void initView() {
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetFavChannelBack);
        mImgbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.RemoveBottom(thisFragment);
            }
        });

        mLvContent = (SwipeMenuListView) rootView.findViewById(R.id.lvSetFavChannelContent);
        favlist = new ArrayList<NewsItem>();
        adapter = new SetFavChannelAdapter(getActivity(), favlist);
        mLvContent.setAdapter(adapter);
        mLvContent.setOnItemClickListener(new clickTheme());//  開啟主題
        initListViewSwipeMenu();
    }

    private void initListViewSwipeMenu() {
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                /*// create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);*/

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(90));// set item width
                deleteItem.setIcon(R.drawable.ic_delete);// set a icon
                menu.addMenuItem(deleteItem);// add to menu
            }
        };
        // set creator
        mLvContent.setMenuCreator(creator);

        // step 2. listener item click event
        mLvContent.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int _pos = position;
                switch (index) {
                    case 0:
                        showDialog("确认", "确定从收藏中删除此频道吗?", "确定", "取消", getActivity(), new DialogCallBack() {
                            @Override
                            public void onEnter() {
                                showLoadingDiaLog(getActivity(), "删除中,请稍后...");
                                WebService.SetChannelFavGood("Baby_Set_Channel_Favorite", null, UserMstr.userData.getUserID(), favlist.get(_pos).CHANNEL_ID, "CLEAR", new WebService.WebCallback() {

                                    @Override
                                    public void CompleteCallback(String id, Object obj) {
                                        cancleDiaLog();
                                        if (obj.equals("1")) {
                                            DisplayToast("频道已删除");
                                            favlist.remove(_pos);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            DisplayToast("频道删除失败");
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                        break;
                }
                return false;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void getData() {
        showLoadingDiaLog(getActivity(), "资料读取中，请稍后...");
        WebService.GetFavoriteList(null, UserMstr.userData.getUserID(), new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                cancleDiaLog();
                if (obj == null) {
                    showOKDiaLog(getActivity(), "取得资讯错误！");
                    return;
                }
                JSONArray json = (JSONArray) obj;
                if (json.length() == 0) {
                    showOKDiaLog(getActivity(), "没有任何资讯！");
                    return;
                }
                favlist.clear();
                int i = -1;
                while (++i < json.length()) {
                    NewsItem item = new NewsItem();
                    item.CHANNEL_ID = json.optJSONObject(i).optString("CHANNEL_ID");
                    item.CHANNEL_TITLE = json.optJSONObject(i).optString("CHANNEL_TITLE");
                    item.THUMB_URL = json.optJSONObject(i).optString("THUMB_URL");
                    item.MEDIA_TYPE = json.optJSONObject(i).optString("MEDIA_TYPE");
                    item.MEDIA_CONTENT = json.optJSONObject(i).optString("MEDIA_CONTENT");
                    item.GOOD_CNT = "null";
                    item.FAVORITE_CNT = "null";
                    favlist.add(item);
                }
                mLvContent.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    //  開啟主題
    private class clickTheme implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NewsItem _item = favlist.get(position);
            NewsChannelFragment newsItemFragment = new NewsChannelFragment();
            newsItemFragment.onCallBack = new NewsChannelFragment.itmeCallBack() {
                @Override
                public void onBack() {
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