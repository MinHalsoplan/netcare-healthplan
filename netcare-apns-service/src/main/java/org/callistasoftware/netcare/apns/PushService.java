package org.callistasoftware.netcare.apns;

/**
 * This service handles pushing notifications to Apple APNS.
 */
public interface PushService {

	/**
	 * Sends a push alert to Apple apns service.
	 * 
	 * @param pushMessage
	 *            The token and the message.
	 */
	void sendPushAlert(PushMessage pushMessage);

}
