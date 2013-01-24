package org.callistasoftware.netcare.android;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.os.AsyncTask;

public class LoginTask extends AsyncTask<String, String, ServiceResult<String>> {

	private final Context ctx;
	private final ServiceCallback<String> cb;
	
	public LoginTask(final Context ctx, final ServiceCallback<String> callback) {
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
			final RestTemplate rest = new RestTemplate(true);
			String orderRef = rest.postForObject(ApplicationUtil.getServerBaseUrl(ctx) + "/mobile/bankid/authenticate", data, String.class);
			return new ServiceResultImpl<String>(orderRef, true, null);
		} catch (final Exception e) {
			return new ServiceResultImpl<String>(null, false, e.getMessage());
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
