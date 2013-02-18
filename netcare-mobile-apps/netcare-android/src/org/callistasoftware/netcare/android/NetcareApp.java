package org.callistasoftware.netcare.android;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.Application;
import android.content.Context;

public class NetcareApp extends Application {

	private static DefaultHttpClient client = null;
	private static RestTemplate rest = null;
	
	private static String currentSession = null;
	
	public static String getCurrentSession() {
		return currentSession;
	}
	
	public static void setCurrentSession(final String session) {
		currentSession = session;
	}
	
	public static RestTemplate getRestClient(final Context ctx) {
		if (rest == null) {
			rest = newRest(ctx);
		}
		
		return rest;
	}
	
	public static DefaultHttpClient getHttpClient(final Context ctx) {
		if (client == null) {
			client = newHttpClient(ctx);
		}
		
		return client;
	}
	
	private static DefaultHttpClient newHttpClient(final Context ctx) {
		BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", newSSLFactory(ctx), 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		
		DefaultHttpClient httpclient = new DefaultHttpClient(cm, params);
		httpclient.getCookieStore().getCookies();
		
		return httpclient;
	}
	
	private static SSLSocketFactory newSSLFactory(final Context ctx) {
		try {
			final KeyStore ks = KeyStore.getInstance("BKS");
			final InputStream is = ctx.getResources().openRawResource(R.raw.truststore);
			
			try {
				ks.load(is, "password".toCharArray());
			} finally {
				is.close();
			}
			
			return createFactory(ks);
			
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static SSLSocketFactory createFactory(final KeyStore truststore) {

	    SSLSocketFactory factory;
	    try {
	        factory = new SSLSocketFactory(truststore);
	        factory.setHostnameVerifier((X509HostnameVerifier) SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }

	    return factory;
	}
	
	private static RestTemplate newRest(final Context ctx) {
		final RestTemplate rest = new RestTemplate(true);
		rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory(getHttpClient(ctx)));
		
		return rest;
	}
}
