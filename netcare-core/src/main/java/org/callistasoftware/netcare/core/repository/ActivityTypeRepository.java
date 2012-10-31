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

import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityTypeRepository extends JpaRepository<ActivityTypeEntity, Long>, JpaSpecificationExecutor<ActivityTypeEntity> {
	/**
	 * Find activity types created on the given care unit
	 * @param hsaId
	 * @return
	 * @deprecated Use {@link ActivityCategoryRepository}{@link #findByCareUnit(CareUnitEntity)} instead
	 */
	@Deprecated
	@Query("select e from ActivityTypeEntity as e where e.careUnit.hsaId = :hsaId order by e.name asc")
	List<ActivityTypeEntity> findByCareUnit(@Param("hsaId") final String hsaId);
	
	@Query("select e from ActivityTypeEntity as e where " +
			"(e.accessLevel = 'CAREUNIT' and e.careUnit = :careUnit) " +
			"or (e.accessLevel = 'NATIONAL') " +
			"or (e.accessLevel = 'COUNTY_COUNCIL' and e.careUnit.countyCouncil = :countyCouncil)")
	List<ActivityTypeEntity> findByCareUnit(@Param("careUnit") final CareUnitEntity careUnit
			, @Param("countyCouncil") final CountyCouncilEntity countyCouncil);
	
	@Query("select e from ActivityTypeEntity as e where " +
			"(e.accessLevel = 'CAREUNIT' and e.careUnit.hsaId = :hsaId) " +
			"or (e.accessLevel = 'NATION') " +
			"or (e.accessLevel = 'COUNTY_COUNCIL' and e.careUnit.countyCouncil.id = :countyCouncilId)")
	List<ActivityTypeEntity> findAllAccessible(
			@Param("hsaId") final String careUnitHsa, 
			@Param("countyCouncilId") final Long countyCouncilId);
}
