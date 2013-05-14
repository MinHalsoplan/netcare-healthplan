package org.callistasoftware.netcare.android.helper;

import android.content.Context;
import android.preference.PreferenceManager;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestHelper {
    private static RestHelper instance;
    private Context context;

    private RestTemplate restTemplate;

    private RestHelper(final Context context) {
        this.context = context;
    }

    public static RestHelper newInstance(final Context context) {
        if (instance == null) {
            instance = new RestHelper(context);
        }

        return instance;
    }

    public RestTemplate getRestService() {
        ClientHttpRequestFactory requestFactory = null;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("devMode", false)) {
            final String crn = PreferenceManager.getDefaultSharedPreferences(context).getString("devCrn", "191212121212");
            final DefaultHttpClient client = new DefaultHttpClient();
            client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(crn, ""));

            requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        }

        if (restTemplate == null) {
            restTemplate = new RestTemplate(true);
        }

        if (requestFactory != null) {
            restTemplate.setRequestFactory(requestFactory);
        }

        return restTemplate;
    }

}
