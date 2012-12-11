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
package org.callistasoftware.netcare.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.NoCurrentPatientMessage;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("")
@Controller
public class HomeController extends ControllerSupport {

	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private HealthPlanService service;

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String goHome() {

		log.info("User {} is being redirected to home");
		final UserBaseView user = this.getLoggedInUser();
		if (user == null) {
			throw new SecurityException("User is not logged in");
		}

		if (user.isCareActor()) {
			log.debug("Redirecting to admin home");
			return "redirect:admin/home";
		}

		log.debug("Redirecting to user home");
		return "redirect:user/home";
	}

	@RequestMapping(value = "/admin/home", method = RequestMethod.GET)
	public String displayAdminHome(final HttpSession session) {
		log.info("Displaying home for admin");
		final PatientBaseView pbv = this.getCurrentPatient(session);
		if (pbv == null) {
			log.debug("No patient in session.");
		} else {
			log.debug("Current patient in session is: " + this.getCurrentPatient(session).getFirstName());
		}

		return "admin/home";
	}

	@RequestMapping(value = "/admin/healthplans", method = RequestMethod.GET)
	public String displayCreateOrdination(final Model m, final HttpSession session, final Locale locale) {
		log.info("Displaying create new health plan");

		if (session.getAttribute("currentPatient") == null) {
			m.addAttribute("result", ServiceResultImpl.createFailedResult(new NoCurrentPatientMessage()));
		}

		return "admin/healthplan";
	}

	@RequestMapping(value = "/admin/healthplans/{healthplanId}/plan/new", method = RequestMethod.GET)
	public String displayNewActivityDefinition(@PathVariable(value = "healthplanId") final Long healthPlan,
			final HttpSession session, final Model m) {
		m.addAttribute("healthPlanId", healthPlan);
		return "admin/plan";
	}
	
	@RequestMapping(value="/admin/healthplans/{healthPlanId}/plan/{definitionId}", method=RequestMethod.GET)
	public String planActivity(@PathVariable("healthPlanId") final Long healthPlanId,
			@PathVariable("definitionId") final Long definitionId, final Model m) {
		
		m.addAttribute("healthPlanId", healthPlanId);
		m.addAttribute("definitionId", definitionId);
		
		return "admin/plan";

	}

	@RequestMapping(value = "/admin/patients", method = RequestMethod.GET)
	public String displayPatients() {
		return "admin/patients";
	}

	@RequestMapping(value = "/admin/templates", method = RequestMethod.GET)
	public String displayActivityTypes() {
		return "admin/activity-template";
	}

	@RequestMapping(value = "/admin/template/{id}", method = RequestMethod.GET)
	public String displayTemplate(@PathVariable(value = "id") final Long id, Model model) {
		log.info("Getting activity type with id {}", id);
		model.addAttribute("currentId", id);
		return "admin/template";
	}

	@RequestMapping(value = "/admin/template", method = RequestMethod.GET)
	public String newTemplate(Model model) {
		log.info("Creating new activity type ");
		model.addAttribute("currentId", -1);
		return "admin/template";
	}

	@RequestMapping(value = "/admin/activity/list", method = RequestMethod.GET)
	public String displayReportedActivities() {
		return "admin/reportedActivities";
	}

	@RequestMapping(value = "/shared/results", method = RequestMethod.GET)
	public String displayPatientResults() {
		return "shared/results";
	}
	
	@RequestMapping(value="/shared/select-results", method=RequestMethod.GET)
	public String selectResults(final HttpSession session, final Model model) {
		
		final UserBaseView user = getLoggedInUser();
		if (user.isCareActor()) {
			final PatientBaseView patient = getCurrentPatient(session);
			if (patient == null) {
				throw new IllegalStateException("Could not retreive patient from session when displaying. The care giver does not seem to work with any patient at the moment.");
			}
			model.addAttribute("patientId", patient.getId());
		} else {
			model.addAttribute("patientId", user.getId());
		}
		
		return "shared/select-results";
	}

	@RequestMapping(value = "/user/profile", method = RequestMethod.GET)
	public String displayUserProfile() {
		log.info("Displaying user profile");
		return "user/profile";
	}

	@RequestMapping(value = "/user/home", method = RequestMethod.GET)
	public String displayUserHome() {
		log.info("Displaying home for user");
		return "user/home";
	}

	@RequestMapping(value = "/user/report", method = RequestMethod.GET)
	public String displayUserReport() {
		log.info("Displaying home for user");
		return "user/report";
	}
	
	@RequestMapping(value = "/user/extra-report", method = RequestMethod.GET)
	public String displayUserExtraReport() {
		log.info("Displaying home for user");
		return "user/extra-report";
	}
	
	@RequestMapping(value="/nation-admin/units", method=RequestMethod.GET)
	public String displayUnitAdministration() {
		return "nation-admin/units";
	}
	
	@RequestMapping(value = "/nation-admin/categories", method = RequestMethod.GET)
	public String displayActivityCategories() {
		return "nation-admin/categories";
	}
	
	@RequestMapping(value="/nation-admin/careunits", method = RequestMethod.GET)
	public String displayCareUnits() {
		return "nation-admin/careunits";
	}
}
