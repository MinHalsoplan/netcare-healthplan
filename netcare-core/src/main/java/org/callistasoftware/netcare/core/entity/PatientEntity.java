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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="patient")
@PrimaryKeyJoinColumn(name="id")
public class PatientEntity extends UserEntity {

	@Column
	private String civicRegistrationNumber;
	
	@Column
	private boolean isMobile;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private CareGiverEntity primaryCareGiver;
	
	
	public static PatientEntity newEntity(final String name, final String civicRegistrationNumber) {
		return new PatientEntity(name, civicRegistrationNumber);
	}
	
	PatientEntity() {
		super();
	}
	
	PatientEntity(final String name, final String civicRegistrationNumber) {
		super(name);
		this.setCivicRegistrationNumber(civicRegistrationNumber);
	}

	public String getCivicRegistrationNumber() {
		return civicRegistrationNumber;
	}

	void setCivicRegistrationNumber(String civicRegistrationNumber) {
		this.civicRegistrationNumber = civicRegistrationNumber;
	}

	public boolean isMobile() {
		return isMobile;
	}

	void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}

	public void setPrimaryCareGiver(CareGiverEntity primaryCareGiver) {
		this.primaryCareGiver = primaryCareGiver;
	}

	public CareGiverEntity getPrimaryCareGiver() {
		return primaryCareGiver;
	}
}
