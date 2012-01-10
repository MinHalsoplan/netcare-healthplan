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
package org.callistasoftware.netcare.core.spi.impl;

import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.repository.UserRepository;
import org.callistasoftware.netcare.model.entity.PermissionRestrictedEntity;
import org.callistasoftware.netcare.model.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class ServiceSupport {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserRepository repo;
	
	public void setSecurityContext(final SecurityContext sc) {
		SecurityContextHolder.setContext(sc);
	}
	
	/**
	 * Get the current logged in user
	 * @return
	 */
	protected UserEntity getCurrentUser() {
		this.log.debug("Get current logged in user...");
		final UserBaseView user = (UserBaseView) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user != null) {
			this.log.debug("Logged in user found. Fetch from repository {}", user.getUsername());
			final UserEntity u = this.repo.findOne(user.getId());
			if (u == null) {
				throw new IllegalStateException("User with username: " + user.getUsername() + " claimed to be logged in. But the user was not found in persistent storage.");
			} else {
				return u;
			}
		}
		
		this.log.debug("No currently logged in user. Return null");
		return null;
	}
	
	protected void verifyWriteAccess(final PermissionRestrictedEntity object) {
		final UserEntity user = this.getCurrentUser();
		if (user != null) {
			if (!object.isWriteAllowed(user)) {
				throw new SecurityException("Not allowed");
			}
		} else {
			throw new IllegalAccessError("Anonymous access not allowed.");
		}
	}
}
