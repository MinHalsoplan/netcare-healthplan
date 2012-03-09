package org.callistasoftware.netcare.android;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class LoginTask extends AsyncTask<String, String, ServiceResult<HttpResponse>> {

	private final Context ctx;
	private final ServiceCallback<HttpResponse> cb;
	private ProgressDialog p;
	
	public LoginTask(final Context ctx, final ServiceCallback<HttpResponse> callback) {
		this.ctx = ctx;
		this.cb = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.p = ProgressDialog.show(this.ctx, ctx.getString(R.string.loading), ctx.getString(R.string.loginProgress), true, false);
	}
	
	@Override
	protected ServiceResult<HttpResponse> doInBackground(final String... params) {
		
		final DefaultHttpClient client = new DefaultHttpClient();
		client.addRequestInterceptor(new HttpRequestInterceptor() {
			
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				final AuthState state = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
				state.setAuthScheme(new BasicScheme());
				state.setCredentials(new UsernamePasswordCredentials(params[0], params[1]));
			}
		}, 0);
		
		final HttpGet get = new HttpGet(ApplicationUtil.getServerBaseUrl(ctx) + "/mobile/checkcredentials");
		try {
			final HttpResponse response = client.execute(get);
			
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
		
		this.p.dismiss();
		
		if (result.isSuccess()) {
			this.cb.onSuccess(result.getData());
		} else {
			this.cb.onFailure(result.getErrorMessage());
		}
	}

}
