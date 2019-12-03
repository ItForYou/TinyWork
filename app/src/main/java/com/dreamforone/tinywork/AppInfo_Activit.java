package com.dreamforone.tinywork;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

public class AppInfo_Activit extends Activity{
	
	private WebView mWebview = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_appinfo);
		super.onCreate(savedInstanceState);
		
		WebView();
	}
	
	private void WebView() {
		mWebview = (WebView) findViewById(R.id.app_webview);
		mWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebview.setScrollbarFadingEnabled(true);
		mWebview.setHorizontalScrollBarEnabled(false);
		mWebview.setVerticalScrollBarEnabled(false);
		mWebview.setWebViewClient(new WebViewClientClass());
		mWebview.setWebChromeClient(new WebChromeClientClass());
		mWebview.addJavascriptInterface(new WebChromeClientClass(), "androidfile");	
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		} else {
			mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		try {
			if (Build.VERSION.SDK_INT < 11) {
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
		webSettings.setDatabasePath("data/data/com.dreamforone.tinywork/databases");
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
		
		mWebview.loadUrl("http://www.dreamforone.com/~tinywork/theme/app/info2.php");
		
	}
	
	private class WebChromeClientClass extends WebChromeClient {
		
		
	}
	
	private class WebViewClientClass extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

		}
	}
}
