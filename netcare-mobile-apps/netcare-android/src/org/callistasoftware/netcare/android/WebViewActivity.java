package org.callistasoftware.netcare.android;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import org.callistasoftware.netcare.android.helper.GCMHelper;

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
        wv.addJavascriptInterface(new AndroidJs(), "Android");
		
		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					p.dismiss();
				}
			}

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, "Javascript debug: " + consoleMessage.message());
                return true;
            }

        });

		final String url = ApplicationHelper.newInstance(getApplicationContext()).getUrl("/mobile/start");
		Log.d(TAG, "Load url: " + url);

        final boolean devMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("devMode", false);
        if (!devMode) {
            startWebView(wv, url);
        } else {
            startWebViewInDevMode(wv, url, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("devCrn", "191212121212"));
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

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "Override url loading for: " + url);
                view.loadUrl(url);
                if (url.endsWith("/logout")) {
                    startActivity(new Intent(getApplicationContext(), StartActivity.class));
                    finish();
                }

                return true;
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

    void startWebViewInDevMode(final WebView webview, final String url, final String crn) {

        Log.i(TAG, "Starting webview in dev mode...");
        GCMHelper.newInstance(getApplicationContext()).gcmRegister();

        final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                Log.d(TAG, "Responding to basic auth with crn: " + crn);
                webview.setHttpAuthUsernamePassword(host, "netcare-mobile", crn, "");
                handler.proceed(crn, "");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "Should url overload for url: " + url);
                return super.shouldOverrideUrlLoading(view, url);    //To change body of overridden methods use File | Settings | File Templates.
            }
        });

        webview.loadUrl(url);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }

    private class AndroidJs {

        public int logout() {
            Log.d(TAG, "Logging out. Bring user to login screen...");
            startActivity(new Intent(WebViewActivity.this, StartActivity.class));

            return 0;
        }
    }
}
