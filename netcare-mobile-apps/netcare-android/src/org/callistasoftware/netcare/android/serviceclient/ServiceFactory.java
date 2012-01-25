package org.callistasoftware.netcare.android.serviceclient;

import java.util.Properties;

import org.callistasoftware.android.net.HttpClientConfiguration;
import org.callistasoftware.netcare.android.ApplicationUtil;
import org.callistasoftware.netcare.android.R;

import android.content.Context;

public class ServiceFactory {

	public static ServiceClient newServiceClient(final Context ctx, final HttpClientConfiguration config) {
		try {
			final Properties p = new Properties();
			p.load(ctx.getResources().openRawResource(R.raw.app));
		
			return new ServiceImpl(ApplicationUtil.getProperties(ctx).getProperty("host"), "/netcare-web", config, ctx);
		} catch (final Exception e) {
			throw new RuntimeException("Could not load properties from file.");
		}
	}
}
