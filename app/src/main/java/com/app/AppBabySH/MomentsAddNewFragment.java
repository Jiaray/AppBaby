package com.app.AppBabySH;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.MyAlertDialog;
import com.app.AppBabySH.adapter.MomentsImageAdapter;
import com.app.AppBabySH.item.MomentsImageItem;
import com.app.Common.UserMstr;
import com.app.Common.WebService;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lazylist.FileCache;
import lazylist.ImageLoader;

import static com.app.Common.ComFun.Md5;

/**
 * Created by ray on 2015/5/23.
 */
public class MomentsAddNewFragment extends BaseFragment {
    final private String TAG = "MomentsAddNewFragment";
    //  初始必須帶入資料
    public String Class_ID, Add_Type;
    //
    private GlobalVar centerV;
    private View rootView;
    private MainTabActivity main;
    private MomentsAddNewFragment thisFragment;
    private Integer i;
    //
    private TextView mTxtCancel, mTxtSend;
    private EditText mEdtContent;
    private GridView mGdvPreview;
    //
    private ArrayList<MomentsImageItem> momentsIMGlist;
    private MomentsImageAdapter adapter;
    private ProgressDialog pd;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    //上傳用
    private ArrayList<String> aryPicPath;
    private String strUpToken;
    private Integer posPic;
    private File data;
    private JSONArray jsonAryPic;
    private String tmpPath;
    private String mCurrentPhotoPath;// 图片路径

    public CallBack onCallBack;

