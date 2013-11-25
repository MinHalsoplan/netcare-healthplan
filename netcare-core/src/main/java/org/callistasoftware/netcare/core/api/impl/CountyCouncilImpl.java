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
package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.CountyCouncil;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;

public class CountyCouncilImpl implements CountyCouncil {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String name;

	CountyCouncilImpl() {
	}
	
	public static CountyCouncil newFromEntity(final CountyCouncilEntity cc) {
		
		final CountyCouncilImpl impl = new CountyCouncilImpl();
		impl.setCode(String.valueOf(cc.getMeta().getCode()));
		impl.setName(cc.getMeta().getName());
		
		return impl;
	}
	
	@Override
	public String getCode() {
		return this.code;
	}
	
	public void setCode(final String code) {
		this.code = code;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

}
