/**
 * Copyright (C) 2011,2012 Callista Enterprise AB <info@callistaenterprise.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.callistasoftware.netcare.core.spi.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.callistasoftware.netcare.core.repository.UserRepository;
import org.callistasoftware.netcare.core.spi.PushNotificationService;
import org.callistasoftware.netcare.model.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Implementation of service interface
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 */
public class PushNotificationServiceImpl extends ServiceSupport implements PushNotificationService {

	private final static Logger log = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
	
	@Autowired
	private UserRepository repo;
	
	@Value("${c2dm.username}") private String c2dmUsername;
	@Value("${c2dm.password") private String c2dmPassword;
	@Value("${c2dm.auth-url") private String c2dmAuthUrl;
	@Value("${c2dm.url}") private String c2dmUrl;
	@Value("${c2dm.client-app}") private String c2dmClientApp;
	
	@Override
	public void sendPushNotification(String subject, String message,
			Long toUserId) {
		
		final UserEntity user = this.repo.findOne(toUserId);
		if (user == null) {
			throw new UsernameNotFoundException("The user could not be found.");
		}
		
		// Check which provider to use
		final boolean c2dm = user.getProperties().containsKey("c2dmRegistrationId");
		if (c2dm) {
			final String registrationId = user.getProperties().get("c2dmRegistrationId");
			this.sendGooglePushNotification(this.fetchGoogleAuthToken(), registrationId, subject, message, null, null);
		}
		
		throw new UnsupportedOperationException("Only implemented for c2dm so far...");
	}

	String fetchGoogleAuthToken() {
		
		try {
			final URL url = new URL(this.c2dmAuthUrl);
			final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			
			final StringBuilder builder = new StringBuilder();
			builder.append("accountType").append("=").append("HOSTED_OR_GOOGLE").append("&");
			builder.append("Email").append("=").append(this.c2dmUsername).append("&");
			builder.append("Passwd").append("=").append(this.c2dmPassword).append("&");
			builder.append("service").append("=").append("ac2dm").append("&");
			builder.append("source").append("=").append(this.c2dmClientApp);
			
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			final byte[] data = builder.toString().getBytes("UTF-8");
			final OutputStream out = con.getOutputStream();
			
			out.write(data);
			out.close();
			
			final int response = con.getResponseCode();
			if (response == 403) {
				throw new RuntimeException("Could not login with specified Google account. Please verify the credentials");
			}
			
			if (response == 200) {
				log.debug("Call was successful");
			
				final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String authToken = null;
				String line = "";
				
				log.debug("Parsing input stream");
				while ((line = reader.readLine()) != null) {
					
					final String[] split = line.split("=");
					if (split[0].equals("Auth")) {
						authToken = split[1];
						break;
					}
				}
				
				if (authToken != null) {
					log.debug("Found auth token");
					return authToken;
				} else {
					throw new IOException("Could not find any auth token in the response");
				}
			}
			
		} catch (IOException e) {
			log.warn("Caught exception when trying to fetch auth token from Google", e);
			throw new RuntimeException("Caught exception when trying to fetch auth token from Google", e);
		}
		
		return null;
	}
	
	void sendGooglePushNotification(
			final String authToken
			, final String registrationId
			, final String title
			, final String message
			, final String refType
			, final String refValue) {
		
		try {
			
			final StringBuilder builder = new StringBuilder();
			builder.append("registration_id").append("=").append(registrationId).append("&");
			builder.append("collapse_key").append("=").append("netcare").append("&");
			builder.append("data.ref").append("=").append(refType + "," + refValue).append("&");
			builder.append("data.title").append("=").append(title).append("&");
			builder.append("data.message").append("=").append(message).append("&");
			builder.append("data.timestamp").append("=").append(new Long(System.currentTimeMillis())).append("&");
			builder.append("delay_while_idle").append("=").append("true");
			
			/*
			 * Need this because we cannot verify google's host
			 */
			final HostnameVerifier verifier = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			
			final URL url = new URL(this.c2dmUrl);
			final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setHostnameVerifier(verifier);
			
			final byte[] data = builder.toString().getBytes("UTF-8");
			
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset='UTF-8");
			con.setRequestProperty("Content-Length", null);
			con.setRequestProperty("Authorization", "GoogleLogin auth=" + authToken);
			
			final OutputStream out = con.getOutputStream();
			out.write(data);
			out.close();
			
			int response = con.getResponseCode();
			if (response == 200) {
				log.debug("Got OK from Google server. Extracting information from the response...");
				
				final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line = null;
				
				while ((line = reader.readLine()) != null) {
					final String[] split = line.split("=");
					if (split[0].equals("Error")) {
						throw new IOException("There was an error in the response from Google. Description: " + split[1]);
					}
				}
				
			}
		} catch (IOException e) {
			log.warn("Could not send push notification to Google server.", e);
		}
	}
}
