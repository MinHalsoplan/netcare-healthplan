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
public class MeasurementDefinitionEntity implements Comparable<MeasurementDefinitionEntity> {
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
	private float target;
	
	@Column(name="min_target")
	private float minTarget;
	
	@Column(name="max_target")
	private float maxTarget;
	
	MeasurementDefinitionEntity() {
	}

	public static MeasurementDefinitionEntity newEntity(ActivityDefinitionEntity activityDefinition, MeasurementTypeEntity measurementType) {
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


	public float getTarget() {
		return target;
	}

	public void setTarget(float target) {
		if (!this.getMeasurementType().getValueType().equals(MeasurementValueType.SINGLE_VALUE)) {
			throw new IllegalStateException("Cannot set a target value for an interval measurement definition");
		}
		this.target = target;
	}

	public float getMinTarget() {
		return minTarget;
	}

	public void setMinTarget(float minTarget) {
		if (!this.getMeasurementType().getValueType().equals(MeasurementValueType.INTERVAL)) {
			throw new IllegalStateException("Cannot set min value for a single valued measurement definition");
		}
		this.minTarget = minTarget;
	}

	public float getMaxTarget() {
		return maxTarget;
	}

	public void setMaxTarget(float maxTarget) {
		if (!this.getMeasurementType().getValueType().equals(MeasurementValueType.INTERVAL)) {
			throw new IllegalStateException("Cannot set max value for a single valued measurement definition");
		}
		this.maxTarget = maxTarget;
	}

	public MeasurementTypeEntity getMeasurementType() {
		return measurementType;
	}

	void setMeasurementType(MeasurementTypeEntity measurementType) {
		this.measurementType = measurementType;
	}

	@Override
	public int compareTo(MeasurementDefinitionEntity m) {
		return this.getMeasurementType().compareTo(m.getMeasurementType());
	}
}
