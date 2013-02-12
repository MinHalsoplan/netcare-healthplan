package org.callistasoftware.netcare.android;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.Application;

public class NetcareApp extends Application {

	private static final DefaultHttpClient client = newHttpClient();
	private static final RestTemplate rest = newRest();
	
	private static String currentSession = null;
	
	public static String getCurrentSession() {
		return currentSession;
	}
	
	public static void setCurrentSession(final String session) {
		currentSession = session;
	}
	
	public static RestTemplate getRestClient() {
		return rest;
	}
	
	public static DefaultHttpClient getHttpClient() {
		return client;
	}
	
	private static DefaultHttpClient newHttpClient() {
		BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		
		DefaultHttpClient httpclient = new DefaultHttpClient(cm, params);
		httpclient.getCookieStore().getCookies();
		
		return httpclient;
	}
	
	private static RestTemplate newRest() {
		final RestTemplate rest = new RestTemplate(true);
		rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory(getHttpClient()));
		
		return rest;
	}
}
