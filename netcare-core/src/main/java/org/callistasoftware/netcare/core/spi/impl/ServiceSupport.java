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
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.PermissionRestrictedEntity;
import org.callistasoftware.netcare.model.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class ServiceSupport {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserRepository repo;
	
	protected Logger getLog() {
		return this.log;
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
		log.debug("=== VERIFY WRITE ACCESS ===");
		this.verifyAccess(getCurrentUser(), object, true);
		log.debug("===========================");
	}
	
	protected void verifyReadAccess(final PermissionRestrictedEntity object) {
		log.debug("=== VERIFY READ ACCESS ===");
		this.verifyAccess(getCurrentUser(), object, false);
		log.debug("==========================");
	}
	
	private void verifyAccess(final UserEntity entity, final PermissionRestrictedEntity object, final boolean write) {
		final UserEntity user = this.getCurrentUser();
		if (user != null) {
			
			final boolean access;
			if (write) {
				access = object.isWriteAllowed(user);
			} else {
				access = object.isReadAllowed(user);
			}
			
			if (!access) {
				throw new SecurityException("User " + user.getFirstName() + "(" + user.getId() + ") does not have write permissions on this item.");
			}
		} else {
			throw new SecurityException("Anonymous access not allowed.");
		}
	}
	
	protected PatientEntity getPatient() {
		final UserEntity user = this.getCurrentUser();
		if (user.isCareActor()) {
			throw new IllegalStateException("Expected a patient in the security context but was a care giver.");
		}
		
		return (PatientEntity) user;
	}
	
	protected CareActorEntity getCareActor() {
		final UserEntity user = this.getCurrentUser();
		if (user.isCareActor()) {
			return (CareActorEntity) user;
		}
		
		throw new IllegalStateException("Expected a care giver in the security context but was a patient.");
	}
}
