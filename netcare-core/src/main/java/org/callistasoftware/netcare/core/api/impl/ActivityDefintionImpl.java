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
package org.callistasoftware.netcare.core.api.impl;

import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.DayTime;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.FrequencyTime;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Implementation of an activity defintion
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class ActivityDefintionImpl implements ActivityDefinition {
	
	private int goal;
	private ActivityTypeImpl type;
	
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
		
		final List<FrequencyTime> frTimes = entity.getFrequency().getTimes();
//		final String[] times = new String[frTimes.size()];
//		
//		for (int i = 0; i < frTimes.size(); i++) {
//			final FrequencyTime time = frTimes.get(i);
//			times[i] = new StringBuilder().append(time.getHour()).append(":").append(time.getMinute()).toString();
//		}
//		
//		// FIXME - Implement full support
//		dto.setTimes(times);
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
}
