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
package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.Measurement;
import org.callistasoftware.netcare.core.api.MeasurementDefinition;
import org.callistasoftware.netcare.model.entity.MeasurementEntity;

public class MeasurementImpl implements Measurement {

	private Long id;
	private MeasurementDefinition measurementDefinition;
	private float target;
	private float maxTarget;
	private float minTarget;
	private float reportedValue;
	
	public static Measurement newFromEntity(MeasurementEntity entity) {
		MeasurementImpl m = new MeasurementImpl();
	
		m.id = entity.getId();
		m.measurementDefinition = MeasurementDefinitionImpl.newFromEntity(entity.getMeasurementDefinition());
		m.target = entity.getTarget();
		m.maxTarget = entity.getMaxTarget();
		m.minTarget = entity.getMinTarget();
		m.reportedValue = entity.getReportedValue();
		
		return m;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	public void setMeasurementDefinition(MeasurementDefinition measurementDefinition) {
		this.measurementDefinition = measurementDefinition;
	}

	public void setTarget(float target) {
		this.target = target;
	}

	public void setMaxTarget(float maxTarget) {
		this.maxTarget = maxTarget;
	}

	public void setMinTarget(float minTarget) {
		this.minTarget = minTarget;
	}

	public void setReportedValue(float reportedValue) {
		this.reportedValue = reportedValue;
	}

	@Override
	public MeasurementDefinition getMeasurementDefinition() {
		return measurementDefinition;
	}

	@Override
	public float getTarget() {
		return target;
	}

	@Override
	public float getMinTarget() {
		return minTarget;
	}

	@Override
	public float getMaxTarget() {
		return maxTarget;
	}

	@Override
	public float getReportedValue() {
		return reportedValue;
	}

}
