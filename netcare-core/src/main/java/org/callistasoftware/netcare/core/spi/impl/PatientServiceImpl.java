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

import java.util.ArrayList;
import java.util.List;

import org.callistasoftware.netcare.core.api.CareUnit;
import org.callistasoftware.netcare.core.api.Patient;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.PatientProfile;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.PatientImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.messages.ListEntitiesMessage;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.spi.PatientService;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientServiceImpl extends ServiceSupport implements PatientService {

	private static Logger log = LoggerFactory.getLogger(PatientServiceImpl.class);
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SaltSource saltSource;
	
	@Autowired
	private CareUnitRepository cuRepo;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Override
	public ServiceResult<PatientBaseView[]> findPatients(String freeTextSearch) {
		if (freeTextSearch.length() < 3) {
			throw new IllegalArgumentException("Method cannot be invoked if the search string does not contain at least 3 charcaters");
		}
		
		final StringBuilder search = new StringBuilder().append("%").append(freeTextSearch).append("%"); 
		final List<PatientEntity> hits = this.patientRepository.findPatients(search.toString());
		
		final List<PatientBaseView> dtos = new ArrayList<PatientBaseView>(hits.size());
		for (final PatientEntity ent : hits) {
			dtos.add(PatientBaseViewImpl.newFromEntity(ent)); 
		}
		
		return ServiceResultImpl.createSuccessResult(dtos.toArray(new PatientBaseView[dtos.size()]), new ListEntitiesMessage(PatientEntity.class, dtos.size()));
	}

	@Override
	public ServiceResult<Patient> loadPatient(Long id) {
		final PatientEntity ent = this.patientRepository.findOne(id);
		return ServiceResultImpl.createSuccessResult(PatientImpl.newFromEntity(ent), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<Patient[]> loadPatientsOnCareUnit(final CareUnit careUnit) {
		log.info("Loading patients on care unit {}", careUnit.getHsaId());
		
		final CareUnitEntity cu = this.cuRepo.findByHsaId(careUnit.getHsaId());
		if (cu == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(CareUnitEntity.class, careUnit.getHsaId()));
		}
		
		final List<PatientEntity> patients = this.patientRepository.findByCareUnit(cu.getHsaId());
		log.debug("Found {} patients with health plans at care unit {}", patients.size(), cu.getHsaId());
		
		return ServiceResultImpl.createSuccessResult(PatientImpl.newFromEntities(patients), new ListEntitiesMessage(PatientEntity.class, patients.size()));
	}

	@Override
	public ServiceResult<Patient> createPatient(Patient patient) {
		log.info("Creating new patient {}", patient.getCivicRegistrationNumber());
		PatientEntity ent = this.patientRepository.findByCivicRegistrationNumber(patient.getCivicRegistrationNumber());
		if (ent == null) {
			final PatientEntity newPatient = PatientEntity.newEntity(patient.getFirstName(), patient.getSurName(), patient.getCivicRegistrationNumber());
			newPatient.setPhoneNumber(patient.getPhoneNumber());
			ent = this.patientRepository.save(newPatient);
		}		
		return ServiceResultImpl.createSuccessResult(PatientImpl.newFromEntity(ent), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<Patient> deletePatient(Long id) {
		log.info("Deleting patient {}", id);
		final PatientEntity patient = this.patientRepository.findOne(id);
		if (patient == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(PatientEntity.class, id));
		}
		
		this.verifyWriteAccess(patient);
		
		this.patientRepository.delete(patient);
		
		return ServiceResultImpl.createSuccessResult(PatientImpl.newFromEntity(patient), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<Patient> updatePatient(Long id, PatientProfile patient) {
		log.info("Updating patient {}", id);
		final PatientEntity p = this.patientRepository.findOne(id);
		if (p == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(PatientEntity.class, id));
		}
		
		this.verifyWriteAccess(p);
		
		p.setFirstName(patient.getFirstName());
		p.setSurName(patient.getSurName());
		p.setEmail(patient.getEmail());
		p.setMobile(patient.isMobile());
		p.setPhoneNumber(patient.getPhoneNumber());
		
		final Object salt = this.saltSource.getSalt((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		
		if (patient.isMobile()) {
			
			getLog().debug("Setting mobile password for patient {}. P1 = {}, P2 = {}", new Object[] { patient.getId(), patient.getPassword(), patient.getPassword2() });
			
			if (patient.getPassword1() == null || patient.getPassword2() == null) {
				throw new NullPointerException("User password must not be null");
			}
			
			if (!patient.getPassword1().equals(patient.getPassword2())) {
				throw new IllegalArgumentException("User password must not be null and the two entered values must match.");
			}
			
			String pw = this.passwordEncoder.encodePassword(patient.getPassword2(), salt);
			
			log.debug("Encrypted password is {}", pw);
			p.setPassword(pw);
		}
		
		return ServiceResultImpl.createSuccessResult(PatientImpl.newFromEntity(p), new GenericSuccessMessage());
	}
}
