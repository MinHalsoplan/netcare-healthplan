package org.callistasoftware.netcare.android.serviceclient;

import org.callistasoftware.android.net.HttpClientConfiguration;
import org.callistasoftware.netcare.android.ApplicationUtil;

import android.content.Context;

public class ServiceFactory {

	public static ServiceClient newServiceClient(final Context ctx, final HttpClientConfiguration config) {
		return new ServiceImpl(ApplicationUtil.getProperty(ctx, "host"), "/netcare-web", config, ctx);
	}
}
