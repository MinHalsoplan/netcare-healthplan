package org.callistasoftware.netcare.android.push;

import org.callistasoftware.android.c2dm.DefaultPushRegistrationHandler;
import org.callistasoftware.android.c2dm.PushHandler;
import org.callistasoftware.android.c2dm.PushRegistrationHandler;
import org.callistasoftware.netcare.android.ApplicationUtil;

import android.util.Log;

/**
 * Push service class. Configures the android-c2dm framework
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 */
public class PushService extends org.callistasoftware.android.c2dm.PushService {

	private static final String TAG = PushService.class.getSimpleName();
	
	private String registrationUrl;
	private String email;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		this.email = "callista.push@gmail.com";
		this.registrationUrl = ApplicationUtil.getServerBaseUrl(getApplicationContext()) + "/api/push/register";
		Log.d(TAG, "Registration url for push services resolved to: " + this.registrationUrl);
	}
	
	@Override
	public PushRegistrationHandler registerPushRegistrationHandler() {
		return new DefaultPushRegistrationHandler();
	}

	@Override
	public PushHandler registerPushHandler() {
		return new org.callistasoftware.netcare.android.push.PushHandler();
	}

	@Override
	public String registerPushEmail() {
		return this.email;
	}

	@Override
	public String registerPushRegistrationUrl() {
		return this.registrationUrl;
	}
}
