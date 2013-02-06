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

import java.util.Map;

import org.callistasoftware.netcare.api.rest.ApiSupport;
import org.callistasoftware.netcare.core.spi.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/push")
public class PushApi extends ApiSupport {

	@Autowired
	private UserDetailsService service;
	
    @RequestMapping(value="/gcm", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public void c2dmRegistration(@RequestBody final Map<String, String> data) {
		this.logAccess("register", "gcm");
		service.registerForGcm(data.get("c2dmRegistrationId"));
	}

    @RequestMapping(value="/apns", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public void apnsRegistration(@RequestParam(value="apnsRegistrationId") final String apnsRegistrationId) {
		this.logAccess("register", "apns");
		service.registerForApnsPush(apnsRegistrationId);
	}
    
    @RequestMapping(value="/gcm", method=RequestMethod.DELETE)
    @ResponseBody
    public void gcmUnregistration() {
    	this.logAccess("unregister", "gcm");
    	service.unregisterGcm();
    }
    
    @RequestMapping(value="/apns", method=RequestMethod.DELETE)
    @ResponseBody
    public void apnsUnregistration() {
    	this.logAccess("unregister", "apns");
    	service.unregisterApns();
    }

}
