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

import java.util.List;
import java.util.Locale;

import org.callistasoftware.netcare.core.api.ActivityCategory;
import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.MeasurementType;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;

/**
 * Implementation of an activity type
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class ActivityTypeImpl implements ActivityType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private ActivityCategoryImpl category;
	private boolean measuringSense;
	private String minScaleText;
	private String maxScaleText;
	
	private MeasurementType[] measureValues;
	
	//
	public ActivityTypeImpl() {}
	
	public static ActivityTypeImpl newFromEntity(final ActivityTypeEntity entity, final Locale l) {
		final ActivityTypeImpl dto = new ActivityTypeImpl();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setCategory((ActivityCategoryImpl) ActivityCategoryImpl.newFromEntity(entity.getCategory()));
		dto.measuringSense = entity.isMeasuringSense();
		dto.setMinScaleText(entity.getSenseLabelLow());
		dto.setMaxScaleText(entity.getSenseLabelHigh());
		
		final MeasurementType[] values = new MeasurementTypeImpl[entity.getMeasurementTypes().size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = MeasurementTypeImpl.newFromEntity(entity.getMeasurementTypes().get(i));
		}
		
		dto.measureValues = values;
		
		return dto;
	}
	
	public static ActivityType[] newFromEntities(final List<ActivityTypeEntity> entities, final Locale l) {
		final ActivityType[] dtos = new ActivityTypeImpl[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = ActivityTypeImpl.newFromEntity(entities.get(i), l);
		}
		
		return dtos;
	}
	
	@Override
	public Long getId() {
		return this.id;
	}
	
	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public ActivityCategory getCategory() {
		return this.category;
	}
	
	public void setCategory(final ActivityCategoryImpl category) {
		this.category = category;
	}

	@Override
	public boolean isMeasuringSense() {
		return measuringSense;
	}

	@Override
	public String getMinScaleText() {
		return this.minScaleText;
	}
	
	public void setMinScaleText(final String minScaleText) {
		this.minScaleText = minScaleText;
	}

	@Override
	public String getMaxScaleText() {
		return this.maxScaleText;
	}
	
	public void setMaxScaleText(final String maxScaleText) {
		this.maxScaleText = maxScaleText;
	}

	@Override
	public MeasurementType[] getMeasureValues() {
		return this.measureValues;
	}
	
	public void setMeasureValues(final MeasurementTypeImpl[] measureValues) {
		this.measureValues = measureValues;
	}
	
	public void setMeasuringSense(boolean measuringSense) {
		this.measuringSense = measuringSense;
	}

	public void setMeasureValues(MeasurementType[] measureValues) {
		this.measureValues = measureValues;
	}
}
