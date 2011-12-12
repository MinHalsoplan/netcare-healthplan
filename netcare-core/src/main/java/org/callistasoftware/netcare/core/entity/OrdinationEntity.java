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

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="ordination")
public class OrdinationEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column
	private String name;
	
	@Column(name="start_date")
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name="end_date")
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	@Column
	private int duration;
	
	@Column(name="duration_unit")
	private DurationUnit durationUnit;
	
	@ManyToOne
	private CareGiverEntity issuedBy;
	
	@OneToMany
	private List<ActivityDefinitionEntity> activityDefinitions;
	
	
	public OrdinationEntity() {
		activityDefinitions = new LinkedList<ActivityDefinitionEntity>();
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		calculateEnd();
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setIssuedBy(CareGiverEntity issuedBy) {
		this.issuedBy = issuedBy;
	}

	public CareGiverEntity getIssuedBy() {
		return issuedBy;
	}

	public void setActivityDefinitions(List<ActivityDefinitionEntity> activityDefinitions) {
		this.activityDefinitions = activityDefinitions;
	}

	public List<ActivityDefinitionEntity> getActivityDefinitions() {
		return activityDefinitions;
	}

	public void setDuration(int duration) {
		this.duration = duration;
		calculateEnd();
	}

	public int getDuration() {
		return duration;
	}

	public void setDurationUnit(DurationUnit durationUnit) {
		this.durationUnit = durationUnit;
		calculateEnd();
	}

	public DurationUnit getDurationUnit() {
		return durationUnit;
	}
	
	protected void calculateEnd() {
		if (durationUnit != null && startDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(startDate);
			c.add(durationUnit == DurationUnit.MONTH ? Calendar.MONTH : Calendar.WEEK_OF_YEAR, duration);
			endDate = c.getTime();
		} else {
			endDate = null;
		}
	}
}
