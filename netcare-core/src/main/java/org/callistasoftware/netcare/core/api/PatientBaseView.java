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
package org.callistasoftware.netcare.core.api;

import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Defines a base view of a patient
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@JsonDeserialize(as=PatientBaseViewImpl.class)
public interface PatientBaseView extends UserBaseView {

	/**
	 * Get the civic registration number for the patient
	 * @return
	 */
	String getCivicRegistrationNumber();
	
	/**
	 * Whether the user is allowed to use the mobile
	 * channel
	 * @return
	 */
	boolean isMobile();
}
