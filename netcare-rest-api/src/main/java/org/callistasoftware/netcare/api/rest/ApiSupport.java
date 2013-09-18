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

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Support class for REST API controllers
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 * 
 */
public abstract class ApiSupport {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final Logger pdlLog = LoggerFactory.getLogger("org.callistasoftware.netcare.api.rest.PdlLogger");

	protected UserBaseView getUser() {
		return (UserBaseView) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	protected PatientBaseView getCurrentPatient(final HttpSession session) {
		final PatientBaseView patient = (PatientBaseView) session.getAttribute("currentPatient");
		return patient;
	}

	protected boolean isCareActor() {
		return this.getUser().isCareActor();
	}

	protected boolean isPatient() {
		return !this.getUser().isCareActor();
	}

	protected void logAccess(final String action, final String what) {
		pdlLog(null, action, what);
		getLog().info("User {}, Action: {}->{}", new Object[] { this.getUser().getUsername(), action, what });
	}

	protected void logAccess(final String action, final String what, ServletRequest request) {
		pdlLog((HttpServletRequest) request, action, what);
		getLog().info("User {}, Action: {}->{}", new Object[] { this.getUser().getUsername(), action, what });
	}

	private void pdlLog(HttpServletRequest request, String action, final String what) {
		UserBaseView user = this.getUser();
		if (user.isCareActor()) {
			String patientName = "no session";
			String patientCivicId = "no session";
			String path = "no session";
			CareActorBaseView careActor = (CareActorBaseView) user;
			if (request != null) {
				patientName = "we have a session";
				PatientBaseView patient = getCurrentPatient(request.getSession());
				if (patient != null) {
					patientName = patient.getName();
					patientCivicId = patient.getCivicRegistrationNumber();
					path = request.getPathInfo();
				}
			}
			pdlLog.info("User hsa-id: {} name: {} Patient civic id: {} Name: {} Action: {}->{}, URI: {} ",
					new Object[] { careActor.getHsaId(), careActor.getUsername(), patientCivicId, patientName, action,
							what, path });
		} 

	}

	protected final Logger getLog() {
		return this.log;
	}
}
