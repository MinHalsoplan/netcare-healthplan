package org.callistasoftware.netcare.android.helper;

import android.content.Context;
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
        if (restTemplate == null) {
            restTemplate = new RestTemplate(true);
        }

        return restTemplate;
    }

}
