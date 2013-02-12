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
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("measurement")
public class MeasurementTypeEntity extends ActivityItemTypeEntity {

	@Column(name = "value_type")
	private MeasurementValueType valueType;

	@ManyToOne
	private MeasureUnitEntity unit;

	@Column(name = "alarm_enabled")
	private boolean alarmEnabled;

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
			MeasurementValueType valueType, MeasureUnitEntity unit, final boolean alarmEnabled, int seqno) {
		MeasurementTypeEntity entity = new MeasurementTypeEntity();
		entity.setActivityType(activityType);
		entity.setName(name);
		entity.setValueType(valueType);
		entity.setUnit(unit);
		entity.setAlarmEnabled(alarmEnabled);
		entity.setSeqno(seqno);
		activityType.addActivityItemType(entity);
		return entity;
	}

	public MeasurementValueType getValueType() {
		return this.valueType;
	}

	public void setValueType(final MeasurementValueType valueType) {
		this.valueType = valueType;
	}

	public MeasureUnitEntity getUnit() {
		return unit;
	}

	public void setUnit(MeasureUnitEntity unit) {
		this.unit = unit;
	}

	public boolean isAlarmEnabled() {
		return alarmEnabled;
	}

	public void setAlarmEnabled(boolean alarmEnabled) {
		this.alarmEnabled = alarmEnabled;
	}
}
