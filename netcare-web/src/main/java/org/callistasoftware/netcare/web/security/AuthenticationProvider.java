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
package org.callistasoftware.netcare.web.security;

import org.callistasoftware.netcare.core.spi.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationProvider.class);
	
	@Autowired
	private UserDetailsService service;
	
	@Override
	public Authentication authenticate(final Authentication auth)
			throws AuthenticationException {
		log.info("Authenticating {}", auth.getName());
		try {
			final UserDetails details = this.service.loadUserByUsername(auth.getName());
			return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
		} catch (final UsernameNotFoundException e) {
			
		}
		
		throw new AuthenticationServiceException("Bad credentials");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

}
