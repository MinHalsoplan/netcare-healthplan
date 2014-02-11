/**
 * Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>
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

import org.callistasoftware.netcare.mvk.authentication.service.MvkTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/mvk")
@Profile("qa")
public class CreateTokenController {

	private static final Logger log = LoggerFactory.getLogger(CreateTokenController.class);
	
	@Autowired
	private MvkTokenService service;
	
	@RequestMapping(value="/token/forPatient", method=RequestMethod.GET)
	public String createTokenForPatient(final Model m) {
		log.info("Generating a guid by calling mvk push id service...");
		m.addAttribute("guid", service.createAuthenticationTokenForPatient("191212121220"));
		
		return "redirect:/netcare/setup";
	}
	
	@RequestMapping(value="/token/forCareGiver", method=RequestMethod.GET)
	public String createTokenForCareGiver(final Model m) {
		log.info("Generating a guid by calling mvk push id service...");
		m.addAttribute("guid", service.createAuthenticationTokenForCareActor("hsa-id-1234", "care-unit-hsa", "Callista Vårdcentral"));
		
		return "redirect:/netcare/setup";
	}
}
