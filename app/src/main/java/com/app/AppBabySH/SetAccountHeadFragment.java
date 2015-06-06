package com.app.AppBabySH;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.AppBabySH.base.BaseFragment;
import com.app.Common.FileCache;
import com.app.Common.ImageLoader;
import com.app.Common.MyAlertDialog;
import com.app.Common.UserMstr;
import com.app.Common.WebService;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static com.app.Common.ComFun.Md5;

public class SetAccountHeadFragment extends BaseFragment {
    final private String TAG = "setAccountHeadF";
    private MainTabActivity main;
    private View rootView;
    private SetAccountHeadFragment thisFragment;

    private ImageButton mImgbBack;
    private ImageView mImgUserHead;
    private Button mBtnCommit, mBtnChoose;

    private boolean change;
    private String mCurrentPhotoPath;
    private File data;
    private String key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //共用宣告
        main = (MainTabActivity) getActivity();
        thisFragment = this;
        rootView = inflater.inflate(R.layout.profile_set_acc_head_fragment, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initView();
        return rootView;
    }

    private void initView() {
        change = false;
        mImgbBack = (ImageButton) rootView.findViewById(R.id.imgbSetAccHeadBack);
        mBtnCommit = (Button) rootView.findViewById(R.id.btnSetAccHeadCommit);
        mBtnChoose = (Button) rootView.findViewById(R.id.btnSetAccHeadChoose);
        mImgUserHead = (ImageView) rootView.findViewById(R.id.imgSetAccHeadPic);
        mImgbBack.setOnClickListener(new onClick());
        mBtnCommit.setOnClickListener(new onClick());
        mBtnChoose.setOnClickListener(new onClick());
        ImageLoader.getInstance().DisplayRoundedCornerImage(
                UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("USER_AVATAR"), mImgUserHead);
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbSetAccHeadBack:
                    main.RemoveBottomNotAddTab(thisFragment);
                    break;
                case R.id.btnSetAccHeadChoose:
                    openAlbum();
                    break;
                case R.id.btnSetAccHeadCommit:
                    if (change) {
                        uploadPic();
                    } else {
                        DisplayToast("尚未更换头像");
                    }
                    break;
            }
        }
    }

    //  打开本地相册
    public void openAlbum() {
        ImageLoader.getInstance().clearCache();
        SelectMultiImgFragment selectImg = new SelectMultiImgFragment();
        selectImg.Max_Num = 1;
        selectImg.onCallBack = new SelectMultiImgFragment.CallBack() {
            @Override
            public void onEnter(List imgList) {
                showLoadingDiaLog(getActivity(), "图片处理中");
                if (imgList.size() > 0) {
                    change = true;
                    File f = new File(imgList.get(0).toString());
                    mCurrentPhotoPath = FileCache.getInstance().getCachePath() + Md5(f.getName() + System.currentTimeMillis(), true) + ".jpg";
                    ImageLoader.getInstance().AChangeSmallSizeToB(f.getAbsolutePath(), mCurrentPhotoPath);
                    ImageLoader.getInstance().DisplayRoundedCornerImage(mCurrentPhotoPath, mImgUserHead);
                }
                cancleDiaLog();
            }

            @Override
            public void onBack() {
                main.RemoveTab();
            }
        };
        main.OpenBottom(selectImg);
    }

    //  取得七牛驗證碼
    private void uploadPic() {
        showLoadingDiaLog(getActivity(), "图片上传中...");
        WebService.GetUpToken(null, "baby-m", new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                if (obj == null) {
                    MyAlertDialog.Show(getActivity(), "GetUpToken Error!");
                    return;
                }
                data = new File(mCurrentPhotoPath);
                key = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf("/") + 1, mCurrentPhotoPath.length());
                UploadManager uploadManager = new UploadManager();
                uploadManager.put(data, key, obj.toString(),
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                updateWebData();
                            }
                        }, null);
            }
        });
    }

    //  更新資料庫數據
    private void updateWebData() {
        final String USER_AVATAR = getResources().getString(R.string.BaseWebUrl) + key;
        WebService.SetChangeAvatar(null,
                UserMstr.userData.getUserID(),
                USER_AVATAR, new WebService.WebCallback() {
                    @Override
                    public void CompleteCallback(String id, Object obj) {
                        if (obj == null) {
                            MyAlertDialog.Show(getActivity(), "SetChangeAvatar Error!");
                            return;
                        }
                        cancleDiaLog();
                        showOKDiaLog(getActivity(), "头像設置完成");
                        JSONObject tmpObj = UserMstr.userData.getBaseInfoAry().optJSONObject(0);
                        try {
                            tmpObj.put("USER_AVATAR", USER_AVATAR);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        main.RemoveBottomNotAddTab(thisFragment);
                    }
                });
    }
}