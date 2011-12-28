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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.springframework.context.i18n.LocaleContextHolder;

public class ScheduledActivityImpl implements ScheduledActivity {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private boolean due;
	private int actual;
	private String reported;
	private String scheduledDate;
	private String scheduledTime;
	private Option scheduledDay;
	private int week;
	private ActivityDefinition activityDefinition;
	private PatientBaseView patient;
	
	public static ScheduledActivity[] newFromEntities(final List<ScheduledActivityEntity> entities) {
		final ScheduledActivity[] dtos = new ScheduledActivity[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = ScheduledActivityImpl.newFromEntity(entities.get(i));
		}
		
		return dtos;
	}
	
	public static ScheduledActivity newFromEntity(ScheduledActivityEntity entity) {
		ScheduledActivityImpl a = new ScheduledActivityImpl();
		
		a.id = entity.getId();
		a.activityDefinition = ActivityDefintionImpl.newFromEntity(entity.getActivityDefinitionEntity());
		Date time = entity.getScheduledTime();
		a.due = time.after(new Date());
		a.scheduledDate = ApiUtil.formatDate(time);
		a.scheduledTime = ApiUtil.formatTime(time);
		if (entity.getReportedTime() != null) {
			a.reported = ApiUtil.formatDate(entity.getReportedTime()) + " " + ApiUtil.formatTime(entity.getReportedTime());
		}
		a.actual = entity.getActualValue();
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		a.scheduledDay = new Option("weekday." + day, LocaleContextHolder.getLocale());
		
		a.week = cal.get(Calendar.WEEK_OF_YEAR);
		
		a.patient = PatientBaseViewImpl.newFromEntity(entity.getActivityDefinitionEntity().getHealthPlan().getForPatient());
		
		return a;
	}
		
	
	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String getTime() {
		// TODO Auto-generated method stub
		return scheduledTime;
	}

	
	@Override
	public boolean isDue() {
		// TODO Auto-generated method stub
		return due;
	}

	public String getDate() {
		return scheduledDate;
	}

	@Override
	public ActivityDefinition getDefinition() {
		// TODO Auto-generated method stub
		return activityDefinition;
	}

	@Override
	public Option getDay() {
		// TODO Auto-generated method stub
		return scheduledDay;
	}

	@Override
	public int getWeek() {
		// TODO Auto-generated method stub
		return week;
	}

	public int getActual() {
		return actual;
	}

	public String getReported() {
		return reported;
	}

	@Override
	public PatientBaseView getPatient() {
		return this.patient;
	}

}
