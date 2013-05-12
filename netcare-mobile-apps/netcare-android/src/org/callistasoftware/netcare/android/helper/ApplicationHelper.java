package org.callistasoftware.netcare.android.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ApplicationHelper {
	
	private static final String TAG = ApplicationHelper.class.getSimpleName();
    private static ApplicationHelper instance = null;

    private Context ctx;

    private ApplicationHelper(final Context context) {
        this.ctx = context;
    }

    public static ApplicationHelper newInstance(final Context context) {
        if (instance == null) {
            instance = new ApplicationHelper(context);
        }

        return instance;
    }

    public final String validateCrn(String crn) {
        if (isWhitespace(crn)) {
            return null;
        }

        if (crn.length() == 10) {
            return "19" + crn;
        } else if (crn.length() == 12) {
            return crn;
        } else {
            return null;
        }
    }
	
	private final String getServerBaseUrl() {
        final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this.ctx);

		final StringBuilder builder = new StringBuilder();
		
		boolean secure = p.getBoolean("secure", true);
		String host = p.getString("host", "netcare.callistasoftware.org");
		String port = p.getString("port", "443");
		
		if (secure) {
			builder.append("https://");
		} else {
			builder.append("http://");
		}
		
		builder.append(host).append(":").append(port);
		
		Log.d(TAG, "Url now resolved to: " + builder.toString());
		return builder.toString();
	}

    public final String getUrl(final String relativePath) {
        return new StringBuilder(getServerBaseUrl()).append(relativePath).toString();
    }
	
	public final boolean isWhitespace(final String s) {
		if (s == null) {
			return true;
		}
		
		return s.trim().length() == 0;
	}
}
