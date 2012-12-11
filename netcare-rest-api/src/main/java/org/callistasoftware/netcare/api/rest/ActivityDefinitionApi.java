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
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityDefinitionImpl;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/activityPlans", produces="application/json")
public class ActivityDefinitionApi extends ApiSupport {

	@Autowired 
	private HealthPlanService service;
	
	@RequestMapping(value="", method=RequestMethod.GET)
	@ResponseBody
	public ServiceResult<ActivityDefinition[]> listActivities(@RequestParam(value="patient", required=false) final Long patientId) {
		logAccess("lista", "aktiviteter");
		ServiceResult<ActivityDefinition[]> sr = service.getPlannedActivitiesForPatient(patientId);
		return sr;
	}
	
	@RequestMapping(value="", method=RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ServiceResult<ActivityDefinition> createActivityDefintion(@RequestBody final ActivityDefinitionImpl activity) {
		logAccess("create", "activity definition");
		return this.service.addActvitiyToHealthPlan(activity);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public ServiceResult<ActivityDefinition> updateDefinition(@PathVariable("id") final Long id,
			@RequestBody final ActivityDefinition data) {
		logAccess("update", "activity definition");
		return this.service.updateActivity(data);
	}
	
	@RequestMapping(value="/{id}/enableReminder", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<ActivityDefinition> enableReminder(@PathVariable("id") final Long id) {
		logAccess("enable", "reminder");
		return service.updateReminder(id, true);
	}
	
	@RequestMapping(value="/{id}/disableReminder", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<ActivityDefinition> disableReminder(@PathVariable("id") final Long id) {
		logAccess("disable", "reminder");
		return service.updateReminder(id, false);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ServiceResult<ActivityDefinition> loadActivityDefinition(@PathVariable("id") final Long id) {
		logAccess("load", "activity definition");
		return this.service.loadDefinition(id);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public ServiceResult<ActivityDefinition> deleteDefinition(@PathVariable(value="id") final Long definitionId) {
		this.logAccess("delete", "activity definition");
		return this.service.deleteActivity(definitionId);
	}
}
