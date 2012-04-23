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
package org.callistasoftware.netcare.core.spi;

import org.callistasoftware.netcare.core.support.TestSupport;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.EnhancedApnsNotification;
import com.notnoop.apns.PayloadBuilder;

public class OtherTests extends TestSupport {
	//@Test
	public void sendApnsNotification() {
		 String registrationId = "ab4fa0c794688dca7c149cbac1001ed2f451d02e4c55cc25be45e7693ad3aae3";
		 String message = "Hello";
		 ApnsServiceBuilder sb = APNS.newService();
		 
		 sb.withCert("/tmp/apns-dev.p12", "Enterprisy").withSandboxDestination();
		 
		 PayloadBuilder pb = APNS.newPayload();
		 pb.alertBody(message);
		 pb.sound("default");
		 pb.badge(1);
		 		 
		 sb.build().push(new EnhancedApnsNotification(1, 900, registrationId, pb.build()));
	}

}
