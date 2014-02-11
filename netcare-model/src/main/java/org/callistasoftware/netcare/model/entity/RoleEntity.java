/**
 * Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>
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
@Table(name="nc_role")
public class RoleEntity {
	
	public static final String CARE_ACTOR = "CARE_ACTOR";
	public static final String COUNTY_COUNCIL_ADMINISTRATOR = "COUNTY_COUNCIL_ADMINISTRATOR";
	public static final String PATIENT = "PATIENT";
	public static final String NATION_ADMINISTRATOR = "NATION_ADMINISTRATOR";
	public static final String SYSTEM_ADMINISTRATOR = "SYSTEM_ADMINISTRATOR";

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(unique=true, nullable=false)
	private String dn;
	
	RoleEntity() {}
	
	RoleEntity(final String dn) {
		this.dn = dn;
	}

	public static RoleEntity newRole(final String dn) {
		return new RoleEntity(dn);
	}
	
	public Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}

	public String getDn() {
		return dn;
	}

	void setDn(String dn) {
		this.dn = dn;
	}
}
