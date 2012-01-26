package org.callistasoftware.netcare.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ApplicationUtil {
	
	private static final String TAG = ApplicationUtil.class.getSimpleName();
	
	public static String getProperty(final Context ctx, final String property) {
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		final String val = sp.getString(property, null);
		
		Log.d(TAG, "Resolvning property " + property + ". Property resolved to: " + val);
		return val;
	}
}
