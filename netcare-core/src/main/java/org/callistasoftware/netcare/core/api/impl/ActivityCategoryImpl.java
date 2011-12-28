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

import org.callistasoftware.netcare.core.api.ActivityCategory;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;

/**
 * Implementation of an activity category
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class ActivityCategoryImpl implements ActivityCategory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	
	public static ActivityCategory createNewDto(final String name) {
		final ActivityCategoryImpl impl = new ActivityCategoryImpl();
		impl.setName(name);
		
		return impl;
	}
	
	public static ActivityCategory newFromEntity(final ActivityCategoryEntity entity) {
		final ActivityCategoryImpl impl = new ActivityCategoryImpl();
		impl.setId(entity.getId());
		impl.setName(entity.getName());
		
		return impl;
	}
	
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
