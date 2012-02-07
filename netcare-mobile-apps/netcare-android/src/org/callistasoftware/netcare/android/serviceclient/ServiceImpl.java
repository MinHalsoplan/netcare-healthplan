package org.callistasoftware.netcare.android.serviceclient;

import java.io.IOException;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.callistasoftware.android.net.HttpClientConfiguration;
import org.callistasoftware.android.serviceclient.AbstractServiceClient;
import org.callistasoftware.android.serviceclient.ServiceResult;
import org.callistasoftware.android.serviceclient.ServiceResultImpl;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;

import android.content.Context;
import android.util.Log;

class ServiceImpl extends AbstractServiceClient implements ServiceClient {

	private static final String TAG = ServiceImpl.class.getSimpleName();
	
	public ServiceImpl(String host, String baseUrl,
			HttpClientConfiguration config, Context context) {
		super(host, baseUrl, config, context);
	}

	@Override
	public ServiceResult<Boolean> login() {
		return super.doServiceCall("/netcare/mobile/checkcredentials", Boolean.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T parseServiceResponse(HttpResponse response,
			Class<T> returnClass) throws JsonParseException,
			JsonMappingException, IllegalStateException, IOException {
	
		final JsonFactory jFac = new JsonFactory();
		final JsonParser parser = jFac.createJsonParser(response.getEntity().getContent());
		
		parser.nextToken();
		
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			final String currentName = parser.getCurrentName();
			Log.d(TAG, "Found: " + currentName);
			
			parser.nextToken();
			if ("data".equals(currentName)) {
				return (T) Boolean.valueOf(parser.getBooleanValue());
			}	
		}
		
		throw new RuntimeException("Could not find boolean value in JSON.");
	}

	@Override
	public ServiceResult<Boolean> registerForPush(String registrationId) {
		try {
			final HttpPost post = new HttpPost(getBaseUrl() + "/api/push/register/c2dm");		
			post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("c2dmRegistrationId", registrationId))));
			
			final HttpResponse response = getHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return ServiceResultImpl.newSuccessfulServiceResult(Boolean.TRUE);
			} else {
				return ServiceResultImpl.newFailedServiceResult("Bad response code.");
			}
			
		} catch (final Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Failed to register for push. Exception is: " + e.getMessage());
			
			return ServiceResultImpl.newFailedServiceResult(e.getMessage());
		}
	}

}
