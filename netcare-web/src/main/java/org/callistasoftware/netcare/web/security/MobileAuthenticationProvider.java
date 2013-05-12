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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Authentication provider that checks username/passwords. This provider is
 * used in the scenario when the user has attempteed to access the /netcare/mobile
 * url.
 * 
 * This provider is used from {@link MobileAuthenticationManager}.
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class MobileAuthenticationProvider extends DaoAuthenticationProvider {

    private static Logger log = LoggerFactory.getLogger(MobileAuthenticationProvider.class);

	@Autowired
	@Override
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		super.setUserDetailsService(userDetailsService);
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
        log.info("==== Mobile Authentication [DEV MODE] ====");
        log.info("User: {}", authentication.getName());

        final UserDetails details = getUserDetailsService().loadUserByUsername(authentication.getName());
        log.info("User found: {}", details != null);
        log.info("==========================================");

        return new UsernamePasswordAuthenticationToken(details, "", details.getAuthorities());
	}
}
