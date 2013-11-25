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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "nc_care_unit")
public class CareUnitEntity implements PermissionRestrictedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "hsa_id", length = 64, unique = true, nullable = false)
	private String hsaId;

	@Column(length = 64)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "county_council_id")
	private CountyCouncilEntity countyCouncil;

	CareUnitEntity() {
	}

	CareUnitEntity(final String hsaId, CountyCouncilEntity countyCouncil) {
		this();
		this.setHsaId(hsaId);
		this.setCountyCouncil(countyCouncil);
	}

	public static CareUnitEntity newEntity(final String hsaId, CountyCouncilEntity countyCouncil) {
		return new CareUnitEntity(hsaId, countyCouncil);
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

	public void setHsaId(String hsaId) {
		this.hsaId = hsaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CountyCouncilEntity getCountyCouncil() {
		return countyCouncil;
	}

	public void setCountyCouncil(CountyCouncilEntity countyCouncil) {
		this.countyCouncil = countyCouncil;
	}

	@Override
	public boolean isReadAllowed(UserEntity user) {
		if (user instanceof CareActorEntity) {
			final CareActorEntity ca = (CareActorEntity) user;
			if (ca.getCareUnit().getId().equals(this.getId())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isWriteAllowed(UserEntity user) {
		return this.isReadAllowed(user);
	}
}
