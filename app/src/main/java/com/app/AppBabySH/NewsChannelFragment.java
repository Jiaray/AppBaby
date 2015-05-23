package com.app.AppBabySH;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.AppBabySH.UIBase.BaseFragment;
import com.app.AppBabySH.UIBase.MyAlertDialog;
import com.app.Common.UserMstr;
import com.app.Common.WebService;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.laiwang.controller.UMLWHandler;
import com.umeng.socialize.laiwang.media.LWDynamicShareContent;
import com.umeng.socialize.laiwang.media.LWShareContent;
import com.umeng.socialize.media.GooglePlusShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.TwitterShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.umeng.socialize.whatsapp.controller.UMWhatsAppHandler;
import com.umeng.socialize.whatsapp.media.WhatsAppShareContent;
import com.umeng.socialize.yixin.controller.UMYXHandler;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by ray on 2015/4/29.
 */
public class NewsChannelFragment extends BaseFragment {
    private static final String TAG = "NewsChannelFragment";
    private View rootView;
    private MainTabActivity main;
    private NewsChannelFragment thisFragment;

    public itmeCallBack onCallBack;

    public interface itmeCallBack {
        public void onBack();
    }

    /*初始資料*/
    public String CHANNEL_ID, CHANNEL_TITLE, THUMB_URL, MEDIA_TYPE, MEDIA_CONTENT, GOOD_CNT, FAVORITE_CNT;

    /*本頁面Layout*/
    private ImageButton backBtn, shareBtn, goodBtn, favBtn;
    private WebView themeWebv;
    private TextView goodTxt, favTxt;
    private UMSocialService mController;

