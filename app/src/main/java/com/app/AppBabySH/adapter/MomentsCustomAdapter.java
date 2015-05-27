package com.app.AppBabySH.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.app.AppBabySH.GlobalVar;
import com.app.AppBabySH.R;
import com.app.AppBabySH.item.MomentsImageItem;
import com.app.AppBabySH.item.MomentsItem;

import java.util.ArrayList;

/**
 * Created by ray on 2015/5/22.
 */
public class MomentsCustomAdapter extends BaseAdapter {
    private static final String TAG = "MomentsCustomAdapter";
    private LayoutInflater minflater;
    private ArrayList<MomentsItem> list;
    private ArrayList<MomentsImageItem> momentsIMGlist;
    private MomentsImageAdapter adapter;
    private ViewHolder viewHolder;
    private GlobalVar centerV;
    private Integer i;

    public interface CallBack {
        public void onImgAdapterClick(MomentsImageItem $item);
    }

    public CallBack onCallBack;

    public MomentsCustomAdapter(Context context,
                                ArrayList<MomentsItem> _list) {
        if (context == null) return;
        centerV = (GlobalVar) context.getApplicationContext();
        this.minflater = LayoutInflater.from(context);
        this.list = _list;
    }

    static class ViewHolder {
        TextView mTxtDate, mTxtMonth, mTxtContent, mTxtPicNum;
        GridView mGdvImages;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = null;
        final MomentsItem item = list.get(position);
        final MomentsItem checkItem;
        checkItem = position > 0 ? list.get(position - 1) : null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = minflater.inflate(R.layout.moments_custom_item, null);
            viewHolder.mTxtDate = (TextView) convertView.findViewById(R.id.txtMomentsPersonalDate);
            viewHolder.mTxtMonth = (TextView) convertView.findViewById(R.id.txtMomentsPersonalMonth);
            viewHolder.mTxtContent = (TextView) convertView.findViewById(R.id.txtMomentsPersonalContent);
            viewHolder.mTxtPicNum = (TextView) convertView.findViewById(R.id.txtMomentsPersonalPicNum);
            viewHolder.mGdvImages = (GridView) convertView.findViewById(R.id.gdvMomentsPersonalImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item != null) {
            viewHolder.mTxtDate.setText(getDateType(item.ENTRY_DATE, "date"));
            viewHolder.mTxtMonth.setText(getDateType(item.ENTRY_DATE, "month"));
            if (checkItem != null && item.ENTRY_DATE.equals(checkItem.ENTRY_DATE)) {
                viewHolder.mTxtDate.setTextColor(Color.TRANSPARENT);
                viewHolder.mTxtMonth.setTextColor(Color.TRANSPARENT);
            } else {
                viewHolder.mTxtDate.setTextColor(Color.BLACK);
                viewHolder.mTxtMonth.setTextColor(Color.BLACK);
            }
            viewHolder.mTxtContent.setText(item.DESCRIPTION);
            viewHolder.mTxtPicNum.setText("共 " + item.ATCH.size() + " 張");
            createImage(item, convertView);
        }

        return convertView;
    }


    private String getDateType(String $date, String $type) {
        String getStr = $date;
        if ($type.equals("date")) {
            getStr = $date.substring(6, $date.length());
        } else if ($type.equals("month")) {
            switch (Integer.valueOf($date.substring(4, 6))) {
                case 1:
                    getStr = "一月";
                    break;
                case 2:
                    getStr = "二月";
                    break;
                case 3:
                    getStr = "三月";
                    break;
                case 4:
                    getStr = "四月";
                    break;
                case 5:
                    getStr = "五月";
                    break;
                case 6:
                    getStr = "六月";
                    break;
                case 7:
                    getStr = "七月";
                    break;
                case 8:
                    getStr = "八月";
                    break;
                case 9:
                    getStr = "九月";
                    break;
                case 10:
                    getStr = "十月";
                    break;
                case 11:
                    getStr = "十一月";
                    break;
                case 12:
                    getStr = "十二月";
                    break;
            }
        }
        return getStr;
    }

    /*創建圖示*/
    private void createImage(MomentsItem item, View convertView) {
        //清空IMG資料列
        if (momentsIMGlist != null) {
            momentsIMGlist.clear();
        } else {
            momentsIMGlist = new ArrayList<MomentsImageItem>();
        }
        viewHolder.mGdvImages.setAdapter(null);
        adapter = new MomentsImageAdapter(convertView.getContext(), momentsIMGlist, "personal");
        adapter.onImgCallBack = new MomentsImageAdapter.callBackImgItem() {
            @Override
            public void onImgClick(MomentsImageItem $item) {
                onCallBack.onImgAdapterClick($item);
            }

            @Override
            public void onAddClick() {
            }
        };
        if (item.ATCH.size() > 1) {
            viewHolder.mGdvImages.setNumColumns(2);
        } else {
            viewHolder.mGdvImages.setNumColumns(1);
        }

        //創建內容
        i = -1;
        while (++i < item.ATCH.size()) {
            if (i >= 4) continue;
            MomentsImageItem imgitem = new MomentsImageItem();
            imgitem.CIRCLE_ID = item.ATCH.get(i).optString("CIRCLE_ID");
            imgitem.MEDIA_TYPE = item.ATCH.get(i).optString("MEDIA_TYPE");
            imgitem.URL = item.ATCH.get(i).optString("URL");
            imgitem.SEQ = item.ATCH.get(i).optString("SEQ");
            momentsIMGlist.add(imgitem);
        }
        viewHolder.mGdvImages.setAdapter(adapter);
    }
}
