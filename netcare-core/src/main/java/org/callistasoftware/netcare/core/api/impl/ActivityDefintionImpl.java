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

import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.DayTime;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.FrequencyDay;
import org.callistasoftware.netcare.model.entity.FrequencyTime;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Implementation of an activity definition
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class ActivityDefintionImpl implements ActivityDefinition {
	
	private int goal;
	private ActivityTypeImpl type;
	private String startDate;
	private int activityRepeat;
	
	private DayTimeImpl[] dayTimes;
	
	public static ActivityDefinition[] newFromEntities(final List<ActivityDefinitionEntity> entities) {
		final ActivityDefinition[] dtos = new ActivityDefintionImpl[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = ActivityDefintionImpl.newFromEntity(entities.get(i));
		}
		
		return dtos;
	}
	
	public static ActivityDefinition newFromEntity(final ActivityDefinitionEntity entity) {
		final ActivityDefintionImpl dto = new ActivityDefintionImpl();
		dto.setType(ActivityTypeImpl.newFromEntity(entity.getActivityType(), LocaleContextHolder.getLocale()));
		dto.setGoal(entity.getActivityTarget());
		Frequency frequency = entity.getFrequency();
		dto.setActivityRepeat(frequency.getWeekFrequency());
		dto.setStartDate(ApiUtil.formatDate(entity.getStartDate()));
		
		final List<FrequencyDay> frDays = frequency.getDays();
		DayTime[] dayTimes = new DayTime[frDays.size()];
		for (int i = 0; i < dayTimes.length; i++) {
			DayTimeImpl dt = new DayTimeImpl();
			dt.setDay(ApiUtil.toStringDay(frDays.get(i).getDay()));
			List<FrequencyTime> frTimes = frDays.get(i).getTimes();
			String[] times = new String[frTimes.size()];
			for (int j = 0; j < times.length; j++) {
				times[j] = FrequencyTime.marshal(frTimes.get(j));
			}
			dt.setTimes(times);
		}
		
		return dto;
	}
	
	@Override
	public int getGoal() {
		return this.goal;
	}
	
	public void setGoal(final int goal) {
		this.goal = goal;
	}

	@Override
	public ActivityType getType() {
		return this.type;
	}
	
	public void setType(final ActivityTypeImpl type) {
		this.type = type;
	}

	@Override
	public DayTime[] getDayTimes() {
		return this.dayTimes;
	}
	
	public void setDayTimes(final DayTimeImpl[] dayTimes) {
		this.dayTimes = dayTimes;
	}

	@Override
	public String getStartDate() {
		return this.startDate;
	}
	
	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public void setActivityRepeat(int weekFrequency) {
		this.activityRepeat = weekFrequency;
	}

	@Override
	public int getActivityRepeat() {
		return activityRepeat;
	}

}
