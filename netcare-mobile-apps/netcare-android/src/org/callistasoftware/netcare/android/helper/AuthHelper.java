package org.callistasoftware.netcare.android.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import org.callistasoftware.netcare.android.R;
import org.callistasoftware.netcare.android.ServiceCallback;
import org.callistasoftware.netcare.android.WebViewActivity;
import org.callistasoftware.netcare.android.task.AuthenticateTask;
import org.callistasoftware.netcare.android.task.CollectTask;

public class AuthHelper {

    public static final String NETCARE_AUTH_HEADER = "X-netcare-order";

    private static final String TAG = AuthHelper.class.getSimpleName();

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

    private void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void startAuthentication(final String crn, final ServiceCallback<Intent> callback) {
        new AuthenticateTask(this.context, new ServiceCallback<String>() {
            @Override
            public void onSuccess(final String orderRef) {
                /*
                 * The authenticate task will return an order ref which we must validate
                 * The order ref is a string
                 */
                Log.d(TAG, "Order reference returned: " + orderRef);
                setSessionId(orderRef);

                Intent intent = new Intent();
                intent.setPackage("com.bankid.bus");
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE); //optional
                intent.addCategory(Intent.CATEGORY_DEFAULT); //optional
                intent.setType("bankid");
                intent.setData(Uri.parse("bankid://www.bankid.com?redirect=null"));

                callback.onSuccess(intent);

            }

            @Override
            public void onFailure(String reason) {
                setSessionId(null);
                callback.onFailure(reason);
            }
        }).execute(crn);
    }

    public void completeAuthentication(final ServiceCallback callback) {
        Log.d(TAG, "BankID application have returned control to us...");
        new CollectTask(context, new ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    callback.onSuccess(response);
                    GCMHelper.newInstance(context).gcmRegister();
                } else {
                    setSessionId(null);
                    callback.onFailure("Response from service was null");
                }
            }

            @Override
            public void onFailure(String reason) {
                setSessionId(null);
                callback.onFailure(reason);
            }
        }).execute(getSessionId());
    }
}
