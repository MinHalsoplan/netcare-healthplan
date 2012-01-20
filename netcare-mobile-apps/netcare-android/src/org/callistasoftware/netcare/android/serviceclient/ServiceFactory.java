package org.callistasoftware.netcare.android.serviceclient;

import org.callistasoftware.android.net.HttpClientConfiguration;

import android.content.Context;

public class ServiceFactory {

	public static ServiceClient newServiceClient(final Context ctx, final HttpClientConfiguration config) {
		return new ServiceImpl("192.168.0.109", "/netcare-web", config, ctx);
	}
}
