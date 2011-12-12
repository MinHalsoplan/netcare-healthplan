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

import org.callistasoftware.netcare.core.api.Ordination;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.spi.OrdinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/ordination")
public class OrdinationApi {

	private static final Logger log = LoggerFactory.getLogger(OrdinationApi.class);
	
	@Autowired 
	private OrdinationService service;
	
	@RequestMapping(value="/{patient}/create", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public ServiceResult<Ordination> createOrdination(@PathVariable(value="patient") final Long patient, final Model m, final Authentication auth) {
		log.info("Creating a new ordination. Creator: {}, Ordination: {}, Patient: {}", new Object[] {auth.getName(), patient});
		
		return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
	}
	
	@RequestMapping(value="/{patient}/list")
	public ServiceResult<Ordination[]> listOrdinations() {
		return null;
	}
}
