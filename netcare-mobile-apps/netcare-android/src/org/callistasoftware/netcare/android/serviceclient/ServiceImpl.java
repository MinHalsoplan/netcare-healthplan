package org.callistasoftware.netcare.android.serviceclient;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.callistasoftware.android.net.HttpClientConfiguration;
import org.callistasoftware.android.serviceclient.AbstractServiceClient;
import org.callistasoftware.android.serviceclient.ServiceResult;
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
		return super.doServiceCall("/api/patient/checkcredentials", Boolean.class);
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

}
