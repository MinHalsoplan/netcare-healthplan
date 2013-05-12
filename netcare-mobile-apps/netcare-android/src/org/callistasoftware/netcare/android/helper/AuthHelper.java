package org.callistasoftware.netcare.android.helper;

import android.content.Context;

public class AuthHelper {

    public static final String NETCARE_AUTH_HEADER = "X-netcare-order";

    private static AuthHelper instance;
    private Context context;

    private String sessionId;

    private AuthHelper(final Context context) {
        this.context = context;
    }

    public static AuthHelper newInstance(final Context context) {
        if (instance == null) {
            instance = new AuthHelper(context);
        }

        return instance;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
