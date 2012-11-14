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
package org.callistasoftware.netcare.api.rest;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.PatientEvent;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityReportImpl;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/patient")

public class PatientApi extends ApiSupport {
	private static final Logger log = LoggerFactory.getLogger(PatientApi.class);
	
	@Autowired
	private HealthPlanService planService;

	@RequestMapping(value="/plans", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<HealthPlan[]> listOrdinations(final Authentication auth) {
		logAccess("lista", "planer");
		Long patientId = ((PatientBaseView) auth.getPrincipal()).getId();
		log.info("User {} list health plans", patientId);
		final ServiceResult<HealthPlan[]> plans = planService.loadHealthPlansForPatient(patientId);
		log.debug("Found # plans {} for patient {}", plans.getData().length, patientId);
		return plans;
	}
	
	@RequestMapping(value="/activities", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ActivityDefinition[]> listActivities(final Authentication auth) {
		logAccess("lista", "aktiviteter");
		log.info("User {} list activities", auth);
		ServiceResult<ActivityDefinition[]> sr = planService.getPlannedActivitiesForPatient((PatientBaseView)auth.getPrincipal());
		log.debug("Found # activities {} for patient {}", sr.getData().length, auth);
		return sr;
	}
	
    @RequestMapping(value="/schema/{id}/accept", method=RequestMethod.POST, produces="application/json", consumes="application/json")
	@ResponseBody
	public ServiceResult<ScheduledActivity> report(@PathVariable(value="id") final Long id,
			@RequestBody final ActivityReportImpl dto, final Authentication auth) {
		logAccess("rapportera", "händelse");
		return planService.reportReady(id, dto);
	}
	
	@RequestMapping(value="/event", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<PatientEvent> event(final Authentication auth) {
		logAccess("lista", "händelser");
		return planService.getActualEventsForPatient((PatientBaseView)auth.getPrincipal());
	}
	
	@RequestMapping(value="/schema/min-halso-plan", method=RequestMethod.GET, produces="text/calendar")
	@ResponseBody
	public String getCalendar(final Authentication auth) {
		logAccess("exportera", "kalender");
		return planService.getICalendarEvents((PatientBaseView)auth.getPrincipal());
	}
	
	@RequestMapping(value="/result/{id}/resultat.csv",method=RequestMethod.GET, produces="application/vnd.ms-excel")
	@ResponseBody
	public String getPlanReports(@PathVariable(value="id") final Long activityDefId) {
		logAccess("exportera", "resultat");
		return planService.getPlanReports(getUser(), activityDefId);
	}
}