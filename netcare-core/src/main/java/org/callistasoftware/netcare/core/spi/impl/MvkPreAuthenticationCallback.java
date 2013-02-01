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

import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.impl.CareActorBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.repository.CareActorRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.repository.RoleRepository;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.EntityUtil;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.RoleEntity;
import org.callistasoftware.netcare.mvk.authentication.service.api.AuthenticationResult;
import org.callistasoftware.netcare.mvk.authentication.service.api.PreAuthenticationCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MvkPreAuthenticationCallback extends ServiceSupport implements PreAuthenticationCallback {

	@Autowired
	private CareUnitRepository cuRepo;
	
	@Autowired
	private CareActorRepository careActorRepo;
	
	@Autowired
	private PatientRepository pRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Override
	public UserDetails createMissingUser(AuthenticationResult preAuthenticated) {
		getLog().info("User {} has not been here before, create the user...", preAuthenticated.getUsername());
		
		if (preAuthenticated.isCareActor()) {
			
			getLog().debug("The user is a care actor...");
			
			final String careUnit = preAuthenticated.getCareUnitHsaId();
			CareUnitEntity cu = this.cuRepo.findByHsaId(careUnit);
			if (cu == null) {
				getLog().debug("Could not find care unit {}, create it.", careUnit);
				cu = this.createCareUnit(careUnit, preAuthenticated.getCareUnitName());
			}
			
			getLog().debug("Care unit found. Hsa id: {}", cu.getHsaId());
			
			getLog().debug("Lookup care actor role...");
			final RoleEntity caRole = roleRepo.findByDn("CARE_ACTOR");
			
			final CareActorEntity ca = CareActorEntity.newEntity("system-generated-name", "system-generated-name", preAuthenticated.getUsername(), cu);
			ca.addRole(caRole);	
			
			final CareActorEntity savedCa = this.careActorRepo.save(ca);
			getLog().debug("Care actor saved. Belongs to care unit: {}", savedCa.getCareUnit().getHsaId());
			
			final CareActorBaseView dto = CareActorBaseViewImpl.newFromEntity(savedCa);
			getLog().debug("Constructed dto for care actor: {}", savedCa.getId());
			
			return dto;
			
		} else {
			
			getLog().info("The user is a patient...");
			final PatientEntity p = this.pRepo.save(PatientEntity.newEntity("system-generated-name", "system-generated-name", preAuthenticated.getUsername()));
			getLog().debug("User {} has now been saved.", p.getCivicRegistrationNumber());
			
			return PatientBaseViewImpl.newFromEntity(p);
		}
	}
	
	private CareUnitEntity createCareUnit(final String hsaId, final String name) {
		getLog().debug("Creating new care unit {} - {}", hsaId, name);
		
		//TODO What should CountyCouncil be here?
		CareUnitEntity cu = CareUnitEntity.newEntity(hsaId,null);
		
		if (name == null) {
			cu.setName("Vårdenhetsnamn saknas");
		} else {
			cu.setName(name);
		}
		
		cu = this.cuRepo.save(cu);
		getLog().debug("Created care unit {}, {}", cu.getHsaId(), cu.getName());
		
		return cu;
	}

	@Override
	public UserDetails verifyPrincipal(Object principal) {
		if (principal instanceof CareActorBaseViewImpl) {
			getLog().debug("Already authenticated as a care giver. Return principal object.");
			return (CareActorBaseView) principal;
		} else if (principal instanceof PatientBaseViewImpl) {
			getLog().debug("Already authenticated as a patient. Return principal object.");
			return (PatientBaseView) principal;
		} else {
			return null;
		}
	}

	@Override
	public UserDetails lookupPrincipal(AuthenticationResult auth) throws UsernameNotFoundException {
		if (auth.isCareActor()) {
			getLog().debug("The authentication result indicates that the user is a care giver. Check for the user in care giver repository");
			final CareActorEntity ca = this.careActorRepo.findByHsaId(auth.getUsername());
			if (ca == null) {
				getLog().debug("Could not find any care giver matching {}", auth.getUsername());
			} else {
				
				/*
				 * If the user logged in on a different care unit than last time, we
				 * must handle this here
				 */
				if (!ca.getCareUnit().getHsaId().equals(auth.getCareUnitHsaId())) {
					
					CareUnitEntity newCareUnit = this.cuRepo.findByHsaId(auth.getCareUnitHsaId());
					if (newCareUnit == null) {
						/*
						 * The user logged in from a care unit that does not exist, create it
						 */
						newCareUnit = this.createCareUnit(auth.getCareUnitHsaId(), auth.getCareUnitName());
					}
					
					ca.setCareUnit(newCareUnit);
				}
				
				return CareActorBaseViewImpl.newFromEntity(ca);
			}
			
		} else {
			getLog().debug("The authentication result indicates that the user is a patient. Check for the user in patient repository");
			final PatientEntity patient = this.pRepo.findByCivicRegistrationNumber(EntityUtil.formatCrn(auth.getUsername()));
			if (patient == null) {
				getLog().debug("Could not find any patients matching {}. Trying with care givers...", auth.getUsername());
			} else {
				return PatientBaseViewImpl.newFromEntity(patient);
			}
		}
		
		throw new UsernameNotFoundException("User " + auth.getUsername() + " could not be found in the system.");
	}
}
