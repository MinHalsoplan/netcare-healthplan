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
package org.callistasoftware.netcare.core.api.impl;

import java.util.Collection;
import java.util.Collections;

import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

/**
 * Implementation of a patient base view
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class PatientBaseViewImpl extends UserBaseViewImpl implements PatientBaseView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String civicRegistrationNumber;

	public PatientBaseViewImpl() {
		super(null, null);
	}
	
	public PatientBaseViewImpl(final Long id, final String name) {
		super(id, name);
	}
	
	public void setId(final Long id) {
		this.id = id;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public boolean isCareGiver() {
		return false;
	}

	@Override
	public String getCivicRegistrationNumber() {
		return this.civicRegistrationNumber;
	}
	
	public void setCivicRegistrationNumber(final String civicRegistrationNumber) {
		this.civicRegistrationNumber = civicRegistrationNumber;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new GrantedAuthorityImpl("ROLE_USER"));
	}
}