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
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.api.messages.EntityNotUniqueMessage;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientServiceImpl extends ServiceSupport implements PatientService {

	private static Logger log = LoggerFactory.getLogger(PatientServiceImpl.class);
	
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
		final List<PatientEntity> hits = this.patientRepository.findByNameLikeOrEmailLikeOrCivicRegistrationNumberLike(search.toString(), search.toString(), search.toString());
		
		final List<PatientBaseView> dtos = new ArrayList<PatientBaseView>(hits.size());
		for (final PatientEntity ent : hits) {
			dtos.add(PatientBaseViewImpl.newFromEntity(ent)); 
		}
		
		return ServiceResultImpl.createSuccessResult(dtos.toArray(new PatientBaseView[dtos.size()]), new ListEntitiesMessage(PatientEntity.class, dtos.size()));
	}

	@Override
	public ServiceResult<PatientBaseView> loadPatient(Long id) {
		final PatientEntity ent = this.patientRepository.findOne(id);
		return ServiceResultImpl.createSuccessResult(PatientBaseViewImpl.newFromEntity(ent), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<PatientBaseView[]> loadPatientsOnCareUnit(final CareUnit careUnit) {
		log.info("Loading patients on care unit {}", careUnit.getHsaId());
		
		final CareUnitEntity cu = this.cuRepo.findByHsaId(careUnit.getHsaId());
		if (cu == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(CareUnitEntity.class, careUnit.getHsaId()));
		}
		
		final List<PatientEntity> patients = this.patientRepository.findByCareUnit(cu.getHsaId());
		log.debug("Found {} patients with health plans at care unit {}", patients.size(), cu.getHsaId());
		
		return ServiceResultImpl.createSuccessResult(PatientBaseViewImpl.newFromEntities(patients), new ListEntitiesMessage(PatientEntity.class, patients.size()));
	}

	@Override
	public ServiceResult<PatientBaseView> createPatient(PatientBaseView patient) {
		log.info("Creating new patient {}", patient.getCivicRegistrationNumber());
		final PatientEntity ent = this.patientRepository.findByCivicRegistrationNumber(patient.getCivicRegistrationNumber());
		if (ent != null) {
			return ServiceResultImpl.createFailedResult(new EntityNotUniqueMessage(PatientEntity.class, "cnr"));
		}
		
		final PatientEntity p = this.patientRepository.save(PatientEntity.newEntity(patient.getName(), patient.getCivicRegistrationNumber()));
		return ServiceResultImpl.createSuccessResult(PatientBaseViewImpl.newFromEntity(p), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<PatientBaseView> deletePatient(Long id) {
		log.info("Deleting patient {}", id);
		final PatientEntity patient = this.patientRepository.findOne(id);
		if (patient == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(PatientEntity.class, id));
		}
		
		this.verifyWriteAccess(patient);
		
		this.patientRepository.delete(patient);
		
		return ServiceResultImpl.createSuccessResult(PatientBaseViewImpl.newFromEntity(patient), new GenericSuccessMessage());
	}
}
