package com.app.AppBabySH.adapter;

import android.content.Context;
import android.graphics.Color;
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

import com.app.AppBabySH.GlobalVar;
import com.app.AppBabySH.R;
import com.app.AppBabySH.item.MomentsImageItem;
import com.app.AppBabySH.item.MomentsItem;
import com.app.Common.ComFun;

import java.util.ArrayList;

import com.app.Common.ImageLoader;

/**
 * Created by ray on 2015/5/11.
 */
public class MomentsAdapter extends BaseAdapter {
    private static final String TAG = "MomentsAdapter";
    private LayoutInflater minflater;
    private ArrayList<MomentsItem> list;
    private ArrayList<MomentsImageItem> momentsIMGlist;
    private ViewHolder viewHolder;
    private Integer i = -1, j;
    private String contentStr;
    private String replyNameColor = "#930000";
    private String targetNameColor = "#930000";
    private GlobalVar centerV;
    private MomentsImageAdapter adapter;
    private ViewGroup onClickVG;
    private View targetV;

    public interface CallBack {
        public void onClick(String $actionType, MomentsItem $item);

        public void onCommentClick(MomentsItem $item, String $atReplySN, String $atReplyName);

        public void onImgAdapterClick(MomentsImageItem $item);
    }

    public CallBack onCallBack;

    public MomentsAdapter(Context context,
                          ArrayList<MomentsItem> _list) {
        if (context == null) return;
        centerV = (GlobalVar) context.getApplicationContext();
        this.minflater = LayoutInflater.from(context);
        this.list = _list;
    }

