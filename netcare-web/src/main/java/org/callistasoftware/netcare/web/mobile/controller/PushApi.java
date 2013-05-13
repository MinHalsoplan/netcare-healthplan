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

import java.util.HashMap;
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
        saveEnvironmentProperties("Android", data);
		service.registerForGcm(data.get("c2dmRegistrationId"));
	}

    @RequestMapping(value="/apns", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
    public void apnsRegistration(@RequestBody final Map<String, String> data) {
        this.logAccess("register", "apns");
        saveEnvironmentProperties("iOS", data);
		service.registerForApnsPush(data.get("apnsRegistrationId"));
	}

    /**
     * Saves env data from request.
     * @param data
     */
    private void saveEnvironmentProperties(String os, Map<String, String> data) {
        Map<String, String> props = new HashMap<String, String>();

        props.put("os.name", os);

        String osVersion = data.get("os.version");
        if (osVersion != null && osVersion != "") {
            props.put("os.version", osVersion);
        }

        String appVersion = data.get("app.version");
        if (appVersion != null && appVersion != "") {
            props.put("app.version", appVersion);
        }

        service.addUserProperties(props);
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
