package com.app.AppBabySH.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.app.AppBabySH.CenterVariable;
import com.app.AppBabySH.item.MomentsImageItem;

import java.util.ArrayList;

import lazylist.ImageLoader;

/**
 * Created by ray on 2015/5/13.
 */
public class MomentsImageAdapter extends BaseAdapter {
    private static final String TAG = "MomentsImageAdapter";
    private LayoutInflater minflater;
    private ArrayList<MomentsImageItem> list;
    public ImageLoader imageLoader;
    private CenterVariable centerV;
    private Integer imageSize;

    public MomentsImageAdapter(Context context,
                               ArrayList<MomentsImageItem> _list) {
        //Log.v(TAG, "in MomentsImageAdapter");
        if (context == null) return;
        centerV = (CenterVariable) context.getApplicationContext();
        this.minflater = LayoutInflater.from(context);
        this.list = _list;
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (list.size()) {
            case 1:
                imageSize = (int) ((double) centerV.windowWidth * centerV.momentsImageSizeAry[2]);
                break;
            case 2:
            case 4:
                imageSize = (int) ((double) centerV.windowWidth * centerV.momentsImageSizeAry[1]);
                break;
            default:
                imageSize = (int) ((double) centerV.windowWidth * centerV.momentsImageSizeAry[0]);
                break;
        }
        MomentsImageItem item = list.get(position);
        ImageView iv;
        if (convertView == null) {
            iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new GridView.LayoutParams(imageSize, imageSize));
            iv.setAdjustViewBounds(false);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //iv.setPadding(18, 18, 18, 18);
        } else {
            iv = (ImageView) convertView;
        }
        //Log.v(TAG, "CIRCLE_ID:" + item.CIRCLE_ID + "ImageAdapter Url:" + item.URL);
        imageLoader.DisplayImage(item.URL, iv);
        return iv;
    }
}
