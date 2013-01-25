package org.callistasoftware.netcare.android;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
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
	private EditText crn;
	private Button login;
	private String orderRef;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.start);
        
        this.login = (Button) this.findViewById(R.id.loginButton);
        this.crn = (EditText) this.findViewById(R.id.crn);
        this.login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Call authenticate
				new AuthenticateTask(getApplicationContext(), new ServiceCallback<String>() {
					@Override
					public void onSuccess(String response) {
						crn.setEnabled(false);
						login.setEnabled(false);
						
						orderRef = response;
						
						Intent intent = new Intent();
						intent.setPackage("com.bankid.bus");
						intent.setAction(Intent.ACTION_VIEW);
						intent.addCategory(Intent.CATEGORY_BROWSABLE); //optional
						intent.addCategory(Intent.CATEGORY_DEFAULT); //optional
						intent.setType("bankid");            
						intent.setData(Uri.parse("bankid://www.bankid.com?redirect=null"));
						startActivityForResult(intent, 0);
					}
					
					@Override
					public void onFailure(String reason) {
						Log.e(TAG, "Error when doing authenticate(). Reason: " + reason);
						Toast.makeText(StartActivity.this, "Anslutning misslyckades. Försök igen senare...", Toast.LENGTH_LONG).show();
						
						crn.setEnabled(true);
						login.setEnabled(true);
					}
					
				}).execute(crn.getText().toString());
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (orderRef == null) {
    		throw new IllegalStateException("An orderRef should exist after BankID säkerhetsapp have returned.");
    	}
    	
    	Log.d(TAG, "BankID application have returned control to us...");
    	new CollectTask(StartActivity.this, new ServiceCallback<Map<String,String>>() {
			
			@Override
			public void onSuccess(Map<String, String> response) {
				Log.d(TAG, "We are now authenticated. User is: " + response.get("username"));
				startActivity(new Intent(StartActivity.this, WebViewActivity.class));
			}
			
			@Override
			public void onFailure(String reason) {
				Log.e(TAG, "Failed to collect. Error is: " + reason);
				
				Log.e(TAG, "Error when doing collect(). Reason: " + reason);
				Toast.makeText(StartActivity.this, "Autentisering med Min hälsoplan misslyckades. Försök igen senare...", Toast.LENGTH_LONG).show();
				
				crn.setEnabled(true);
				login.setEnabled(true);
			}
		}).execute(orderRef);
    }
}