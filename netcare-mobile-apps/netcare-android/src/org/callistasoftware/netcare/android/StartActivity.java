package org.callistasoftware.netcare.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.callistasoftware.netcare.android.helper.ApplicationHelper;
import org.callistasoftware.netcare.android.helper.AuthHelper;
import org.callistasoftware.netcare.android.helper.GCMHelper;

public class StartActivity extends Activity {
	
	private static final String TAG = StartActivity.class.getSimpleName();
	private EditText crn;
	private Button login;

    private SharedPreferences p;

    private boolean devMode;
    private boolean autoLogin;
    private String crnFromPreferences;

    private void refreshPreferences() {
        this.p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.devMode = p.getBoolean("devMode", false);
        this.autoLogin = p.getBoolean("rememberCrn", false);
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        this.login = (Button) this.findViewById(R.id.loginButton);
        this.crn = (EditText) this.findViewById(R.id.crn);

        refreshPreferences();
        doAutoLoginIfPossible();

        this.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final String validated = ApplicationHelper.newInstance(getApplicationContext()).validateCrn(crn.getText().toString().trim());
            if (validated != null) {

                if (StartActivity.this.autoLogin) {
                    Log.i(TAG, "Saving civic registration number to shared preferences");
                    p.edit().putString("crn", validated).commit();
                }

                doLogin(validated);
            } else {
                Toast.makeText(getApplicationContext(), "Personnummret är ogiltigt", Toast.LENGTH_LONG).show();
            }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPreferences();
    }

    void doAutoLoginIfPossible() {
        if (this.autoLogin) {
            this.crnFromPreferences = ApplicationHelper.newInstance(getApplicationContext()).validateCrn(p.getString("crn", ""));
            if (this.crnFromPreferences != null) {
                Log.i(TAG, "Performing auto-login using: " + crn);
                crn.setText(this.crnFromPreferences);
                doLogin(this.crnFromPreferences);
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Kunde inte logga in automatiskt eftersom personnummret var ogiltigt.", Toast.LENGTH_LONG).show();
            }
        }
    }

    void doLogin(final String civicRegistrationNumber) {

        if (this.devMode) {
            final SharedPreferences.Editor e = p.edit();
            e.putString("devCrn", civicRegistrationNumber);
            e.commit();

            startActivity(new Intent(StartActivity.this, WebViewActivity.class));
            finish();

            return;
        }

        AuthHelper.newInstance(getApplicationContext()).startAuthentication(civicRegistrationNumber, new ServiceCallback<Intent>() {
            @Override
            public void onSuccess(Intent response) {
                startActivityForResult(response, 0);
            }

            @Override
            public void onFailure(String reason) {
                crn.setEnabled(true);
                login.setEnabled(true);
                showDialog(reason);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AuthHelper.newInstance(getApplicationContext()).completeAuthentication(new ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) {
                startActivity(new Intent(StartActivity.this, WebViewActivity.class));
                finish();
            }

            @Override
            public void onFailure(String reason) {
                showDialog(getResources().getString(R.string.generic_error));
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
    	return true;
    }
    
    private void showDialog(final String message) {
    	final AlertDialog.Builder d = new AlertDialog.Builder(this);
    	d.setTitle("Information");
    	d.setIcon(android.R.drawable.ic_dialog_alert);
    	d.setMessage(message);
    	d.setNeutralButton("OK", new Dialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	
    	d.create().show();
    }
}