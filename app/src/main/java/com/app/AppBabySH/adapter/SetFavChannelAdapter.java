package com.app.AppBabySH.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.AppBabySH.R;
import com.app.AppBabySH.item.NewsItem;

import java.util.ArrayList;

import com.app.Common.ImageLoader;

/**
 * 收藏頻道 - 主畫面 ListView 的 Adapter
 */
public class SetFavChannelAdapter extends BaseAdapter {
    private LayoutInflater minflater;
    private ArrayList<NewsItem> list;// 資料
    private ViewHolder viewHolder;

    public SetFavChannelAdapter(Context context,
                                ArrayList<NewsItem> _list) {
        if (context == null) return;
        this.minflater = LayoutInflater.from(context);
        this.list = _list;
    }

    @Override
    public int getCount() {
        return (list == null) ? 0 : list.size();
    }

    @Override
    public NewsItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = null;
        NewsItem item = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = minflater.inflate(R.layout.profile_set_favchannel_item, null);
            viewHolder.hLyAll = (LinearLayout) convertView.findViewById(R.id.lySetFavChannel);
            viewHolder.hImgPic = (ImageView) convertView.findViewById(R.id.imgSetFavChannelPic);
            viewHolder.hTxtTitle = (TextView) convertView.findViewById(R.id.imgSetFavChannelTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (item != null) {
            viewHolder.hTxtTitle.setText(item.CHANNEL_TITLE);
            ImageLoader.getInstance().DisplayImage(item.THUMB_URL, viewHolder.hImgPic);
        }
        return convertView;
    }

    static class ViewHolder {
        LinearLayout hLyAll;
        TextView hTxtTitle;
        ImageView hImgPic;
    }
}