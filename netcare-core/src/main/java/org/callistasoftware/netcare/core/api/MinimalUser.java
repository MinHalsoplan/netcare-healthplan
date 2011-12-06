/**
 * Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>
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
package org.callistasoftware.netcare.core.api;

public interface MinimalUser {

	/**
	 * The id of the user
	 * @return
	 */
	Long getId();
	
	/**
	 * The user's unique username. usually a crn or hsaid
	 * @return
	 */
	String getUsername();
	
	/**
	 * The full name of the user
	 * @return
	 */
	String getName();
	
	/**
	 * Return the email address of the user
	 * @return
	 */
	String getEmail();
	
	/**
	 * Checks whether this user is signed up
	 * for mobile usage
	 * @return
	 */
	boolean isMobile();
	
	/**
	 * Returns the pin code for this user. If the user
	 * is a mobile user this code is used for login
	 * to the system
	 * @return
	 */
	String getPinCode();
	
	/**
	 * True if the user is a care giver, false otherwise
	 * @return
	 */
	boolean isCareGiver();
	
	/**
	 * True if the user is a patient, false otherwise
	 * @return
	 */
	boolean isPatient();
}
