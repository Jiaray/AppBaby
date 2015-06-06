package com.app.AppBabySH.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.app.AppBabySH.GlobalVar;
import com.app.AppBabySH.R;
import com.app.AppBabySH.item.MomentsImageItem;

import java.util.ArrayList;

import com.app.Common.ImageLoader;

/**
 * Created by ray on 2015/5/13.
 */
public class MomentsImageAdapter extends BaseAdapter {
    private static final String TAG = "MomentsImageAdapter";
    private LayoutInflater minflater;
    private ArrayList<MomentsImageItem> list;
    private GlobalVar centerV;
    private String strSizeType;
    private Integer imageSize;
    public callBackImgItem onImgCallBack;

    public interface callBackImgItem {
        public void onImgClick(MomentsImageItem $item);

        public void onAddClick();
    }

    public MomentsImageAdapter(Context $context,
                               ArrayList<MomentsImageItem> $list,
                               String $sizeType) {
        if ($context == null) return;
        centerV = (GlobalVar) $context.getApplicationContext();
        this.minflater = LayoutInflater.from($context);
        this.list = $list;
        strSizeType = $sizeType;
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
        if (strSizeType.equals("normal")) {
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
        } else if (strSizeType.equals("personal")) {
            if (list.size() <= 2) {
                imageSize = (int) ((double) centerV.windowWidth * 0.35);
            } else {
                imageSize = (int) ((double) centerV.windowWidth * 0.175);
            }
        } else if (strSizeType.equals("preview")) {
            imageSize = (int) ((double) centerV.windowWidth * 0.3);
        }

        MomentsImageItem item = list.get(position);
        if (strSizeType.equals("preview") && item.URL == null) {
            View addView = minflater.inflate(R.layout.moments_addnew_additem, null);
            addView.findViewById(R.id.add).setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            onImgCallBack.onAddClick();
                        }
                    });
            return addView;
        } else {
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
            ImageLoader.getInstance().DisplayImage(item.URL, iv);
            //ImageLoader.getInstance().DisplayWebUrlImage(item.URL, iv);
            iv.setOnClickListener(new openBigPic(item));
            return iv;
        }
    }

    class openBigPic implements View.OnClickListener {
        private MomentsImageItem imgItem;

        public openBigPic(MomentsImageItem $item) {
            imgItem = $item;
        }

        @Override
        public void onClick(View v) {
            onImgCallBack.onImgClick(imgItem);
        }
    }
}


