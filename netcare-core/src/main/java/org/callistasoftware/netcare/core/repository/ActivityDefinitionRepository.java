/**
 * Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>
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

import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityDefinitionRepository extends JpaRepository<ActivityDefinitionEntity, Long> {
	
	@Query("select e from ActivityDefinitionEntity as e inner join " +
			"e.healthPlan as hp " +
			"where hp.forPatient = :patient and " +
			"hp.endDate > :now " +
			"order by e.startDate desc")
	List<ActivityDefinitionEntity> findByPatientAndNow(
			@Param("patient") final PatientEntity patient,
			@Param("now") final Date now);

	@Query("select itemdef.activityDefinition from ActivityItemDefinitionEntity as itemdef " + 
			"where itemdef.id=:itemDefId")
	ActivityDefinitionEntity findByActivityItemDefinitionId(@Param("itemDefId") final Long itemDefId);
}
