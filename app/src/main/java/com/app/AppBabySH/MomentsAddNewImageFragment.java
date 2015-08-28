package com.app.AppBabySH;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.AppBabySH.item.MomentsItem;
import com.app.Common.ImageLoader;
import com.app.Common.ImgViewPager.GestureImageView;
import com.app.Common.MyAlertDialog;
import com.app.Common.UserMstr;
import com.app.Common.WebService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ray on 2015/5/20.
 */
public class MomentsAddNewImageFragment extends BaseFragment {
    private static final String TAG = "MomentsImageFragment";
    //  初始必須帶入資料
    public ArrayList<String> aryPicPath;
    public String SEQ = "1";
    //
    private View rootView;
    private MainTabActivity main;
    private MomentsAddNewImageFragment thisFragment;
    //介面元件
    private ViewPager mVPimage;
    private TextView mTxtNo;
    private ImageButton mImgBtnBack;

    //
    private Integer pos;
    private FullScreenImageAdapter adapter;

    public imgCallBack onCallBack;
    private Button mbtnDel;

    public interface imgCallBack {
        public void onBack(ArrayList<String> $backPicAry);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.moments_addnew_imageview_fragment,
                    container, false);
            thisFragment = this;
            createRootView();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        main = (MainTabActivity) getActivity();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        onCallBack.onBack(aryPicPath);
        main.RemoveTab();
    }

    private void createRootView() {
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mTxtNo = (TextView) rootView.findViewById(R.id.txtAddNewMomentsImageNo);
        mTxtNo.setText(SEQ + "/" + aryPicPath.size());
        mImgBtnBack = (ImageButton) rootView.findViewById(R.id.imgbtnAddNewMomentsImageBack);
        mImgBtnBack.setOnClickListener(new ImageFragmentClick());
        mbtnDel = (Button) rootView.findViewById(R.id.btnAddNewMomentsImageDel);
        mbtnDel.setOnClickListener(new ImageFragmentClick());
        mVPimage = (ViewPager) rootView.findViewById(R.id.vpAddNewMomentsImageContent);
        pos = Integer.valueOf(SEQ) - 1;
        adapter = new FullScreenImageAdapter(aryPicPath);
        mVPimage.setAdapter(adapter);
        mVPimage.setCurrentItem(pos);
        mVPimage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                SEQ = String.valueOf((position + 1));
                mTxtNo.setText(SEQ + "/" + aryPicPath.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    //  按鈕功能
    class ImageFragmentClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbtnAddNewMomentsImageBack:
                    main.RemoveBottom(thisFragment);
                    break;
                case R.id.btnAddNewMomentsImageDel:
                    int checkNo = Integer.valueOf(SEQ) - 1;
                    aryPicPath.remove(checkNo);
                    if(aryPicPath.size() > 0){
                        checkNo--;
                        if(checkNo < 0)checkNo = 0;
                        SEQ = String.valueOf(checkNo + 1);
                        mTxtNo.setText(SEQ + "/" + aryPicPath.size());
                        pos = Integer.valueOf(SEQ) - 1;
                        adapter = new FullScreenImageAdapter(aryPicPath);
                        mVPimage.setAdapter(adapter);
                        mVPimage.setCurrentItem(pos);
                    }else{
                        main.RemoveBottom(thisFragment);
                    }
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
}
