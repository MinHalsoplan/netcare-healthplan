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

import org.callistasoftware.netcare.core.api.impl.CareGiverBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.spi.UserDetailsService;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of a user details service
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@Service
@Transactional
public class UserDetailsServiceImpl extends ServiceSupport implements UserDetailsService {
	
	private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private CareGiverRepository careGiverRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		log.info("Lookup user {}", username);
		
		/*
		 * Username will be civic registration number
		 * for patients and hsa id for care givers
		 */
		final PatientEntity patient = this.patientRepository.findByCivicRegistrationNumber(username);
		if (patient == null) {
			log.debug("Could not find any patients matching {}. Trying with care givers...", username);
			
			final CareGiverEntity cg = this.careGiverRepository.findByHsaId(username);
			if (cg == null) {
				log.debug("Could not find any care giver matching {}", username);
			} else {
				return CareGiverBaseViewImpl.newFromEntity(cg);
			}
		} else {
			
			log.debug("Patient found.");
			log.debug("Mobile user: " + patient.isMobile());
			
			return PatientBaseViewImpl.newFromEntity(patient);
		}
		
		throw new UsernameNotFoundException("Please check your credentials");
		
	}

	@Override
	public void registerForC2dmPush(String c2dmRegistrationId) {
		final UserEntity user = this.getCurrentUser();
		
		log.info("User: {} registers for c2dm push using reg id: {}", user.getName(), c2dmRegistrationId);
		user.getProperties().put("c2dmRegistrationId", c2dmRegistrationId);
	}

	@Override
	public void registerForApnsPush(String apnsRegistrationId) {
		final UserEntity user = this.getCurrentUser();
		
		log.info("User: {} registers for apns push using reg id: {}", user.getName(), apnsRegistrationId);
		user.getProperties().put("apnsRegistrationId", apnsRegistrationId);
	}

}
