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

import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

/**
 * Implementation of a care giver base view
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class CareGiverBaseViewImpl extends UserBaseViewImpl implements CareGiverBaseView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String hsaId;
	
	public CareGiverBaseViewImpl() {
		super(null, null);
	}
	
	public CareGiverBaseViewImpl(final Long id, final String name) {
		super(id, name);
	}
	
	@Override
	public String getHsaId() {
		return this.hsaId;
	}
	
	public void setHsaId(final String hsaId) {
		this.hsaId = hsaId;
	}

	@Override
	public boolean isCareGiver() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new GrantedAuthorityImpl("ROLE_ADMIN"));
	}

}
