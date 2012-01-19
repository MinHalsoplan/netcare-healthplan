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

import java.util.List;

import org.callistasoftware.netcare.core.api.Patient;
import org.callistasoftware.netcare.model.entity.PatientEntity;

public class PatientImpl extends PatientBaseViewImpl implements Patient {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String phoneNumber;
	private boolean mobile;
	
	private String password;
	private String password2;
	
	private String email;
	
	PatientImpl() {
		super();
	}
	
	PatientImpl(final PatientEntity entity) {
		super(entity);
		
		this.setPhoneNumber(entity.getPhoneNumber());
		this.setMobile(entity.isMobile());
		this.setEmail(entity.getEmail());
	}
	
	public static Patient newFromEntity(final PatientEntity entity) {
		return new PatientImpl(entity);
	}
	
	public static Patient[] newFromEntities(final List<PatientEntity> entities) {
		final Patient[] dtos = new Patient[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = PatientImpl.newFromEntity(entities.get(i));
		}
		
		return dtos;
	}
	
	@Override
	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public boolean isMobile() {
		return this.mobile;
	}
	
	public void setMobile(final boolean mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	@Override
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(final String email) {
		this.email = email;
	}
}
