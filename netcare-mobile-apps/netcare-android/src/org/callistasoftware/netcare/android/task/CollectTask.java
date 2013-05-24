package org.callistasoftware.netcare.android.task;

import android.content.Context;
import android.os.AsyncTask;
import org.callistasoftware.netcare.android.R;
import org.callistasoftware.netcare.android.ServiceCallback;
import org.callistasoftware.netcare.android.ServiceResult;
import org.callistasoftware.netcare.android.ServiceResultImpl;
import org.callistasoftware.netcare.android.helper.ApplicationHelper;
import org.callistasoftware.netcare.android.helper.AuthHelper;
import org.callistasoftware.netcare.android.helper.RestHelper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class CollectTask extends AsyncTask<String, String, ServiceResult<String>> {
	
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
		params.add(AuthHelper.NETCARE_AUTH_HEADER, args[0]);
		
		try {
			final RestTemplate rest = RestHelper.newInstance(ctx.getApplicationContext()).getRestService();
			final org.springframework.http.HttpEntity<Map<String, String>> ent = new org.springframework.http.HttpEntity<Map<String,String>>(params);
			
			
			@SuppressWarnings("rawtypes")
			final ResponseEntity<Map> exchange = rest.exchange(
					ApplicationHelper.newInstance(ctx.getApplicationContext()).getUrl("/mobile/bankid/complete"),
					HttpMethod.GET, 
					ent, 
					Map.class);
			
			return new ServiceResultImpl<String>((String) exchange.getBody().get("username"), true, null);
			
		} catch (final Exception e) {
			e.printStackTrace();
			return new ServiceResultImpl<String>(null, false, ctx.getResources().getString(R.string.generic_error));
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
