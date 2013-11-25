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
package org.callistasoftware.netcare.model.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("measurement")
public class MeasurementDefinitionEntity extends ActivityItemDefinitionEntity {

	@Column(name = "target")
	private float target;

	@Column(name = "min_target")
	private float minTarget;

	@Column(name = "max_target")
	private float maxTarget;

	MeasurementDefinitionEntity() {
	}

	public static MeasurementDefinitionEntity newEntity(ActivityDefinitionEntity activityDefinition,
			ActivityItemTypeEntity activityItemType) {
		MeasurementDefinitionEntity entity = new MeasurementDefinitionEntity();
		entity.setActivityDefinition(activityDefinition);
		entity.setActivityItemType(activityItemType);
		return entity;
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
		return (MeasurementTypeEntity) getActivityItemType();
	}

}
