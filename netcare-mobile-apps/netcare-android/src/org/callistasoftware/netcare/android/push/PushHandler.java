package org.callistasoftware.netcare.android.push;

import org.callistasoftware.android.net.HttpClientConfiguration;
import org.callistasoftware.android.net.HttpConfigurationFactory;
import org.callistasoftware.netcare.android.ApplicationUtil;
import org.callistasoftware.netcare.android.WebViewActivity;
import org.callistasoftware.netcare.android.serviceclient.ServiceFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		final String subject = intent.getExtras().getString("title");
		final String message = intent.getExtras().getString("message");
		final String when = intent.getExtras().getString("timestamp");
		
		final Intent notificationIntent = new Intent(context, WebViewActivity.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, 0);
		
		final Notification notification = new Notification(android.R.drawable.sym_def_app_icon, message, Long.valueOf(when));
		notification.defaults |= Notification.DEFAULT_ALL;
		
		notification.setLatestEventInfo(context.getApplicationContext(), subject, message, contentIntent);
		notificationManager.notify(1, notification);
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
