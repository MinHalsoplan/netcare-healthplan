package org.callistasoftware.netcare.android;

import org.callistasoftware.android.ServiceCallTask;
import org.callistasoftware.android.ServiceCallback;
import org.callistasoftware.android.net.HttpClientConfiguration;
import org.callistasoftware.android.net.HttpConfigurationFactory;
import org.callistasoftware.android.serviceclient.ServiceResult;
import org.callistasoftware.netcare.android.serviceclient.ServiceClient;
import org.callistasoftware.netcare.android.serviceclient.ServiceFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Start activity for the netcare app. Prompts for username and
 * password and let the user log in.
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class StartActivity extends Activity {
    
	private static final String TAG = StartActivity.class.getSimpleName();
	
	private EditText cnr;
	private EditText pin;
	
	private Button login;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        this.cnr = (EditText) this.findViewById(R.id.cnr);
        this.pin = (EditText) this.findViewById(R.id.pin);
        
        this.login = (Button) this.findViewById(R.id.loginButton);
        this.login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Logging in user " + cnr.getText().toString());
				final String username = cnr.getText().toString().trim();
				final String password = pin.getText().toString().trim();
				
				new ServiceCallTask<Boolean>(StartActivity.this, new ServiceCallback<Boolean>() {

					@Override
					public String getProgressMessage() {
						return getApplicationContext().getString(R.string.loginProgress);
					}

					@Override
					public ServiceResult<Boolean> doCall(final Context ctx) {
						
						final HttpClientConfiguration config = HttpConfigurationFactory.newPlainConfigurationWithBasicAuthentication(
								Integer.valueOf(ApplicationUtil.getProperties(getApplicationContext()).getProperty("port"))
								, username
								, password);
						
						final ServiceClient sc = ServiceFactory.newServiceClient(ctx, config);
						return sc.login();
					}

					@Override
					public void onSuccess(ServiceResult<Boolean> result) {
						
						/*
						 * Save credentials
						 */
						final Editor edit = StartActivity.this.getSharedPreferences("NETCARE", MODE_PRIVATE).edit();
						edit.putString("username", username);
						edit.putString("password", password);
						edit.commit();
						
						/*
						 * We're fine. If the user saved the credentials
						 * save them. Otherwise just keep them in memory
						 */
						if (result.getData().equals(Boolean.TRUE)) {
							startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
						}
					}
				}).execute();
			}
		});
    }
}