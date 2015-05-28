package com.app.AppBabySH;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.AppBabySH.UIBase.BaseFragment;
import com.app.AppBabySH.UIBase.MyAlertDialog;
import com.app.Common.SelectMultiImg.SelectMultiImgAdapter;
import com.app.Common.SelectMultiImg.SelectMultiImgDirItem;
import com.app.Common.SelectMultiImg.SelectMultiImgDirPopupWindow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ray on 2015/5/27.
 */
public class SelectMultiImgFragment extends BaseFragment implements SelectMultiImgDirPopupWindow.OnImageDirSelected {
    private static final String TAG = "SelectMultiImgFragment";
    public Integer Max_Num;
    //
    private View rootView;
    private MainTabActivity main;
    private SelectMultiImgFragment thisFragment;
    private ProgressDialog mProgressDialog;

    /**
     * 存儲文件夾中的圖片數量
     */
    private int mPicsSize;
    /**
     * 圖片數量最多的文件夾
     */
    private File mImgDir;
    /**
     * 所有的圖片
     */
    private String allImgsFolderPath = "/0/所有图片";
    private List<String> mAllImgs;
    private List<String> mImgs;

    private GridView mGirdView;
    private SelectMultiImgAdapter mAdapter;
    /**
     * 臨時的輔助類，用於防止同一個文件夾的多次掃描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 掃描拿到所有的圖片文件夾
     */
    private List<SelectMultiImgDirItem> mImageFloders = new ArrayList<SelectMultiImgDirItem>();

    private Button mImgBEnter;
    private RelativeLayout mBottomLy;

    private TextView mChooseDir;
    private TextView mImageCount;

    private int mScreenHeight;

    private SelectMultiImgDirPopupWindow mListImageDirPopupWindow;



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.selectmultiimg_fragment,
                container, false);
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        mAdapter.mSelectedImage = new LinkedList<String>();
        mAdapter.MaxNum = Max_Num;
        initView();
        getImages();
        initEvent();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onCallBack.onBack();
    }

    public CallBack onCallBack;

    public interface CallBack {
        public void onEnter(List imgList);
        public void onBack();
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Log.i(TAG, "Handler-handleMessage:" + msg);
            mProgressDialog.dismiss();
            // 為View綁定數據
            data2View();
            // 初始化展示文件夾的popupWindw
            initListDirPopupWindw();
        }
    };

    /**
     * 為View綁定數據
     */
    private void data2View() {
        if (mImgDir == null) {
            Toast.makeText(rootView.getContext().getApplicationContext(), "没扫描到图片", Toast.LENGTH_SHORT).show();
            return;
        }
        showAllPhoto();
    }

    ;

    /**
     * 初始化展示文件夾的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new SelectMultiImgDirPopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(rootView.getContext().getApplicationContext())
                .inflate(R.layout.selectmultiimage_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // 設置背景顏色變暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        // 設置選擇文件夾的回調
        mListImageDirPopupWindow.setOnImageDirSelected(thisFragment);
    }

    /**
     * 利用ContentProvider掃描手機中的圖片，此方法在運行在子線程中 完成圖片的掃描，最終獲得jpg最多的那個文件夾
     */
    private void getImages() {
        Log.i(TAG, "getImages!");
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(rootView.getContext().getApplicationContext(), "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 顯示進度條
        mProgressDialog = ProgressDialog.show(getActivity(), null, "正在加载...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String firstImage = null;
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getActivity().getContentResolver();

                // 只查詢jpeg和png的圖片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                //Log.i("Ray", "總數量:" + mCursor.getCount());

                //所有圖片選項
                SelectMultiImgDirItem imageFloder = new SelectMultiImgDirItem();
                String path;
                mAllImgs = new ArrayList<String>();
                while (mCursor.moveToNext()) {
                    // 獲取圖片的路徑
                    path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    mAllImgs.add(path);
                    // 拿到第一張圖片的路徑
                    if (firstImage == null) {
                        firstImage = path;
                        //  新增"所有圖片"的選項
                        imageFloder.setDir(allImgsFolderPath);
                        imageFloder.setFirstImagePath(firstImage);
                        imageFloder.setCount(mAllImgs.size());
                        mImageFloders.add(imageFloder);
                    }
                    // 獲取該圖片的父路徑名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    // 利用一個HashSet防止多次掃描同一個文件夾（不加這個判斷，圖片多起來還是相當恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new SelectMultiImgDirItem();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);

                    if (picSize > mPicsSize) {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();

                //  補上全部圖片的數量
                int i = -1;
                while (++i < mImageFloders.size()) {
                    if (mImageFloders.get(i).getDir().equals(allImgsFolderPath)) {
                        mImageFloders.get(i).setCount(mAllImgs.size());
                    }
                }

                // 掃描完成，輔助的HashSet也就可以釋放內存了
                mDirPaths = null;

                // 通知Handler掃描圖片完成
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();

    }

    /**
     * 初始化View
     */
    private void initView() {
        mGirdView = (GridView) rootView.findViewById(R.id.gdvSelectMultiImgImgs);
        mChooseDir = (TextView) rootView.findViewById(R.id.txtSelectMultiImgDirItemName);
        mImageCount = (TextView) rootView.findViewById(R.id.txtSelectMultiImgNum);
        mBottomLy = (RelativeLayout) rootView.findViewById(R.id.lySelectMultiImgChooseDir);
        mImgBEnter = (Button) rootView.findViewById(R.id.btnSelectMultiImgEnter);
    }

    private void initEvent() {
        mImgBEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBack.onEnter(mAdapter.mSelectedImage);
                main.RemoveBottom(thisFragment);
            }
        });
        /**
         * 為底部的布局設置點擊事件，彈出popupWindow
         */
        mBottomLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

                // 設置背景顏色變暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = .3f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    //@Override
    public void selected(SelectMultiImgDirItem floder) {
        mImgDir = new File(floder.getDir());
        if (mImgDir.toString().equals(allImgsFolderPath)) {
            showAllPhoto();
        } else {
            mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
                        return true;
                    return false;
                }
            }));
            mAdapter = new SelectMultiImgAdapter(rootView.getContext().getApplicationContext(), mImgs, R.layout.selectmultiimage_photo_item, mImgDir.getAbsolutePath());
            mGirdView.setAdapter(mAdapter);
            mImageCount.setText("共 "+floder.getCount() + " 张");
            mChooseDir.setText(floder.getName());
        }
        // mAdapter.notifyDataSetChanged();
        mListImageDirPopupWindow.dismiss();
    }

    private void showAllPhoto() {
        mAdapter = new SelectMultiImgAdapter(rootView.getContext().getApplicationContext(), mAllImgs, R.layout.selectmultiimage_photo_item, "all");
        mGirdView.setAdapter(mAdapter);
        mChooseDir.setText(allImgsFolderPath.substring(allImgsFolderPath.lastIndexOf("/")));
        mImageCount.setText("共 "+mAllImgs.size() + " 张");
    }

}
