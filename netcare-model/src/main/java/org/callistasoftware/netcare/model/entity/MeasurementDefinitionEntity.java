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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="nc_measurement_definition")
public class MeasurementDefinitionEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="activity_def_id")
	private ActivityDefinitionEntity activityDefinition;

	@ManyToOne(optional=false)
	@JoinColumn(name="measurement_type_id")
	private MeasurementTypeEntity measurementType;
		
	@Column(name="target")
	private int target;
	
	@Column(name="min_target")
	private int minTarget;
	
	@Column(name="max_target")
	private int maxTarget;
	
	MeasurementDefinitionEntity() {
	}

	static MeasurementDefinitionEntity newEntity(ActivityDefinitionEntity activityDefinition, MeasurementTypeEntity measurementType) {
		MeasurementDefinitionEntity entity = new MeasurementDefinitionEntity();
		entity.setActivityDefinition(activityDefinition);
		entity.setMeasurementType(measurementType);
		return entity;
	}

	public Long getId() {
		return id;
	}

	public ActivityDefinitionEntity getActivityDefinition() {
		return activityDefinition;
	}

	void setActivityDefinition(ActivityDefinitionEntity activityDefinition) {
		this.activityDefinition = activityDefinition;
	}


	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		if (measurementType.isIntervalTarget()) {
			throw new IllegalStateException("Set target on an interval measurement is not allowed.");
		}
		this.target = target;
	}

	public int getMinTarget() {
		return minTarget;
	}

	public void setMinTarget(int minTarget) {
		if (!measurementType.isIntervalTarget()) {
			throw new IllegalStateException("Attempt to set min target on a non-interval measurement.");
		}
		this.minTarget = minTarget;
	}

	public int getMaxTarget() {
		return maxTarget;
	}

	public void setMaxTarget(int maxTarget) {
		if (!measurementType.isIntervalTarget()) {
			throw new IllegalStateException("Attempt to set min target on a non-interval measurement.");
		}
		this.maxTarget = maxTarget;
	}

	public MeasurementTypeEntity getMeasurementType() {
		return measurementType;
	}

	void setMeasurementType(MeasurementTypeEntity measurementType) {
		this.measurementType = measurementType;
	}
}
