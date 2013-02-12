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

import java.io.IOException;

import org.callistasoftware.netcare.core.repository.UserRepository;
import org.callistasoftware.netcare.core.spi.PushNotificationService;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * Implementation of service interface
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 */
@Service
public class PushNotificationServiceImpl extends ServiceSupport implements PushNotificationService {

	@Autowired
	private UserRepository repo;

	// C2DM
	@Value("${gcm.authKey}")
	private String gcmAuthKey;

	// APNS
	@Value("${apns.cert-file}")
	private String apnsCertFile;
	@Value("${apns.cert-password}")
	private String apnsCertPassword;
	@Value("${apns.production}")
	private boolean apnsProduction;
	@Value("${apns.service.url}")
	private String apnsServiceUrl;
	
	private int apnsCount = 1;

	void sendGcmNotification(final PatientEntity p, final String title, final String msg) {
		if (!p.isGcmUser()) {
			throw new IllegalStateException(
					"User is not an Android user. We should never attempt to send a GCM message if we don't have a valid GCM registration id");
		}

		final String regId = p.getProperties().get("c2dmRegistrationId");

		try {
			Sender sender = new Sender(this.gcmAuthKey);
			Message message = new Message.Builder().addData("title", title).addData("message", msg)
					.addData("timestamp", String.valueOf(System.currentTimeMillis())).build();

			Result result = sender.send(message, regId, 5);

			if (result.getMessageId() != null) {
				String canonicalRegId = result.getCanonicalRegistrationId();
				if (canonicalRegId != null) {
					// same device has more than on registration ID: update
					// database
					getLog().debug(
							"Google indicates that the device has more than one registration id. Update to the correct one");
					p.getProperties().put("c2dmRegistrationId", canonicalRegId);
				}
			} else {
				String error = result.getErrorCodeName();

				if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
					// application has been removed from device - unregister
					// database
					getLog().debug(
							"Google indicates that the registration id has been removed from the device. Remove from our database.");
					p.getProperties().remove("c2dmRegistrationId");
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
			getLog().warn("Could not send push through the GCM network. Error was: " + e.getMessage());
		}
	}

	@Override
	public void sendPushNotification(String subject, String message, PatientEntity user) {

		// Check which provider to use
		final boolean c2dm = user.getProperties().containsKey("c2dmRegistrationId");
		if (c2dm) {
			this.sendGcmNotification(user, subject, message);
			return;
		}

		final boolean apns = user.getProperties().containsKey("apnsRegistrationId");
		if (apns) {
			final String registrationId = user.getProperties().get("apnsRegistrationId");
			this.sendApnsNotification(registrationId, message);
			return;
		}

		getLog().error("Unable to find mobile push registration id for user {}", user.getId());
	}

	//
	void sendApnsNotification(final String registrationId, final String message) {
		getLog().info("Preparing to send APNS message: {}", apnsCount);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("token", registrationId);
		params.add("message", message);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(params,headers);

		String result = new RestTemplate().postForObject(apnsServiceUrl, request, String.class);

		if(result.equals("success")) {
			getLog().debug("Push notification " + apnsCount + " sent. Result: " + result);
			apnsCount++;
		} else {
			getLog().error("Push notification could not be sent. Server result:\n" + result);
		}
	}
}
