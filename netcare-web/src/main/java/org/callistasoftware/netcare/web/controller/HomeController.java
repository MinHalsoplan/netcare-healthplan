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
package org.callistasoftware.netcare.web.controller;

import javax.servlet.http.HttpSession;

import org.callistasoftware.netcare.core.api.MinimalUser;
import org.callistasoftware.netcare.core.api.impl.DefaultSystemMessage;
import org.callistasoftware.netcare.core.api.impl.MinimalUserImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("")
@Controller
public class HomeController extends ControllerSupport {

	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * This should ONLY be accessible in a pre-authentication scenario
	 * and ONLY if the user does not already exist
	 * @return
	 */
	@RequestMapping(value="/initial", method=RequestMethod.GET)
	public String performInitialSetup(final Model m) {
		
		log.info("Performing initial setup of a new user from MVK.");
		
		m.addAttribute("user", new MinimalUserImpl());
		return "create";
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public String createInitialUser(@ModelAttribute("user") final MinimalUser user, final BindingResult result) {
		
		throw new RuntimeException("A runtime exception");
		
		//log.info("Creating new user");
		//return "redirect:/home";
	}
	
	@RequestMapping(value="/home", method=RequestMethod.GET)
	public String goHome() {
		
		log.info("User {} is being redirected to home");
		final MinimalUser user = this.getLoggedInUser();
		if (user == null) {
			throw new SecurityException("User is not logged in");
		}
		
		if (user.isCareGiver()) {
			log.debug("Redirecting to admin home");
			return "redirect:/admin/home";
		}
		
		if (user.isPatient()) {
			log.debug("Redirecting to user home");
			return "redirect:/patient/home";
		}
		
		throw new IllegalStateException("User is neither care giver nor patient.");
	}
	
	@RequestMapping(value="/admin/home", method=RequestMethod.GET)
	public String displayAdminHome() {
		log.info("Displaying home for admin");
		return "admin/home";
	}
	
	@RequestMapping(value="/admin/ordination/new", method=RequestMethod.GET)
	public String displayCreateOrdination(final Model m, final HttpSession session) {
		log.info("Displaying create new ordination");
		
		if (session.getAttribute("currentPatient") == null) {
			m.addAttribute("result", ServiceResultImpl.createFailedResult(new DefaultSystemMessage("Du arbetar för närvarande inte med någon patient. Var god välj en patient att arbeta med först.")));
		}
		
		return "admin/ordinations";
	}
	
	@RequestMapping(value="/user/home", method=RequestMethod.GET)
	public String displayUserHome() {
		log.info("Displaying home for user");
		return "user/home";
	}
	
	
}
