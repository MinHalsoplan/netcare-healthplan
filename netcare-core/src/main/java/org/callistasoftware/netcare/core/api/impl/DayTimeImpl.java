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
package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.DayTime;
import org.callistasoftware.netcare.core.api.Option;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.i18n.LocaleContextHolder;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DayTimeImpl implements DayTime {
	private String day;
	private String[] times;
	
	public void setDay(String day) {
		this.day = day;
	}


	@Override
	public String getDay() {
		return this.day;
	}
	

	public void setTimes(String[] times) {
		this.times = times;
	}

	@Override
	public String[] getTimes() {
		return this.times;
	}


	@Override
	public Option getDayCaption() {
		return new Option(getDay(), LocaleContextHolder.getLocale());
	}
}
