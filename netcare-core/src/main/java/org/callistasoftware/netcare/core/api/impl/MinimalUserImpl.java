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

import org.callistasoftware.netcare.core.api.MinimalUser;

public class MinimalUserImpl implements MinimalUser {

	private Long id;
	private String username;
	private String name;
	private String email;
	private boolean mobile;
	private String pinCode;
	private boolean careGiver;
	private boolean patient;
	
	public MinimalUserImpl() {
		
	}
	
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	void setName(final String name) {
		this.name = name;
	}

	@Override
	public boolean isCareGiver() {
		return this.careGiver;
	}

	@Override
	public boolean isPatient() {
		return this.patient;
	}

	@Override
	public String getEmail() {
		return this.email;
	}
	
	void setEmail(final String email) {
		this.email = email;
	}

	@Override
	public boolean isMobile() {
		return this.mobile;
	}
	
	void setMobile(final boolean isMobile) {
		this.mobile = isMobile;
	}

	@Override
	public String getPinCode() {
		return this.pinCode;
	}
	
	void setPinCode(final String pinCode) {
		this.pinCode = pinCode;
	}
}
