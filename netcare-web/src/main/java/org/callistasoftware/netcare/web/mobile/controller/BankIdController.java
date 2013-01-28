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
package org.callistasoftware.netcare.web.mobile.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.callistasoftware.netcare.commons.bankid.CivicRegistrationNumber;
import org.callistasoftware.netcare.commons.bankid.service.BankIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/bankid")
@Profile(value={"mobile-qa", "mobile-prod"})
public class BankIdController {
	
	private static final Logger log = LoggerFactory.getLogger(BankIdController.class);
	
	@Autowired
	private BankIdService service;
	
	@Value("${bankid.redirectUrl}")
	private String redirectUrl;
	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String authenticate(@RequestParam(value="crn", required=true) String crn, HttpServletResponse response) throws IOException {
		
		log.info("Authenticating user {}", crn);
		
        try {
            return service.authenticate(new CivicRegistrationNumber(crn));
        } catch (Exception e) {
        	
        	e.printStackTrace();
        	
        	log.error("Exception caught in bank id controller", e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
        return "";
    }

    @RequestMapping(value = "/complete", method = RequestMethod.GET, produces="application/json")
    @ResponseBody
    public UserDetails collect(final HttpServletResponse response) throws IOException {
    	final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (auth != null) {
    		return (UserDetails) auth.getPrincipal();
    	}
    	
    	log.debug("We have no authentication.. Returning unathorized.");
    	
    	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    	return null;
    }
}
