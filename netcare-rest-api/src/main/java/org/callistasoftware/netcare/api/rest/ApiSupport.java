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

import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Support class for REST API controllers
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public abstract class ApiSupport {
	
	private static final Logger log = LoggerFactory.getLogger(ApiSupport.class);

	protected UserBaseView getUser() {
		return (UserBaseView) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	protected PatientBaseView getCurrentPatient(final HttpSession session) {
		final PatientBaseView patient = (PatientBaseView) session.getAttribute("currentPatient");
		return patient;
	}
	
	protected boolean isCareGiver() {
		return this.getUser().isCareGiver();
	}
	
	protected boolean isPatient() {
		return !this.getUser().isCareGiver();
	}
	
	protected void logAccess(final String action, final String what, final PatientBaseView target, final CareGiverBaseView careGiver) {
		log.info("User {} (hsa-id: {}) [{} -> {}]. Patient: {} (cnr: {})"
				, new Object[] {careGiver.getName()
						, careGiver.getHsaId()
						, action
						, what
						, target.getName()
						, target.getCivicRegistrationNumber()});
	}
}
