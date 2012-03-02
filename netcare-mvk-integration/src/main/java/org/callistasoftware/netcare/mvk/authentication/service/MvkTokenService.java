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
package org.callistasoftware.netcare.mvk.authentication.service;


public interface MvkTokenService {
	
	/**
	 * Create an auth token for a patient with the given civic registration number
	 * @param userId
	 * @return
	 */
	String createAuthenticationTokenForPatient(final String userId);
	
	/**
	 * Create an auth token for a care giver with the given hsa id
	 * @param userId
	 * @param careUnitHsaId
	 * @param careUnitName
	 * @return
	 */
	String createAuthenticationTokenForCareGiver(final String userId, final String careUnitHsaId, final String careUnitName);
}
