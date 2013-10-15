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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.Patient;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.PatientProfile;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.spi.PatientService;
import org.callistasoftware.netcare.core.spi.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/user")
public class UserApi extends ApiSupport {

	private static final Logger log = LoggerFactory.getLogger(UserApi.class);

	@Autowired
	private PatientService patientService;

	@Autowired
	private UserDetailsService userService;

	@RequestMapping(value = "/find", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ServiceResult<PatientBaseView[]> findUsers(
			@RequestParam(value = "search", required = true) final String search) {
		log.info("Finding patients... Search string is: {}", search);
		return this.patientService.findPatients(search);
	}

	@RequestMapping(value = "/load", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ServiceResult<Patient[]> loadPatients(HttpServletRequest request) throws IllegalAccessException {

		final UserBaseView user = this.getUser();
		if (user.isCareActor()) {
			final CareActorBaseView ca = (CareActorBaseView) user;
			ServiceResult<Patient[]> result = this.patientService.loadPatientsOnCareUnit(ca.getCareUnit());
			for (Patient patient : result.getData()) {
				this.logAccess("load", "patients", request, patient, " ");
			}
			return result;
		} else {
			throw new IllegalAccessException();
		}
	}

	@RequestMapping(value = "/{patient}/load", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ServiceResult<Patient> loadPatient(@PathVariable(value = "patient") final Long patient,
			HttpServletRequest request) {
		ServiceResult<Patient> result = this.patientService.loadPatient(patient);
		this.logAccess("load", "patient", request, result.getData(), " ");
		return result;
	}

	@RequestMapping(value = "/{patient}/update", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ServiceResult<Patient> updatePatient(@PathVariable(value = "patient") final Long patient,
			@RequestBody final PatientProfile patientData, HttpServletRequest request) {
		ServiceResult<Patient> result = this.patientService.updatePatient(patient, patientData);
		this.logAccess("update", "patient", request, result.getData(), " ");
		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ServiceResult<Patient> createNewPatient(@RequestBody final Patient patient, HttpServletRequest request)
			throws IllegalAccessException {
		this.logAccess("create", "patient", request, patient, " ");

		final UserBaseView user = this.getUser();
		if (user.isCareActor()) {
			return this.patientService.createPatient(patient);
		} else {
			throw new IllegalAccessException();
		}
	}

	@RequestMapping(value = "/saveUserData", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ServiceResult<Boolean> saveUserData(@RequestParam(value = "firstName") final String firstName,
			@RequestParam(value = "surName") final String surName) {
		this.logAccessWithoutPdl("save", "user_data");
		return this.userService.saveUserData(firstName, surName);
	}

	@RequestMapping(value = "/{patient}/delete", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ServiceResult<Patient> deletePatient(@PathVariable(value = "patient") final Long patientId,
			HttpServletRequest request) {
		Patient patient = patientService.loadPatient(patientId).getData();
		this.logAccess("delete", "patient", request, patient, " ");
		return this.patientService.deletePatient(patientId);
	}

	@RequestMapping(value = "/{patient}/select", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ServiceResult<? extends PatientBaseView> selectPatient(
			@PathVariable(value = "patient") final Long patientId, final HttpSession session) {
		log.info("Selecting patient {}", patientId);

		final ServiceResult<Patient> result = this.patientService.loadPatient(patientId);
		final PatientBaseView currentPatient = getCurrentPatient(session);

		if (result.isSuccess()) {
			if (currentPatient == null) {
				if (result.isSuccess()) {
					log.debug("Setting new current patient in session scope. New patient is: {}", result.getData()
							.getFirstName());
					session.setAttribute("currentPatient", result.getData());
				}
			} else {
				log.debug("Replacing patient {} with {} as current patient in session scope",
						currentPatient.getFirstName(), result.getData().getFirstName());
				session.setAttribute("currentPatient", result.getData());
			}
		}

		return result;
	}

	@RequestMapping(value = "/unselect", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ServiceResult<PatientBaseView> unselect(final HttpSession session) {
		session.removeAttribute("currentPatient");
		return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
	}
}
