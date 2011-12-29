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

import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.DefaultSystemMessage;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
	private MessageSource messages;
	
	@Autowired
	private HealthPlanService service;
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String displayLoginForm() {
		return "login";
	}
	
	@RequestMapping(value="/home", method=RequestMethod.GET)
	public String goHome() {
		
		log.info("User {} is being redirected to home");
		final UserBaseView user = this.getLoggedInUser();
		if (user == null) {
			throw new SecurityException("User is not logged in");
		}
		
		if (user.isCareGiver()) {
			log.debug("Redirecting to admin home");
			return "redirect:admin/home";
		}
		
		log.debug("Redirecting to user home");
		return "redirect:user/home";
	}
	
	@RequestMapping(value="/admin/home", method=RequestMethod.GET)
	public String displayAdminHome(final HttpSession session) {
		log.info("Displaying home for admin");
		final PatientBaseView pbv = this.getCurrentPatient(session);
		if (pbv == null) {
			log.debug("No patient in session.");
		} else {
			log.debug("Current patient in session is: " + this.getCurrentPatient(session).getName());
		}
		
		
		return "admin/home";
	}
	
	@RequestMapping(value="/admin/healthplan/new", method=RequestMethod.GET)
	public String displayCreateOrdination(final Model m, final HttpSession session, final Locale locale) {
		log.info("Displaying create new ordination");
		
		if (session.getAttribute("currentPatient") == null) {
			m.addAttribute("result", ServiceResultImpl.createFailedResult(new DefaultSystemMessage(messages.getMessage("noCurrentPatientError", new Object[0], locale))));
		}
		
		return "admin/healthplan";
	}
	
	@RequestMapping(value="/admin/healthplan/{healthplanId}/view", method=RequestMethod.GET)
	public String displayNewActivityDefinition(@PathVariable(value="healthplanId") final Long healthPlan, final HttpSession session, final Model m) {
		log.info("Getting ordination {}", healthPlan);
		
		final ServiceResult<HealthPlan> result = this.service.loadHealthPlan(healthPlan, this.getCurrentPatient(session));
		m.addAttribute("result", result);
		if (result.isSuccess()) {
			m.addAttribute("hideMessages", Boolean.TRUE);
		}
		
		log.debug("Returning health plan with id: {}", result.getData().getId());
		
		return "admin/activity";
	}
	
	@RequestMapping(value="/admin/categories", method=RequestMethod.GET)
	public String displayActivityCategories() {
		return "admin/categories";
	}
	
	@RequestMapping(value="/user/home", method=RequestMethod.GET)
	public String displayUserHome() {
		log.info("Displaying home for user");
		return "user/home";
	}
	
	
}
