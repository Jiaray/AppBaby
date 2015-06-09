package com.app.AppBabySH.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.AppBabySH.R;
import com.app.AppBabySH.item.NewsItem;

import java.util.ArrayList;

import com.app.Common.ImageLoader;

/**
 * 頻道 - 主畫面 ListView 的 Adapter
 */
public class NewsAdapter extends BaseAdapter {
    private LayoutInflater minflater;
    private ArrayList<NewsItem> list;// 資料
    private ViewHolder viewHolder;

    public NewsAdapter(Context context,
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
        final NewsItem item = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = minflater.inflate(R.layout.news_item, null);
            viewHolder.titleTxt = (TextView) convertView.findViewById(R.id.txtNewsTitle);
            viewHolder.goodTxt = (TextView) convertView.findViewById(R.id.news_goodTxt);
            viewHolder.favTxt = (TextView) convertView.findViewById(R.id.news_favTxt);
            viewHolder.mainIMGIMG = (ImageView) convertView.findViewById(R.id.news_imageImg);
            viewHolder.goodIMG = (ImageView) convertView.findViewById(R.id.news_goodImg);
            viewHolder.favIMG = (ImageView) convertView.findViewById(R.id.news_favImg);
            viewHolder.newIMG = (ImageView) convertView.findViewById(R.id.imgNewsNewLogo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item != null) {
            viewHolder.titleTxt.setText(item.CHANNEL_TITLE);
            viewHolder.goodTxt.setText(item.GOOD_CNT);
            viewHolder.favTxt.setText(item.FAVORITE_CNT);
            ImageLoader.getInstance().DisplayImage(item.THUMB_URL, viewHolder.mainIMGIMG);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView titleTxt, goodTxt, favTxt;
        ImageView newIMG, mainIMGIMG, goodIMG, favIMG;
    }
}