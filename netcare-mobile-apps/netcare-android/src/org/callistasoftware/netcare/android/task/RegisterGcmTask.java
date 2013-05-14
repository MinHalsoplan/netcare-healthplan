package org.callistasoftware.netcare.android.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import org.callistasoftware.netcare.android.ServiceCallback;
import org.callistasoftware.netcare.android.ServiceResult;
import org.callistasoftware.netcare.android.ServiceResultImpl;
import org.callistasoftware.netcare.android.helper.ApplicationHelper;
import org.callistasoftware.netcare.android.helper.AuthHelper;
import org.callistasoftware.netcare.android.helper.RestHelper;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

public class RegisterGcmTask extends AsyncTask<String, String, ServiceResult<String>> {

    private static final String TAG = RegisterGcmTask.class.getSimpleName();

    private Context context;
    private ServiceCallback<String> callback;

    private boolean devMode;
    private String devCrn;

    public RegisterGcmTask(final Context context, final ServiceCallback<String> callback) {
        this.context = context;
        this.callback = callback;
        this.devMode = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("devMode", false);
        this.devCrn = PreferenceManager.getDefaultSharedPreferences(context).getString("devCrn", "191212121212");
    }

    @Override
    protected ServiceResult<String> doInBackground(String... strings) {
        try {
            final RestTemplate rest = RestHelper.newInstance(context).getRestService();

            final HttpHeaders headers = new HttpHeaders();
            headers.put(AuthHelper.NETCARE_AUTH_HEADER, Collections.singletonList(AuthHelper.newInstance(context).getSessionId()));

            final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
            body.add("c2dmRegistrationId", strings[0]);
            body.add("os.version", String.valueOf(Build.VERSION.SDK_INT));

            final PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            body.add("app.version", pi.versionName + "-" + pi.versionCode);

            final HttpEntity<Map<String, String>> ent = new HttpEntity<Map<String,String>>(body.toSingleValueMap(), headers);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> result = rest.exchange(ApplicationHelper.newInstance(context).getUrl("/mobile/push/gcm"),
                    HttpMethod.POST,
                    ent,
                    Map.class);

            if (result.getStatusCode().equals(HttpStatus.OK)) {
                Log.i(TAG, "Successfully registered for push notifications.");
                return new ServiceResultImpl<String>("Successfully registered for push", true, null);
            } else {
                Log.w(TAG, "Could not register for push notifications. Response code: " + result.getStatusCode());
                return new ServiceResultImpl<String>(null, false, "Could not register for push notifications");
            }
        } catch (final Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to register for push. Exception is: " + e.getMessage());
            return new ServiceResultImpl<String>(null, false, "Failed to register for push. Exception is: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(ServiceResult<String> result) {
        if (result.isSuccess()) {
            callback.onSuccess(null);
        } else {
            callback.onFailure(null);
        }
    }
}
