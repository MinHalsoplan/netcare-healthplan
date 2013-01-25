package org.callistasoftware.netcare.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.webview);
		
		final boolean push = ApplicationUtil.getBooleanProperty(getApplicationContext(), "push");
		if (push) {
			Log.d(TAG, "Registering for push");
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);

			GCMRegistrar.register(this, "1072676211966");
		}
		
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
		
		Log.d(TAG, "Displaying url in web view.");
		p = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loadingActivities));
		final String url = ApplicationUtil.getServerBaseUrl(getApplicationContext()) + "/mobile/start";
		Log.d(TAG, "Load url: " + url);
		
		wv.loadUrl(url);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }
}
