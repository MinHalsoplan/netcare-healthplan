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
@Table(name="nc_measurement_type")
public class MeasurementTypeEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="activity_type_id")
	private ActivityTypeEntity activityType;
	
	@Column(name="name", length=32)
	private String name;
	
	@Column(name="value_type", nullable=false)
	private MeasurementValueType valueType;
	
	@Column(name="alarm_enabled", length=1)
	private String alarmEnabled;
	
	@Column(name="unit", nullable=false)
	private MeasureUnit unit;

	MeasurementTypeEntity() {
	}
	
	/**
	 * Creates a measurement entity.
	 * 
	 * @param name the name.
	 * @param intervalTarget indicates if the target is an interval.
s	 * @param unit the unit.
	 * @return the entity.
	 */
	public static MeasurementTypeEntity newEntity(ActivityTypeEntity activityType, String name, MeasurementValueType valueType, MeasureUnit unit) {
		MeasurementTypeEntity entity = new MeasurementTypeEntity();
		entity.setActivityType(activityType);
		entity.setName(name);
		entity.setValueType(valueType);
		entity.setUnit(unit);
		activityType.addMeasurementType(entity);
		return entity;
	}
	
	public Long getId() {
		return id;
	}

	public ActivityTypeEntity getActivityType() {
		return activityType;
	}

	void setActivityType(ActivityTypeEntity activityType) {
		this.activityType = activityType;
	}

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	public MeasurementValueType getValueType() {
		return this.valueType;
	}

	void setValueType(final MeasurementValueType valueType) {
		this.valueType = valueType;
	}

	public boolean isAlarmEnabled() {
		return "Y".equals(alarmEnabled);
	}

	public void setAlarmEnabled(boolean alarmEnabled) {
		this.alarmEnabled = alarmEnabled ? "Y" : null;
	}

	public MeasureUnit getUnit() {
		return unit;
	}

	void setUnit(MeasureUnit unit) {
		this.unit = unit;
	}

}
