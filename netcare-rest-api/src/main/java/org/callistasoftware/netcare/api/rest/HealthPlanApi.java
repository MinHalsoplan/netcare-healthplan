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
package org.callistasoftware.netcare.api.rest;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityDefintionImpl;
import org.callistasoftware.netcare.core.api.impl.HealthPlanImpl;
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
@RequestMapping(value="/healthplan")
public class HealthPlanApi extends ApiSupport {

	private static final Logger log = LoggerFactory.getLogger(HealthPlanApi.class);
	
	@Autowired 
	private HealthPlanService service;
	
	@RequestMapping(value="/{patient}/create", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public ServiceResult<HealthPlan> createHealthPlan(@RequestBody final HealthPlanImpl dto, @PathVariable(value="patient") final Long patient, final Authentication auth) {
		log.info("Creating a new ordination. Creator: {}, Ordination: {}, Patient: {}", new Object[] {auth.getPrincipal(), patient});
		
		return this.service.createNewHealthPlan(dto, (CareGiverBaseView) auth.getPrincipal(), patient);
	}
	
	@RequestMapping(value="/{patient}/list", method=RequestMethod.GET)
	@ResponseBody
	public ServiceResult<HealthPlan[]> listHealthPlans(@PathVariable(value="patient") final Long patient, final Authentication auth) {
		log.info("Care giver {} is listing ordinations for patient {}", ((CareGiverBaseView) auth.getPrincipal()).getHsaId(), patient);
		final ServiceResult<HealthPlan[]> ordinations = this.service.loadHealthPlansForPatient(patient);
		
		log.debug("Found {} for patient {}", ordinations.getData().length, patient);
		return ordinations;
	}
	
	@RequestMapping(value="/{patient}/{ordination}/delete", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<HealthPlan> deleteHealthPlan(@PathVariable(value="patient") final Long patient, @PathVariable(value="ordination") final Long ordination) {
		log.info("Deleting ordination {} for patient {}", ordination, patient);
		return this.service.deleteHealthPlan(ordination);
	}
	
	@RequestMapping(value="/{healthPlanId}/activity/new", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public ServiceResult<HealthPlan> createActivityDefintion(@RequestBody final ActivityDefintionImpl activity, @PathVariable(value="healthPlanId") final Long healthPlanId) {
		log.info("User {} is adding a new activity defintion for health plan {}", new Object[] {this.getUser(), healthPlanId});
		
		return this.service.addActvitiyToHealthPlan(healthPlanId, activity);
	}
	
	@RequestMapping(value="/{healthPlanId}/activity/list", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ActivityDefinition[]> loadActivityDefinitions(@PathVariable(value="healthPlanId") final Long healthPlan) {
		log.info("User {} is listing activity defintions for health plan {}", new Object[] {this.getUser(), healthPlan});
		return this.service.loadActivitiesForHealthPlan(healthPlan);
	}
}