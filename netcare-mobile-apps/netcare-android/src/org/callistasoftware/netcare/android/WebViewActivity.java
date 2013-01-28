package org.callistasoftware.netcare.android;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.android.gcm.GCMRegistrar;

public class WebViewActivity extends Activity {

	private final static String TAG = WebViewActivity.class.getSimpleName();
	
	private ProgressDialog p;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		//super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.webview);
		
		Log.d(TAG, "Displaying url in web view.");
		p = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loadingActivities));
		
		final WebView wv = (WebView) this.findViewById(R.id.webview);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.clearFormData();
		wv.clearHistory();
		wv.clearCache(true);
		
		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					p.dismiss();
				}
			}
		});
		
		final String url = ApplicationUtil.getServerBaseUrl(getApplicationContext()) + "/mobile/start";
		Log.d(TAG, "Load url: " + url);
		
		final String session = NetcareApp.getCurrentSession();
		if (session == null) {
			throw new IllegalStateException("Order reference is not set.");
		}
		
		final Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-netcare-order", NetcareApp.getCurrentSession());
		
		wv.loadUrl(url, headers);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }
}
