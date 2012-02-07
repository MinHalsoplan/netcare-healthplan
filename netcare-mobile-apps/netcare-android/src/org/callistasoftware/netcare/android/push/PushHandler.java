package org.callistasoftware.netcare.android.push;

import org.callistasoftware.android.net.HttpClientConfiguration;
import org.callistasoftware.android.net.HttpConfigurationFactory;
import org.callistasoftware.netcare.android.ApplicationUtil;
import org.callistasoftware.netcare.android.serviceclient.ServiceFactory;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Handles push events from Google C2DM servers
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class PushHandler implements
		org.callistasoftware.android.c2dm.PushHandler {

	private static final String TAG = PushHandler.class.getSimpleName();
	
	@Override
	public void handlePushEvent(Context context, Intent intent) {
		Log.i(TAG, "Received push message: " + intent.getAction());
	}

	@Override
	public void handlePushRegistration(Context context, Intent intent,
			String registrationId, String server) {
		Log.i(TAG, "Received push registration message. Registration id is: " + registrationId);
		final String username = ApplicationUtil.getProperty(context, "crn");
		final String pin = ApplicationUtil.getProperty(context, "pin");
		
		final HttpClientConfiguration config = HttpConfigurationFactory.newPlainConfigurationWithBasicAuthentication(8080, username, pin);
		
		Log.d(TAG, "Sending registrationn id to server...");
		ServiceFactory.newServiceClient(context, config).registerForPush(registrationId);
	}

	@Override
	public void handlePushUnregistration(Context context, Intent intent) {
		Log.i(TAG, "Received push unregistration message");
	}
}
