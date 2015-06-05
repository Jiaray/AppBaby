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
 * 收藏頻道 - 主畫面 ListView 的 Adapter
 */
public class SetFavChannelAdapter extends BaseAdapter {
    private LayoutInflater minflater;
    private ArrayList<NewsItem> list;// 資料
    public ImageLoader imageLoader;
    private ViewHolder viewHolder;

    public interface CallBack {
        public void onClick(NewsItem _item);
    }

    public CallBack onCallBack;

    public SetFavChannelAdapter(Context context,
                                ArrayList<NewsItem> _list) {
        if (context == null) return;
        this.minflater = LayoutInflater.from(context);
        this.list = _list;
        imageLoader = new ImageLoader();
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
            convertView = minflater.inflate(R.layout.profile_set_favchannel_item, null);
            viewHolder.hImgPic = (ImageView) convertView.findViewById(R.id.imgSetFavChannelPic);
            viewHolder.hTxtTitle = (TextView) convertView.findViewById(R.id.imgSetFavChannelTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item != null) {
            viewHolder.hTxtTitle.setText(item.CHANNEL_TITLE);
            viewHolder.item = item;
            imageLoader.DisplayImage(item.THUMB_URL, viewHolder.hImgPic);
            viewHolder.hImgPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCallBack.onClick(item);
                }
            });
        }
        return convertView;
    }

    static class ViewHolder {
        TextView hTxtTitle;
        ImageView hImgPic;
        NewsItem item;
    }
}