/**
 * Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>
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

import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduledActivityRepository extends JpaRepository<ScheduledActivityEntity, Long> {
		
	/**
	 * Used to display activities and report back results for the patient. <p>
	 *  
	 * @param patient the patient.
	 * @param start start date.
	 * @param end end date.
	 * @return the list.
	 */
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp " +
			"where hp.forPatient = :patient and " +
			"hp.active = 'true' and " +
			"ad.removedFlag = 'false' and " +
			"e.scheduledTime between :start and :end " +
			"order by e.scheduledTime asc")
	List<ScheduledActivityEntity> findByPatientAndScheduledTimeBetween(
			@Param("patient") final PatientEntity patient,
			@Param("start") final Date start,
			@Param("end") final Date end);

	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp " +
			"where hp.forPatient = :patient and ad.removedFlag = 'false' " +
			"and (e.scheduledTime < :expires and e.reportedTime is null and e.status = 0) " +
			"order by e.scheduledTime asc")
	List<ScheduledActivityEntity> findDueExpiresAt(@Param("patient") final PatientEntity patient
			, @Param("expires") final Date expires);
		
	/**
	 * Get a list of all reported activities for a specific patient within a time interval.
	 * 
	 * @param hsaId
	 * @param patient
	 * @param start
	 * @param end
	 * @return
	 */
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp inner join " +
			"hp.careUnit as c where c.hsaId = :careUnit " +
			"and hp.forPatient = :patient " +
			"and hp.active = 'true' " +
			"and ad.removedFlag = 'false' " +
			"and e.reportedTime is not null and e.status != 1 and (e.reportedTime between :start and :end)")
	List<ScheduledActivityEntity> findByCareUnitPatientBetween(@Param("careUnit") final String careUnit,
			@Param("patient") PatientEntity patient, @Param("start") final Date start, @Param("end") final Date end);

	/**
	 * Used to display activity reports for a care giver within a time interval.
	 * 
	 * @param careUnit the unit.
	 * @return the list.
	 */
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp inner join " +
			"hp.careUnit as c where c.hsaId = :careUnit " +
			"and hp.active = 'true' " +
			"and ad.removedFlag = 'false' " +
			"and e.reportedTime is not null and e.status != 1 and (e.reportedTime between :start and :end)")
	List<ScheduledActivityEntity> findByCareUnitBetween(@Param("careUnit") final String careUnit
			, @Param("start") final Date start
			, @Param("end") final Date end);
	
	/**
	 * Used to display actvivity reports for a care giver.
	 * 
	 * @param careUnit the unit.
	 * @return the list.
	 */
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp inner join " +
			"hp.careUnit as c where c.hsaId = :careUnit " +
			"and hp.active = 'true' " +
			"and ad.removedFlag = 'false' " +
			"and e.reportedTime is not null and e.status != 1")
	List<ScheduledActivityEntity> findByCareUnit(@Param("careUnit") final String careUnit);
	
	/**
	 * Returns a list of unreported activities before a certain timestamp. <p>
	 * 
	 * Used for batch handling of notifications and alarms.
	 * 
	 * @param scheduledTime the timestamp.
	 * @return the list
	 */
	List<ScheduledActivityEntity> findByScheduledTimeLessThanAndReportedTimeIsNull(final Date scheduledTime);
	
	
	/**
	 * Find reported activities for a certain activity definition within a specified interval <p>
	 * 
	 * Used to build result graphs.
	 * 
	 * @param activityDefintionId the id.
	 * @param start the start time.
	 * @param end the end time.
	 * 
	 * @return the list.
	 */
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp where " +
			"hp.id = :healthPlanId and e.reportedTime is not null and e.scheduledTime between :start and :end")
	List<ScheduledActivityEntity> findReportedActivitiesForHealthPlan(@Param("healthPlanId") final Long healthPlanId
			, @Param("start") final Date start
			, @Param("end") final Date end, final Sort sort);
	
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp where " +
			"ad.removedFlag = 'false' and " +
			"hp.id = :healthPlanId")
	List<ScheduledActivityEntity> findScheduledActivitiesForHealthPlan(@Param("healthPlanId") final Long healthPlanId);
	
	@Query("select e from ScheduledActivityEntity as e inner join " +
			"e.activityDefinition as ad inner join " +
			"ad.healthPlan as hp where " +
			"ad.removedFlag = 'false' and hp.active = 'true' " +
			"and ad.id = :definitionId and e.extra = 'false' order by e.scheduledTime desc limit 1")
	List<ScheduledActivityEntity> findLatestScheduledActivityForActivity(@Param("definitionId") final Long definitionId);

}
