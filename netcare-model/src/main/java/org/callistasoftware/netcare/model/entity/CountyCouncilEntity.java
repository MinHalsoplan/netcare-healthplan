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
@Table(name = "nc_county_council")
public class CountyCouncilEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name="meta")
	private Integer meta;

	public CountyCouncilEntity() {
	}

	public CountyCouncilEntity(CountyCouncil metaData) {
		this.meta = metaData.getCode();
	}

	public Long getId() {
		return id;
	}

	public CountyCouncil getMeta() {
		return CountyCouncil.fromCode(this.meta);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public static CountyCouncilEntity newEntity(CountyCouncil meta) {
		return new CountyCouncilEntity(meta);
	}

}
