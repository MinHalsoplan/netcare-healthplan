package org.callistasoftware.netcare.android.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.callistasoftware.netcare.android.R;
import org.callistasoftware.netcare.android.ServiceCallback;
import org.callistasoftware.netcare.android.ServiceResult;
import org.callistasoftware.netcare.android.ServiceResultImpl;
import org.callistasoftware.netcare.android.helper.ApplicationHelper;
import org.callistasoftware.netcare.android.helper.RestHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class AuthenticateTask extends AsyncTask<String, String, ServiceResult<String>> {

	private static final String TAG = AuthenticateTask.class.getSimpleName();
	
	private final Context ctx;
	private final ServiceCallback<String> cb;
	
	public AuthenticateTask(final Context ctx, final ServiceCallback<String> callback) {
		this.ctx = ctx;
		this.cb = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected ServiceResult<String> doInBackground(final String... params) {
		
		final MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
		data.add("crn", params[0]);
		
		try {
			final RestTemplate rest = RestHelper.newInstance(ctx.getApplicationContext()).getRestService();
			rest.setErrorHandler(new ResponseErrorHandler() {
				
				@Override
				public boolean hasError(ClientHttpResponse response) throws IOException {
					Log.d(TAG, "hasError() - " + response.getRawStatusCode());
					if (response.getRawStatusCode() >= 400) {
						return true;
					}
					
					return false;
				}
				
				@Override
				public void handleError(ClientHttpResponse response) throws IOException {
					
					Log.d(TAG, "handleError() - " + response.getRawStatusCode());
					
					if (response.getRawStatusCode() == 404) {
						throw new HttpClientErrorException(HttpStatus.NOT_FOUND, ctx.getResources().getString(R.string.no_account));
					}
					
					if (response.getRawStatusCode() >= 400 && response.getRawStatusCode() < 600) {
						throw new HttpClientErrorException(response.getStatusCode(), ctx.getResources().getString(R.string.generic_error));
					}
				}
			});
			
			String orderRef = rest.postForObject(ApplicationHelper.newInstance(ctx.getApplicationContext()).getUrl("/mobile/bankid/authenticate"), data, String.class);
			return new ServiceResultImpl<String>(orderRef, true, null);
		} catch (final HttpClientErrorException e) {
			return new ServiceResultImpl<String>(null, false, e.getStatusText());
		}
	}
	
	@Override
	protected void onPostExecute(ServiceResult<String> result) {
		super.onPostExecute(result);
		
		if (result.isSuccess()) {
			this.cb.onSuccess(result.getData());
		} else {
			this.cb.onFailure(result.getErrorMessage());
		}
	}

}
