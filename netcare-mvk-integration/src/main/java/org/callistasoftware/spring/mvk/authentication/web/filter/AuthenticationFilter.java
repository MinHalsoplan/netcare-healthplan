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
package org.callistasoftware.spring.mvk.authentication.web.filter;

import javax.servlet.http.HttpServletRequest;

import org.callistasoftware.spring.mvk.authentication.service.MvkAuthenticationService;
import org.callistasoftware.spring.mvk.authentication.service.api.impl.AuthenticationRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class AuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
	
	private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	@Value("${mvk.guidParamName}")
	private String guidParameterName;
	
	@Autowired
	private MvkAuthenticationService service;

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return "n/a";
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		log.info("Getting preauthenticated principal");
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			log.debug("Authentication was null, check for guid parameter");
			final String guid = request.getParameter(this.guidParameterName);
			if (guid != null) {
				log.debug("Guid parameter found. Validate it against MVK...");
				return this.service.authenticate(AuthenticationRequestImpl.newAuthenticationRequest(guid));
			}
		} else {
			log.debug("Authentication found. Proceed...");
			return auth.getPrincipal();
		}
		
		log.warn("Reached end of processing and still no authentication. Return null...");
		return null;
	}

}
