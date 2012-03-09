package org.callistasoftware.netcare.android.push;

import java.io.IOException;
import java.util.Collections;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.callistasoftware.netcare.android.ApplicationUtil;
import org.callistasoftware.netcare.android.R;
import org.callistasoftware.netcare.android.WebViewActivity;

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
		
		final Notification notification = new Notification(R.drawable.jkpg_icon, message, Long.valueOf(when));
		notification.defaults |= Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notification.setLatestEventInfo(context.getApplicationContext(), subject, message, contentIntent);
		notificationManager.notify(1, notification);
	}

	@Override
	public void handlePushRegistration(Context context, Intent intent,
			String registrationId, String server) {
		Log.i(TAG, "Received push registration message. Registration id is: " + registrationId);
		final String username = ApplicationUtil.getProperty(context, "crn");
		final String pin = ApplicationUtil.getProperty(context, "pin");
		
		try {
			final DefaultHttpClient client = new DefaultHttpClient();
			client.addRequestInterceptor(new HttpRequestInterceptor() {
				
				@Override
				public void process(HttpRequest request, HttpContext context)
						throws HttpException, IOException {
					final AuthState state = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
					state.setAuthScheme(new BasicScheme());
					state.setCredentials(new UsernamePasswordCredentials(username, pin));
				}
			}, 0);
			
			final String baseUrl = ApplicationUtil.getServerBaseUrl(context);
            final HttpPost post = new HttpPost(baseUrl + "/api/push/register/c2dm");           
            post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("c2dmRegistrationId", registrationId))));
            
            final HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	Log.i(TAG, "Successfully registered for push notifications.");
            } else {
            	Log.w(TAG, "Could not register for push notifications. Response code: " + response.getStatusLine().getStatusCode());
            }
            
	    } catch (final Exception e) {
	            e.printStackTrace();
	            Log.d(TAG, "Failed to register for push. Exception is: " + e.getMessage());
	    }
	}

	@Override
	public void handlePushUnregistration(Context context, Intent intent) {
		Log.i(TAG, "Received push unregistration message");
	}
}
