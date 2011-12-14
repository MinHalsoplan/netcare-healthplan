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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.Ordination;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.impl.OrdinationImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.entity.CareGiverEntity;
import org.callistasoftware.netcare.core.entity.DurationUnit;
import org.callistasoftware.netcare.core.entity.OrdinationEntity;
import org.callistasoftware.netcare.core.entity.PatientEntity;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.OrdinationRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.spi.OrdinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of service defintion
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@Service
public class OrdinationServiceImpl implements OrdinationService {

	private static final Logger log = LoggerFactory.getLogger(OrdinationServiceImpl.class);
	
	@Autowired
	private OrdinationRepository repo;
	
	@Autowired
	private CareGiverRepository careGiverRepository;
	@Autowired
	private PatientRepository patientRepository;
	
	@Override
	public ServiceResult<Ordination[]> loadOrdinationsForPatient(Long patientId) {
		final PatientEntity forPatient = patientRepository.findOne(patientId);
		final List<OrdinationEntity> entities = this.repo.findByForPatient(forPatient);
		
		final Ordination[] dtos = new Ordination[entities.size()];
		int count = 0;
		for (final OrdinationEntity ent : entities) {
			final OrdinationImpl dto = OrdinationImpl.newFromEntity(ent, null);
			dtos[count++] = dto;
		}
		
		return ServiceResultImpl.createSuccessResult(dtos, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<Ordination> createNewOrdination(final Ordination o, final CareGiverBaseView careGiver, final Long patientId) {		
		log.info("Creating new ordination {}", o.getName());
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			final Date start = sdf.parse(o.getStartDate());
			final DurationUnit du = DurationUnit.valueOf(o.getDurationUnit().getCode());
			
			final CareGiverEntity cg = this.careGiverRepository.findByHsaId(careGiver.getHsaId());
			
			final PatientEntity patient = this.patientRepository.findOne(patientId);
			
			final OrdinationEntity newEntity = OrdinationEntity.newEntity(cg, patient, o.getName(), start, o.getDuration(), du);
			
			final OrdinationEntity saved = this.repo.save(newEntity);
			final Ordination dto = OrdinationImpl.newFromEntity(saved, null);
			
			return ServiceResultImpl.createSuccessResult(dto, new GenericSuccessMessage());
			
		} catch (ParseException e1) {
			throw new IllegalArgumentException("Could not parse date.", e1);
		}
	}

	@Override
	public ServiceResult<Ordination> deleteOrdination(Long ordinationId) {
		log.info("Deleting ordination {}", ordinationId);
		this.repo.delete(ordinationId);
		
		return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
	}

}
