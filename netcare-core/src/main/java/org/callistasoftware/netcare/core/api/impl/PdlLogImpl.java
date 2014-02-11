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
package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.PdlLog;
import org.callistasoftware.netcare.model.entity.PdlLogEntity;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PdlLogImpl implements PdlLog {

	private static final long serialVersionUID = 1L;

	private long id;
	private String hsaId;
	private String careActorName;
	private String civicId;
	private String patientName;
	private String action;
	private String healtPlanName;

	public static PdlLog newFromEntity(PdlLogEntity entity) {
		
		PdlLogImpl pdlLog = new PdlLogImpl();

		pdlLog.id = entity.getId();
		pdlLog.action = entity.getAction();
		pdlLog.careActorName = entity.getCareActorName();
		pdlLog.civicId = entity.getCivicId();
		pdlLog.hsaId = entity.getHsaId();
		pdlLog.patientName = entity.getPatientName();
		pdlLog.healtPlanName = entity.getHealtPlanName();

		return pdlLog;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getHealtPlanName() {
		return healtPlanName;
	}

	public void setHealtPlanName(String healtPlanName) {
		this.healtPlanName = healtPlanName;
	}


}
