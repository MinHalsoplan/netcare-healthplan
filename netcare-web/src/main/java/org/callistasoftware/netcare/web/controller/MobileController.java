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

import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/mobile")
public class MobileController extends ControllerSupport {
	
	@RequestMapping(value="/checkcredentials", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<Boolean> checkUserCredentials() {
		return ServiceResultImpl.createSuccessResult(Boolean.TRUE, new GenericSuccessMessage());
	}

	@RequestMapping(value="/start", method=RequestMethod.GET)
	public String displayMobileStartPage() {
		return "mobile/start";
	}
	
	@RequestMapping(value="/activity/{activity}/report", method=RequestMethod.GET)
	public String displayReportPage(@PathVariable(value="activity") final Long activity, final Model m) {
		m.addAttribute("activityId", activity);
		return "mobile/report";
	}
}
