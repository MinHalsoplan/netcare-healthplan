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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/templates")
public class ActivityTypeApi extends ApiSupport {

	@Autowired
	private ActivityTypeService service;


	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ServiceResult<ActivityType[]> listTemplates(@RequestParam(value = "name") final String text,
			@RequestParam("category") final String category, @RequestParam("level") final String level) {
		this.logAccess("search", "activity template");
		return this.service.searchForActivityTypes(text, category, level);
	}

	@RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ServiceResult<ActivityType> newTemplate(@RequestBody final ActivityTypeImpl activityType) {
		this.logAccess("create", "activity template");
		return this.service.createActivityType(activityType);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ServiceResult<ActivityType> loadTemplate(@PathVariable(value = "id") final String id) {
		this.logAccess("get", "activity template");
		return this.service.getActivityType(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ServiceResult<ActivityType> deleteTemplate(@PathVariable(value="id") final Long id) {
		this.logAccess("delete", "activity template");
		return this.service.deleteActivityTemplate(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ServiceResult<ActivityType> updateActivityType(@PathVariable("id") final String id,
			@RequestBody final ActivityTypeImpl activityType) {
		this.logAccess("update", "activity type");
		return this.service.updateActivityType(activityType);
	}

}
