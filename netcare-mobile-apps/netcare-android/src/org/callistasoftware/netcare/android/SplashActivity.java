package org.callistasoftware.netcare.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash);

        final Handler h = new Handler();
		h.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				finish();
				SplashActivity.this.startActivity(new Intent(SplashActivity.this, StartActivity.class));
			}
		}, 1500);
		
	}
}
