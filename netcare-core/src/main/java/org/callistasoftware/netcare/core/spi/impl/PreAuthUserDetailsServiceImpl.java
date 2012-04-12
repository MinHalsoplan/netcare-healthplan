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

import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.impl.CareGiverBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.EntityUtil;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.mvk.authentication.service.api.AuthenticationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PreAuthUserDetailsServiceImpl extends ServiceSupport implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
	
	@Autowired
	private CareUnitRepository cuRepo;
	
	@Autowired
	private CareGiverRepository cgRepo;
	
	@Autowired
	private PatientRepository pRepo;
	
	@Override
	public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authToken)
			throws UsernameNotFoundException {
		getLog().info("Retrieve information about user. The user is pre-authenticated...");
		
		final AuthenticationResult preAuthenticated;
		if (authToken.getPrincipal() instanceof AuthenticationResult) {
			getLog().debug("Not yet authenticated. We have an authentication result from MVK.");
			preAuthenticated = (AuthenticationResult) authToken.getPrincipal();
		} else if (authToken.getPrincipal() instanceof CareGiverBaseViewImpl) {
			getLog().debug("Already authenticated as a care giver. Return principal object.");
			return (CareGiverBaseView) authToken.getPrincipal();
		} else if (authToken.getPrincipal() instanceof PatientBaseViewImpl) {
			getLog().debug("Already authenticated as a patient. Return principal object.");
			return (PatientBaseView) authToken.getPrincipal();
		} else {
			throw new RuntimeException("Unknown authentication...");
		}
		
		/*
		 * Username will be civic registration number
		 * for patients and hsa id for care givers
		 */
		if (preAuthenticated.isCareGiver()) {
			getLog().debug("The authentication result indicates that the user is a care giver. Check for the user in care giver repository");
			final CareGiverEntity cg = this.cgRepo.findByHsaId(preAuthenticated.getUsername());
			if (cg == null) {
				getLog().debug("Could not find any care giver matching {}", preAuthenticated.getUsername());
			} else {
				return CareGiverBaseViewImpl.newFromEntity(cg);
			}
		} else {
			getLog().debug("The authentication result indicates that the user is a patient. Check for the user in patient repository");
			final PatientEntity patient = this.pRepo.findByCivicRegistrationNumber(EntityUtil.formatCrn(preAuthenticated.getUsername()));
			if (patient == null) {
				getLog().debug("Could not find any patients matching {}. Trying with care givers...", preAuthenticated.getUsername());
			} else {
				return PatientBaseViewImpl.newFromEntity(patient);
			}
		}
		
		/*
		 * We could not find the user, create
		 */
		return this.createMissingUser(preAuthenticated);
	}
	
	public UserDetails createMissingUser(final AuthenticationResult preAuthenticated) {
		getLog().info("User {} has not been here before, create the user...", preAuthenticated.getUsername());
		
		if (preAuthenticated.isCareGiver()) {
			
			getLog().debug("The user is a care giver...");
			
			final String careUnit = preAuthenticated.getCareUnitHsaId();
			CareUnitEntity cu = this.cuRepo.findByHsaId(careUnit);
			if (cu == null) {
				getLog().debug("Could not find care unit {}, create it.", careUnit);
				
				cu = CareUnitEntity.newEntity(careUnit);
				cu.setName(preAuthenticated.getCareUnitName());
				
				cu = this.cuRepo.save(CareUnitEntity.newEntity(careUnit));
				getLog().debug("Created care unit {}, {}", cu.getHsaId(), cu.getName());
			}
			
			final CareGiverEntity cg = this.cgRepo.save(CareGiverEntity.newEntity("system-generated-name", "system-generated-name", preAuthenticated.getUsername(), cu));
			getLog().debug("Created care giver {}", cg.getFirstName());
			
			return CareGiverBaseViewImpl.newFromEntity(cg);
		} else {
			
			getLog().info("The user is a patient...");
			final PatientEntity p = this.pRepo.save(PatientEntity.newEntity("system-generated-name", "system-generated-name", preAuthenticated.getUsername()));
			getLog().debug("User {} has now been saved.", p.getCivicRegistrationNumber());
			
			return PatientBaseViewImpl.newFromEntity(p);
		}
	}
}
