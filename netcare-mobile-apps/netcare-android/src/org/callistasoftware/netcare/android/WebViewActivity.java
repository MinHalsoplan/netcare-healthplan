package org.callistasoftware.netcare.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class WebViewActivity extends Activity {

	private final static String TAG = WebViewActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.webview);
		
		final SharedPreferences sp = this.getSharedPreferences("NETCARE", MODE_PRIVATE);
		
		final String username = sp.getString("username", null);
		final String password = sp.getString("password", null);
		
		if (username == null || password == null) {
			Log.d(TAG, "Credentials empty, bring to home screen.");
			Toast.makeText(getApplicationContext(), "Inga användaruppgifter sparade. Vänligen logga in igen...", 3000);
			startActivity(new Intent(getApplicationContext(), StartActivity.class));
		}
		
		Log.d(TAG, "Setting basic authentication credentials. Username: " + username + " password: " + password);
		
		final WebView wv = (WebView) this.findViewById(R.id.webview);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setHttpAuthUsernamePassword("192.168.0.113", "Spring Security Application", username, password);
		
		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				setProgress(newProgress * 1000);
			}
		});
		
		Log.d(TAG, "Displaying url in web view.");
		wv.loadUrl("http://" +username+":"+password+"@192.168.0.113:8080/netcare-web/netcare/mobile/start");
	}
}
