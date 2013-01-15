package org.callistasoftware.netcare.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

/**
 * Start activity for the netcare app. Prompts for username and
 * password and let the user log in.
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class StartActivity extends Activity {
	
	private Button login;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.start);
        
        this.login = (Button) this.findViewById(R.id.loginButton);
        this.login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setPackage("com.bankid.bus");
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE); //optional
				intent.addCategory(Intent.CATEGORY_DEFAULT); //optional
				intent.setType("bankid");            
				intent.setData(Uri.parse("bankid://www.bankid.com?redirect=null"))
				;
				startActivityForResult(intent, 0);
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }
}