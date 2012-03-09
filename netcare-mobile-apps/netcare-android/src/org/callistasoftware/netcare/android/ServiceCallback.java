package org.callistasoftware.netcare.android;

public interface ServiceCallback<T> {
	
	void onSuccess(final T response);
	
	void onFailure(final String reason);
	
}
