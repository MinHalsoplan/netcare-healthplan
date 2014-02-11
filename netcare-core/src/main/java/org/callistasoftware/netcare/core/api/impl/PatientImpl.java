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

import java.util.List;

import org.callistasoftware.netcare.core.api.Patient;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientImpl extends PatientBaseViewImpl implements Patient {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String phoneNumber;
	private String email;
	
	public PatientImpl() {
		super();
	}
	
	PatientImpl(final PatientEntity entity) {
		super(entity);
		
		this.setPhoneNumber(entity.getPhoneNumber());
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
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}
