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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="nc_care_giver")
@PrimaryKeyJoinColumn(name="id")
public class CareActorEntity extends UserEntity {

	@Column(name="hsa_id", length=64, nullable=false, unique=true)
	private String hsaId;
	
	@ManyToMany
	private Set<RoleEntity> roles;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="care_unit_id")
	private CareUnitEntity careUnit;
	
	CareActorEntity() {
		this.roles = new HashSet<RoleEntity>();
	}
	
	CareActorEntity(final String firstName, final String surName) {
		super(firstName, surName);
		this.roles = new HashSet<RoleEntity>();
	}
	
	CareActorEntity(final String firstName, final String surName, final String hsaId, final CareUnitEntity careUnit) {
		this(firstName, surName);
		this.setHsaId(hsaId);
		this.setCareUnit(careUnit);
		this.roles = new HashSet<RoleEntity>();
	}

	public static CareActorEntity newEntity(final String firstName, final String surName, final String hsaId, final CareUnitEntity careUnit) {
		return new CareActorEntity(firstName, surName, hsaId, careUnit);
	}

	public String getHsaId() {
		return hsaId;
	}

	void setHsaId(String hsaId) {
		this.hsaId = EntityUtil.notNull(hsaId);
	}

	public CareUnitEntity getCareUnit() {
		return this.careUnit;
	}
	
	public void setCareUnit(final CareUnitEntity careUnit) {
		this.careUnit = careUnit;
	}
	
	public Set<RoleEntity> getRoles() {
		return roles;
	}
	
	void setRoles(Set<RoleEntity> roles) {
		this.roles = roles;
	}
	
	public void addRole(final RoleEntity role) {
		this.roles.add(role);
	}
	
	public void removeRole(final RoleEntity role) {
		this.roles.remove(role);
	}

	@Override
	public boolean isCareActor() {
		return true;
	}

	@Override
	public String getUsername() {
		return this.getHsaId();
	}
	
	public boolean hasRole(final String role) {
		for (final RoleEntity r : getRoles()) {
			if (r.getDn().equals(role)) {
				return true;
			}
		}
		
		return false;
	}
}
