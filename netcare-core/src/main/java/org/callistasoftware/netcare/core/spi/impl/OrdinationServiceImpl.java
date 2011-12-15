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

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.Ordination;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.OrdinationImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.DefaultSystemMessage;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.core.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.core.entity.CareGiverEntity;
import org.callistasoftware.netcare.core.entity.DurationUnit;
import org.callistasoftware.netcare.core.entity.Frequency;
import org.callistasoftware.netcare.core.entity.FrequencyDay;
import org.callistasoftware.netcare.core.entity.FrequencyTime;
import org.callistasoftware.netcare.core.entity.OrdinationEntity;
import org.callistasoftware.netcare.core.entity.PatientEntity;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.OrdinationRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.spi.OrdinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
	private ActivityTypeRepository activityTypeRepository;
	
	@Autowired
	private CareGiverRepository careGiverRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private ActivityDefinitionRepository activityDefintionRepository;
	
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

	@Override
	public ServiceResult<Ordination> loadOrdination(Long ordinationId,
			PatientBaseView patient) {
		final OrdinationEntity entity = this.repo.findOne(ordinationId);
		if (entity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(OrdinationEntity.class, ordinationId));
		}
		
		if (!entity.getForPatient().getId().equals(patient.getId())) {
			return ServiceResultImpl.createFailedResult(new DefaultSystemMessage("Du har inte behörigheten att se denna ordination"));
		}
		
		final Ordination dto = OrdinationImpl.newFromEntity(entity, null);
		return ServiceResultImpl.createSuccessResult(dto, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<Ordination> addActivityDefintionToOrdination(
			Long ordinationId, final ActivityDefinition dto) {
		log.info("Adding activity defintion to existing ordination with id {}", ordinationId);
		final OrdinationEntity entity = this.repo.findOne(ordinationId);
		if (entity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(OrdinationEntity.class, ordinationId));
		}
		
		log.debug("Ordination entity found and resolved.");
		
		final ActivityTypeEntity typeEntity = this.activityTypeRepository.findOne(dto.getType().getId());
		if (typeEntity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityTypeEntity.class, dto.getType().getId()));
		}
		
		log.debug("Activity type entity found and resolved");
		
		/*
		 * Create the day frequence based on what the user
		 * selected.
		 */
		log.debug("Processing the day frequence...");
		final FrequencyDay dayFrequence = new FrequencyDay();
		dayLoop:for (final int d : dto.getDays()) {
			switch (d) {
			case 0: dayFrequence.addDay(FrequencyDay.MON); continue dayLoop;
			case 1: dayFrequence.addDay(FrequencyDay.TUE); continue dayLoop;
			case 2: dayFrequence.addDay(FrequencyDay.WED); continue dayLoop;
			case 3: dayFrequence.addDay(FrequencyDay.THU); continue dayLoop;
			case 4: dayFrequence.addDay(FrequencyDay.FRI); continue dayLoop;
			case 5: dayFrequence.addDay(FrequencyDay.SAT); continue dayLoop;
			case 6: dayFrequence.addDay(FrequencyDay.SUN); continue dayLoop;
			default:
				throw new IllegalArgumentException("Value " + d + " is not a valid day");
			} 
		}
		
		log.debug("Day frequence resolved to: {}", dayFrequence.getDays());
		
		final Frequency frequence = new Frequency();
		frequence.setFrequencyDay(dayFrequence);
		
		log.debug("Processing the time frequency...");
		for (final String time : dto.getTimes()) {
			frequence.getTimes().add(FrequencyTime.fromTimeString(time));
		}
		
		log.debug("Time frequence resolved to: {}", frequence.getTimes());
		
		final ActivityDefinitionEntity newEntity = ActivityDefinitionEntity.newEntity(entity, typeEntity, frequence);
		newEntity.setActivityTarget(dto.getGoal());
		
		final ActivityDefinitionEntity savedEntity = this.activityDefintionRepository.save(newEntity);
		log.debug("Activity defintion saved.");
		
		entity.addActivityDefinition(savedEntity);
		final OrdinationEntity savedOrdination = this.repo.save(entity);
		log.debug("Ordination saved");
		
		log.debug("Creating result. Success!");
		final Ordination result = OrdinationImpl.newFromEntity(savedOrdination, LocaleContextHolder.getLocale());
		return ServiceResultImpl.createSuccessResult(result, new GenericSuccessMessage());
	}

}
