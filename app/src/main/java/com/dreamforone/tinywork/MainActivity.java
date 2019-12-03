package com.dreamforone.tinywork;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import com.dreamforone.data.MyPhoneData;
import com.dreamforone.util.ForYouConfig;
import com.dreamforone.util.NetworkCheck;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

public class MainActivity extends Activity implements OnClickListener{
    NetworkCheck netCheck;
    private final String[] navItems = {"로그아웃","공지사항","자주하는 질문(QnA)","고객센터","개인정보 취급 방침","위치기반 이용약관","서비스 수수료 규정","서비스 취소 및 환급규정","서비스 이용약관","서비스 권장 금액표","회사 정보" };
    private ListView lvNavList;
    private FrameLayout flContainer;
    private DrawerLayout dlDrawer;
    private Button btn;

    private boolean end = false;
    private ImageView IvIntro = null;
    private WebView mWebview = null;
    private ArrayList<BottomData> mData = new ArrayList<MainActivity.BottomData>();

    private ProgressDialog m_progressdialog = null;
    private ProgressBar mProgressBar = null;

    private ForYouConfig mConfig = null;
    public String appName = "";
    public AdapterViewFlipper avf;
    Handler handler = new Handler();
    private String IntentURL = "";

    final int MapRequestCode = 2;

    public boolean isLoading = false;

    private static final String TYPE_IMAGE = "image/*";
    private static final int INPUT_FILE_REQUEST_CODE = 1;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    Button btn_guin = null;
    Button btn_gujik = null;
    public static boolean execBoolean =false;
    /****************************************************************************************************************/

    final public static String MAIN_URL = "http://www.tinywork.co.kr/";
    final public static String MAIN_URL2 = "http://www.tinywork.co.kr/bbs/board.php?bo_table=service";
    final public static String COME_URL = MAIN_URL + "s1.htm";
    final public static String LOGIN_URL = "http://www.tinywork.co.kr/bbs/login.php";
    final public static String LOGOUT_URL = "http://www.tinywork.co.kr/bbs/logout.php";

    final private static String TEL = "010-8945-5430";

