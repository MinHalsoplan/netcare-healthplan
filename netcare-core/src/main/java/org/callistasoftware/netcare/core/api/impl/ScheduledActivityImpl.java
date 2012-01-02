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
	private int actualValue;
	private String reported;
	private String date;
	private String time;
	private Option today;
	private ActivityDefinition activityDefinition;
	private PatientBaseView patient;
	private String actualTime;
	private int sense;
	private String note;
	private boolean rejected;
	
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
		
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		a.today = new Option("weekday." + day, LocaleContextHolder.getLocale());		
		
		cal.add(Calendar.HOUR_OF_DAY, 3);
		Date time = entity.getScheduledTime();
		a.due = (time.compareTo(cal.getTime()) < 0);
		a.date = ApiUtil.formatDate(time);
		a.time = ApiUtil.formatTime(time);
		if (entity.getReportedTime() != null) {
			a.reported = ApiUtil.formatDate(entity.getReportedTime()) + " " + ApiUtil.formatTime(entity.getReportedTime());
		}
		if (entity.getActualTime() != null) {
			a.actualTime = ApiUtil.formatDate(entity.getActualTime()) + " " + ApiUtil.formatTime(entity.getActualTime());
		}
		
		a.rejected = entity.isRejected();
		a.actualValue = entity.getActualValue();
		a.patient = PatientBaseViewImpl.newFromEntity(entity.getActivityDefinitionEntity().getHealthPlan().getForPatient());
		a.sense = entity.getPerceivedSense();
		a.note = entity.getNote();
		
		return a;
	}
		
	
	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String getTime() {
		return time;
	}

	
	@Override
	public boolean isDue() {
		// TODO Auto-generated method stub
		return due;
	}

	@Override
	public ActivityDefinition getDefinition() {
		// TODO Auto-generated method stub
		return activityDefinition;
	}

	@Override
	public Option getToday() {
		return today;
	}

	@Override
	public int getActualValue() {
		return actualValue;
	}

	@Override
	public String getDate() {
		return date;
	}

	@Override
	public String getReported() {
		return reported;
	}

	@Override
	public String getActualTime() {
		return actualTime;
	}

	@Override
	public PatientBaseView getPatient() {
		return patient;
	}

	@Override
	public int getSense() {
		return sense;
	}

	@Override
	public String getNote() {
		return note;
	}

	@Override
	public boolean isRejected() {
		return rejected;
	}

}
