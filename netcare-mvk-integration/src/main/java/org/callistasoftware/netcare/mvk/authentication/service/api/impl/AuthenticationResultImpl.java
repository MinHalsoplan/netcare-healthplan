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
package org.callistasoftware.netcare.mvk.authentication.service.api.impl;

import mvk.asb.interaction.validateidresponder._1._0.ValidateIdResponseType;

import org.callistasoftware.netcare.mvk.authentication.service.api.AuthenticationResult;

public class AuthenticationResultImpl implements AuthenticationResult {

	private final String username;
	
	private String careUnitHsaId;
	private String careUnitName;
	
	private final boolean caregiver;
	
	AuthenticationResultImpl(final String username, final boolean careGiver) {
		this.username = username;
		this.caregiver = careGiver;
	}
	
	AuthenticationResultImpl(final ValidateIdResponseType response) {
		this(response.getSsoobject().getUserId(), response.getSsoobject().getUserType().equals("VA"));
		
		if (isCareGiver()) {
			this.careUnitHsaId = response.getSsoobject().getHealthcareFacility().getHealthcareFacilityId();
			this.careUnitName = response.getSsoobject().getHealthcareFacility().getHealthcareFacilityName();
		} else {
			this.careUnitHsaId = null;
			this.careUnitName = null;
		}
	}
	
	public static AuthenticationResult createFromResponse(final ValidateIdResponseType response) {
		return new AuthenticationResultImpl(response);
	}
	
	public static AuthenticationResult newPatient(final String civicRegistrationNumber) {
		return new AuthenticationResultImpl(civicRegistrationNumber, false);
	}
	
	public static AuthenticationResult newCareGiver(final String hsaId, final String careUnitHsaId, final String careUnitName) {
		final AuthenticationResultImpl dto = new AuthenticationResultImpl(hsaId, true);
		dto.setCareUnitHsaId(careUnitHsaId);
		dto.setCareUnitName(careUnitName);
		
		return dto;
	}

	@Override
	public boolean isCareGiver() {
		return this.caregiver;
	}

	@Override
	public String getCareUnitHsaId() {
		return this.careUnitHsaId;
	}
	
	void setCareUnitHsaId(final String careUnitHsaId) {
		this.careUnitHsaId = careUnitHsaId;
	}

	@Override
	public String getCareUnitName() {
		return this.careUnitName;
	}
	
	void setCareUnitName(final String careUnitName) {
		this.careUnitName = careUnitName;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

}
