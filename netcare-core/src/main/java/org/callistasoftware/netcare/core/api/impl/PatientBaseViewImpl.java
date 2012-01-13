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

import java.util.Collection;
import java.util.List;

import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.springframework.security.core.GrantedAuthority;

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

	PatientBaseViewImpl() {
		super(null, null);
	}
	
	PatientBaseViewImpl(final Long id, final String name, final String civicRegistrationNumber) {
		super(id, name);
		this.setCivicRegistrationNumber(civicRegistrationNumber);
	}
	
	PatientBaseViewImpl(final PatientEntity entity) {
		super(entity.getId(), entity.getName());
		this.setCivicRegistrationNumber(entity.getCivicRegistrationNumber());
	}
	
	public static PatientBaseView newFromEntity(final PatientEntity entity) {
		return new PatientBaseViewImpl(entity);
	}
	
	public static PatientBaseView[] newFromEntities(final List<PatientEntity> entities) {
		final PatientBaseView[] dtos = new PatientBaseViewImpl[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = PatientBaseViewImpl.newFromEntity(entities.get(i));
		}
		
		return dtos;
	}
	
	void setId(final Long id) {
		this.id = id;
	}
	
	void setName(final String name) {
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
	
	void setCivicRegistrationNumber(final String civicRegistrationNumber) {
		this.civicRegistrationNumber = civicRegistrationNumber;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Role.getPatientRoleSet();
	}
}
