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

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.impl.PdlLogImpl;
import org.callistasoftware.netcare.core.spi.PdlLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Support class for REST API controllers
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 * 
 */
public abstract class ApiSupport {

	Properties actionTranslations = null;

	@Value("${support.email}")
	private String supportEmail;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PdlLogService pdlLogService;

	protected UserBaseView getUser() {
		return (UserBaseView) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	protected PatientBaseView getCurrentPatient(final HttpSession session) {
		return (PatientBaseView) session.getAttribute("currentPatient");
	}

	protected boolean isCareActor() {
		return this.getUser().isCareActor();
	}

	protected boolean isPatient() {
		return !this.getUser().isCareActor();
	}

	private void debugLog(final String action, final String what) {
		getLog().info("User {}, Action: {}->{}", new Object[] { this.getUser().getUsername(), action, what });
	}

	protected void logAccessWithoutPdl(final String action, final String what) {
		debugLog(action, what);
	}

	protected void logAccess(final String action, final String what, String healtPlanName, HttpServletRequest request) {
		debugLog(action, what);

		PatientBaseView patient = null;
		if (request != null) {
			patient = getCurrentPatient(request.getSession());
		}
		pdlLog(action, what, patient, healtPlanName, request);
	}

	protected void logAccess(final String action, final String what, HttpServletRequest request,
			PatientBaseView patient, String healtPlanName) {
		debugLog(action, what);
		pdlLog(action, what, patient, healtPlanName, request);
	}

	/**
	 * PDL logging is only done if the user is a CareActor and if information about
	 * one ore many patients are concerned. In case of info concerning many persons returned by one api call, 
	 * one log entry is written for each person
	 */
	private void pdlLog(String action, final String what, PatientBaseView patient, String healtPlanName,
			HttpServletRequest request) {

		if (!isCareActor()) {
			return;
		}
		if (patient == null) {
			getLog().info("pdlLogging requested but no patient present Action: {}->{}", new Object[] { action, what });
			return;
		}

		if (healtPlanName == null) {
			healtPlanName = " ";
		}

		CareActorBaseView careActor = (CareActorBaseView) this.getUser();
		String actionLabel = getActionLabel(action, what);

		PdlLogImpl pdlLogImpl = new PdlLogImpl();
		pdlLogImpl.setAction(actionLabel);
		pdlLogImpl.setCareActorName(careActor.getName());
		pdlLogImpl.setCivicId(patient.getCivicRegistrationNumber());
		pdlLogImpl.setHsaId(careActor.getHsaId());
		pdlLogImpl.setPatientName(patient.getName());
		pdlLogImpl.setHealtPlanName(healtPlanName);
		pdlLogService.createPdlLog(pdlLogImpl);
	}

	private String getActionLabel(String action, final String what) {
		String actionId = action + "->" + what;
		if (actionTranslations == null) {
			actionTranslations = new Properties();
			try {
				actionTranslations.load(this.getClass().getClassLoader().getResourceAsStream("action.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String actionLabel = actionTranslations.getProperty(actionId);
		if (actionLabel == null) {
			return actionId;
		}
		return actionLabel;
	}

	protected final Logger getLog() {
		return this.log;
	}
}
