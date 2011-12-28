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

import org.springframework.security.core.GrantedAuthority;

/**
 * Implements {@link GrantedAuthority}
 * 
 * @author Peter
 *
 */
public class Role implements GrantedAuthority {
	/**
	 * User role.
	 */
	public static Collection<? extends GrantedAuthority> ROLE_USER = Collections.singleton(new Role("ROLE_USER"));
	/**
	 * Admin role.
	 */
	public static Collection<? extends GrantedAuthority> ROLE_ADMIN = Collections.singleton(new Role("ROLE_ADMIN"));
	
	
	private static final long serialVersionUID = 1L;
	private String authority;

	private Role() {}

	/**
	 * Creates a {@link GrantedAuthority}
	 * 
	 * @param authority the role name.
	 */
	private Role(String authority) {
		this.authority = authority;
	}
	
	@Override
	public String getAuthority() {
		return authority;
	}
}
