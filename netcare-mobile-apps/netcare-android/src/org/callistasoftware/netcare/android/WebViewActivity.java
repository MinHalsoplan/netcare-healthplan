package org.callistasoftware.netcare.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;

public class WebViewActivity extends Activity {

	private final static String TAG = WebViewActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final WebView wv = new WebView(this);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setScrollbarFadingEnabled(true);
		
		setContentView(wv);
		
		/*WebViewDatabase.getInstance(this).clearHttpAuthUsernamePassword();
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedHttpAuthRequest(WebView view,
					HttpAuthHandler handler, String host, String realm) {
				super.onReceivedHttpAuthRequest(view, handler, host, realm);
				Log.d(TAG, "Received authentication request.");
				Log.d(TAG, "Target host: " + host);
				Log.d(TAG, "Target realm: " + realm);
				
				handler.proceed("191212121212", "0000");
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		
		wv.setHttpAuthUsernamePassword("192.168.0.113", "Spring Security Application", "191212121212", "0000");
		*/
		Log.d(TAG, "Displaying url in web view.");
		wv.loadUrl("http://191212121212:0000@192.168.0.113:8080/netcare-web/netcare/mobile/start");
	}
}
