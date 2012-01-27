package org.callistasoftware.netcare.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends Activity {

	private final static String TAG = WebViewActivity.class.getSimpleName();
	
	private ProgressDialog p;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.webview);
		
		final SharedPreferences sp = this.getSharedPreferences("NETCARE", MODE_PRIVATE);
		
		final String username = sp.getString("username", null);
		final String password = sp.getString("password", null);
		
		if (username == null || password == null) {
			Log.d(TAG, "Credentials empty, bring to home screen.");
			Toast.makeText(getApplicationContext(), "Inga anv�ndaruppgifter sparade. V�nligen logga in igen...", 3000);
			startActivity(new Intent(getApplicationContext(), StartActivity.class));
		}
		
		final WebView wv = (WebView) this.findViewById(R.id.webview);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.clearFormData();
		wv.clearHistory();
		wv.clearCache(true);
		
		wv.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype, long contentLength) {
				Log.d(TAG, "Download " + mimetype + " " + url);
			}
		});
		
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedHttpAuthRequest(WebView view,
					HttpAuthHandler handler, String host, String realm) {
				Log.d(TAG, "Setting basic authentication credentials. Username: " + username + " password: " + password);
				handler.proceed(username, password);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				
				Log.d(TAG, "ERROR ==== Code: " + errorCode + " Desc: " + description + " URL: " + failingUrl);
				
				final Intent i = new Intent(WebViewActivity.this.getApplicationContext(), StartActivity.class);
				i.putExtra("error", true);
				i.putExtra("errorMessage", description);
				
				startActivity(i);
				WebViewActivity.this.finish();
			}
		});
		
		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				Log.d(TAG, "Progress is: " + newProgress);
				if (newProgress == 100) {
					p.dismiss();
				}
			}
		});
		
		Log.d(TAG, "Displaying url in web view.");
		p = ProgressDialog.show(this, "Laddar", "V�nligen v�nta medan sidan laddar klart.");
		wv.loadUrl("http://" + ApplicationUtil.getProperty(getApplicationContext(), "host") + ":" + ApplicationUtil.getProperty(getApplicationContext(), "port")
				+ "/netcare-web/netcare/mobile/start");
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }
}
