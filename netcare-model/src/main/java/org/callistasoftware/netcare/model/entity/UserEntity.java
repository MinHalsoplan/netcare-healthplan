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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name="nc_user")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class UserEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="first_name", length=64, nullable=false)
	private String firstName;
	
	@Column(name="sur_name", length=64, nullable=false)
	private String surName;
	
	@Column(length=256)
	private String email;
	
	@ElementCollection(fetch=FetchType.LAZY)
    @Column(name="value")
    @MapKeyColumn(name="name")
    @CollectionTable(name = "nc_user_properties", joinColumns = {@JoinColumn(name="user_id")})
	private Map<String, String> properties;
	
	UserEntity() {
	}
	
	UserEntity(final String firstName, final String surName) {
		this.setFirstName(firstName);
		this.setSurName(surName);
		this.properties = new HashMap<String, String>();
	}
	
	public Long getId() {
		return this.id;
	}
	
	void setId(final Long id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public void setFirstName(final String name) {
		this.firstName = name;
	}
	
	public String getSurName() {
		return this.surName;
	}
	
	public void setSurName(final String surName) {
		this.surName = surName;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(final String email) {
		this.email = email;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof UserEntity) && equals((UserEntity)o);
	}
	
	private boolean equals(UserEntity o) {
		return (this == o) || o.getId().equals(id); 
	}
	
	public abstract boolean isCareGiver();
	
	public abstract String getUsername();
}
