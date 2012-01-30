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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Keeps a reported measurement value. <p>
 * 
 * When the value is reported the current target settings is copied from the definition in question, this behavior supports
 * definitions changes during the life-time of an activity.
 * 
 * @author Peter
 */
@Entity
@Table(name="nc_measurement")
public class MeasurementEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional=false)
	@JoinColumn(name="scheduled_activity_id")
	private ScheduledActivityEntity scheduledActivity;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="measurement_definition_id")
	private MeasurementDefinitionEntity measurementDefinition;

	@Column(name="target_value")
	private int targetValue;
	
	@Column(name="min_target")
	private int minTarget;
	
	@Column(name="max_target")
	private int maxTarget;

	@Column(name="reported_value")
	private int reportedValue;


	MeasurementEntity() {
	}

	static MeasurementEntity newEntity(ScheduledActivityEntity scheduledActivity, MeasurementDefinitionEntity measurementDefinition) {
		MeasurementEntity entity = new MeasurementEntity();
		
		entity.setScheduledActivity(scheduledActivity);
		entity.setMeasurementDefinition(measurementDefinition);
				
		return entity;
	}
	
	public Long getId() {
		return id;
	}

	public ScheduledActivityEntity getScheduledActivity() {
		return scheduledActivity;
	}

	void setScheduledActivity(ScheduledActivityEntity scheduledActivity) {
		this.scheduledActivity = scheduledActivity;
	}

	public MeasurementDefinitionEntity getMeasurementDefinition() {
		return measurementDefinition;
	}

	void setMeasurementDefinition(MeasurementDefinitionEntity measurementDefinition) {
		this.measurementDefinition = measurementDefinition;
	}

	public int getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(int targetValue) {
		this.targetValue = targetValue;
	}

	public int getMinTarget() {
		return minTarget;
	}

	public void setMinTarget(int minTarget) {
		this.minTarget = minTarget;
	}

	public int getMaxTarget() {
		return maxTarget;
	}

	public void setMaxTarget(int maxTarget) {
		this.maxTarget = maxTarget;
	}

	public int getReportedValue() {
		return reportedValue;
	}

	/**
	 * Copy target values from definition.
	 */
	private void copyActualTargets() {
		// copy target values
		if (getMeasurementDefinition().getMeasurementType().equals(MeasurementValueType.INTERVAL)) {
			setMinTarget(measurementDefinition.getMinTarget());
			setMaxTarget(measurementDefinition.getMaxTarget());
		} else {
			setTargetValue(measurementDefinition.getTarget());
		}		
	}

	public void setReportedValue(int reportedValue) {
		copyActualTargets();
		this.reportedValue = reportedValue;
	}
}

