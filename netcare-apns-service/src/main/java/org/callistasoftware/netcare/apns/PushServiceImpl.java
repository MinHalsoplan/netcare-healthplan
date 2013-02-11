package org.callistasoftware.netcare.apns;

import javapns.Push;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import javapns.notification.ResponsePacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PushServiceImpl implements PushService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private Logger messageLogger = LoggerFactory.getLogger("MESSAGE");

	@Value("${apns.cert-file}")
	private String apnsCertFile;
	@Value("${apns.cert-password}")
	private String apnsCertPassword;
	@Value("${apns.production}")
	private boolean apnsProduction;

	@Override
	public void sendPushAlert(PushMessage pushMessage) {

		validate(pushMessage);

		try {

			PushedNotifications pushedNotifications = Push.alert(pushMessage.getMessage(), apnsCertFile,
					apnsCertPassword, apnsProduction, pushMessage.getDeviceToken());

			for (PushedNotification notification : pushedNotifications) {
				if (notification.isSuccessful()) {
					/* Apple accepted the notification and should deliver it */
					log.debug("Push notification sent successfully to: " + notification.getDevice().getToken());
					messageLogger.info(pushMessage.getDeviceToken() + " - " + pushMessage.getMessage());
					/* TODO Query the Feedback Service regularly */
				} else {
					String invalidToken = notification.getDevice().getToken();
					log.warn("Push notification failed. Invalid token: " + invalidToken, notification.getException());

					/*
					 * If the problem was an error-response packet returned by
					 * Apple, get it
					 */
					ResponsePacket theErrorResponse = notification.getResponse();
					if (theErrorResponse != null) {
						log.warn("Apple error", theErrorResponse.getMessage());
					}
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	protected void validate(PushMessage pushMessage) {
		if (!(pushMessage != null && StringUtils.hasText(pushMessage.getDeviceToken()) && StringUtils
				.hasText(pushMessage.getMessage()))) {
			throw new RuntimeException("PushMessage missing data: " + pushMessage);
		}
	}
}
