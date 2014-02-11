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
package org.callistasoftware.netcare.api.rest;

import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.spi.ScheduleService;
import org.callistasoftware.netcare.model.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/scheduledActivities", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize(RoleEntity.PATIENT)
public class ScheduleApi extends ApiSupport {

	// All methods only performed by patients, no pdl logging

	@Autowired
	private ScheduleService schedule;

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public ServiceResult<ScheduledActivity[]> getSchema(
			@RequestParam(value = "reported", required = false) final boolean reported,
			@RequestParam(value = "due", required = false) final boolean due,
			@RequestParam(value = "start", required = false) final Long start,
			@RequestParam(value = "end", required = false) final Long end) {

		logAccessWithoutPdl("lista", "schema");

		getLog().debug("Reported: {}, Due: {}, Start: {}, End: {}", new Object[] { reported, due, start, end });
		return schedule.load(reported, due, start, end);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ServiceResult<ScheduledActivity> report(@RequestBody final ScheduledActivity report) {
		logAccessWithoutPdl("report", "activity");
		return schedule.reportReady(report);
	}

	@RequestMapping(value = "/latestForDefinition", method = RequestMethod.GET)
	@ResponseBody
	public ServiceResult<ScheduledActivity> loadLatestForDefinition(@RequestParam("definitionId") final Long definition) {
		logAccessWithoutPdl("load", "latest_activity");
		return schedule.loadLatestForDefinition(definition);
	}
}
