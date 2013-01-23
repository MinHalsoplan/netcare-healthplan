package org.callistasoftware.netcare.android;

import java.io.IOException;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;

public class LoginTask extends AsyncTask<String, String, ServiceResult<HttpResponse>> {

	private final Context ctx;
	private final ServiceCallback<HttpResponse> cb;
	
	public LoginTask(final Context ctx, final ServiceCallback<HttpResponse> callback) {
		this.ctx = ctx;
		this.cb = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected ServiceResult<HttpResponse> doInBackground(final String... params) {
		
		try {
			final DefaultHttpClient client = new DefaultHttpClient();
			final HttpPost post = new HttpPost(ApplicationUtil.getServerBaseUrl(ctx) + "/mobile/bankid/authenticate");
			post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("crn", params[0]))));
			
			final HttpResponse response = client.execute(post);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return new ServiceResultImpl<HttpResponse>(response, true, "");
			} else {
				return new ServiceResultImpl<HttpResponse>(null, false, response.getStatusLine().getStatusCode() + "");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return new ServiceResultImpl<HttpResponse>(null, false, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new ServiceResultImpl<HttpResponse>(null, false, e.getMessage());
		}
	}
	
	@Override
	protected void onPostExecute(ServiceResult<HttpResponse> result) {
		super.onPostExecute(result);
		
		if (result.isSuccess()) {
			this.cb.onSuccess(result.getData());
		} else {
			this.cb.onFailure(result.getErrorMessage());
		}
	}

}
