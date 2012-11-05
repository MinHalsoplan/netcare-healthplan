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
import org.callistasoftware.netcare.core.api.ActivityItemValues;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.Measurement;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.model.entity.ActivityItemValuesEntity;
import org.callistasoftware.netcare.model.entity.EstimationEntity;
import org.callistasoftware.netcare.model.entity.MeasurementEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.TextEntity;
import org.callistasoftware.netcare.model.entity.YesNoEntity;
import org.springframework.context.i18n.LocaleContextHolder;

public class ScheduledActivityImpl implements ScheduledActivity {

	private static final long serialVersionUID = 1L;

	private long id;
	private boolean due;
	private String reported;
	private String date;
	private String time;
	private Option day;
	private ActivityDefinition activityDefinition;
	private PatientBaseView patient;
	private String actualTime;
	private String note;
	private ActivityItemValues[] activityItemValues;
	private boolean rejected;

	public static ScheduledActivity[] newFromEntities(final List<ScheduledActivityEntity> entities) {
		final ScheduledActivity[] dtos = new ScheduledActivity[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = ScheduledActivityImpl.newFromEntity(entities.get(i));
		}

		return dtos;
	}

	public static ScheduledActivity newFromEntity(ScheduledActivityEntity entity) {
		ScheduledActivityImpl scheduledActivity = new ScheduledActivityImpl();

		scheduledActivity.id = entity.getId();
		scheduledActivity.activityDefinition = ActivityDefinitionImpl.newFromEntity(entity
				.getActivityDefinitionEntity());

		Calendar cal = Calendar.getInstance();
		cal.setTime(entity.getScheduledTime());
		int day = cal.get(Calendar.DAY_OF_WEEK);
		scheduledActivity.day = new Option("weekday." + day, LocaleContextHolder.getLocale());

		cal.setTime(new Date());
		ApiUtil.dayBegin(cal);
		Date time = entity.getScheduledTime();
		scheduledActivity.due = (time.compareTo(cal.getTime()) < 0);
		scheduledActivity.date = ApiUtil.formatDate(time);
		scheduledActivity.time = ApiUtil.formatTime(time);
		if (entity.getReportedTime() != null) {
			scheduledActivity.reported = ApiUtil.formatDate(entity.getReportedTime()) + " "
					+ ApiUtil.formatTime(entity.getReportedTime());
		}
		if (entity.getActualTime() != null) {
			scheduledActivity.actualTime = ApiUtil.formatDate(entity.getActualTime()) + " "
					+ ApiUtil.formatTime(entity.getActualTime());
		}

		List<ActivityItemValuesEntity> activityEntities = entity.getActivities();
		scheduledActivity.activityItemValues = new Measurement[activityEntities.size()];
		for (int i = 0; i < scheduledActivity.activityItemValues.length; i++) {
			ActivityItemValuesEntity activityItemValuesEntity = activityEntities.get(i);
			if (activityItemValuesEntity instanceof MeasurementEntity) {
				scheduledActivity.activityItemValues[i] = MeasurementImpl
						.newFromEntity((MeasurementEntity) activityItemValuesEntity);
			} else if (activityItemValuesEntity instanceof EstimationEntity) {
				scheduledActivity.activityItemValues[i] = EstimationImpl
						.newFromEntity((EstimationEntity) activityItemValuesEntity);
			} else if (activityItemValuesEntity instanceof YesNoEntity) {
				scheduledActivity.activityItemValues[i] = YesNoImpl
						.newFromEntity((YesNoEntity) activityItemValuesEntity);
			} else if (activityItemValuesEntity instanceof TextEntity) {
				scheduledActivity.activityItemValues[i] = TextImpl.newFromEntity((TextEntity) activityItemValuesEntity);
			}
		}
		scheduledActivity.rejected = entity.isRejected();
		scheduledActivity.patient = PatientBaseViewImpl.newFromEntity(entity.getActivityDefinitionEntity()
				.getHealthPlan().getForPatient());
		scheduledActivity.note = entity.getNote();

		return scheduledActivity;
	}

	@Override
	public long getId() {
		return id;
	}

	public ActivityDefinition getActivityDefinition() {
		return activityDefinition;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDue(boolean due) {
		this.due = due;
	}

	public void setReported(String reported) {
		this.reported = reported;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setDay(Option day) {
		this.day = day;
	}

	public void setActivityDefinition(ActivityDefinition activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	public void setPatient(PatientBaseView patient) {
		this.patient = patient;
	}

	public void setActualTime(String actualTime) {
		this.actualTime = actualTime;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	@Override
	public String getTime() {
		return time;
	}

	@Override
	public boolean isDue() {
		return due;
	}

	@Override
	public ActivityDefinition getDefinition() {
		return activityDefinition;
	}

	@Override
	public Option getDay() {
		return day;
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
	public String getNote() {
		return note;
	}

	@Override
	public boolean isRejected() {
		return rejected;
	}

	@Override
	public ActivityItemValues[] getActivityItemValues() {
		return activityItemValues;
	}

	public void setActivityItemValues(ActivityItemValues[] activityItemValues) {
		this.activityItemValues = activityItemValues;
	}
}
