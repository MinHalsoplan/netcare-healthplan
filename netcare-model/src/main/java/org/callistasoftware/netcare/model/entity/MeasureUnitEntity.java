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
import javax.persistence.Table;

@Entity
@Table(name="nc_measure_unit")
public class MeasureUnitEntity implements PermissionRestrictedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private String dn;
	
	@Column(nullable=false)
	private String name;
	
	public static MeasureUnitEntity newEntity(final String name, final String dn) {
		final MeasureUnitEntity mue = new MeasureUnitEntity();
		mue.setDn(dn);
		mue.setName(name);
		return mue;
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
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDn() {
		return dn;
	}
	
	public void setDn(String dn) {
		this.dn = dn;
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
