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

import org.callistasoftware.netcare.core.api.ServiceResult;

import java.util.Map;


/**
 * Defines the user details service
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {

	/**
	 * Save user data for the current logged in user
	 * @param firstName
	 * @param surName
	 */
	ServiceResult<Boolean> saveUserData(final String firstName, final String surName);
	
	/**
	 * Registers the current logged in user for c2dm push
	 * notifications
	 * @param c2dmRegistrationId - The c2dm registration id
	 */
	void registerForGcm(final String c2dmRegistrationId);
	
	void unregisterGcm();

	/**
	 * Registers the current logged in user for apple psh notifications.
	 * 
	 * @param apnsRegistrationId the apns registration id.
	 */
	void registerForApnsPush(String apnsRegistrationId);

    /**
     *
     */
    void unregisterApns();

    /**
     *
     * @param props
     */
    void addUserProperties(Map<String, String> props);

}
