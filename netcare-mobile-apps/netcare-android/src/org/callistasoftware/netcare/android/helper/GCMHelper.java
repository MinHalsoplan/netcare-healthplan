package org.callistasoftware.netcare.android.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import org.callistasoftware.netcare.android.ServiceCallback;
import org.callistasoftware.netcare.android.task.RegisterGcmTask;
import org.callistasoftware.netcare.android.task.UnRegisterGcmTask;

public class GCMHelper {
    private static final String TAG = GCMHelper.class.getSimpleName();
    private static GCMHelper instance;

    private Context context;

    private GCMHelper(final Context context) {
        this.context = context;
    }

    public static GCMHelper newInstance(final Context context) {
        if (instance == null) {
            instance = new GCMHelper(context);
        }

        return instance;
    }

    public void gcmRegister() {
        final boolean push = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("push", true);
        if (push) {
            Log.i(TAG, "Register for push notifications...");
            GCMRegistrar.checkDevice(context);
            GCMRegistrar.checkManifest(context);
            GCMRegistrar.register(context, "1072676211966");
        } else {
            Log.i(TAG, "Skipping push registration since the user have not enabled them.");
            Log.i(TAG, "Unregister push notifications from the server...");
            gcmUnregister();
        }

    }

    public void gcmUnregister() {
        GCMRegistrar.checkDevice(context);
        GCMRegistrar.unregister(context);

        new UnRegisterGcmTask(context, new ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Successfully unregistered push");
            }

            @Override
            public void onFailure(String reason) {
                Log.e(TAG, "Could not unregister push");
            }
        });
    }

    public void publishRegistrationId(final String registrationId) {
        new RegisterGcmTask(context, new ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Successfully published registration id to server");
            }

            @Override
            public void onFailure(String reason) {
                Log.d(TAG, "Failed to publish registration id to server");
            }
        }).execute(registrationId);
    }

    public void unpublishRegistrationId(final String registrationId) {
        new UnRegisterGcmTask(context, new ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Unpublished registration id successfully");
            }

            @Override
            public void onFailure(String reason) {
                Log.d(TAG, "Failed to unpublish registration id");
            }
        });
    }
}
