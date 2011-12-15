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
package org.callistasoftware.netcare.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class ScheduledActivityEntity implements Comparable<ScheduledActivityEntity> {

	@Column(name="scheduled_time", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date scheduledTime;
	
	@Column(name="reported_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportedTime;
	
	@Column(name="actual_value")
	private int actualValue;
	
	@Column(name="target_value", nullable=false)
	private int targetValue;

	ScheduledActivityEntity() {
	}
	
	/**
	 * Creates a {@link ScheduledActivityEntity}.
	 * 
	 * @param activityDefinitionEntity the {@link ActivityDefinitionEntity}, must be not null
	 * @param scheduledTime the scheduled timestamp (datetime)
	 * @return a scheduled activity
	 */
	public static ScheduledActivityEntity newEntity(ActivityDefinitionEntity activityDefinitionEntity, Date scheduledTime) {
		ScheduledActivityEntity scheduledActivityEntity = new ScheduledActivityEntity();
		activityDefinitionEntity.addScheduledActivityEntity(scheduledActivityEntity);
		scheduledActivityEntity.setScheduledTime(scheduledTime);
		scheduledActivityEntity.setTargetValue(activityDefinitionEntity.getActivityTarget());
		return scheduledActivityEntity;
	}
	
	protected void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = EntityUtil.notNull(scheduledTime);
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setReportedTime(Date reportedTime) {
		this.reportedTime = reportedTime;
	}

	public Date getReportedTime() {
		return reportedTime;
	}

	public void setActualValue(int actualValue) {
		this.actualValue = actualValue;
	}

	public int getActualValue() {
		return actualValue;
	}

	@Override
	public int compareTo(ScheduledActivityEntity r) {
		return scheduledTime.compareTo(r.getScheduledTime());
	}

	/**
	 * The target value as it was defined when this activity was scheduled.
	 * 
	 * @param targetValue the origin target value to measure against.
	 */
	public void setTargetValue(int targetValue) {
		this.targetValue = targetValue;
	}

	/**
	 * Returns the target value as it was defined when this activity was scheduled.
	 * 
	 * @return the origin target value.
	 */
	public int getTargetValue() {
		return targetValue;
	}
}
