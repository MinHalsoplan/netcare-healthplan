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
package org.callistasoftware.netcare.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="nc_scheduled_activity")
public class ScheduledActivityEntity implements Comparable<ScheduledActivityEntity> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name="scheduled_time", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date scheduledTime;
	
	@Column(name="reported_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportedTime;
	
	@Column(name="actual_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date actualTime;
	
	@Column(name="rejected")
	private boolean rejected;
	
	@Column(name="actual_value")
	private int actualValue;
		
	@Column(name="note", length=128)
	private String note;
	
	@Column(name="perceived_sense")
	private int perceivedSense;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="activity_def_id")
	private ActivityDefinitionEntity activityDefinition;
	
	ScheduledActivityEntity() {
		rejected = false;
	}
	
	/**
	 * Creates a {@link ScheduledActivityEntity}.
	 * 
	 * @param activityDefinition the {@link ActivityDefinitionEntity}, must be not null
	 * @param scheduledTime the scheduled timestamp (datetime)
	 * @return a scheduled activity
	 */
	public static ScheduledActivityEntity newEntity(ActivityDefinitionEntity activityDefinition, Date scheduledTime) {
		ScheduledActivityEntity scheduledActivityEntity = new ScheduledActivityEntity();
		scheduledActivityEntity.setActivityDefinitionEntity(activityDefinition);
		scheduledActivityEntity.setScheduledTime(scheduledTime);
		return scheduledActivityEntity;
	}
	
	public Long getId() {
		return id;
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
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof ScheduledActivityEntity) && equals((ScheduledActivityEntity)o);
	}
	
	private boolean equals(ScheduledActivityEntity o) {
		return (this == o) || o.getId().equals(id); 
	}


	protected void setActivityDefinitionEntity(ActivityDefinitionEntity activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	public ActivityDefinitionEntity getActivityDefinitionEntity() {
		return activityDefinition;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNote() {
		return note;
	}

	public void setPerceivedSense(int perceivedSense) {
		this.perceivedSense = perceivedSense;
	}

	public int getPerceivedSense() {
		return perceivedSense;
	}

	public void setActualTime(Date actualTime) {
		this.actualTime = actualTime;
	}

	public Date getActualTime() {
		return actualTime;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	public boolean isRejected() {
		return rejected;
	}
}
