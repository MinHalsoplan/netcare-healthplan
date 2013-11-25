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
package org.callistasoftware.netcare.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="nc_activity_category")
public class ActivityCategoryEntity implements PermissionRestrictedEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(length=64, nullable=false, unique=true)
	private String name;
	
	ActivityCategoryEntity() {
	}
	
	ActivityCategoryEntity(final String name) {
		this();
		this.setName(name);
	}
	
	public static ActivityCategoryEntity newEntity(final String name) {
		return new ActivityCategoryEntity(name);
	}
	
	public Long getId() {
		return id;
	}
	
	void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	void setName(String name) {
		if (name == null || name.trim().equals("")) {
			throw new IllegalArgumentException("Name must not be null or empty.");
		}
		this.name = name;
	}

	@Override
	public boolean isReadAllowed(UserEntity user) {
		return true;
	}

	@Override
	public boolean isWriteAllowed(UserEntity user) {
		if (user.isCareActor()) {
			final CareActorEntity ca = (CareActorEntity) user;
			for (final RoleEntity r : ca.getRoles()) {
				if (r.getDn().equals(RoleEntity.NATION_ADMINISTRATOR)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
