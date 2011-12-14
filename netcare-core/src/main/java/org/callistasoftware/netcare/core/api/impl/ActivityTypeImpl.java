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
package org.callistasoftware.netcare.core.api.impl;

import java.util.Locale;

import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.entity.ActivityTypeEntity;

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
	private Option unit;
	
	public static ActivityTypeImpl newFromEntity(final ActivityTypeEntity entity, final Locale l) {
		final ActivityTypeImpl dto = new ActivityTypeImpl();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setUnit(new Option(entity.getUnit().name(), l));
		
		return dto;
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
	public Option getUnit() {
		return this.unit;
	}
	
	public void setUnit(final Option unit) {
		this.unit = unit;
	}
}
