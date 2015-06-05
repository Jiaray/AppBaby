package com.app.AppBabySH;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.MyAlertDialog;
import com.app.AppBabySH.item.MomentsItem;
import com.app.Common.ImgViewPage.GestureImageView;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lazylist.ImageLoader;

/**
 * Created by ray on 2015/5/20.
 */
public class MomentsImageFragment extends BaseFragment {
    private static final String TAG = "MomentsImageFragment";
    //  初始必須帶入資料
    public MomentsItem CricleItem;
    public Boolean openFunBar = false;
    public String SEQ = "1";
    //
    private View rootView;
    private MainTabActivity main;
    private MomentsImageFragment thisFragment;
    //介面元件
    private RelativeLayout mLyFunBar;
    private ViewPager mVPimage;
    private ImageButton mImgBtnBack;
    private TextView mTxtNo, mTxtGood, mTxtFav, mTxtContent;
    private LinearLayout mLyGood,mLyFav,mLyComment;

    //
    private ArrayList<String> imgPaths;
    private Integer pos;
    private FullScreenImageAdapter adapter;
    private AlertDialog.Builder alertD;
    private Boolean hadGood;

    public imgCallBack onCallBack;
    private String afterBackAction;

    public interface imgCallBack {
        public void onBack(MomentsItem $item, String $actionType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.moments_imageview_fragment,
                    container, false);
            main = (MainTabActivity) getActivity();
            thisFragment = this;
            createRootView();
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onCallBack != null) onCallBack.onBack(CricleItem, afterBackAction);
    }

    private void createRootView() {
        mLyFunBar = (RelativeLayout) rootView.findViewById(R.id.lyMomentsImageFun);
        mTxtNo = (TextView) rootView.findViewById(R.id.txtMomentsImageNo);
        mTxtNo.setText(SEQ + "/" + CricleItem.ATCH.size());
        mTxtGood = (TextView) rootView.findViewById(R.id.txtMomentsImageGood);
        mTxtFav = (TextView) rootView.findViewById(R.id.txtMomentsImageFav);
        mImgBtnBack = (ImageButton) rootView.findViewById(R.id.imgbtnMomentsImageBack);
        mTxtContent = (TextView) rootView.findViewById(R.id.txtMomentsImageContent);
        mTxtContent.setText(CricleItem.DESCRIPTION);
        mLyGood = (LinearLayout) rootView.findViewById(R.id.lyMomentsImageGood);
        mLyFav = (LinearLayout) rootView.findViewById(R.id.lyMomentsImageFav);
        mLyComment = (LinearLayout) rootView.findViewById(R.id.lyMomentsImageComment);
        mLyGood.setOnClickListener(new ImageFragmentClick());
        mLyFav.setOnClickListener(new ImageFragmentClick());
        mLyComment.setOnClickListener(new ImageFragmentClick());
        mImgBtnBack.setOnClickListener(new ImageFragmentClick());
        mVPimage = (ViewPager) rootView.findViewById(R.id.vpMomentsImageContent);
        if(!openFunBar){
            mTxtContent.setVisibility(View.VISIBLE);
            mLyFunBar.setVisibility(View.GONE);
        }else{
            mTxtContent.setVisibility(View.GONE);
            mLyFunBar.setVisibility(View.VISIBLE);
        }
        imgPaths = new ArrayList<String>();
        int i = -1;
        while (++i < CricleItem.ATCH.size()) imgPaths.add(CricleItem.ATCH.get(i).optString("URL"));
        pos = Integer.valueOf(SEQ) - 1;
        adapter = new FullScreenImageAdapter(imgPaths);
        mVPimage.setAdapter(adapter);
        mVPimage.setCurrentItem(pos);
        mVPimage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTxtNo.setText((position + 1) + "/" + CricleItem.ATCH.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        setWebInfo();
    }

    //  設定網路按讚收藏資訊
    private void setWebInfo() {
        mTxtGood.setText("" + 0);
        mTxtFav.setText("" + 0);
        WebService.GetCircleHadGood(null, CricleItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                if (obj == null) {
                    DisplayToast("無法點讚資訊!");
                    return;
                }
                JSONObject json = (JSONObject) obj;
                if (json.length() == 0) {
                    DisplayToast("無法點讚資訊!");
                    return;
                }
                if (json.optString("GOOD_CNT").equals("1")) {
                    hadGood = true;
                } else {
                    hadGood = false;
                }
                mTxtGood.setText("" + CricleItem.GOOD.size());
            }
        });

        WebService.GetCircleKeepToGrowCount(null, CricleItem.CIRCLE_ID, new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                if (obj == null) {
                    DisplayToast("無法取得收藏數量!");
                    return;
                }
                JSONObject json = (JSONObject) obj;
                if (json.length() == 0) {
                    DisplayToast("無法取得收藏數量!");
                    return;
                }
                mTxtFav.setText("" + json.optInt("GROW_CNT"));
            }
        });
    }

    //  按鈕功能
    class ImageFragmentClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbtnMomentsImageBack:
                    afterBackAction = "";
                    main.RemoveBottom(thisFragment);
                    break;
                case R.id.lyMomentsImageGood:
                    goodCircle();
                    break;
                case R.id.lyMomentsImageFav:
                    favCircle();
                    break;
                case R.id.lyMomentsImageComment:
                    afterBackAction = "comment";
                    main.RemoveBottom(thisFragment);
                    break;
            }
        }
    }

    //  載入圖片
    class FullScreenImageAdapter extends PagerAdapter {

        private ArrayList<String> _imagePaths;

        // constructor
        public FullScreenImageAdapter(ArrayList<String> imagePaths) {
            this._imagePaths = imagePaths;
        }

        @Override
        public int getCount() {
            return this._imagePaths.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((GestureImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GestureImageView imgDisplay = new GestureImageView(rootView.getContext());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            ImageLoader.getInstance().DisplayWebUrlImage(this._imagePaths.get(position), imgDisplay);
            ((ViewPager) container).addView(imgDisplay);
            return imgDisplay;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((GestureImageView) object);

        }
    }


    //  對班級圈按讚
    private void goodCircle() {
        if (hadGood) {
            DisplayToast("已按讚!");
        } else {
            WebService.SetCircleGood(null, CricleItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
                @Override
                public void CompleteCallback(String id, Object obj) {
                    Map map = new HashMap();
                    map.put("CIRCLE_ID", CricleItem.CIRCLE_ID);
                    map.put("USER_ID", UserMstr.userData.getUserID());
                    map.put("NIC_NAME", UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("NIC_NAME"));
                    JSONObject newJsonObj = new JSONObject(map);
                    // 按下"收到"以後要做的事情
                    CricleItem.GOOD.add(newJsonObj);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    //  收藏班級圈
    private void favCircle() {
        WebService.GetCircleHadKeepToGrow(null, CricleItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "Error!");
                    return;
                }
                JSONObject json = (JSONObject) obj;
                if (json.optString("GROW_CNT").equals("1")) {
                    DisplayToast("已收藏!");
                } else {
                    WebService.SetCircleKeepToGrow(null, CricleItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
                        @Override
                        public void CompleteCallback(String id, Object obj) {
                            alertD = new AlertDialog.Builder(getActivity());
                            alertD.setTitle("檢查"); //設定dialog 的title顯示內容
                            alertD.setMessage("收藏成功!");
                            alertD.setCancelable(false); //關閉 Android 系統的主要功能鍵(menu,home等...)
                            alertD.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertD.show();
                        }
                    });
                }
            }
        });
    }
}
