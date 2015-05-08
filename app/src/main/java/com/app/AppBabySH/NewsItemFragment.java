package com.app.AppBabySH;

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
import org.json.JSONObject;

/**
 * Created by ray on 2015/4/29.
 */
public class NewsItemFragment extends BaseFragment {
    private static final String TAG = "NewsItemFragment";
    private View rootView;
    private MainTabActivity main;
    private NewsItemFragment thisFragment;

    public itmeCallBack onCallBack;
    public interface itmeCallBack{
        public void onBack();
    }

    /*初始資料*/
    public String CHANNEL_ID,CHANNEL_TITLE,THUMB_URL,MEDIA_TYPE,MEDIA_CONTENT,GOOD_CNT,FAVORITE_CNT;

    /*本頁面Layout*/
    private ImageButton backBtn,shareBtn;
    private WebView themeWebv;
    private TextView goodTxt,favTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.newsitem_fragment,
                    container, false);
            creatRootView();
        }

        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        main = (MainTabActivity) getActivity();
		/* 初始化本頁面 */
        thisFragment=this;
        return rootView;
    }
    /**
     * 產生主畫面
     */
    private void creatRootView(){
        backBtn = (ImageButton) rootView.findViewById(R.id.newsitem_backBtn);
        shareBtn = (ImageButton) rootView.findViewById(R.id.newsitem_shareBtn);
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
            }
        });
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
        if (onCallBack!=null)onCallBack.onBack();
    }

}
