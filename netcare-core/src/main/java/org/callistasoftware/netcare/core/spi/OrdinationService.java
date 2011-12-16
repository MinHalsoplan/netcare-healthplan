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
package org.callistasoftware.netcare.core.spi;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;

/**
 * Defines the ordination service that will provide
 * all required functionality for dealing with
 * ordinations
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public interface OrdinationService {

	/**
	 * Load ordinations for a patient
	 * @param patient
	 * @return
	 */
	ServiceResult<HealthPlan[]> loadOrdinationsForPatient(final Long patient);
	
	/**
	 * Creates a new ordination.
	 * 
	 * @param ordination
	 * @param creator
	 * @param patientId
	 * @return
	 */
	ServiceResult<HealthPlan> createNewOrdination(final HealthPlan ordination, final CareGiverBaseView creator, final Long patientId);
	
	/**
	 * Delete the ordination with the specified id
	 * @param ordinationId
	 * @return
	 */
	ServiceResult<HealthPlan> deleteOrdination(final Long ordinationId);
	
	/**
	 * Load a specific ordination
	 * @param ordinationId
	 * @param patient
	 * @return
	 */
	ServiceResult<HealthPlan> loadOrdination(final Long ordinationId, final PatientBaseView patient);
	
	/**
	 * Adds an activity defintion to an existing ordination specified by its id
	 * @param ordinationId
	 * @return
	 */
	ServiceResult<HealthPlan> addActivityDefintionToOrdination(final Long ordinationId, final ActivityDefinition dto);
}
