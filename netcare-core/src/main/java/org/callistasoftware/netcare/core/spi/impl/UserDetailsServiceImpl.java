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
package org.callistasoftware.netcare.core.spi.impl;

import org.callistasoftware.netcare.core.api.impl.CareGiverBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.entity.CareGiverEntity;
import org.callistasoftware.netcare.core.entity.PatientEntity;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.spi.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of a user details service
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
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
				final CareGiverBaseViewImpl bv = new CareGiverBaseViewImpl(cg.getId(), cg.getName());
				BeanUtils.copyProperties(cg, bv);
				
				return bv;
			}
		} else {
			final PatientBaseViewImpl bv = new PatientBaseViewImpl(patient.getId(), patient.getName());
			BeanUtils.copyProperties(patient, bv);
			
			return bv;
		}
		
		throw new UsernameNotFoundException("Please check your credentials");
		
	}

}
