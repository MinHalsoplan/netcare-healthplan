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

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nc_pdl_log")
public class PdlLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "date", nullable = false)
	private Timestamp date;

	@Column(name = "hsa_id", length = 64, nullable = false)
	private String hsaId;

	@Column(name = "care_actor_name", length = 64, nullable = false)
	private String careActorName;

	@Column(name = "civic_id", length = 20, nullable = false)
	private String civicId;

	@Column(name = "patient_name", length = 20, nullable = false)
	private String patientName;

	@Column(name = "action", length = 50, nullable = false)
	private String action;

	PdlLogEntity() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getHsaId() {
		return hsaId;
	}

	public void setHsaId(String hsaId) {
		this.hsaId = hsaId;
	}

	public String getCareActorName() {
		return careActorName;
	}

	public void setCareActorName(String careActorName) {
		this.careActorName = careActorName;
	}

	public String getCivicId() {
		return civicId;
	}

	public void setCivicId(String civicId) {
		this.civicId = civicId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof PdlLogEntity) && equals((PdlLogEntity) o);
	}

	private boolean equals(PdlLogEntity o) {
		return (this == o) || o.getId().equals(id);
	}

	public static PdlLogEntity newEntity(String action, String careActorName, String civicId, String hsaId,
			String patientName) {
		PdlLogEntity pdlLogEntity = new PdlLogEntity();

		pdlLogEntity.date = new Timestamp(System.currentTimeMillis());
		pdlLogEntity.hsaId = hsaId;
		pdlLogEntity.careActorName = careActorName;
		pdlLogEntity.civicId = civicId;
		pdlLogEntity.patientName = patientName;
		pdlLogEntity.action = action;
		
		return pdlLogEntity;
		
	
	}
}
