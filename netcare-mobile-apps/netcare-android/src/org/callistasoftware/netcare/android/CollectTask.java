package org.callistasoftware.netcare.android;

import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.os.AsyncTask;

public class CollectTask extends AsyncTask<String, String, ServiceResult<Map<String, String>>> {

	private Context ctx;
	private ServiceCallback<Map<String, String>> cb;
	
	public CollectTask(final Context ctx, final ServiceCallback<Map<String, String>> cb) {
		this.ctx = ctx;
		this.cb = cb;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected ServiceResult<Map<String, String>> doInBackground(
			String... args) {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("X-netcare-order", args[0]);
		
		try {
			final RestTemplate rest = new RestTemplate(true);
			final org.springframework.http.HttpEntity<Map<String, String>> ent = new org.springframework.http.HttpEntity<Map<String,String>>(params);
			
			@SuppressWarnings("unchecked")
			final Map<String, String> user = (Map<String, String>) rest.exchange(
					ApplicationUtil.getServerBaseUrl(ctx) + "/mobile/bankid/complete", 
					HttpMethod.POST, 
					ent, 
					Map.class);
			
			return new ServiceResultImpl<Map<String,String>>(user, true, null);
		} catch (final Exception e) {
			e.printStackTrace();
			return new ServiceResultImpl<Map<String, String>>(null, false, e.getMessage());
		}
	}
	
	@Override
	protected void onPostExecute(ServiceResult<Map<String, String>> result) {
		super.onPostExecute(result);
		
		if (result.isSuccess()) {
			this.cb.onSuccess(result.getData());
		} else {
			this.cb.onFailure(result.getErrorMessage());
		}
	}

}
