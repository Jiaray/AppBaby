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

import lazylist.ImageLoader;

/**
 * 頻道 - 主畫面 ListView 的 Adapter
 */
public class NewsAdapter extends BaseAdapter {
    private LayoutInflater minflater;
    private ArrayList<NewsItem> list;// 資料
    public ImageLoader imageLoader;
    private ViewHolder viewHolder;


    public interface CallBack {
        public void onClick(NewsItem _item);
    }

    public CallBack onCallBack;

    public NewsAdapter(Context context,
                       ArrayList<NewsItem> _list) {
        if (context == null) return;
        this.minflater = LayoutInflater.from(context);
        this.list = _list;
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder = null;
        final NewsItem item = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = minflater.inflate(R.layout.newsmain_item, null);
            viewHolder.titleTxt = (TextView) convertView.findViewById(R.id.news_titleTxt);
            viewHolder.goodTxt = (TextView) convertView.findViewById(R.id.news_goodTxt);
            viewHolder.favTxt = (TextView) convertView.findViewById(R.id.news_favTxt);
            viewHolder.mainIMGIMG = (ImageView) convertView.findViewById(R.id.news_imageImg);
            viewHolder.goodIMG = (ImageView) convertView.findViewById(R.id.news_goodImg);
            viewHolder.favIMG = (ImageView) convertView.findViewById(R.id.news_favImg);
            viewHolder.newIMG = (ImageView) convertView.findViewById(R.id.news_newImg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item != null) {
            viewHolder.titleTxt.setText(item.CHANNEL_TITLE);
            viewHolder.goodTxt.setText(item.GOOD_CNT);
            viewHolder.favTxt.setText(item.FAVORITE_CNT);
            viewHolder.item = item;
            /*if (item.R_STATUS.equals("0")) {
                viewHolder.bg.setImageResource(R.drawable.list_bg_yellow);
            } else {
                viewHolder.bg.setImageResource(R.drawable.internalmsg_listbg);
            }*/
            imageLoader.DisplayImage(item.THUMB_URL, viewHolder.mainIMGIMG);
            viewHolder.mainIMGIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCallBack.onClick(item);
                }
            });
        }
        return convertView;

    }


    static class ViewHolder {
        TextView titleTxt, goodTxt, favTxt;
        ImageView newIMG,mainIMGIMG,goodIMG,favIMG;
        NewsItem item;
    }

}