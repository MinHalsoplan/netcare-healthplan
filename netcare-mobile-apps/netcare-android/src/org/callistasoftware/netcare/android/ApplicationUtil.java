package org.callistasoftware.netcare.android;

import java.util.Properties;

import android.content.Context;

public class ApplicationUtil {

	private static Properties p = null;
	
	public static Properties getProperties(final Context ctx) {
		if (p != null) {
			return p;
		}
		
		try {
			p = new Properties();
			p.load(ctx.getResources().openRawResource(R.raw.app));
			
			return p;
		} catch (final Exception e) {
			throw new RuntimeException("Could not load properties from file.");
		}
	}
}
