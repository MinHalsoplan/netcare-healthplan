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
package org.callistasoftware.netcare.web.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Authentication manager used from the BasicAuthenticationFilter. An instance of this
 * class is passed to the filter in the netcare-security.xml
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 */
public class MobileAuthenticationManager extends ProviderManager {

	private static Logger log = LoggerFactory.getLogger(MobileAuthenticationManager.class);
	
	public MobileAuthenticationManager(final List<AuthenticationProvider> providers) {
		super(providers);
	}
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		log.info("==== MOBILE AUTHENTICATION ====");
		final Authentication authenticate = super.authenticate(authentication);
		log.info("===============================");
		
		return authenticate;
	}
}
