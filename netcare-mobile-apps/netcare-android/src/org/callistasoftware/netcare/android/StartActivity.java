package org.callistasoftware.netcare.android;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Toast;

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
				
				if (ApplicationUtil.isWhitespace(username) || ApplicationUtil.isWhitespace(password)) {
					Log.d(TAG, "Display toast to inform user that blanks are not allowed.");
					Toast.makeText(StartActivity.this.getApplicationContext(), getString(R.string.provideCredentials), 5000).show();
					return;
				}
				
				new LoginTask(v.getContext(), new ServiceCallback<HttpResponse>() {
					
					@Override
					public void onSuccess(HttpResponse response) {
						/*
						 * Save credentials
						 */
						Log.d(TAG, "Storing user / pin for user.");
						final Editor edit = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
						edit.putString("crn", username);
						edit.putString("pin", password);
						edit.commit();
						
						startActivity(new Intent(StartActivity.this, WebViewActivity.class));
					}
					
					@Override
					public void onFailure(String reason) {
						Log.d(TAG, "Display toast to inform user that blanks are not allowed.");
						Toast.makeText(StartActivity.this.getApplicationContext(), getString(R.string.credentialsError), 5000).show();
						return;
					}
				}).execute(username, password);
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }
}