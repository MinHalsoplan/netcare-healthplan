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

import org.callistasoftware.netcare.core.api.MeasureUnit;
import org.callistasoftware.netcare.model.entity.MeasureUnitEntity;

public class MeasureUnitImpl implements MeasureUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String dn;
	private String name;
	
	public MeasureUnitImpl() {
		this.id = -1L;
	}
	
	public MeasureUnitImpl(final MeasureUnitEntity entity) {
		this.id = entity.getId();
		this.dn = entity.getDn();
		this.name = entity.getName();
	}
	
	public static MeasureUnit newFromEntity(final MeasureUnitEntity entity) {
		return new MeasureUnitImpl(entity);
	}
	
	public static MeasureUnit[] newFromEntities(final List<MeasureUnitEntity> entities) {
		final MeasureUnit[] units = new MeasureUnit[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			units[i] = newFromEntity(entities.get(i));
		}
		
		return units;
	}
	
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDn() {
		return this.dn;
	}

}
