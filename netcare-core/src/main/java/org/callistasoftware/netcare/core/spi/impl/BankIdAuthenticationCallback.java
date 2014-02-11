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

import org.callistasoftware.netcare.commons.bankid.BankIdAuthenticationResult;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.model.entity.EntityUtil;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankIdAuthenticationCallback extends ServiceSupport implements org.callistasoftware.netcare.commons.bankid.BankIdAuthenticationCallback {

	@Autowired private PatientRepository pRepo;
	
	@Override
	public UserDetails createMissingUser(BankIdAuthenticationResult auth) {
		getLog().info("The user is a patient...");
		final PatientEntity p = this.pRepo.save(PatientEntity.newEntity(auth.getFirstName(), auth.getSurName(), auth.getCivicRegistrationNumber()));
		getLog().debug("User {} has now been saved.", p.getCivicRegistrationNumber());
		
		return PatientBaseViewImpl.newFromEntity(p);
	}

	@Override
	public UserDetails verifyPrincipal(Object principal) {
		if (principal instanceof PatientBaseViewImpl) {
			getLog().debug("Already authenticated as a patient. Return principal object.");
			return (PatientBaseView) principal;
		} else {
			return null;
		}
	}

	@Override
	public UserDetails lookupPrincipal(BankIdAuthenticationResult auth)
			throws UsernameNotFoundException {
		getLog().debug("The authentication result indicates that the user is a patient. Check for the user in patient repository");
		final PatientEntity patient = this.pRepo.findByCivicRegistrationNumber(EntityUtil.formatCrn(auth.getCivicRegistrationNumber()));
		if (patient == null) {
			getLog().debug("Could not find any patients matching {}. Trying with care givers...", auth.getCivicRegistrationNumber());
		} else {
			return PatientBaseViewImpl.newFromEntity(patient);
		}
		
		throw new UsernameNotFoundException("User " + auth.getCivicRegistrationNumber() + " could not be found in the system.");
	}

}
