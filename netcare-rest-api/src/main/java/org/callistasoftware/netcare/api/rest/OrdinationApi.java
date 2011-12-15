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

import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.Ordination;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.OrdinationImpl;
import org.callistasoftware.netcare.core.spi.OrdinationService;
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
@RequestMapping(value="/ordination")
public class OrdinationApi extends ApiSupport {

	private static final Logger log = LoggerFactory.getLogger(OrdinationApi.class);
	
	@Autowired 
	private OrdinationService service;
	
	@RequestMapping(value="/{patient}/create", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public ServiceResult<Ordination> createOrdination(@RequestBody final OrdinationImpl dto, @PathVariable(value="patient") final Long patient, final Authentication auth) {
		log.info("Creating a new ordination. Creator: {}, Ordination: {}, Patient: {}", new Object[] {auth.getPrincipal(), patient});
		
		return this.service.createNewOrdination(dto, (CareGiverBaseView) auth.getPrincipal(), patient);
	}
	
	@RequestMapping(value="/{patient}/list", method=RequestMethod.GET)
	@ResponseBody
	public ServiceResult<Ordination[]> listOrdinations(@PathVariable(value="patient") final Long patient, final Authentication auth) {
		log.info("Care giver {} is listing ordinations for patient {}", ((CareGiverBaseView) auth.getPrincipal()).getHsaId(), patient);
		final ServiceResult<Ordination[]> ordinations = this.service.loadOrdinationsForPatient(patient);
		
		log.debug("Found {} for patient {}", ordinations.getData().length, patient);
		return ordinations;
	}
	
	@RequestMapping(value="/{patient}/{ordination}/delete", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<Ordination> deleteOrdination(@PathVariable(value="patient") final Long patient, @PathVariable(value="ordination") final Long ordination) {
		log.info("Deleting ordination {} for patient {}", ordination, patient);
		return this.service.deleteOrdination(ordination);
	}
	
	@RequestMapping(value="/{ordination}/activitydefinition/create", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<Ordination> createActivityDefintion(@PathVariable(value="ordination") final Long ordination) {
		log.info("User {} is adding a new activity defintion for ordination {}", new Object[] {this.getUser(), ordination});
		
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
