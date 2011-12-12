/**
 * Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>
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

import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.DefaultSystemMessage;
import org.callistasoftware.netcare.core.api.impl.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.entity.DurationUnit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Api for support operations such as loading units etc.
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@Controller
@RequestMapping(value="/support")
public class SupportApi {

	@RequestMapping(value="/units/load", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<String[]> loadUnits() {
		final String[] units = {"min", "km"};
		final ServiceResultImpl<String[]> result = ServiceResultImpl.createSuccessResult(units, new DefaultSystemMessage("Operation returned success"));
		
		return result;
	}
	
	@RequestMapping(value="/durations/load", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<DurationUnit[]> loadDurations() {
		return ServiceResultImpl.createSuccessResult(DurationUnit.values(), new GenericSuccessMessage());
	}
}
