package org.callistasoftware.netcare.android.serviceclient;

import org.callistasoftware.android.serviceclient.ServiceResult;

public interface ServiceClient {

	ServiceResult<Boolean> login();
	
	ServiceResult<Boolean> registerForPush(final String registrationId);
}
