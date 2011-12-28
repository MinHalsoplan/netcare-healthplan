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

import java.util.Locale;

import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.DefaultSystemMessage;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
public class SupportApi extends ApiSupport {
	
	private static final Logger log = LoggerFactory.getLogger(SupportApi.class);
	
	@Autowired
	private HealthPlanService service;
	
	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value="/units/load", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<Option[]> loadUnits() {
		
		log.info("Loading measure units...");
		
		final MeasureUnit[] units = MeasureUnit.values();
		final Option[] opts = new Option[units.length];
		for (int i = 0; i < units.length; i++) {
			log.debug("Processing {}", units[i].name());
			opts[i] = new Option(units[i].name(), LocaleContextHolder.getLocale());
		}
		
		return ServiceResultImpl.createSuccessResult(opts, new GenericSuccessMessage());
	}
	
	@RequestMapping(value="/durations/load", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<Option[]> loadDurations(final Locale locale) {
		log.info("Loading duration units...");
		
		final DurationUnit[] units = DurationUnit.values();
		final Option[] durationUnits = new Option[units.length];
		int count = 0;
		for (final DurationUnit du : units) {
			log.debug("Processing {}", du.name());
			durationUnits[count++] = new Option(du.name(), locale);
		}
		
		return ServiceResultImpl.createSuccessResult(durationUnits, new GenericSuccessMessage());
	}
	
	@RequestMapping(value="/months/load", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<String[]> loadMonths(final Locale locale) {
		
		final String[] months = new String[12];
		months[0] = this.messageSource.getMessage("january", null, locale);
		months[1] = this.messageSource.getMessage("february", null, locale);
		months[2] = this.messageSource.getMessage("march", null, locale);
		months[3] = this.messageSource.getMessage("april", null, locale);
		months[4] = this.messageSource.getMessage("may", null, locale);
		months[5] = this.messageSource.getMessage("june", null, locale);
		months[6] = this.messageSource.getMessage("july", null, locale);
		months[7] = this.messageSource.getMessage("august", null, locale);
		months[8] = this.messageSource.getMessage("september", null, locale);
		months[9] = this.messageSource.getMessage("october", null, locale);
		months[10] = this.messageSource.getMessage("november", null, locale);
		months[11] = this.messageSource.getMessage("december", null, locale);
		
		return ServiceResultImpl.createSuccessResult(months, new GenericSuccessMessage());
	}
	
	@RequestMapping(value="/weekdays/load", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<String[]> loadWeekdays(final Locale locale) {
		final String[] weekdays = new String[7];
		weekdays[0] = this.messageSource.getMessage("sunday", null, locale);
		weekdays[1] = this.messageSource.getMessage("monday", null, locale);
		weekdays[2] = this.messageSource.getMessage("tuesday", null, locale);
		weekdays[3] = this.messageSource.getMessage("wednesday", null, locale);
		weekdays[4] = this.messageSource.getMessage("thursday", null, locale);
		weekdays[5] = this.messageSource.getMessage("friday", null, locale);
		weekdays[6] = this.messageSource.getMessage("saturday", null, locale);
		
		return ServiceResultImpl.createSuccessResult(weekdays, new GenericSuccessMessage());
	}
	
	@RequestMapping(value="/reported/latest", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ScheduledActivity[]> loadLatestReportedActivities() {
		final UserBaseView user = this.getUser();
		if (!user.isCareGiver()) {
			return ServiceResultImpl.createFailedResult(new DefaultSystemMessage("Unauthorized"));
		}
		
		final CareGiverBaseView cbv = (CareGiverBaseView) user;
		
		log.info("User {} is loading latest reported activities", user.getUsername());
		return this.service.loadLatestReportedForAllPatients(cbv.getCareUnit());
	}
}
