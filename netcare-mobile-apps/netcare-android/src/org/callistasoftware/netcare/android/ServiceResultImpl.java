package org.callistasoftware.netcare.android;

public class ServiceResultImpl<T> implements ServiceResult<T> {

	private final T data;
	private final boolean success;
	private final String errorMessage;
	
	public ServiceResultImpl(final T data, final boolean success, final String errorMessage) {
		this.data = data;
		this.success = success;
		this.errorMessage = errorMessage;
	}
	
	@Override
	public boolean isSuccess() {
		return this.success;
	}

	@Override
	public T getData() {
		return this.data;
	}

	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

}
