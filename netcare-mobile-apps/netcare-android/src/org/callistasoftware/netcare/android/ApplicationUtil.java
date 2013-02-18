package org.callistasoftware.netcare.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ApplicationUtil {
	
	private static final String TAG = ApplicationUtil.class.getSimpleName();
	
	public static String getServerBaseUrl(final Context ctx) {
		final StringBuilder builder = new StringBuilder();
		
		boolean secure = getBooleanProperty(ctx, "secure");
		String host = getProperty(ctx, "host");
		String port = getProperty(ctx, "port");
		
		Log.d(TAG, "Secure " + secure);
		Log.d(TAG, "Host: " + host);
		Log.d(TAG, "Port: " + port);
		
		if (secure) {
			builder.append("https://");
		} else {
			builder.append("http://");
		}
		
		if (isWhitespace(host)) {
			host = "netcare.callistasoftware.org";
		}
		
		if (isWhitespace(port)) {
			port = "443";
		}
		
		builder.append(host).append(":").append(port);
		
		Log.d(TAG, "Url now resolved to: " + builder.toString());
		return builder.toString();
	}
	
	public static String getProperty(final Context ctx, final String property) {
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		final String val = sp.getString(property, null);
		
		Log.d(TAG, "Resolvning property " + property + ". Property resolved to: " + val);
		return val;
	}
	
	public static boolean getBooleanProperty(final Context ctx, final String property) {
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		final boolean val = sp.getBoolean(property, true);
		
		Log.d(TAG, "Resolved property "+ property + " to " + val);
		return val;
	}
	
	public static final boolean isWhitespace(final String s) {
		if (s == null) {
			return true;
		}
		
		return s.trim().length() == 0;
	}
}
