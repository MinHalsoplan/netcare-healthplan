package org.callistasoftware.netcare.android;

import org.callistasoftware.android.ServiceCallTask;
import org.callistasoftware.android.ServiceCallback;
import org.callistasoftware.android.net.HttpClientConfiguration;
import org.callistasoftware.android.net.HttpConfigurationFactory;
import org.callistasoftware.android.serviceclient.ServiceResult;
import org.callistasoftware.netcare.android.serviceclient.ServiceClient;
import org.callistasoftware.netcare.android.serviceclient.ServiceFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebViewDatabase;
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
        
        final boolean error = getIntent().getBooleanExtra("error", false);
        
        /*
         * Reset the credentials database
         */
        Log.d(TAG, "Clearing credentials...");
        WebViewDatabase.getInstance(getApplicationContext()).clearHttpAuthUsernamePassword();
        
        final String username = ApplicationUtil.getProperty(getApplicationContext(), "cnr");
        final String pinCode = ApplicationUtil.getProperty(getApplicationContext(), "pin");
        
        if (username != null && pinCode != null && !error) {
        	Log.d(TAG, "Credentials are already stored. Proceeed to login.");
        	startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
        }
        
        setContentView(R.layout.start);
        
        if (error) {
        	final String errorMessage = getIntent().getStringExtra("errorMessage");
        	
        	final AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
			builder.setMessage(errorMessage)
			.setCancelable(false)
			.setTitle(getString(R.string.error))
			.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			
			builder.create().show();
        }
        
        this.cnr = (EditText) this.findViewById(R.id.cnr);
        this.pin = (EditText) this.findViewById(R.id.pin);
        
        this.login = (Button) this.findViewById(R.id.loginButton);
        this.login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Logging in user " + cnr.getText().toString());
				final String username = cnr.getText().toString().trim();
				final String password = pin.getText().toString().trim();
				
				/*
				 * Save credentials
				 */
				Log.d(TAG, "Storing user / pin for user.");
				final Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				edit.putString("cnr", username);
				edit.putString("pin", password);
				edit.commit();
				
				new ServiceCallTask<Boolean>(StartActivity.this, new ServiceCallback<Boolean>() {

					@Override
					public String getProgressMessage() {
						return getApplicationContext().getString(R.string.loginProgress);
					}

					@Override
					public ServiceResult<Boolean> doCall(final Context ctx) {
						
						final HttpClientConfiguration config = HttpConfigurationFactory.newPlainConfigurationWithBasicAuthentication(
								Integer.valueOf(ApplicationUtil.getProperty(getApplicationContext(), "port"))
								, username
								, password);
						
						final ServiceClient sc = ServiceFactory.newServiceClient(ctx, config);
						return sc.login();
					}

					@Override
					public void onSuccess(ServiceResult<Boolean> result) {
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }
}