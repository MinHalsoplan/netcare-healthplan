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

import javax.servlet.http.HttpSession;

import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.spi.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/user")
public class UserApi {
	
	private static final Logger log = LoggerFactory.getLogger(UserApi.class);
	
	@Autowired
	private PatientService patientService;

	@RequestMapping(value="/create", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String createUser() {
		return "hello world";
	}
	
	@RequestMapping(value = "/find", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<PatientBaseView[]> findUsers(@RequestParam(value="search", required=true) final String search) {
		log.info("Finding patients... Search string is: {}", search);
		return this.patientService.findPatients(search);
	}
	
	@RequestMapping(value="/{patient}/select", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public ServiceResult<PatientBaseView> selectPatient(@PathVariable(value="patient") final Long patientId, final HttpSession session) {
		log.info("Selecting patient {}", patientId);
		
		final ServiceResult<PatientBaseView> result = this.patientService.loadPatient(patientId);
		final PatientBaseView currentPatient = (PatientBaseView) session.getAttribute("currentPatient");
		
		if (result.isSuccess()) {
			if (currentPatient == null) {
				if (result.isSuccess()) {
					log.debug("Setting new current patient in session scope. New patient is: {}", result.getData().getName());
					session.setAttribute("currentPatient", result.getData());
				}
			} else {
				log.debug("Replacing patient {} with {} as current patient in session scope", currentPatient.getName(), result.getData().getName());
				session.setAttribute("currentPatient", result.getData());
			}
		}
				
		return result;
	}
}