    static class ViewHolder {
        TextView mTxtName, mTxtTitle, mTxtClass, mTxtDate;
        ImageView mImgHeader;
        LinearLayout mLyDel, mLySetGood, mLySetFav, mLyToComment;
        ImageButton mImgbCommentFun;
        GridView mGdvPic;
        LinearLayout mLyComment, mLyGood, mLyReply;
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
            viewHolder.mImgHeader = (ImageView) convertView.findViewById(R.id.imgMomentsItemHeader);
            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.txtMomentsItemName);
            viewHolder.mTxtTitle = (TextView) convertView.findViewById(R.id.txtMomentsItemTitle);
            viewHolder.mGdvPic = (GridView) convertView.findViewById(R.id.gdvMomentsItemPic);
            viewHolder.mTxtClass = (TextView) convertView.findViewById(R.id.txtMomentsItemClassName);
            viewHolder.mLyComment = (LinearLayout) convertView.findViewById(R.id.lyMomentsItemComment);
            viewHolder.mImgbCommentFun = (ImageButton) convertView.findViewById(R.id.btnMomentsItemCommentFun);
            viewHolder.mLyDel = (LinearLayout) convertView.findViewById(R.id.lyMomentsItemDel);
            viewHolder.mLySetGood = (LinearLayout) convertView.findViewById(R.id.lyMomentsItemSetGood);
            viewHolder.mLySetFav = (LinearLayout) convertView.findViewById(R.id.lyMomentsItemSetFav);
            viewHolder.mLyToComment = (LinearLayout) convertView.findViewById(R.id.lyMomentsItemToComment);
            viewHolder.mTxtDate = (TextView) convertView.findViewById(R.id.txtMomentsItemDate);
            viewHolder.mLyGood = (LinearLayout) convertView.findViewById(R.id.lyMomentsItemGood);
            viewHolder.mLyReply = (LinearLayout) convertView.findViewById(R.id.lyMomentsItemReply);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item != null) {
            ImageLoader.getInstance().DisplayRoundedCornerImage(item.USER_AVATAR, viewHolder.mImgHeader);
            viewHolder.mLyComment.setVisibility(View.GONE);
            viewHolder.mTxtName.setText(item.NIC_NAME);
            viewHolder.mTxtTitle.setText(item.DESCRIPTION);
            viewHolder.mTxtClass.setText(item.SCHOOL_NAME + item.CLASS_NAME);
            viewHolder.mTxtDate.setText(ComFun.date2UserSee(item.ENTRY_DATE, "/") + " " + ComFun.time2UserSee(item.ENTRY_TIME, ":").substring(0, 5));
            // TODO 創建圖片
            createImage(item, convertView);

            // TODO 設定按讚人數
            setGoodInfo(item);

            // TODO 創建回覆列表
            createReply(item, convertView);

            viewHolder.mImgHeader.setOnClickListener(new MyOnClickListener(item));
            viewHolder.mImgbCommentFun.setOnClickListener(new MyOnClickListener(item));
            viewHolder.mLyDel.setOnClickListener(new MyOnClickListener(item));
            viewHolder.mLySetGood.setOnClickListener(new MyOnClickListener(item));
            viewHolder.mLySetFav.setOnClickListener(new MyOnClickListener(item));
            viewHolder.mLyToComment.setOnClickListener(new AtReply(item, "", ""));
        }
        return convertView;
    }


    /*創建圖示*/
    private void createImage(final MomentsItem item, View convertView) {
        //清空IMG資料列
        if (momentsIMGlist != null) {
            momentsIMGlist.clear();
        } else {
            momentsIMGlist = new ArrayList<MomentsImageItem>();
        }
        viewHolder.mGdvPic.setAdapter(null);
        adapter = new MomentsImageAdapter(convertView.getContext(), momentsIMGlist, "normal");
        adapter.onImgCallBack = new MomentsImageAdapter.callBackImgItem() {
            @Override
            public void onImgClick(MomentsImageItem $item) {
                onCallBack.onImgAdapterClick($item);
            }

            @Override
            public void onAddClick() {
            }
        };
        //設定顯示列數
        switch (item.ATCH.size()) {
            case 1:
                viewHolder.mGdvPic.setNumColumns(1);
                break;
            case 4:
            case 2:
                viewHolder.mGdvPic.setNumColumns(2);
                break;
            default:
                viewHolder.mGdvPic.setNumColumns(3);
                break;
        }

        //先把高度設定好(原因:GridView in ListView 當內容 Adapter 後，畫面無法展開)
        ViewGroup.LayoutParams lp = viewHolder.mGdvPic.getLayoutParams();
        if (item.ATCH.size() == 0) {
            lp.height = 0;
        } else if (item.ATCH.size() == 1) {
            lp.height = (int) (centerV.windowWidth * centerV.momentsImageSizeAry[2]);
        } else if (item.ATCH.size() == 2) {
            lp.height = (int) (centerV.windowWidth * centerV.momentsImageSizeAry[1]);
        } else if (item.ATCH.size() == 3) {
            lp.height = (int) (centerV.windowWidth * centerV.momentsImageSizeAry[0]);
        } else if (item.ATCH.size() == 4) {
            lp.height = (int) (centerV.windowWidth * centerV.momentsImageSizeAry[1]) * 2;
        } else if (item.ATCH.size() > 4 && item.ATCH.size() <= 6) {
            lp.height = (int) (centerV.windowWidth * centerV.momentsImageSizeAry[0]) * 2;
        } else if (item.ATCH.size() > 6 && item.ATCH.size() <= 9) {
            lp.height = (int) (centerV.windowWidth * centerV.momentsImageSizeAry[0]) * 3;
        }
        viewHolder.mGdvPic.setLayoutParams(lp);

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
        viewHolder.mGdvPic.setAdapter(adapter);
    }

    /*設定按讚人數*/
    private void setGoodInfo(MomentsItem item) {
        if (item.GOOD.size() == 0) {
            viewHolder.mLyGood.setVisibility(View.GONE);
        } else {
            viewHolder.mLyGood.setVisibility(View.VISIBLE);
            contentStr = "";
            i = -1;
            while (++i < item.GOOD.size()) {
                contentStr += item.GOOD.get(i).optString("NIC_NAME");
                if ((i + 1) < item.GOOD.size()) contentStr += ",";
            }
            TextView peopleTxt = (TextView) viewHolder.mLyGood.findViewById(R.id.txtMomentsItemPeople);
            peopleTxt.setText(contentStr);
        }
    }

    /*創建回覆列表*/
    private void createReply(MomentsItem item, View convertView) {
        viewHolder.mLyReply.removeAllViews();
        i = -1;
        while (++i < item.REPLY.size()) {
            Button replyBtn = new Button(convertView.getContext());
            replyBtn.setGravity(Gravity.CENTER | Gravity.LEFT);
            replyBtn.setBackgroundColor(Color.TRANSPARENT);
            replyBtn.setTextSize(16);
            contentStr = "<font color=\"" + replyNameColor + "\">" + item.REPLY.get(i).optString("NIC_NAME") + "</font>";
            if (!item.REPLY.get(i).optString("AT_REPLY_SN").equals("")) {
                j = -1;
                getReplySN:
                while (++j < item.REPLY.size()) {
                    if (item.REPLY.get(i).optString("AT_REPLY_SN").equals(item.REPLY.get(j).optString("REPLY_SN"))) {
                        contentStr += "回覆<font color=\"" + targetNameColor + "\">" + item.REPLY.get(j).optString("NIC_NAME") + " : </font>";
                        break getReplySN;
                    }
                }
            } else {
                contentStr += "<font color=\"" + replyNameColor + "\"> : </font>";
            }
            replyBtn.setOnClickListener(new AtReply(item, item.REPLY.get(i).optString("REPLY_SN"), item.REPLY.get(i).optString("NIC_NAME")));
            contentStr += item.REPLY.get(i).optString("REPLY_DESC");
            replyBtn.setText(Html.fromHtml(contentStr));
            viewHolder.mLyReply.addView(replyBtn);
        }
    }

    /*回覆*/
    class AtReply implements View.OnClickListener {
        private MomentsItem callBackItem;
        private String atSN, atName;

        public AtReply(MomentsItem $itme, String $atSN, String $atName) {
            callBackItem = $itme;
            atSN = $atSN;
            atName = $atName;
        }

        public void onClick(View v) {
            Log.v(TAG, "AtReply onClick!");
            onCallBack.onCommentClick(callBackItem, atSN, atName);
            if (targetV != null) targetV.setVisibility(View.GONE);
        }
    }

    /*點擊按鈕監聽*/
    class MyOnClickListener implements View.OnClickListener {
        private MomentsItem callBackItem;

        public MyOnClickListener(MomentsItem $item) {
            callBackItem = $item;
        }

        public void onClick(View v) {
            switch (v.getId()) {
                //Open Personal Page
                case R.id.imgMomentsItemHeader:
                    if (targetV != null) targetV.setVisibility(View.GONE);
                    onCallBack.onClick("personal", callBackItem);
                    break;
                //Open Comment
                case R.id.btnMomentsItemCommentFun:
                    onClickVG = (ViewGroup) v.getParent();
                    targetV = onClickVG.findViewById(R.id.lyMomentsItemComment);
                    if (targetV.isShown()) {
                        targetV.setVisibility(View.GONE);
                    } else {
                        targetV.setVisibility(View.VISIBLE);
                    }
                    break;

                //About Comment
                case R.id.lyMomentsItemDel://Del Circle
                    if (targetV != null) targetV.setVisibility(View.GONE);
                    onCallBack.onClick("del", callBackItem);
                    break;
                case R.id.lyMomentsItemSetGood://Set Circle Good
                    if (targetV != null) targetV.setVisibility(View.GONE);
                    onCallBack.onClick("good", callBackItem);
                    break;
                case R.id.lyMomentsItemSetFav://Fav Circle
                    if (targetV != null) targetV.setVisibility(View.GONE);
                    onCallBack.onClick("fav", callBackItem);
                    break;
            }
        }
    }

}


