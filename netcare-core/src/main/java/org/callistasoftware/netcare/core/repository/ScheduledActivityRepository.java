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

import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduledActivityRepository extends JpaRepository<ScheduledActivityEntity, Long> {
	
	List<ScheduledActivityEntity> findByActivityDefinition(ActivityDefinitionEntity activityDefinitionEntity);
	
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp " +
			"where hp.forPatient = :patient and " +
			"e.scheduledTime between :start and :end and e.status != 1")
	List<ScheduledActivityEntity> findByPatientAndScheduledTimeBetween(
			@Param("patient") final PatientEntity patient,
			@Param("start") final Date start,
			@Param("end") final Date end);
			
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp inner join " +
			"hp.careUnit as c where c.hsaId = :careUnit " +
			"and e.reportedTime != null and e.status != 1")
	List<ScheduledActivityEntity> findByCareUnit(@Param("careUnit") final String careUnit);
	
	/**
	 * Returns a list of unreported activities before a certain timestamp.
	 * @param scheduledTime the timestamp.
	 * @return the list
	 */
	List<ScheduledActivityEntity> findByScheduledTimeLessThanAndReportedTimeIsNull(final Date scheduledTime);
	
	
	/**
	 * Find reported activities for a certain activity defintion within a specified interval
	 * @param activityDefintionId
	 * @param start
	 * @param end
	 * @return
	 */
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp where " +
			"hp.id = :healthPlanId and e.reportedTime != null and e.scheduledTime between :start and :end")
	List<ScheduledActivityEntity> findReportedActivitiesForHealthPlan(@Param("healthPlanId") final Long healthPlanId
			, @Param("start") final Date start
			, @Param("end") final Date end, final Sort sort);
	
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp where " +
			"hp.id = :healthPlanId")
	List<ScheduledActivityEntity> findScheduledActivitiesForHealthPlan(@Param("healthPlanId") final Long healthPlanId);
}
