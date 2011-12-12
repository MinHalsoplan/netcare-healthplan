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
package org.callistasoftware.netcare.core.entity;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="nc_care_giver")
@PrimaryKeyJoinColumn(name="id")
public class CareGiverEntity extends UserEntity {

	@Column(length=64, nullable=false, unique=true)
	private String hsaId;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="careGiverDelegatee")
	private List<CareGiverDelegationEntity> careGiverDelegations;
	
	CareGiverEntity() {
	}
	
	CareGiverEntity(final String name) {
		super(name);
		careGiverDelegations = new LinkedList<CareGiverDelegationEntity>();
	}
	
	CareGiverEntity(final String name, final String hsaId) {
		this(name);
		this.setHsaId(hsaId);
	}

	public static CareGiverEntity newEntity(final String name, final String hsaId) {
		return new CareGiverEntity(name, hsaId);
	}

	public String getHsaId() {
		return hsaId;
	}

	void setHsaId(String hsaId) {
		this.hsaId = hsaId;
	}

	public List<CareGiverDelegationEntity> getCareGiverDelegations() {
		return careGiverDelegations;
	}
}
