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

import org.callistasoftware.netcare.core.api.PatientBaseView;

/**
 * Implementation of a patient base view
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class PatientBaseViewImpl implements PatientBaseView {

	private Long id;
	private String name;
	private String civicRegistrationNumber;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Long getId() {
		return this.id;
	}
	
	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
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

}
