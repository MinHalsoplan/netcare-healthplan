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
package org.callistasoftware.netcare.core.spi.impl;

import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.impl.CareActorBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.repository.CareActorRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.spi.UserDetailsService;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Implementation of a user details service
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@Service
@Transactional
public class UserDetailsServiceImpl extends ServiceSupport implements UserDetailsService {
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private CareActorRepository careActorRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		getLog().info("Lookup user {}", username);
		
		/*
		 * Username will be civic registration number
		 * for patients and hsa id for care givers
		 */
		final PatientEntity patient = this.patientRepository.findByCivicRegistrationNumber(username);
		if (patient == null) {
			getLog().debug("Could not find any patients matching {}. Trying with care givers...", username);
			
			final CareActorEntity ca = this.careActorRepository.findByHsaId(username);
			if (ca == null) {
				getLog().debug("Could not find any care giver matching {}", username);
			} else {
				
				getLog().debug("Found care actor with {} roles", ca.getRoles().size());
				
				return CareActorBaseViewImpl.newFromEntity(ca);
			}
		} else {
			getLog().debug("Patient found.");
			return PatientBaseViewImpl.newFromEntity(patient);
		}
		
		throw new UsernameNotFoundException("Please check your credentials");
		
	}

	@Override
	public void registerForGcm(String c2dmRegistrationId) {
		final UserEntity user = this.getCurrentUser();
		
		getLog().info("User: {} registers for c2dm push using reg id: {}", user.getFirstName(), c2dmRegistrationId);
		user.getProperties().put("c2dmRegistrationId", c2dmRegistrationId);
	}
	
	@Override
	public void unregisterGcm() {
		this.getCurrentUser().getProperties().remove("c2dmRegistrationId");
	}

	@Override
	public void registerForApnsPush(String apnsRegistrationId) {
		final UserEntity user = this.getCurrentUser();
		
		getLog().info("User: {} registers for apns push using reg id: {}", user.getFirstName(), apnsRegistrationId);
		user.getProperties().put("apnsRegistrationId", apnsRegistrationId);
	}
	
	@Override
	public void unregisterApns() {
		getCurrentUser().getProperties().remove("apnsRegistrationId");
	}

    @Override
    public void addUserProperties(Map<String, String> props) {
        final UserEntity user = this.getCurrentUser();
        getLog().info("User: {} adding multiple properties", user.getFirstName());
        user.getProperties().putAll(props);

    }

    @Override
	public ServiceResult<Boolean> saveUserData(String firstName, String surName) {
		final UserEntity user = this.getCurrentUser();
		
		getLog().info("Updating user data for user {}", user.getUsername());
		
		user.setFirstName(firstName);
		user.setSurName(surName);
		
		final UserBaseView ubv;
		if (user.isCareActor()) {
			ubv = CareActorBaseViewImpl.newFromEntity((CareActorEntity) user);
		} else {
			ubv = PatientBaseViewImpl.newFromEntity((PatientEntity) user);
		}
		
		/*
		 * Refresh security context
		 */
		getLog().debug("Refreshing security context with updated firstname/surname");
		SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(ubv, "n/a"));
		
		return ServiceResultImpl.createSuccessResult(Boolean.TRUE, new GenericSuccessMessage());
	}

}
