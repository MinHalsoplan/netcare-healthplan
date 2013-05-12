package org.callistasoftware.netcare.android.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.callistasoftware.netcare.android.*;
import org.callistasoftware.netcare.android.helper.ApplicationHelper;
import org.callistasoftware.netcare.android.helper.AuthHelper;
import org.callistasoftware.netcare.android.helper.RestHelper;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

public class UnRegisterGcmTask extends AsyncTask<String, String, ServiceResult<String>> {

	private static final String TAG = UnRegisterGcmTask.class.getSimpleName();
	
	private Context ctx;
	private ServiceCallback<String> cb;
	
	public UnRegisterGcmTask(final Context ctx, final ServiceCallback<String> cb) {
		this.ctx = ctx;
		this.cb = cb;
	}
	
	@Override
	protected ServiceResult<String> doInBackground(String... params) {
		try {
			final RestTemplate rest = RestHelper.newInstance(ctx.getApplicationContext()).getRestService();
			
			final HttpHeaders headers = new HttpHeaders();
			headers.put(AuthHelper.NETCARE_AUTH_HEADER, Collections.singletonList(AuthHelper.newInstance(ctx.getApplicationContext()).getSessionId()));
	
			final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
			final HttpEntity<Map<String, String>> ent = new HttpEntity<Map<String,String>>(body.toSingleValueMap(), headers);
	
			@SuppressWarnings("rawtypes")
			ResponseEntity<Map> result = rest.exchange(ApplicationHelper.newInstance(ctx.getApplicationContext()).getUrl("/mobile/push/gcm"),
				HttpMethod.DELETE, 
				ent, 
				Map.class);
			
			if (result.getStatusCode().equals(HttpStatus.OK)) {
				Log.i(TAG, "Successfully registered for push notifications.");
				return new ServiceResultImpl<String>(null, true, null);
			} else {
				Log.w(TAG, "Could not register for push notifications. Response code: " + result.getStatusCode());
				return new ServiceResultImpl<String>(null, false, null);
			}
	    } catch (final Exception e) {
	            e.printStackTrace();
	            Log.d(TAG, "Failed to register for push. Exception is: " + e.getMessage());
	            return new ServiceResultImpl<String>(null, false, null);
	    }
	}
	
	@Override
	protected void onPostExecute(ServiceResult<String> result) {
		if (result.isSuccess()) {
			cb.onSuccess(null);
		} else {
			cb.onFailure(null);
		}
	}

}
