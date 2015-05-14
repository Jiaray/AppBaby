package com.app.AppBabySH.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.AppBabySH.CenterVariable;
import com.app.AppBabySH.R;
import com.app.AppBabySH.item.MomentsImageItem;
import com.app.AppBabySH.item.MomentsItem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import lazylist.ImageLoader;

/**
 * Created by ray on 2015/5/11.
 */
public class MomentsAdapter extends BaseAdapter {
    private static final String TAG = "MomentsAdapter";
    private LayoutInflater minflater;
    private ArrayList<MomentsItem> list;// 資料
    private ArrayList<MomentsImageItem> momentsIMGlist;
    public ImageLoader imageLoader;
    private ViewHolder viewHolder;
    private Integer i = -1, j;
    private String contentStr;
    private String replyNameColor = "#930000";
    private String targetNameColor = "#930000";
    private CenterVariable centerV;
    private MomentsImageAdapter adapter;

    public interface CallBack {
        public void onClick(MomentsItem _item);
    }

    public CallBack onCallBack;

    public MomentsAdapter(Context context,
                          ArrayList<MomentsItem> _list) {
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = minflater.inflate(R.layout.moments_item, null);
            viewHolder.headerIMG = (ImageView) convertView.findViewById(R.id.momentsitem_headerIMG);
            viewHolder.nameTxt = (TextView) convertView.findViewById(R.id.momentsitem_nameTxt);
            viewHolder.titleTxt = (TextView) convertView.findViewById(R.id.momentsitem_titleTxt);
            viewHolder.gridView = (GridView) convertView.findViewById(R.id.momentsitem_gridView);
            viewHolder.classTxt = (TextView) convertView.findViewById(R.id.momentsitem_classTxt);
            viewHolder.interactBtn = (ImageButton) convertView.findViewById(R.id.momentsitem_interactBtn);
            viewHolder.dateTxt = (TextView) convertView.findViewById(R.id.momentsitem_dateTxt);
            viewHolder.goodLayout = (LinearLayout) convertView.findViewById(R.id.momentsitem_goodlayout);
            viewHolder.replyLayout = (LinearLayout) convertView.findViewById(R.id.momentsitem_replylayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item != null) {
            imageLoader.DisplayImage(item.USER_AVATAR, viewHolder.headerIMG);
            viewHolder.nameTxt.setText(item.NIC_NAME);
            viewHolder.titleTxt.setText(item.DESCRIPTION);
            viewHolder.classTxt.setText(item.SCHOOL_NAME + item.CLASS_NAME);
            viewHolder.dateTxt.setText(item.ENTRY_DATE + item.ENTRY_TIME);

            // TODO 創建圖片
            createImage(item, convertView);

            // TODO 設定按讚人數
            setGoodInfo(item);

            // TODO 創建回覆列表
            createReply(item, convertView);


            viewHolder.interactBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "設定:" + item.CIRCLE_ID);
                    //onCallBack.onClick(item);
                }
            });
        }
        return convertView;
    }



    /*創建圖示*/
    private void createImage(MomentsItem item, View convertView) {
        //清空IMG資料列
        if (momentsIMGlist != null) {
            momentsIMGlist.clear();
        }else{
            momentsIMGlist = new ArrayList<MomentsImageItem>();
        }
        viewHolder.gridView.setAdapter(null);
        adapter = new MomentsImageAdapter(convertView.getContext(), momentsIMGlist);

        //設定顯示列數
        switch (item.ATCH.size()){
            case 1:
                viewHolder.gridView.setNumColumns(1);
                break;
            case 4:
            case 2:
                viewHolder.gridView.setNumColumns(2);
                break;
            default:
                viewHolder.gridView.setNumColumns(3);
                break;
        }

        //先把高度設定好(原因:GridView in ListView 當內容 Adapter 後，畫面無法展開)
        ViewGroup.LayoutParams  lp = viewHolder.gridView.getLayoutParams();
        if(item.ATCH.size() == 0){
            lp.height = 0;
        }else if(item.ATCH.size() == 1){
            lp.height = (int)(centerV.windowWidth * centerV.momentsImageSizeAry[2]);
        }else if(item.ATCH.size() == 2){
            lp.height = (int)(centerV.windowWidth * centerV.momentsImageSizeAry[1]);
        }else if(item.ATCH.size() == 3){
            lp.height = (int)(centerV.windowWidth * centerV.momentsImageSizeAry[0]);
        }else if(item.ATCH.size() == 4){
            lp.height = (int)(centerV.windowWidth * centerV.momentsImageSizeAry[1]) * 2;
        }else if(item.ATCH.size() > 4 && item.ATCH.size() <= 6){
            lp.height = (int)(centerV.windowWidth * centerV.momentsImageSizeAry[0]) * 2;
        }else if(item.ATCH.size() > 6 && item.ATCH.size() <= 9){
            lp.height = (int)(centerV.windowWidth * centerV.momentsImageSizeAry[0]) * 3;
        }
        viewHolder.gridView.setLayoutParams(lp);

        //創建內容
        i = -1;
        while (++i < item.ATCH.size()) {
            MomentsImageItem imgitem = new MomentsImageItem();
            imgitem.CIRCLE_ID = item.ATCH.get(i).optString("CIRCLE_ID");
            imgitem.MEDIA_TYPE = item.ATCH.get(i).optString("MEDIA_TYPE");
            imgitem.URL = item.ATCH.get(i).optString("URL");
            imgitem.SEQ = item.ATCH.get(i).optString("SEQ");
            momentsIMGlist.add(imgitem);
        }
        viewHolder.gridView.setAdapter(adapter);
    }

    /*設定按讚人數*/
    private void setGoodInfo(MomentsItem item) {
        if (item.GOOD.size() == 0) {
            viewHolder.goodLayout.setVisibility(View.GONE);
        } else {
            viewHolder.goodLayout.setVisibility(View.VISIBLE);
            contentStr = "";
            i = -1;
            while (++i < item.GOOD.size()) {
                contentStr += item.GOOD.get(i).optString("NIC_NAME");
                if ((i + 1) < item.GOOD.size()) contentStr += ",";
            }
            TextView peopleTxt = (TextView) viewHolder.goodLayout.findViewById(R.id.momentsitem_peopleTxt);
            peopleTxt.setText(contentStr);
        }
    }

    /*創建回覆列表*/
    private void createReply(MomentsItem item, View convertView) {
        viewHolder.replyLayout.removeAllViews();
        i = -1;
        while (++i < item.REPLY.size()) {
            Button replyBtn = new Button(convertView.getContext());
            replyBtn.setGravity(Gravity.CENTER | Gravity.LEFT);
            replyBtn.setBackgroundColor(Color.GREEN);
            replyBtn.setTextSize(16);
            contentStr = "<font color=\"" + replyNameColor + "\">" + item.REPLY.get(i).optString("NIC_NAME") + "</font>";
            if (!item.REPLY.get(i).optString("AT_REPLY_SN").equals("")) {
                j = -1;
                getReplySN:
                while (++j < item.REPLY.size()) {
                    if (item.REPLY.get(i).optString("AT_REPLY_SN").equals(item.REPLY.get(j).optString("REPLY_SN"))) {
                        contentStr += "Reply<font color=\"" + targetNameColor + "\">" + item.REPLY.get(j).optString("NIC_NAME") + " : </font>";
                        break getReplySN;
                    }
                }
            } else {
                contentStr += "<font color=\"" + replyNameColor + "\"> : </font>";
            }
            contentStr += item.REPLY.get(i).optString("REPLY_DESC");
            replyBtn.setText(Html.fromHtml(contentStr));
            viewHolder.replyLayout.addView(replyBtn);
        }
    }

    static class ViewHolder {
        TextView nameTxt, titleTxt, classTxt, dateTxt;
        ImageView headerIMG;
        ImageButton interactBtn;
        GridView gridView;
        LinearLayout goodLayout, replyLayout;
    }
}


