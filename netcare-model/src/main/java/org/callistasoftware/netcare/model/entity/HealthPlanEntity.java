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
package org.callistasoftware.netcare.model.entity;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name="nc_ordination")
public class HealthPlanEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false, length=64)
	private String name;
	
	@Column(name="start_date", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name="end_date", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	@Column(nullable=false)
	private int duration;
	
	@Column(name="duration_unit", nullable=false)
	private DurationUnit durationUnit;
	
	@ManyToOne
	@JoinColumn(name="issued_by_care_giver_id")
	private CareGiverEntity issuedBy;
	
	@ManyToOne
	@JoinColumn(name="for_patient_id")
	private PatientEntity forPatient;
	
	@OneToMany(mappedBy="ordination", cascade=CascadeType.PERSIST)
	private List<ActivityDefinitionEntity> activityDefinitions;
	
	
	HealthPlanEntity() {
		activityDefinitions = new LinkedList<ActivityDefinitionEntity>();
	}
	

	public static HealthPlanEntity newEntity(CareGiverEntity issuedBy, PatientEntity forPatient, String name, Date startDate, int duration, DurationUnit unit) {
		HealthPlanEntity entity = new HealthPlanEntity();
		entity.setIssuedBy(issuedBy);
		entity.setForPatient(forPatient);
		entity.setName(name);
		entity.setStartDate(startDate);
		entity.setDurationUnit(unit);
		entity.setDuration(duration);
		return entity;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = EntityUtil.notNull(name);
	}

	public String getName() {
		return name;
	}

	public void setStartDate(Date startDate) {
		this.startDate = EntityUtil.notNull(startDate);
		calculateEnd();
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	protected void setIssuedBy(CareGiverEntity issuedBy) {
		this.issuedBy = EntityUtil.notNull(issuedBy);
	}

	public CareGiverEntity getIssuedBy() {
		return issuedBy;
	}

	void setActivityDefinitions(List<ActivityDefinitionEntity> activityDefinitions) {
		this.activityDefinitions = activityDefinitions;
	}
	
	public boolean addActivityDefinition(ActivityDefinitionEntity activityDefinitionEntity) {
		if (!activityDefinitions.contains(activityDefinitionEntity)) {
			return activityDefinitions.add(activityDefinitionEntity);
		}
		return false;
	}
	
	public boolean removeActivityDefinition(ActivityDefinitionEntity activityDefinitionEntity) {
		return activityDefinitions.remove(activityDefinitionEntity);
	}

	public List<ActivityDefinitionEntity> getActivityDefinitions() {
		return Collections.unmodifiableList(activityDefinitions);
	}

	public void setDuration(int duration) {
		if (duration <= 0 || duration > 24) {
			throw new IllegalArgumentException("Invalid duration: " + duration);
		}
		this.duration = duration;
		calculateEnd();
	}

	public int getDuration() {
		return duration;
	}

	public void setDurationUnit(DurationUnit durationUnit) {
		this.durationUnit = EntityUtil.notNull(durationUnit);
		calculateEnd();
	}

	public DurationUnit getDurationUnit() {
		return durationUnit;
	}
	
	protected void calculateEnd() {
		if (durationUnit != null && startDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(startDate);
			c.add(durationUnit == DurationUnit.MONTHS ? Calendar.MONTH : Calendar.WEEK_OF_YEAR, duration);
			endDate = c.getTime();
		} else {
			endDate = null;
		}
	}


	protected void setForPatient(PatientEntity forPatient) {
		this.forPatient = EntityUtil.notNull(forPatient);
	}


	public PatientEntity getForPatient() {
		return forPatient;
	}
}
