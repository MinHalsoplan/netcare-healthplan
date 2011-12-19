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
package org.callistasoftware.netcare.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="nc_care_unit")
public class CareUnitEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(length=64, unique=true, nullable=false)
	private String hsaId;
	
	@Column(length=64)
	private String name;
	
	CareUnitEntity() {
	}
	
	CareUnitEntity(final String hsaId) {
		this();
		this.setHsaId(hsaId);
	}

	public static CareUnitEntity newEntity(final String hsaId) {
		return new CareUnitEntity(hsaId);
	}
	
	public Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}

	public String getHsaId() {
		return hsaId;
	}

	void setHsaId(String hsaId) {
		this.hsaId = hsaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
