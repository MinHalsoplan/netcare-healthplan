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
package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.PatientProfile;

public class PatientProfileImpl implements PatientProfile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String firstname;
	private String surname;
	private String email;
	private String phone;
	private boolean mobile;
	private String password;
	private String password2;
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	@Override
	public String getFirstname() {
		return this.firstname;
	}

	@Override
	public String getSurname() {
		return this.surname;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public String getPhone() {
		return this.phone;
	}

	@Override
	public boolean isMobile() {
		return this.mobile;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getPassword2() {
		return this.password2;
	}

}
