package org.callistasoftware.netcare.android;

import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.os.AsyncTask;

public class CollectTask extends AsyncTask<String, String, ServiceResult<String>> {

	private static final String TAG = CollectTask.class.getSimpleName();
	
	private Context ctx;
	private ServiceCallback<String> cb;
	
	public CollectTask(final Context ctx, final ServiceCallback<String> cb) {
		this.ctx = ctx;
		this.cb = cb;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected ServiceResult<String> doInBackground(
			String... args) {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("X-netcare-order", args[0]);
		
		try {
			final RestTemplate rest = new RestTemplate(true);
			final org.springframework.http.HttpEntity<Map<String, String>> ent = new org.springframework.http.HttpEntity<Map<String,String>>(params);
			
			
			@SuppressWarnings("rawtypes")
			final ResponseEntity<Map> exchange = rest.exchange(
					ApplicationUtil.getServerBaseUrl(ctx) + "/mobile/bankid/complete", 
					HttpMethod.GET, 
					ent, 
					Map.class);
			
			return new ServiceResultImpl<String>((String) exchange.getBody().get("username"), true, null);
			
		} catch (final Exception e) {
			e.printStackTrace();
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
