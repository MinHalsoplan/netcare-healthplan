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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.callistasoftware.netcare.web.util.WebUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/security")
public class SecurityController extends ControllerSupport {

	@RequestMapping("/login")
	public String login(@RequestParam(value="guid", required=false) final String guid, final HttpSession sc, final Model m) {
		getLog().info("Display login page");
		
		if ((WebUtil.isProfileActive(sc.getServletContext(), "prod") || WebUtil.isProfileActive(sc.getServletContext(), "qa")) && guid != null) {
			m.addAttribute("guid", guid);
			return "redirect:/netcare/setup";
		}
		
		if (WebUtil.isProfileActive(sc.getServletContext(), "prod") && guid == null) {
			return "redirect:/netcare/security/denied";
		}
		
		if (WebUtil.isProfileActive(sc.getServletContext(), "qa")) {
			return "redirect:/netcare/mvk/token/forPatient";
		}
		
		if (WebUtil.isProfileActive(sc.getServletContext(), "test")) {
			return "login";
		}
		
		throw new RuntimeException("Could not determine application mode.");
	}
	
	@RequestMapping(value="/logout")
	public String logout(final HttpSession sc, final HttpServletRequest request) {
		getLog().info("Logout");
		SecurityContextHolder.clearContext();
		
		if (WebUtil.isProfileActive(sc.getServletContext(), "qa") || WebUtil.isProfileActive(sc.getServletContext(), "prod")) {
			request.getSession(false).invalidate();
			return "redirect:/netcare/security/loggedout";
		} else {
			request.getSession(false).invalidate();
			return "redirect:/netcare/security/login";
		}
	}
	
	
	
	@RequestMapping(value="/loggedout")
	public String loggedout() {
		return "loggedout";
	}
	
	
	@RequestMapping(value="/denied")
	public String denied() {
		return "denied";
	}
}