    private String[] bottomText = new String[] {"사용 안내", "서비스 이력", "회원가입/로그인"};
    private int[] bottomImg = new int[] { R.drawable.icon1,
            R.drawable.icon2, R.drawable.icon3,
            R.drawable.icon4, R.drawable.icon5 };
    private String KakaoText = "[단디펀딩] - 저금리시대를 뒤집는 매력적인 P2P핀테크 크라우드펀딩입니다.";
    private boolean SettingGcm = false;
    /****************************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = new Intent(MainActivity.this,SplashActivity.class);
        startActivity(startIntent);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        netCheck = new NetworkCheck(this, this);
        netCheck.networkCheck();
        init();
        Intro();
        Bottom();
        WebView();
        ChangeLogin();

        String url = getIntent().getStringExtra("url");
        if(url != null && url.length() > 0){
            IntentURL = url;
        }

        lvNavList = (ListView) findViewById(R.id.lv_activity_main_nav_list);
        flContainer = (FrameLayout) findViewById(R.id.fl_activity_main_container);

        btn = (Button) findViewById(R.id.btn_main_left);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlDrawer.openDrawer(lvNavList);
            }
        });

        Button btn = (Button) findViewById(R.id.btn_main_gps);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               /* if(NetworkCheck.isBoolean){
                    Intent mIntent = new Intent(MainActivity.this,MapActivity.class);
                    startActivityForResult(mIntent, MapRequestCode);
                }else{
                    Toast.makeText(MainActivity.this,"인터넷 상태가 좋지 않습니다.", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_main_drawer);


        ListViewAdapter adapter = new ListViewAdapter() ;
        lvNavList.setAdapter(adapter);

        adapter.addItem(navItems);

        lvNavList.setOnItemClickListener(new DrawerItemClickListener());

        btn_guin = (Button) findViewById(R.id.btn_main_guin);
        btn_gujik = (Button) findViewById(R.id.btn_main_gujik);

        btn_guin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                loadUrl(MAIN_URL);
                btn_guin.setBackgroundResource(R.drawable.logo1_over);
                btn_gujik.setBackgroundResource(R.drawable.logo2);
            }
        });

        btn_gujik.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/board.php?bo_table=service");
                btn_guin.setBackgroundResource(R.drawable.logo1);
                btn_gujik.setBackgroundResource(R.drawable.logo2_over);
            }
        });

        final LinearLayout view = (LinearLayout) findViewById(R.id.li);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                LinearLayout layout = (LinearLayout) findViewById(R.id.layout_main_bottom);
                layout.setVisibility(View.VISIBLE);
                if ((view.getRootView().getHeight() - view.getHeight()) > view.getRootView().getHeight() / 3) {
                    layout.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });
    }



	/*
	softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
		@Override
		public void onSoftKeyboardHide(){
			new Handler(Looper.getMainLooper()).post(new Runnable(){
				@Override
				public void run(){
					//키보드 내려왔을때
				}
			});
		}

		@Override
		public void onSoftKeyboardShow(){
			new Handler(Looper.getMainLooper()).post(new Runnable(){
				@Override
				public void run(){
					//키보드 올라왔을때
				}
			});
		}
	});
	*/

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().startSync();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync();
        }
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view,int position, long id) {

            if(position == 1){ // 공지사항
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/board.php?bo_table=notice");
            } else if(position == 2){ // 자주하는 질문
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/board.php?bo_table=qna");
            } else if(position == 3){ // 불만 및 개선
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/board.php?bo_table=help");
            } else if(position == 4){ // 개인정보 보호
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/content.php?co_id=service_terms");
            } else if(position == 5){ // 위치기반
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/content.php?co_id=service_position");
            } else if(position == 6){ // 세금규정
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/content.php?co_id=service_money");
            } else if(position == 7){ // 서비스 취소 및 환급규정
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/content.php?co_id=service_refund");
            } else if(position == 8){ // 서비스 이용
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/content.php?co_id=service_service");
            } else if(position == 9){ // 서비스 권장 금액
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/content.php?co_id=service_table");
            } else if(position == 10){ //회사정보
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/content.php?co_id=service_info");
            } else if(position == 0){ // 로그아웃
                mConfig.pref_save("id", "");
                mConfig.pref_save("pw", "");

                ChangeLogin();
                loadUrl(LOGOUT_URL);
            }

            dlDrawer.closeDrawer(lvNavList);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        if(url != null && url.length() > 0){
            loadUrl(url);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MapRequestCode){
            if(resultCode == RESULT_OK){
                String wr_id = data.getStringExtra("wr_id");
                loadUrl("http://www.dreamforone.com/~tinywork/bbs/board.php?bo_table=service&wr_id=" + wr_id);
            }
        }

        if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri[] results = new Uri[]{getResultUri(data)};

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            } else {
                if (mUploadMessage == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri result = getResultUri(data);

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        } else {
            if (mFilePathCallback != null) mFilePathCallback.onReceiveValue(null);
            if (mUploadMessage != null) mUploadMessage.onReceiveValue(null);
            mFilePathCallback = null;
            mUploadMessage = null;
            super.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Uri getResultUri(Intent data) {
        Uri result = null;
        if(data == null || TextUtils.isEmpty(data.getDataString())) {
            // If there is not data, then we may have taken a photo
            if(mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath);
            }
        } else {
            String filePath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                filePath = data.getDataString();
            } else {
                filePath = "file:" + RealPathUtil.getRealPath(this, data.getData());
            }
            result = Uri.parse(filePath);
        }

        return result;
    }

    private void init() {
        m_progressdialog = new ProgressDialog(this);
        m_progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_progressdialog.setTitle("");
        m_progressdialog.setCancelable(true);
        m_progressdialog.setMessage("잠시만 기다려주세요");

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_webview);

        mConfig = new ForYouConfig(this);
        appName = getPackageName();

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", 0);
        intent.putExtra("badge_count_package_name", "com.dreamforone.tinywork");
        intent.putExtra("badge_count_class_name", "com.dreamforone.tinywork.MainActivity");
        sendBroadcast(intent);
        mConfig.pref_save("badgeCount", "0");


    }

    private void loadUrl(String url){
        String post = "mobile_app=1";
        post += "&device=" + MyPhoneData.GetDeviceID(this);
        mWebview.loadUrl(url);
    }

    private void WebView() {
        mWebview = (WebView) findViewById(R.id.webView);
        mWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebview.setScrollbarFadingEnabled(true);
        mWebview.setHorizontalScrollBarEnabled(false);
        mWebview.setVerticalScrollBarEnabled(false);
        mWebview.setWebViewClient(new WebViewClientClass());
        mWebview.setWebChromeClient(new WebChromeClientClass());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        try {
            if (android.os.Build.VERSION.SDK_INT < 11) {
                ZoomButtonsController zoom_controll = null;
                zoom_controll = (ZoomButtonsController) mWebview.getClass().getMethod("getZoomButtonsController").invoke(mWebview);
                zoom_controll.getContainer().setVisibility(View.GONE);
            } else {
                mWebview.getSettings().getClass().getMethod("setDisplayZoomControls", Boolean.TYPE).invoke(mWebview.getSettings(), false);
            }
        } catch (Exception e) {
        }

        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setPluginState(PluginState.ON);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);//
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        //webSettings.setDatabasePath("data/data/com.dreamforone.tinywork/databases");
        webSettings.setDomStorageEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setUserAgentString( webSettings.getUserAgentString() + " (XY ClientApp)" );
        webSettings.setAllowFileAccess(true);
        webSettings.setSavePassword(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath("");
        webSettings.setAppCacheMaxSize(5*1024*1024);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            CookieManager cookieManager = CookieManager.getInstance();

            cookieManager.setAcceptCookie(true);

            cookieManager.setAcceptThirdPartyCookies(mWebview, true);

        }

        loadUrl(MAIN_URL);
        mWebview.addJavascriptInterface(new WebJavascriptInterFace(), "androidfile");
    }




    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if ( view == null || url == null) {
                // 처리하지 못함
                return false;
            }

            if (url.startsWith("tel:")) {
                Intent call_phone = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(call_phone);
                return true;

            } else if (url.startsWith("sms:")) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(i);
                return true;
            } else if(url.indexOf("map")>-1){
                if(NetworkCheck.isBoolean){
                    /*Intent mIntent = new Intent(MainActivity.this,MapActivity.class);
                    startActivityForResult(mIntent, MapRequestCode);*/
                    return true;
                }else{
                    Toast.makeText(MainActivity.this, "인터넷상태가 좋지 않습니다.",Toast.LENGTH_SHORT).show();
                    return true;
                }

            }

            if ( url.contains("play.google.com") ) {
                // play.google.com 도메인이면서 App 링크인 경우에는 market:// 로 변경
                String[] params = url.split("details");
                if ( params.length > 1 ) {
                    url = "market://details" + params[1];
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url) ));
                    return true;
                }
            }


            if ( url.startsWith("http:") || url.startsWith("https:") ) {
                if (url.indexOf("logout.php") > -1 || url.indexOf("login.php") > -1) {
                    try {


                        if(mConfig == null) mConfig = new ForYouConfig(MainActivity.this);
                        mConfig.pref_save("id", "");
                        mConfig.pref_save("pw", "");

                        CookieManager.getInstance().removeAllCookies(null);

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                loadUrl(url);
            } else {
                Intent intent;

                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
                    // 처리하지 못함
                    return false;
                }

                try {
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Intent Scheme인 경우, 앱이 설치되어 있지 않으면 Market으로 연결
                    if ( url.startsWith("intent:") && intent.getPackage() != null) {
                        url = "market://details?id=" + intent.getPackage();
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url) ));
                        return true;
                    } else {
                        // 처리하지 못함
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {

            Button btn = (Button) findViewById(R.id.btn_main_gps);
            btn.setVisibility(View.GONE);

            isLoading = true;
            mHandler.sendEmptyMessageDelayed(2, 750);

            if(url.equals(MAIN_URL)){
                //btn_guin.setBackgroundResource(R.drawable.logo1_over);
                //btn_gujik.setBackgroundResource(R.drawable.logo2);
            } else if(url.indexOf("service.php")>-1){
                //btn_guin.setBackgroundResource(R.drawable.logo1_over);
                //btn_gujik.setBackgroundResource(R.drawable.logo2);
            } else if(url.indexOf("bo_table=service") > -1) {
                //btn_guin.setBackgroundResource(R.drawable.logo1);
                //btn_gujik.setBackgroundResource(R.drawable.logo2_over);
                btn.setVisibility(View.VISIBLE);
            }

            if(url.equals("")){

            }


            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(android.webkit.WebView view, String url) {

            isLoading =false;
            mHandler.removeMessages(2);
            mHandler.removeMessages(3);
            mProgressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);

            if(IntentURL.length() > 0){
                loadUrl(IntentURL);
                IntentURL = "";
            }

            try {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.getInstance().sync();
                } else {
                    CookieManager.getInstance().flush();
                }

                String id = mConfig.pref_get("id","");
                if(id.equals("")){
                    String Allcookie = CookieManager.getInstance().getCookie(url);
                    String cookieArr[] = Allcookie.split(";");
                    for(String str : cookieArr){
                        String cookie[] = str.split("=");
                        if(cookie[0].trim().equals("mb_id")){
                            String cookieVlaue = cookie[1].trim();
                            mConfig.pref_save("id", cookieVlaue);
                            ChangeLogin();
                        }
                    }
                } else {

                }
            } catch (Exception e) {
                Log.i("TAG", "오류 " + e);
            }

        }

        @Override
        public void onReceivedError(android.webkit.WebView view, int errorCode,String description, String failingUrl) {
            mWebview.loadUrl("about:blank");
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("에러");
            alert.setMessage("네트워크가 상태가 불안정합니다.");
            alert.setCancelable(false);
            alert.setNegativeButton("재접속", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    loadUrl(MAIN_URL);
                }
            });

            alert.setPositiveButton("종료", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();

                }
            });
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    private class WebChromeClientClass extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

            // TODO Auto-generated method stub
            // return super.onJsAlert(view, url, message, result);
            new AlertDialog.Builder(view.getContext()).setTitle("알림").setMessage(message)
                    .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).setCancelable(false).create().show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            // TODO Auto-generated method stub
            // return super.onJsConfirm(view, url, message, result);
            new AlertDialog.Builder(view.getContext()).setTitle("알림").setMessage(message)
                    .setPositiveButton("네", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).setNegativeButton("아니오", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).setCancelable(false).create().show();
            return true;
        }

        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }

        @Override
        public void onCloseWindow(WebView w) {
            super.onCloseWindow(w);
            //finish();
        }



        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView.HitTestResult result = view.getHitTestResult();
            String url = result.getExtra();

            if(result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE){
                try{
                    Message hrefMsg = new Message();
                    hrefMsg.setTarget(new Handler());
                    view.requestFocusNodeHref(hrefMsg);
                    url = (String)hrefMsg.getData().get("url");
                }
                catch(Exception e){

                }
            }

            if(url != null && url.length() > 0){
                if(!url.startsWith(MAIN_URL)){
                    Uri uri = Uri.parse(url);
                    Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(it);
                    return false;
                }

            }

            view.removeAllViews();
            WebView newView = new WebView(MainActivity.this);
            WebSettings webSettings = newView.getSettings();

            webSettings.setSaveFormData(false);
            webSettings.setPluginState(PluginState.ON);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setUseWideViewPort(true);//
            webSettings.setSupportZoom(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDatabaseEnabled(true);
            //webSettings.setDatabasePath("data/data/com.dreamforone.tinywork/databases");
            webSettings.setDomStorageEnabled(true);
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            webSettings.setUserAgentString( webSettings.getUserAgentString() + " (XY ClientApp)" );
            webSettings.setAllowFileAccess(true);
            webSettings.setSavePassword(false);
            webSettings.setAppCacheEnabled(true);
            webSettings.setAppCachePath("");
            webSettings.setAppCacheMaxSize(5*1024*1024);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            newView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            newView.setScrollbarFadingEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                newView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                newView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }


            try {
                if (android.os.Build.VERSION.SDK_INT < 11) {

                    ZoomButtonsController zoom_controll = null;

                    zoom_controll = (ZoomButtonsController) mWebview.getClass()
                            .getMethod("getZoomButtonsController").invoke(mWebview);

                    zoom_controll.getContainer().setVisibility(View.GONE);

                } else {

                    newView.getSettings().getClass()
                            .getMethod("setDisplayZoomControls", Boolean.TYPE)
                            .invoke(mWebview.getSettings(), false);

                }
            } catch (Exception e) {
                // TODO: handle exception
            }

            newView.setHorizontalScrollBarEnabled(false);
            newView.setVerticalScrollBarEnabled(false);
            newView.setTag("webview");

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            newView.setLayoutParams(params);
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
            layout.addView(newView);



            newView.setWebChromeClient(new WebChromeClientClass() {
                @Override
                public void onCloseWindow(android.webkit.WebView window) {
                    super.onCloseWindow(window);
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
                    layout.removeView(window);
                }
                @Override
                public boolean onCreateWindow(android.webkit.WebView view,boolean isDialog, boolean isUserGesture,Message resultMsg) {
                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                }

            });
            newView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if ( view == null || url == null) {
                        // 처리하지 못함
                        return false;
                    }

                    if (url.startsWith("tel:")) {
                        Intent call_phone = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(call_phone);
                        return true;

                    } else if (url.startsWith("sms:")) {
                        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(i);
                        return true;
                    } else if(url.indexOf("map")>-1){
                        if(NetworkCheck.isBoolean){
                            /*Intent mIntent = new Intent(MainActivity.this,MapActivity.class);
                            startActivityForResult(mIntent, MapRequestCode);*/
                            return true;
                        }else{
                            Toast.makeText(MainActivity.this,"인터넷 상태가 좋지 않습니다.", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }

                    if ( url.contains("play.google.com") ) {
                        // play.google.com 도메인이면서 App 링크인 경우에는 market:// 로 변경
                        String[] params = url.split("details");
                        if ( params.length > 1 ) {
                            url = "market://details" + params[1];
                            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url) ));
                            return true;
                        }
                    }


                    if ( url.startsWith("http:") || url.startsWith("https:") ) {
                        if (url.indexOf("logout.php") > -1 || url.indexOf("login.php") > -1) {
                            try {

                                if(mConfig == null) mConfig = new ForYouConfig(MainActivity.this);
                                mConfig.pref_save("id", "");
                                mConfig.pref_save("pw", "");

                                //CookieManager.getInstance().removeAllCookies(null);

                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                        loadUrl(url);
                    } else {
                        Intent intent;

                        try {
                            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        } catch (URISyntaxException e) {
                            // 처리하지 못함
                            return false;
                        }

                        try {
                            view.getContext().startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Intent Scheme인 경우, 앱이 설치되어 있지 않으면 Market으로 연결
                            if ( url.startsWith("intent:") && intent.getPackage() != null) {
                                url = "market://details?id=" + intent.getPackage();
                                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url) ));
                                return true;
                            } else {
                                // 처리하지 못함
                                return false;
                            }
                        }
                    }
                    return true;
                }

                @Override
                public void onPageStarted (WebView view, String url, Bitmap favicon){
                }

                @Override
                public void onPageFinished(android.webkit.WebView view,String url) {
                    super.onPageFinished(view, url);
                }
            });

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newView);
            resultMsg.sendToTarget();
            return true;
        }

        // For Android Version < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            //System.out.println("WebViewActivity OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU), n=1");
            mUploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(TYPE_IMAGE);
            startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
        }

        // For 3.0 <= Android Version < 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            //System.out.println("WebViewActivity 3<A<4.1, OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU,aT), n=2");
            openFileChooser(uploadMsg, acceptType, "");
        }

        // For 4.1 <= Android Version < 5.0
        public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
            Log.d(getClass().getName(), "openFileChooser : "+acceptType+"/"+capture);
            mUploadMessage = uploadFile;
            imageChooser();
        }

        // For Android Version 5.0+
        // Ref: https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            System.out.println("WebViewActivity A>5, OS Version : " + Build.VERSION.SDK_INT + "\t onSFC(WV,VCUB,FCP), n=3");
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;
            imageChooser();
            return true;
        }

        private void imageChooser() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(getClass().getName(), "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:"+photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType(TYPE_IMAGE);

            Intent[] intentArray;
            if(takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
        }


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }



    private void ChangeLogin(){
        TextView tv_login = (TextView) findViewById(R.id.tv_main_login);
        String id = mConfig.pref_get("id", "");
        Log.i("TAG", "ChangeLogin id " + id);
        if(id.equals("")){
            tv_login.setText("회원가입/로그인");
        } else {
            tv_login.setText("프로필");
        }
    }

    private void Intro() {
        IvIntro = (ImageView) findViewById(R.id.iv_intro);
        Animation anima = AnimationUtils.loadAnimation(this, R.anim.intro_alpa);
        IvIntro.startAnimation(anima);
        IvIntro.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    private void Bottom() {

        LinearLayout bottom_1 = (LinearLayout) findViewById(R.id.layout_main_bottom1);
        LinearLayout bottom_2 = (LinearLayout) findViewById(R.id.layout_main_bottom2);
        LinearLayout bottom_3 = (LinearLayout) findViewById(R.id.layout_main_bottom3);

        bottom_1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent mIntent = new Intent(MainActivity.this, AppInfo_Activit.class);
                startActivity(mIntent);
            }
        });

        bottom_2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(btn_guin.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.logo1_over).getConstantState())){
                    loadUrl("http://www.dreamforone.com/~tinywork/bbs/register_form_tasks.php");
                } else {
                    loadUrl("http://www.dreamforone.com/~tinywork/bbs/register_form_tasks.php?mode=register_tab2");
                }
            }
        });

        bottom_3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String id = mConfig.pref_get("id", "");
                if(id.equals("")){
                    loadUrl(LOGIN_URL);
                } else {
                    loadUrl(MAIN_URL + "bbs/member_confirm.php?url=http://www.dreamforone.com/~tinywork/bbs/register_form.php");
                }
            }
        });
		/*
		LinearLayout bottom_layout = (LinearLayout) findViewById(R.id.bottom_layout);

		LayoutInflater inflater = getLayoutInflater();

		if(bottomText.length == 0){
			bottom_layout.setVisibility(View.GONE);
			return;
		}
		for(int i = 0; i<bottomText.length; i++){
			View view_icon = inflater.inflate(R.layout.bottom_icon , null );
			View view_line = inflater.inflate(R.layout.bottom_line , null );

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
			params.gravity = Gravity.CENTER;
			view_icon.setLayoutParams(params);

			TextView tv_title = (TextView) view_icon.findViewById(R.id.tv_bottom_icon);
			ImageView iv_image = (ImageView) view_icon.findViewById(R.id.iv_bottom_icon);
			tv_title.setText(bottomText[i]);
			iv_image.setImageResource(bottomImg[i]);
			iv_image.setScaleType(ScaleType.FIT_CENTER);
			//iv_image.setBackgroundResource(bottomImg[i]);

			view_icon.setTag(i);
			view_icon.setOnClickListener(this);

			bottom_layout.addView(view_icon);

			if(i != bottomText.length - 1){
				bottom_layout.addView(view_line);
			}
		}
		*/
    }

    @Override
    public void onBackPressed() {
        if (dlDrawer.isDrawerOpen(lvNavList)) {
            dlDrawer.closeDrawer(lvNavList);
        } else {

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
            for(int i = 0; i<layout.getChildCount(); i++){
                View view = layout.getChildAt(i);
                if(view.getTag() != null && String.valueOf(view.getTag()).equals("webview")){
                    WebView webview = (WebView) view;
                    webview.loadUrl("javascript:self.close()");
                    layout.removeView(webview);
                    return;
                }
            }

            WebBackForwardList list = mWebview.copyBackForwardList();
            if (list.getCurrentIndex() <= 0 && !mWebview.canGoBack()) {
                finishApp();
            } else {
                String url = mWebview.getUrl();
                if(url.equals(MAIN_URL)  || url.indexOf("http://www.dreamforone.com/~tinywork/?mb_id") > -1){
                    finishApp();
                } else if(url.equals(MAIN_URL2) || url.equals(MAIN_URL2+"/")){
                    finishApp();
                } else if(url.indexOf("login.php") > -1){
                    loadUrl(MAIN_URL);
                } else if(url.indexOf("service_terms") > -1){
                    loadUrl(MAIN_URL);
                } else if(url.indexOf("service_position") > -1){
                    loadUrl(MAIN_URL);
                } else if(url.indexOf("service_money") > -1){
                    loadUrl(MAIN_URL);
                } else if(url.indexOf("service_refund") > -1){
                    loadUrl(MAIN_URL);
                } else if(url.indexOf("service_service") > -1){
                    loadUrl(MAIN_URL);
                } else if(url.indexOf("service_table") > -1){
                    loadUrl(MAIN_URL);
                } else if(url.indexOf("service_info") > -1){
                    loadUrl(MAIN_URL);
                } else if (url.indexOf("board.php?bo_table=") > -1 && url.indexOf("wr_id") > -1) {
                    url = url.replaceAll("\\&wr_id=\\d+", "");
                    loadUrl(url);
                } else if (url.indexOf("board.php?bo_table=") > -1) {
                    loadUrl(MAIN_URL);
                }

                else {
                    mWebview.goBack();
                }

            }
        }


    }

    private void AddData(LinearLayout layout, TextView textview, ImageView imageview) {
        BottomData data = new BottomData();
        data.layout = layout;
        data.imageview = imageview;
        data.textview = textview;
        mData.add(data);
    }

    @Override
    public void onClick(View v) {
        switch (Integer.valueOf(v.getTag() + "")) {
            case 0: {
                loadUrl("http://www.dreamforone.com/~tinywork/theme/app/info2.php");
            }
            break;
            case 1: {
                loadUrl("http://www.dandyfunding.com/bbs/myaccount.php");
            }
            break;
            case 2: {
                loadUrl("http://www.dandyfunding.com/bbs/mypage.php");
            }
            break;
            case 3: {

            }
            break;
            case 4: {
                finishApp();
            }
            break;
        }
    }

    private void finishApp() {
        if (end == true) {

            end = false;
            finish();
        } else {
            end = true;
            Toast.makeText(this, "한번 더 클릭시 종료합니다.", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(1, 1500);
        }
    }






    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0){
                IvIntro.setVisibility(View.GONE);
            } else if(msg.what == 1){
                end = false;
            } else if(msg.what == 2){
                if(isLoading == true){
                    mProgressBar.setProgress(0);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(3, 500);
                }

            } else if(msg.what == 3){
                if(isLoading == true){
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        }
    };





    class BottomData {
        LinearLayout layout = null;
        TextView textview = null;
        ImageView imageview = null;
    }

    public class AdViewHolder{
        private View base;
        private ImageView iv;

        AdViewHolder(View base){
            this.base = base;
        }

        ImageView getIv(){
            if(iv == null){
              //  iv = (ImageView) base.findViewById(R.id.iv_ad);
            }
            return iv;
        }
    }
    class WebJavascriptInterFace{
        @JavascriptInterface
        public void getlogin(final String id, final String pw) {
            handler.post(new Runnable() {
                public void run() {
                    mConfig = new ForYouConfig(MainActivity.this);
                    mConfig.pref_save("id", id);
                    mConfig.pref_save("pw", pw);
                    ChangeLogin();
                    //gcm();
                }
            });
        }
    }
}