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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("measurement")
public class MeasurementTypeEntity extends ActivityItemTypeEntity {

	@Column(name = "value_type", nullable = false)
	private MeasurementValueType valueType;

	@Column(name = "unit", nullable = false)
	private MeasureUnit unit;

	MeasurementTypeEntity() {
	}

	/**
	 * Creates a measurement entity.
	 * 
	 * @param name
	 *            the name.
	 * @param intervalTarget
	 *            indicates if the target is an interval.
	 * @param unit
	 *            the unit.
	 * @return the entity.
	 */
	public static MeasurementTypeEntity newEntity(ActivityTypeEntity activityType, String name,
			MeasurementValueType valueType, MeasureUnit unit, final boolean alarmEnabled) {
		MeasurementTypeEntity entity = new MeasurementTypeEntity();
		entity.setActivityType(activityType);
		entity.setName(name);
		entity.setValueType(valueType);
		entity.setUnit(unit);
		entity.setAlarmEnabled(alarmEnabled);
		activityType.addActivityItemType(entity);
		return entity;
	}

	public MeasurementValueType getValueType() {
		return this.valueType;
	}

	void setValueType(final MeasurementValueType valueType) {
		this.valueType = valueType;
	}

	public MeasureUnit getUnit() {
		return unit;
	}

	void setUnit(MeasureUnit unit) {
		this.unit = unit;
	}

}
