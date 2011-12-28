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

import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.callistasoftware.netcare.core.spi.ActivityTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/activityType")
public class ActivityTypeApi extends ApiSupport {

	private static final Logger log = LoggerFactory.getLogger(ActivityTypeApi.class);
	
	@Autowired
	private ActivityTypeService service;
	
	@RequestMapping(value="/load", method=RequestMethod.GET)
	@ResponseBody
	public ServiceResult<ActivityType[]> loadActivityTypes() {
		log.info("User {} (care giver: {}) is loading activity types", getUser().getName(), getUser().isCareGiver());
		return this.service.loadAllActivityTypes();
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ActivityType[]> searchActivityTypes(@RequestParam(value="text") final String text) {
		this.logAccess("search", "activity types");
		return this.service.searchForActivityTypes(text);
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<ActivityType> createNewActivityType(@RequestBody final ActivityTypeImpl activityType) {
		this.logAccess("create", "activity type");
		return this.service.createActivityType(activityType);
	}
}
