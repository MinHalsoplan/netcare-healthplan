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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="care_giver_delegation")
public class CareGiverDelegationEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private CareGiverEntity careGiverDelegatee;
	
	@ManyToOne
	private CareGiverEntity careGiver;
	
	@Temporal(TemporalType.DATE)
	private Date fromDate;
	
	@Temporal(TemporalType.DATE)
	private Date toDate;
	
	
	CareGiverDelegationEntity() {
	}
	
	public static CareGiverDelegationEntity newEntity(CareGiverEntity careGiver, CareGiverEntity delegatee, Date from, Date to) {
		CareGiverDelegationEntity e = new CareGiverDelegationEntity();
		e.setCareGiver(careGiver);
		e.setCareGiverDelegatee(delegatee);
		e.setFromDate(from);
		e.setToDate(to);
		return e;
	}
	
	public Long getId() {
		return id;
	}

	public void setCareGiverDelegatee(CareGiverEntity careGiverDelegatee) {
		this.careGiverDelegatee = careGiverDelegatee;
	}

	public CareGiverEntity getCareGiverDelegatee() {
		return careGiverDelegatee;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setCareGiver(CareGiverEntity careGiver) {
		this.careGiver = careGiver;
	}

	public CareGiverEntity getCareGiver() {
		return careGiver;
	}
	
}
