package org.callistasoftware.netcare.android;

public interface ServiceResult<T> {

	boolean isSuccess();
	
	T getData();
	
	String getErrorMessage();
}
