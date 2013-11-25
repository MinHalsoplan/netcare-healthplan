/**
 * Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>
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

import org.callistasoftware.netcare.core.api.ActivityComment;
import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.ActivityItemValues;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.model.entity.ActivityItemValuesEntity;
import org.callistasoftware.netcare.model.entity.EstimationEntity;
import org.callistasoftware.netcare.model.entity.MeasurementEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.TextEntity;
import org.callistasoftware.netcare.model.entity.YesNoEntity;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.joda.time.DateTime;
import org.springframework.context.i18n.LocaleContextHolder;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ScheduledActivityImpl implements ScheduledActivity {

	private static final long serialVersionUID = 1L;

	private long id;
	private boolean due;
	private String reported;
	private String date;
	private String reportedDate;
	private String time;
	private String reportedTime;
	private Option day;
	private Option reportedDay;
	private ActivityDefinition activityDefinition;
	private PatientBaseView patient;
	private String actualTime;
	
	private String actTime;
	private String actDate;
	private Option actDay;
	
	private String note;
	private ActivityItemValues[] activityItemValues;
	private boolean rejected;
	private ActivityComment[] comments;
	
	private boolean reportingPossible;
	private boolean extra;
	private String healthPlanName;

	public static ScheduledActivity[] newFromEntities(final List<ScheduledActivityEntity> entities) {
		final ScheduledActivity[] dtos = new ScheduledActivity[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = ScheduledActivityImpl.newFromEntity(entities.get(i));
		}

		return dtos;
	}

	public static ScheduledActivity newFromEntity(ScheduledActivityEntity entity) {
		ScheduledActivityImpl scheduledActivity = new ScheduledActivityImpl();

		scheduledActivity.healthPlanName = entity.getActivityDefinitionEntity().getHealthPlan().getName();
		
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
			
			Calendar reportedCal = Calendar.getInstance();
			reportedCal.setTime(entity.getReportedTime());
			
			int repDay = reportedCal.get(Calendar.DAY_OF_WEEK);
			
			Date reportedTime = entity.getReportedTime();
			scheduledActivity.reportedDay = new Option("weekday." + repDay, LocaleContextHolder.getLocale());
			scheduledActivity.reportedDate = ApiUtil.formatDate(reportedTime);
			scheduledActivity.reportedTime = ApiUtil.formatTime(reportedTime);
			scheduledActivity.reported = new StringBuilder(scheduledActivity.reportedDate).append(" ").append(scheduledActivity.reportedTime).toString();
			
			final Calendar act = Calendar.getInstance();
			act.setTime(entity.getActualTime());
			
			int actWeekday = act.get(Calendar.DAY_OF_WEEK);
			scheduledActivity.actDay = new Option("weekday." + actWeekday, LocaleContextHolder.getLocale());
			scheduledActivity.actDate = ApiUtil.formatDate(entity.getActualTime());
			scheduledActivity.actTime = ApiUtil.formatTime(entity.getActualTime());
			
		}
		if (entity.getActualTime() != null) {
			scheduledActivity.actualTime = ApiUtil.formatDate(entity.getActualTime()) + " "
					+ ApiUtil.formatTime(entity.getActualTime());
		}
		
		// Scheduled time within one week?
		final Date oneWeek = new DateTime(System.currentTimeMillis()).plusWeeks(1).toDate();
		final Date scheduled = entity.getScheduledTime();
		
		if (scheduled.before(oneWeek) && entity.getReportedTime() == null) {
			scheduledActivity.setReportingPossible(true);
		} else {
			scheduledActivity.setReportingPossible(false);
		}
		
		scheduledActivity.setExtra(entity.isExtra());

		List<ActivityItemValuesEntity> activityEntities = entity.getActivities();
		scheduledActivity.activityItemValues = new ActivityItemValues[activityEntities.size()];
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
		
		scheduledActivity.comments = new ActivityComment[entity.getComments().size()];
		for(int i=0; i < entity.getComments().size(); i++) {
			scheduledActivity.comments[i] = ActivityCommentImpl.newFromEntity(entity.getComments().get(i));
		}
		
		return scheduledActivity;
	}

	@Override
	public long getId() {
		return id;
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
	public ActivityDefinition getActivityDefinition() {
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
	
	@Override
	public ActivityComment[] getComments() {
		return comments;
	}

	@Override
	public boolean isReportingPossible() {
		return this.reportingPossible;
	}
	
	public void setReportingPossible(final boolean reportingPossible) {
		this.reportingPossible = reportingPossible;
	}

	@Override
	public boolean isExtra() {
		return this.extra;
	}
	
	public void setExtra(final boolean extra) {
		this.extra = extra;
	}

	@Override
	public Option getReportedDay() {
		return this.reportedDay;
	}

	@Override
	public String getReportedTime() {
		return this.reportedTime;
	}

	@Override
	public String getReportedDate() {
		return this.reportedDate;
	}

	@Override
	public Option getActDay() {
		return this.actDay;
	}

	@Override
	public String getActTime() {
		return this.actTime;
	}

	@Override
	public String getActDate() {
		return this.actDate;
	}
 
	public String getHealthPlanName() {
		return healthPlanName;
	}

	public void setHealthPlanName(String healthPlanName) {
		this.healthPlanName = healthPlanName;
	}
	
	
}
