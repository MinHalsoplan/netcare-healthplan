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

import java.util.Locale;

import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.entity.MeasureUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/activityType")
public class ActivityTypeApi extends ApiSupport {

	private static final Logger log = LoggerFactory.getLogger(ActivityTypeApi.class);
	
	@RequestMapping(value="/load", method=RequestMethod.GET)
	@ResponseBody
	public ServiceResult<ActivityType[]> loadActivityTypes(final Locale l) {
		log.info("User {} (care giver: {}) is loading activity types", getUser().getName(), getUser().isCareGiver());
		
		final ActivityTypeImpl ac1 = new ActivityTypeImpl();
		ac1.setId(1L);
		ac1.setName("Simma");
		ac1.setUnit(new Option(MeasureUnit.KILOMETERS.name(), l));
		
		final ActivityTypeImpl ac2 = new ActivityTypeImpl();
		ac2.setId(2L);
		ac2.setName("Yoga");
		ac2.setUnit(new Option(MeasureUnit.MINUTES.name(), l));
		
		final ActivityType[] arr = new ActivityTypeImpl[] { ac1, ac2 };
		return ServiceResultImpl.createSuccessResult(arr, new GenericSuccessMessage());
	}
}
