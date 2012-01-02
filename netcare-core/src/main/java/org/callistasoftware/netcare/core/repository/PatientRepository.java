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
package org.callistasoftware.netcare.core.repository;

import java.util.List;

import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
	/**
	 * Find patients by name, email or civic registration number
	 * @param name
	 * @param email
	 * @param civicRegistrationNumber
	 * @return
	 */
	List<PatientEntity> findByNameLikeOrEmailLikeOrCivicRegistrationNumberLike(final String name, final String email, final String civicRegistrationNumber);
	
	/**
	 * Find a patient by its civic registration number
	 * @param civicRegistrationNumber
	 * @return
	 */
	PatientEntity findByCivicRegistrationNumber(final String civicRegistrationNumber);
	
	@Query("select e from PatientEntity as e inner join e.healthPlans as hp inner join hp.careUnit as cu where cu.hsaId = :hsaId")
	List<PatientEntity> findByCareUnit(@Param("hsaId") final String hsaId);
}
