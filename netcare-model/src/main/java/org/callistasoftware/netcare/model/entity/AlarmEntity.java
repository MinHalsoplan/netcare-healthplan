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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="nc_alarm")
public class AlarmEntity implements PermissionRestrictedEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name="created_time", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdTime;
	
	@Column(name="cause", nullable=false)
	private AlarmCause cause;
	
	@Column(name="care_unit_hsaid", nullable=false)
	private String careUnitHsaId;
	
	@Column(name="ref_entity_id", nullable=false)
	private Long refEntityId;
		
	@Column(name="resolved_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date resolvedTime;
	
	@ManyToOne
	@JoinColumn(name="resolved_by_id")
	private CareGiverEntity resolvedBy;
	
	@ManyToOne
	@JoinColumn(name="patient_id")
	private PatientEntity patient;


	AlarmEntity() {
		createdTime = new Date();
	}
	
	public static AlarmEntity newEntity(AlarmCause cause, PatientEntity patient, String careUnitHsaId, Long refEntityId) {
		AlarmEntity entity = new AlarmEntity();
		entity.cause = cause;
		entity.careUnitHsaId = careUnitHsaId;
		entity.refEntityId = refEntityId;
		entity.patient = patient;
		return entity;
	}
	
	public Long getId() {
		return id;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public AlarmCause getCause() {
		return cause;
	}

	public String getCareUnitHsaId() {
		return careUnitHsaId;
	}

	public Long getRefEntityId() {
		return refEntityId;
	}

	public void setResolvedTime(Date resolvedTime) {
		if (resolvedTime == null) {
			throw new IllegalArgumentException("Inavlid time (null)");
		}
		this.resolvedTime = resolvedTime;
	}

	public Date getResolvedTime() {
		return resolvedTime;
	}

	
	public PatientEntity getPatient() {
		return patient;
	}

	public void setResolvedBy(CareGiverEntity resolvedBy) {
		this.resolvedBy = resolvedBy;
	}

	public CareGiverEntity getResolvedBy() {
		return resolvedBy;
	}

	/**
	 * Convenience method to resolve and set timestamp.
	 * 
	 * @param resolvedBy the user  resolving this alarm.
	 */
	public void resolve(CareGiverEntity resolvedBy) {
		setResolvedBy(resolvedBy);
		setResolvedTime(new Date());
	}

	@Override
	public boolean isReadAllowed(UserEntity userId) {
		return this.isWriteAllowed(userId);
	}

	@Override
	public boolean isWriteAllowed(UserEntity userId) {
		return userId.isCareGiver() && this.getPatient().isWriteAllowed(userId);
	}

}
