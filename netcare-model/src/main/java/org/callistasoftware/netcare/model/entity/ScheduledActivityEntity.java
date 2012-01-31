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

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="nc_scheduled_activity")
public class ScheduledActivityEntity implements Comparable<ScheduledActivityEntity>, PermissionRestrictedEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name="scheduled_time", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date scheduledTime;
	
	@Column(name="reported_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportedTime = null;
	
	@Column(name="actual_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date actualTime;
			
	@Column(name="note", length=128)
	private String note;
	
	@Column(name="perceived_sense")
	private int perceivedSense;
	
	@Column(name="status", nullable=false)
	private ScheduledActivityStatus status;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="activity_def_id")
	private ActivityDefinitionEntity activityDefinition;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="activity", orphanRemoval=true, cascade=CascadeType.ALL)
	private List<ActivityCommentEntity> comments;
	
	@OneToMany(mappedBy="scheduledActivity", fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval=true)
	private List<MeasurementEntity> measurements;

	
	ScheduledActivityEntity() {
		this.setComments(new LinkedList<ActivityCommentEntity>());
		this.setMeasurements(new LinkedList<MeasurementEntity>());
		status = ScheduledActivityStatus.OPEN;
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
		for (MeasurementDefinitionEntity measurementDefinition : activityDefinition.getMeasurementDefinitions()) {
			MeasurementEntity e = MeasurementEntity.newEntity(scheduledActivityEntity, measurementDefinition);
			scheduledActivityEntity.measurements.add(e);
		}
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

	public boolean isRejected() {
		return (status == ScheduledActivityStatus.REJECTED);
	}

	public List<ActivityCommentEntity> getComments() {
		return comments;
	}

	void setComments(List<ActivityCommentEntity> comments) {
		this.comments = comments;
	}

	@Override
	public boolean isReadAllowed(UserEntity user) {
		return this.isWriteAllowed(user);
	}

	@Override
	public boolean isWriteAllowed(UserEntity user) {
		return this.getActivityDefinitionEntity().getHealthPlan().isWriteAllowed(user);
	}
	
	public void setStatus(ScheduledActivityStatus status) {
		this.status = status;
	}
	
	public ScheduledActivityStatus getStatus() {
		return status;
	}

	public List<MeasurementEntity> getMeasurements() {
		Collections.sort(measurements);
		return Collections.unmodifiableList(measurements);
	}
	
	public MeasurementEntity lookupMeasurement(Long id) {
		for (MeasurementEntity m : measurements) {
			if (m.getId().equals(id)) {
				return m;
			}
		}
		throw new IllegalArgumentException("No such measurement found: " + id);
	}

	void setMeasurements(List<MeasurementEntity> measurements) {
		this.measurements = measurements;
	}
}
