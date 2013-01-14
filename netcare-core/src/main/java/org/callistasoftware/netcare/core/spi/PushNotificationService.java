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

import org.callistasoftware.netcare.model.entity.PatientEntity;


/**
 * Sends push notifications through Google C2DM or Apple APN
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 */
public interface PushNotificationService {

	/**
	 * Send push notification to a specified user
	 * @param subject - Subject of the message
	 * @param message - The message
	 * @param toUserId - The id of the receiving user
	 */
	void sendPushNotification(final String subject, final String message, final PatientEntity user);
}
