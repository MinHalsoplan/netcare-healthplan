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

import org.callistasoftware.netcare.mvk.authentication.service.api.AuthenticationRequest;

public class AuthenticationRequestImpl implements AuthenticationRequest {

	private final String authenticationToken;
	
	AuthenticationRequestImpl(final String token) {
		this.authenticationToken = token;
	}
	
	public static AuthenticationRequest newAuthenticationRequest(final String token) {
		return new AuthenticationRequestImpl(token);
	}
	
	@Override
	public String getAuthenticationToken() {
		return this.authenticationToken;
	}

}
