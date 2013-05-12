package org.callistasoftware.netcare.android;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.webkit.*;
import org.callistasoftware.netcare.android.helper.ApplicationHelper;
import org.callistasoftware.netcare.android.helper.AuthHelper;

public class WebViewActivity extends Activity {

	private final static String TAG = WebViewActivity.class.getSimpleName();
	
	private ProgressDialog p;
	
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
		
		final String url = ApplicationHelper.newInstance(getApplicationContext()).getUrl("/mobile/start");
		Log.d(TAG, "Load url: " + url);

        final boolean devMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("devMode", false);
        if (!devMode) {
            startWebView(wv, url);
        } else {
            startWebViewInDevMode(wv, url);
        }
	}

    void startWebView(final WebView webview, final String url) {

        Log.i(TAG, "Starting webview in production mode...");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        final String session = AuthHelper.newInstance(getApplicationContext()).getSessionId();
        if (session == null) {
            throw new IllegalStateException("Order reference is not set.");
        }

        final Map<String, String> headers = new HashMap<String, String>();
        headers.put(AuthHelper.NETCARE_AUTH_HEADER, session);
        webview.loadUrl(url, headers);
    }

    void startWebViewInDevMode(final WebView webview, final String url) {

        Log.i(TAG, "Starting webview in dev mode...");

        final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final String host = p.getString("host", "demo.minhalsoplan.se");
        final String crn = p.getString("crn", "191212121212");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                webview.setHttpAuthUsernamePassword(host, "netcare-mobile", crn, "");
                handler.proceed(crn, "");
            }
        });

        webview.loadUrl(url);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }
}
