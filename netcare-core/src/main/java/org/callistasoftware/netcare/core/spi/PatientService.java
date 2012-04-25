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

import org.callistasoftware.netcare.core.api.CareUnit;
import org.callistasoftware.netcare.core.api.Patient;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.PatientProfile;
import org.callistasoftware.netcare.core.api.ServiceResult;

public interface PatientService {

	/**
	 * Find patients from a free text search.
	 * @param freeTextSearch - Can be either name, email or civic registration number
	 * @return A list of patients or an empty list if no patients could be found
	 */
	ServiceResult<PatientBaseView[]> findPatients(final String freeTextSearch);
	
	/**
	 * Load all patients
	 * @return
	 */
	ServiceResult<Patient[]> loadPatientsOnCareUnit(final CareUnit careUnit);
	
	/**
	 * Load a patient with a specific id
	 * @param id
	 * @return
	 */
	ServiceResult<Patient> loadPatient(final Long id);
	
	/**
	 * Create a new patient
	 * @param patient the patient.
	 * @return the created patient, or the already existing patient.
	 */
	ServiceResult<Patient> createPatient(final Patient patient);
	
	/**
	 * Delete a patient with the given id
	 * @param id
	 * @return
	 */
	ServiceResult<Patient> deletePatient(final Long id);
	
	/**
	 * Update a patient with new profile data
	 * @param id
	 * @param patient
	 * @return
	 */
	ServiceResult<Patient> updatePatient(final Long id, final PatientProfile patient);
}