    public interface CallBack {
        public void onBack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onCallBack != null) onCallBack.onBack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.moments_addnew_fragment, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        centerV = (GlobalVar) rootView.getContext().getApplicationContext();
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        initView();
        return rootView;
    }

    private void initView() {
        aryPicPath = new ArrayList<String>();
        mTxtCancel = (TextView) rootView.findViewById(R.id.txtMomentsAddNewCancel);
        mTxtSend = (TextView) rootView.findViewById(R.id.txtMomentsAddNewSend);
        mEdtContent = (EditText) rootView.findViewById(R.id.edtMomentsAddNewContent);

        //  預覽圖
        mGdvPreview = (GridView) rootView.findViewById(R.id.gdvAddNewPreview);
        ViewGroup.LayoutParams lp = mGdvPreview.getLayoutParams();
        lp.height = (int) (centerV.windowWidth * 0.33) * 3;
        mGdvPreview.setLayoutParams(lp);
        createPreview();

        mTxtCancel.setOnClickListener(new AddNewOnClickListener());
        mTxtSend.setOnClickListener(new AddNewOnClickListener());
    }

    private void createPreview() {
        mGdvPreview.setAdapter(null);
        momentsIMGlist = new ArrayList<MomentsImageItem>();
        i = -1;
        while (++i < aryPicPath.size()) {
            MomentsImageItem imgitem = new MomentsImageItem();
            imgitem.CIRCLE_ID = "";
            imgitem.MEDIA_TYPE = "P";
            imgitem.URL = aryPicPath.get(i);
            imgitem.SEQ = "" + (momentsIMGlist.size() + 1);
            momentsIMGlist.add(imgitem);
        }
        if (momentsIMGlist.size() < 9) {
            Log.i(TAG, "Add Empty Item!!!! size:" + momentsIMGlist.size());
            momentsIMGlist.add(new MomentsImageItem());
        }
        adapter = new MomentsImageAdapter(rootView.getContext(), momentsIMGlist, "preview");
        adapter.onImgCallBack = new MomentsImageAdapter.callBackImgItem() {
            @Override
            public void onImgClick(MomentsImageItem $item) {

            }

            @Override
            public void onAddClick() {
                if (Add_Type.equals("albums")) {
                    openAlbum();
                } else if (Add_Type.equals("camera")) {
                    openCamera();
                }
            }
        };
        mGdvPreview.setAdapter(adapter);
    }

    /*點擊按鈕監聽*/
    class AddNewOnClickListener implements View.OnClickListener {
        public AddNewOnClickListener() {

        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txtMomentsAddNewCancel:
                    i = -1;
                    while (++i < aryPicPath.size()) {
                        FileCache.getInstance().deleteTempFile(aryPicPath.get(i));
                    }
                    main.RemoveBottom(thisFragment);
                    break;
                case R.id.txtMomentsAddNewSend:
                    getUpToken();
                    break;
            }
        }
    }

    //  打开本地相册
    public void openAlbum() {
        SelectMultiImgFragment selectImg = new SelectMultiImgFragment();
        selectImg.Max_Num = 9 - aryPicPath.size();
        selectImg.onCallBack = new SelectMultiImgFragment.CallBack() {
            @Override
            public void onEnter(List imgList) {
                pd = MyAlertDialog.ShowProgress(getActivity(), "图片处理中");
                pd.show();
                i = -1;
                while (++i < imgList.size()) {
                    File f = new File(imgList.get(i).toString());
                    mCurrentPhotoPath = FileCache.getInstance().getCachePath() + Md5(f.getName() + System.currentTimeMillis(), true) + ".jpg";
                    ImageLoader.getInstance().AChangeSmallSizeToB(f.getAbsolutePath(), mCurrentPhotoPath);
                    aryPicPath.add(mCurrentPhotoPath);
                }
                pd.cancel();
                createPreview();
            }

            @Override
            public void onBack() {
                main.RemoveTab();
            }
        };
        main.OpenBottom(selectImg);
    }

    //  拍照
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            // 指定存放拍摄照片的位置
            File f = createImageFile();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                tmpPath = mCurrentPhotoPath;
                File f = new File(tmpPath);
                mCurrentPhotoPath = FileCache.getInstance().getCachePath() + f.getName().substring(4, f.getName().length());
                ImageLoader.getInstance().AChangeSmallSizeToB(tmpPath, mCurrentPhotoPath);
                FileCache.getInstance().deleteTempFile(tmpPath);
                Log.i(TAG, "添加到暫存图库");
                aryPicPath.add(mCurrentPhotoPath);
                createPreview();
            } catch (Exception e) {
                Log.e(TAG, "error", e);
            }
        } else {
            Log.i(TAG, "取消照相后，删除已经创建的临时文件");
            // 取消照相后，删除已经创建的临时文件。
            FileCache.getInstance().deleteTempFile(mCurrentPhotoPath);
        }
    }


    /**
     * 把程序拍摄的照片放到 SD卡的 AppBabySH/Cache 文件夹中
     * 照片的命名规则为：Ray+系統時間(毫秒).jpg >> MD5轉換
     *
     * @throws IOException
     */
    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        String imageFileName = "Ori_" + Md5("Ray" + System.currentTimeMillis(), true) + ".jpg";
        File image = new File(FileCache.getInstance().getCachePath(), imageFileName);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //  取得七牛驗證碼
    private void getUpToken() {
        WebService.GetUpToken(null, "baby-m", new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "GetUpToken Error!");
                    return;
                }
                strUpToken = obj.toString();
                posPic = 0;
                jsonAryPic = new JSONArray();
                uploadPic(posPic);
            }
        });
    }

    //  開始上傳照片
    private void uploadPic(int $pos) {
        posPic = ($pos + 1);
        pd = MyAlertDialog.ShowProgress(getActivity(), "图片上传中(" + posPic + "/" + aryPicPath.size() + ")...");
        pd.show();
        data = new File(aryPicPath.get($pos));
        String key = aryPicPath.get($pos).substring(aryPicPath.get($pos).lastIndexOf("/") + 1, aryPicPath.get($pos).length());
        String token = strUpToken;
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(data, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        JSONObject jsonObj = new JSONObject();
                        try {
                            jsonObj.put("MEDIA_TYPE", "P");
                            jsonObj.put("URL", "http://img.appbaby.net/" + key);
                            jsonObj.put("SEQ", "" + posPic);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        jsonAryPic.put(jsonObj);

                        Log.i(TAG, "complete!");
                        if (posPic == aryPicPath.size()) {
                            connectWeb();
                        } else {
                            uploadPic(posPic);
                        }
                    }
                }, null);
    }

    //  更新資料庫數據
    private void connectWeb() {
        WebService.SetCircleNew(null,
                UserMstr.userData.getUserID(),
                Class_ID, mEdtContent.getText().toString(),
                UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_TYPE"), "", "", String.valueOf(jsonAryPic.length()), jsonAryPic.toString(), new WebService.WebCallback() {
                    @Override
                    public void CompleteCallback(String id, Object obj) {
                        if (obj == null) {
                            MyAlertDialog.Show(getActivity(), "SetCircleNew Error!");
                            return;
                        }
                        pd.cancel();
                        main.RemoveBottom(thisFragment);
                    }
                });
    }
}
