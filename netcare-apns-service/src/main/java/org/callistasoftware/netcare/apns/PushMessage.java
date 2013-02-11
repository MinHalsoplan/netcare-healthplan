package org.callistasoftware.netcare.apns;

public class PushMessage {
	private String deviceToken;
	private String message;

	public PushMessage(String token, String message) {
		this.deviceToken = token;
		this.message = message;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("deviceToken: " + this.deviceToken + "\n message: " + this.message);
		return buf.toString();
	}
}
