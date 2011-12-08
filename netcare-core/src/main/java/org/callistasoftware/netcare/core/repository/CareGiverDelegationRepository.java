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
package org.callistasoftware.netcare.core.repository;

import java.util.List;

import org.callistasoftware.netcare.core.entity.CareGiverDelegationEntity;
import org.callistasoftware.netcare.core.entity.CareGiverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareGiverDelegationRepository extends JpaRepository<CareGiverDelegationEntity, Long> {
	
	/**
	 * Returns a delegatee's all delegations.
	 * 
	 * @param careGiverDelegatee the delegatee, i.e. a nurse working on behalf of a doctor.
	 * 
	 * @return all matching delegations.
	 */
	List<CareGiverDelegationEntity> findByCareGiverDelegatee(CareGiverEntity careGiverDelegatee);
}
