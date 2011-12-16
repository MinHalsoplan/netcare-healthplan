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

import org.callistasoftware.netcare.core.api.CareUnit;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;

public class CareUnitImpl implements CareUnit {

	private String hsaId;
	private String name;
	
	CareUnitImpl(final String hsaId, final String name) {
		this.setHsaId(hsaId);
		this.setName(name);
	}
	
	public static CareUnit newFromEntity(final CareUnitEntity entity) {
		return new CareUnitImpl(entity.getHsaId(), entity.getName());
	}
	
	@Override
	public String getHsaId() {
		return this.hsaId;
	}
	
	public void setHsaId(final String hsaId) {
		this.hsaId = hsaId;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

}
