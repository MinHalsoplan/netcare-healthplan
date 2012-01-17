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

import org.callistasoftware.netcare.core.api.ActivityComment;
import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.ActivityReport;
import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.CareUnit;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.PatientEvent;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.statistics.HealthPlanStatistics;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;

/**
 * Defines the ordination service that will provide
 * all required functionality for dealing with
 * ordinations
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public interface HealthPlanService {

	/**
	 * Load ordinations for a patient
	 * @param patient
	 * @return
	 */
	ServiceResult<HealthPlan[]> loadHealthPlansForPatient(final Long patient);
	
	/**
	 * Creates a new ordination.
	 * 
	 * @param ordination
	 * @param creator
	 * @param patientId
	 * @return
	 */
	ServiceResult<HealthPlan> createNewHealthPlan(final HealthPlan ordination, final CareGiverBaseView creator, final Long patientId);
	
	/**
	 * Delete the ordination with the specified id
	 * @param ordinationId
	 * @return
	 */
	ServiceResult<HealthPlan> deleteHealthPlan(final Long ordinationId);
	
	/**
	 * Load a specific ordination
	 * @param ordinationId
	 * @param patient
	 * @return
	 */
	ServiceResult<HealthPlan> loadHealthPlan(final Long healthPlanId);
	
	/**
	 * Adds an activity definition to an existing ordination specified by its id
	 * @param healthPlanId
	 * @param dto the activity definition.
	 * @param user the user.
	 * 
	 * @return the result.
	 */
	ServiceResult<HealthPlan> addActvitiyToHealthPlan(final Long healthPlanId, final ActivityDefinition dto, final UserBaseView user);
	
	/**
	 * Removes an activity definition. All scheduled activities that not yet has been reported will
	 * be deleted from the system.
	 * @param activityDefinitionId
	 * @return
	 */
	ServiceResult<ActivityDefinition> deleteActivity(final Long activityDefinitionId);
	
	/**
	 * Load all activities for a specific health plan
	 * @param healthPlanId
	 * @return
	 */
	ServiceResult<ActivityDefinition[]> loadActivitiesForHealthPlan(final Long healthPlanId);
	
	/**
	 * Returns scheduled activities for a patient.
	 * 
	 * @param patient the patient.
	 * @return the result.
	 */
	ServiceResult<ScheduledActivity[]> getActivitiesForPatient(final PatientBaseView patient);
	
	/**
	 * Comment a performed activity
	 * @param activityId
	 * @param comment
	 * @return
	 */
	ServiceResult<ScheduledActivity> commentOnPerformedActivity(final Long activityId, final String comment);
	
	/**
	 * Reply to a comment
	 * @param comment
	 * @return
	 */
	ServiceResult<ActivityComment> replyToComment(final Long comment, final String reply);
	
	/**
	 * Get comments on reported activities for the currently logged in patient
	 * @return
	 */
	ServiceResult<ActivityComment[]> loadCommentsForPatient();
	
	/**
	 * Reports on an activity and returns the update.
	 * 
	 * @param scheduledActivityId the id.
	 * @param value the value.
	 * @return an updated {@link ScheduledActivity}
	 */
	ServiceResult<ScheduledActivity> reportReady(final Long scheduledActivityId, final ActivityReport report);
	
	/**
	 * Load the latest reported activities for all patients that
	 * has health plans belonging to the caller's care unit
	 * @return
	 */
	ServiceResult<ScheduledActivity[]> loadLatestReportedForAllPatients(final CareUnit careUnit);
	
	/**
	 * Returns actual activity definitions.
	 * 
	 * @return the result with actual activity definitions.
	 */
	ServiceResult<ActivityDefinition[]> getPlannedActivitiesForPatient(final PatientBaseView patient);
	
	/**
	 * Returns events for a patient.
	 * 
	 * @return the events for the patient.
	 */
	ServiceResult<PatientEvent> getActualEventsForPatient(final PatientBaseView patient);

	/**
	 * Get reported activities for a certain activity definition within a specific time interval
	 * @param activityDefintionId
	 * @param start
	 * @param end
	 * @return
	 */
	ServiceResult<ScheduledActivity[]> getScheduledActivitiesForHealthPlan(final Long healthPlanId);
	
	/**
	 * Get statistical information from a health plan
	 * @param healthPlanId
	 * @return
	 */
	ServiceResult<HealthPlanStatistics> getStatisticsForHealthPlan(final Long healthPlanId);
	
	/**
	 * Schedules activities.
	 * 
	 * @param activityDefinition the activity defintion.
	 */
	void scheduleActivities(ActivityDefinitionEntity activityDefinition) ;
	
	/**
	 * Returns the iCalendar definition. <p>
	 * 
	 * @see <a href="linkplain http://tools.ietf.org/html/rfc5545>RFC-5545"</a>
	 * 
	 * @param patient the patient.
	 * @return the iCalendar object as a string.
	 */
	String getICalendarEvents(PatientBaseView patient);
}
