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
import org.callistasoftware.netcare.core.api.ActivityItemValuesDefinition;
import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.CareUnit;
import org.callistasoftware.netcare.core.api.DayTime;
import org.callistasoftware.netcare.core.api.util.JsonDateSerializer;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityItemDefinitionEntity;
import org.callistasoftware.netcare.model.entity.EstimationDefinitionEntity;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.FrequencyDay;
import org.callistasoftware.netcare.model.entity.FrequencyTime;
import org.callistasoftware.netcare.model.entity.MeasurementDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityStatus;
import org.callistasoftware.netcare.model.entity.TextDefinitionEntity;
import org.callistasoftware.netcare.model.entity.YesNoDefinitionEntity;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Implementation of an activity definition
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 * 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActivityDefinitionImpl implements ActivityDefinition {
	private static final long serialVersionUID = 1L;
	private Long id;
	private ActivityType type;
	private String startDate;
	private int activityRepeat;

	private DayTimeImpl[] dayTimes;
	private String endDate;
	private Long healthPlanId;
	private String healthPlanName;

	@JsonSerialize(using = JsonDateSerializer.class)
	private Date healthPlanStartDate;

	private int numTotal;
	private int numDone;
	private int numTarget;
	private int numExtra;
	private CareActorBaseView issuedBy;
	private CareUnit healthPlanCareUnit;
	
	private ActivityItemValuesDefinition[] goalValues;

	private boolean active;
	private boolean reminder = true;

	public static ActivityDefinition[] newFromEntities(final List<ActivityDefinitionEntity> entities) {
		final ActivityDefinition[] dtos = new ActivityDefinitionImpl[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = ActivityDefinitionImpl.newFromEntity(entities.get(i));
		}

		return dtos;
	}

	public static ActivityDefinition newFromEntity(final ActivityDefinitionEntity entity) {
		final ActivityDefinitionImpl dto = new ActivityDefinitionImpl();
		dto.setId(entity.getId());
		dto.setType(ActivityTypeImpl.newFromEntity(entity.getActivityType(), LocaleContextHolder.getLocale()));
		dto.setFrequency(entity.getFrequency());
		dto.setStartDate(ApiUtil.formatDate(entity.getStartDate()));
		dto.setEndDate(ApiUtil.formatDate(entity.getHealthPlan().getEndDate()));
		dto.setHealthPlanName(entity.getHealthPlan().getName());
		dto.setHealthPlanId(entity.getHealthPlan().getId());
		dto.setActive(!entity.isRemovedFlag());
		dto.setReminder(entity.isReminder());
		dto.setHealthPlanCareUnit(CareUnitImpl.newFromEntity(entity.getHealthPlan().getCareUnit()));

		List<ActivityItemDefinitionEntity> mdl = entity.getActivityItemDefinitions();
		final ActivityItemValuesDefinition[] goalValues = new ActivityItemValuesDefinition[mdl.size()];
		for (int i = 0; i < goalValues.length; i++) {
			if (mdl.get(i) instanceof MeasurementDefinitionEntity) {
				goalValues[i] = MeasurementDefinitionImpl.newFromEntity((MeasurementDefinitionEntity) mdl.get(i));
			} else if (mdl.get(i) instanceof EstimationDefinitionEntity) {
				goalValues[i] = EstimationDefinitionImpl.newFromEntity((EstimationDefinitionEntity) mdl.get(i));
			} else if (mdl.get(i) instanceof YesNoDefinitionEntity) {
				goalValues[i] = YesNoDefinitionImpl.newFromEntity((YesNoDefinitionEntity) mdl.get(i));
			} else if (mdl.get(i) instanceof TextDefinitionEntity) {
				goalValues[i] = TextDefinitionImpl.newFromEntity((TextDefinitionEntity) mdl.get(i));
			}

		}
		dto.goalValues = goalValues;

		dto.calcCompletion(entity.getScheduledActivities());
		CareActorBaseView issuedBy = CareActorBaseViewImpl.newFromEntity(entity.getHealthPlan().getIssuedBy());
		dto.setIssuedBy(issuedBy);

		return dto;
	}

	@Override
	public ActivityType getType() {
		return this.type;
	}

	public void setType(final ActivityType type) {
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

	@Override
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Override
	public String getHealthPlanName() {
		return healthPlanName;
	}

	public void setHealthPlanName(String healthPlanMame) {
		this.healthPlanName = healthPlanMame;
	}

	@Override
	public CareActorBaseView getIssuedBy() {
		return issuedBy;
	}

	protected void setIssuedBy(CareActorBaseView issuedBy) {
		this.issuedBy = issuedBy;
	}

	//
	private void setFrequency(Frequency frequency) {
		setActivityRepeat(frequency.getWeekFrequency());
		final List<FrequencyDay> frDays = frequency.getDays();
		DayTimeImpl[] dayTimes = new DayTimeImpl[frDays.size()];
		for (int i = 0; i < dayTimes.length; i++) {
			DayTimeImpl dt = new DayTimeImpl();
			dt.setDay(ApiUtil.toStringDay(frDays.get(i).getDay()));
			List<FrequencyTime> frTimes = frDays.get(i).getTimes();
			String[] times = new String[frTimes.size()];
			for (int j = 0; j < times.length; j++) {
				times[j] = FrequencyTime.marshal(frTimes.get(j));
			}
			dt.setTimes(times);
			dayTimes[i] = dt;
		}
		setDayTimes(dayTimes);
	}

	//
	private void calcCompletion(List<ScheduledActivityEntity> list) {
		int numDone = 0;
		int numTarget = 0;
		int numExtra = 0;

		Calendar cal = Calendar.getInstance();
		ApiUtil.dayEnd(cal);

		for (ScheduledActivityEntity a : list) {
			if (a.isExtra()) {
				numExtra++;
			}
			
			if (a.getReportedTime() != null && a.getStatus().equals(ScheduledActivityStatus.OPEN) && !a.isExtra()) {
				numDone++;
			}

			if (a.getScheduledTime().compareTo(cal.getTime()) <= 0 && !a.isExtra()) {
				numTarget++;
			}
		}

		setNumExtra(numExtra);
		setNumDone(numDone);
		setNumTotal(list.size());
		setNumTarget(numTarget);
	}

	private void setNumTotal(int numTotal) {
		this.numTotal = numTotal;
	}

	private void setNumDone(int numDone) {
		this.numDone = numDone;
	}
	
	private void setNumExtra(final int numExtra) {
		this.numExtra = numExtra;
	}

	@Override
	public int getNumTotal() {
		return numTotal;
	}

	@Override
	public int getNumDone() {
		return numDone;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	private void setNumTarget(int numTarget) {
		this.numTarget = numTarget;
	}

	@Override
	public int getNumTarget() {
		return numTarget;
	}

	@Override
	public ActivityItemValuesDefinition[] getGoalValues() {
		return this.goalValues;
	}

	public void setGoalValues(final ActivityItemValuesDefinition[] goalValues) {
		this.goalValues = goalValues;
	}

	@Override
	public Long getHealthPlanId() {
		return this.healthPlanId;
	}

	public void setHealthPlanId(final Long healthPlanId) {
		this.healthPlanId = healthPlanId;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	@Override
	public Date getHealthPlanStartDate() {
		return this.healthPlanStartDate;
	}

	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();

		buf.append("==== Activity Definition ====\n");
		buf.append("Health plan: ").append(this.healthPlanName).append("\n");
		buf.append("Start date: ").append(this.startDate).append("\n");
		buf.append("Issued by care giver: ").append(this.issuedBy).append("\n");

		buf.append(this.getType());
		buf.append("=============================\n");

		return buf.toString();
	}

	@Override
	public CareUnit getHealthPlanCareUnit() {
		return this.healthPlanCareUnit;
	}
	
	void setHealthPlanCareUnit(CareUnit healthPlanCareUnit) {
		this.healthPlanCareUnit = healthPlanCareUnit;
	}

	@Override
	public boolean isReminder() {
		return reminder;
	}
	
	public void setReminder(boolean reminder) {
		this.reminder = reminder;
	}

	@Override
	public int getNumExtra() {
		return numExtra;
	}
}
