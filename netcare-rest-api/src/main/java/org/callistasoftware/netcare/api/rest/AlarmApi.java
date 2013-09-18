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

import javax.servlet.ServletRequest;

import org.callistasoftware.netcare.core.api.Alarm;
import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.spi.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/alarm")
public class AlarmApi extends ApiSupport {

	@Autowired
	private AlarmService service;

	@RequestMapping(value = "/list", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServiceResult<Alarm[]> loadAlarmsForCareUnit(ServletRequest request) {
		this.logAccess("list", "alarms", request);
		final UserBaseView user = this.getUser();
		if (user != null && user.isCareActor()) {
			final CareActorBaseView ca = (CareActorBaseView) user;
			return this.service.getCareUnitAlarms(ca.getCareUnit().getHsaId());
		}

		throw new SecurityException(
				"Illegal to access alarms for care unit. No logged in user or user is not a care giver.");
	}

	@RequestMapping(value = "/{alarm}/resolve", produces = "application/json", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult<Alarm> resolveAlarm(
			@PathVariable(value = "alarm") final Long alarm) {
		this.logAccess("resolve", "alarm");
		return this.service.resolveAlarm(alarm);
	}
}
