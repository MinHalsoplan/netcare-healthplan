package org.callistasoftware.netcare.android.push;

import org.callistasoftware.android.c2dm.C2DMReceiver;
import org.callistasoftware.android.c2dm.PushService;

/**
 * Part of c2dm framework configuration.
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class PushReceiver extends C2DMReceiver {

	@Override
	public Class<? extends PushService> getPushServiceClass() {
		return org.callistasoftware.netcare.android.push.PushService.class;
	}
}