    /*收藏、按讚狀態*/
    private boolean isGood = false;
    private boolean isFav = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 首先在您的Activity中添加如下成员变量
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        configPlatforms();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.news_channel_fragment,
                    container, false);
            creatRootView();
            // 设置分享的内容
            setShareContent();
            main = (MainTabActivity) getActivity();
            /* 初始化本頁面 */
            thisFragment = this;
        }
        return rootView;
    }

    /**
     * 產生主畫面
     */
    private void creatRootView() {
        backBtn = (ImageButton) rootView.findViewById(R.id.newsitem_backBtn);
        shareBtn = (ImageButton) rootView.findViewById(R.id.newsitem_shareBtn);
        favBtn = (ImageButton) rootView.findViewById(R.id.newsitem_favBtn);
        goodBtn = (ImageButton) rootView.findViewById(R.id.newsitem_goodBtn);
        themeWebv = (WebView) rootView.findViewById(R.id.newsitem_themeWebView);
        goodTxt = (TextView) rootView.findViewById(R.id.newsitem_goodTxt);
        favTxt = (TextView) rootView.findViewById(R.id.newsitem_favTxt);
        goodTxt.setText(GOOD_CNT);
        favTxt.setText(FAVORITE_CNT);
        Log.v(TAG, "開起網頁 Url = " + MEDIA_CONTENT);
        themeWebv.getSettings().setJavaScriptEnabled(true);
        themeWebv.requestFocus();
        themeWebv.setWebViewClient(new MyWebViewClient());
        themeWebv.loadUrl(MEDIA_CONTENT);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.RemoveBottom(thisFragment);
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addCustomPlatforms();
            }
        });

        goodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGoodState();
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavState();
            }
        });
        getGoodState();
        getFavState();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        cancleDiaLog();
        if (onCallBack != null) onCallBack.onBack();
    }

    /**
     * 取得收藏狀態
     */
    private void getFavState() {
        WebService.GetSetChannelFavGood("Baby_Get_Channel_Had_Favorite", null, UserMstr.userData.getUserID(), CHANNEL_ID, new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                if (ObjisNull(obj, "取得收藏資訊錯誤！")) return;
                JSONObject json = (JSONObject) obj;
                if (JsonisEmpty(json, "沒有取得收藏的任何資訊！")) return;
                isFav = json.optString("FAVORITE_CNT").equals("1");
                Log.v(TAG, "已設定為收藏:" + isFav);
                setImgBtnContent(isFav ? "@mipmap/news_favoriteicon" : "@mipmap/global_backicon", favBtn);
            }
        });
    }

    /**
     * 設定收藏狀態
     */
    private void setFavState() {
        WebService.GetSetChannelFavGood("Baby_Set_Channel_Favorite", null, UserMstr.userData.getUserID(), CHANNEL_ID, new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                if (ObjisNull(obj, "已設定收藏")) return;
                isFav = obj.equals("1");
                Log.v(TAG, "已設定為收藏:" + isFav);
                setImgBtnContent(isFav ? "@mipmap/news_favoriteicon" : "@mipmap/global_backicon", favBtn);
                favTxt.setText(String.valueOf(Integer.valueOf(FAVORITE_CNT) + 1));
            }
        });
    }

    /**
     * 取得喜歡狀態
     */
    private void getGoodState() {
        WebService.GetSetChannelFavGood("Baby_Get_Channel_Had_Good", null, UserMstr.userData.getUserID(), CHANNEL_ID, new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                if (ObjisNull(obj, "取得喜歡資訊錯誤！")) return;
                JSONObject json = (JSONObject) obj;
                if (JsonisEmpty(json, "沒有取得喜歡的任何資訊！")) return;
                isGood = json.optString("GOOD_CNT").equals("1");
                Log.v(TAG, "已設定為喜歡:" + isGood);
                setImgBtnContent(isGood ? "@mipmap/news_good" : "@mipmap/global_backicon", goodBtn);
            }
        });
    }

    /**
     * 設定喜歡狀態
     */
    private void setGoodState() {
        WebService.GetSetChannelFavGood("Baby_Set_Channel_Good ", null, UserMstr.userData.getUserID(), CHANNEL_ID, new WebService.WebCallback() {

            @Override
            public void CompleteCallback(String id, Object obj) {
                if (ObjisNull(obj, "已設定喜歡")) return;
                isGood = obj.equals("1");
                Log.v(TAG, "已設定為喜歡:" + isGood);
                setImgBtnContent(isGood ? "@mipmap/news_good" : "@mipmap/global_backicon", goodBtn);
                goodTxt.setText(String.valueOf(Integer.valueOf(GOOD_CNT) + 1));
            }
        });
    }

    /**
     * 確認資訊錯誤
     *
     * @param $obj
     * @param msg
     * @return
     */
    private boolean ObjisNull(Object $obj, String msg) {
        if ($obj == null) {
            MyAlertDialog.Show(getActivity(), msg);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 確認資訊內容為空
     *
     * @param $json
     * @param msg
     * @return
     */
    private boolean JsonisEmpty(JSONObject $json, String msg) {
        if ($json.length() == 0) {
            MyAlertDialog.Show(getActivity(), msg);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 設定按鈕圖片
     *
     * @param $uri
     * @param $currImgBtn
     */
    private void setImgBtnContent(String $uri, ImageButton $currImgBtn) {
        int imageResource = getResources().getIdentifier($uri, null, getActivity().getPackageName());
        Drawable image = getResources().getDrawable(imageResource);
        $currImgBtn.setImageDrawable(image);
    }


    /**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加腾讯微博SSO授权
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        // 添加人人网SSO授权
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(getActivity(),
                "201874", "28401c0964f04a72a14c812d6132fcef",
                "3bf66e42db1e4fa9829b955cc300b737");
        mController.getConfig().setSsoHandler(renrenSsoHandler);

        // 添加QQ、QZone平台
        addQQQZonePlatform();

        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }


    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent() {

        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(),
                "100424468", "c7394704798a158208a74ab60104f0ba");
        qZoneSsoHandler.addToSocialSDK();
        mController.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);

        // APP ID：201874, API
        // * KEY：28401c0964f04a72a14c812d6132fcef, Secret
        // * Key：3bf66e42db1e4fa9829b955cc300b737.
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(getActivity(),
                "201874", "28401c0964f04a72a14c812d6132fcef",
                "3bf66e42db1e4fa9829b955cc300b737");
        mController.getConfig().setSsoHandler(renrenSsoHandler);

        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        weixinContent.setTitle(CHANNEL_TITLE);
        weixinContent.setTargetUrl(MEDIA_CONTENT);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        circleMedia.setTitle(CHANNEL_TITLE);
        circleMedia.setTargetUrl(MEDIA_CONTENT);
        mController.setShareMedia(circleMedia);

        // 设置renren分享内容
        RenrenShareContent renrenShareContent = new RenrenShareContent();
        renrenShareContent.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        UMImage image = new UMImage(getActivity(),
                BitmapFactory.decodeResource(getResources(), R.drawable.device));
        image.setTitle(CHANNEL_TITLE);
        image.setThumb(THUMB_URL);
        renrenShareContent.setShareImage(image);
        renrenShareContent.setAppWebSite(MEDIA_CONTENT);
        mController.setShareMedia(renrenShareContent);

        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        qzone.setTargetUrl(MEDIA_CONTENT);
        qzone.setTitle(CHANNEL_TITLE);
        mController.setShareMedia(qzone);


        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setTitle(CHANNEL_TITLE);
        qqShareContent.setTargetUrl(MEDIA_CONTENT);
        mController.setShareMedia(qqShareContent);

        TencentWbShareContent tencent = new TencentWbShareContent();
        tencent.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        // 设置tencent分享内容
        mController.setShareMedia(tencent);

        // 设置短信分享内容
        SmsShareContent sms = new SmsShareContent();
        sms.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        mController.setShareMedia(sms);

        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        mController.setShareMedia(sinaContent);

        TwitterShareContent twitterShareContent = new TwitterShareContent();
        twitterShareContent
                .setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        twitterShareContent.setShareMedia(new UMImage(getActivity(), new File("/storage/sdcard0/emoji.gif")));
        mController.setShareMedia(twitterShareContent);

        GooglePlusShareContent googlePlusShareContent = new GooglePlusShareContent();
        googlePlusShareContent
                .setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        mController.setShareMedia(googlePlusShareContent);

        // 来往分享内容
        LWShareContent lwShareContent = new LWShareContent();
        lwShareContent.setTitle(CHANNEL_TITLE);
        lwShareContent.setMessageFrom("来自APPBABY");
        lwShareContent.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        mController.setShareMedia(lwShareContent);

        // 来往动态分享内容
        LWDynamicShareContent lwDynamicShareContent = new LWDynamicShareContent();
        lwDynamicShareContent.setTitle(CHANNEL_TITLE);
        lwDynamicShareContent.setMessageFrom("来自APPBABY");
        lwDynamicShareContent.setShareContent(CHANNEL_TITLE + " " + MEDIA_CONTENT);
        lwDynamicShareContent.setTargetUrl(MEDIA_CONTENT);
        mController.setShareMedia(lwDynamicShareContent);
    }

    /**
     * 添加所有的平台</br>
     */
    private void addCustomPlatforms() {
        // 添加微信平台
        addWXPlatform();
        // 添加QQ平台
        addQQQZonePlatform();
        // 添加来往、来往动态平台
        addLaiWang();
        // 添加易信平台
        addYXPlatform();
        // 添加短信平台
        addSMS();
        // 添加email平台
        addEmail();

        addWhatsApp();

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN,
                SHARE_MEDIA.RENREN, SHARE_MEDIA.EMAIL, SHARE_MEDIA.GOOGLEPLUS, SHARE_MEDIA.LAIWANG,
                SHARE_MEDIA.LAIWANG_DYNAMIC, SHARE_MEDIA.SMS, SHARE_MEDIA.TWITTER, SHARE_MEDIA.YIXIN,
                SHARE_MEDIA.YIXIN_CIRCLE, SHARE_MEDIA.WHATSAPP);
        mController.openShare(getActivity(), false);
    }

    /**
     * 添加短信平台</br>
     */
    private void addSMS() {
        // 添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }

    /**
     * 添加Email平台</br>
     */
    private void addEmail() {
        // 添加email
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();
    }


    /**
     * 添加来往和来往动态平台</br>
     */
    private void addLaiWang() {
        String appToken = "laiwangd497e70d4";
        String secretID = "d497e70d4c3e4efeab1381476bac4c5e";
        // laiwangd497e70d4:来往appToken,d497e70d4c3e4efeab1381476bac4c5e:来往secretID
        // 添加来往的支持
        UMLWHandler umlwHandler = new UMLWHandler(getActivity(), appToken, secretID);
        umlwHandler.addToSocialSDK();

        // 添加来往动态的支持
        UMLWHandler lwDynamicHandler = new UMLWHandler(getActivity(),
                appToken, secretID);
        lwDynamicHandler.setToCircle(true);
        lwDynamicHandler.addToSocialSDK();
    }

    /**
     * @return
     * @功能描述 : 添加微信平台分享
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wx967daebe835fbeac";
        String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(getActivity(), appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(), appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * @return
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     * image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     * 要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     * : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     */
    private void addQQQZonePlatform() {
        String appId = "100424468";
        String appKey = "c7394704798a158208a74ab60104f0ba";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
                appId, appKey);
        qqSsoHandler.setTargetUrl(MEDIA_CONTENT);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * @throws
     * @Title: addYXPlatform
     * @Description:
     */
    private void addYXPlatform() {

        // 添加易信平台
        UMYXHandler yixinHandler = new UMYXHandler(getActivity(),
                "yxc0614e80c9304c11b0391514d09f13bf");
        // 关闭分享时的等待Dialog
        yixinHandler.enableLoadingDialog(false);
        // 设置target Url, 必须以http或者https开头
        yixinHandler.setTargetUrl(MEDIA_CONTENT);
        yixinHandler.addToSocialSDK();

        // 易信朋友圈平台
        UMYXHandler yxCircleHandler = new UMYXHandler(getActivity(),
                "yxc0614e80c9304c11b0391514d09f13bf");
        yxCircleHandler.setToCircle(true);
        yxCircleHandler.addToSocialSDK();

    }

    private void addWhatsApp() {
        UMWhatsAppHandler whatsAppHandler = new UMWhatsAppHandler(getActivity());
        whatsAppHandler.addToSocialSDK();
        WhatsAppShareContent whatsAppShareContent = new WhatsAppShareContent();
//        whatsAppShareContent.setShareContent("share test");
        whatsAppShareContent.setShareImage(new UMImage(getActivity(), R.drawable.icon));
        mController.setShareMedia(whatsAppShareContent);
//        mController.openShare(getActivity(), false);
    }

}