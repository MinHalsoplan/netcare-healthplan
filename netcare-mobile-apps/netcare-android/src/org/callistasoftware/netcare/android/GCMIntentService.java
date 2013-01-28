package org.callistasoftware.netcare.android;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received push message: " + intent.getAction());
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		final String subject = intent.getExtras().getString("title");
		final String message = intent.getExtras().getString("message");
		final String when = intent.getExtras().getString("timestamp");
		
		final Intent notificationIntent = new Intent(context, WebViewActivity.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, 0);
		
		final Notification notification = new Notification(R.drawable.mhp_icon, message, Long.valueOf(when));
		notification.defaults |= Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notification.setLatestEventInfo(context.getApplicationContext(), subject, message, contentIntent);
		notificationManager.notify(1, notification);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Received push registration message. Registration id is: " + registrationId);
		try {
			final RestTemplate rest = NetcareApp.getRestClient();
			
			final HttpHeaders headers = new HttpHeaders();
			headers.put("X-netcare-order", Collections.singletonList(NetcareApp.getCurrentSession()));
	
			final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
			body.add("c2dmRegistrationId", registrationId);
			
			final HttpEntity<Map<String, String>> ent = new HttpEntity<Map<String,String>>(body.toSingleValueMap(), headers);
	
			@SuppressWarnings("rawtypes")
			ResponseEntity<Map> result = rest.exchange(ApplicationUtil.getServerBaseUrl(context) + "/mobile/push/register/c2dm", 
				HttpMethod.POST, 
				ent, 
				Map.class);
			
			if (result.getStatusCode().equals(HttpStatus.OK)) {
				Log.i(TAG, "Successfully registered for push notifications.");
			} else {
				Log.w(TAG, "Could not register for push notifications. Response code: " + result.getStatusCode());
			}
	    } catch (final Exception e) {
	            e.printStackTrace();
	            Log.d(TAG, "Failed to register for push. Exception is: " + e.getMessage());
	    }
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
