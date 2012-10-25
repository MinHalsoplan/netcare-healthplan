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

import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityCategory;
import org.callistasoftware.netcare.core.api.ActivityItemType;
import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.EstimationType;
import org.callistasoftware.netcare.core.api.MeasurementType;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityCategoryImpl;
import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.api.messages.EntityNotUniqueMessage;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.messages.ListEntitiesMessage;
import org.callistasoftware.netcare.core.repository.ActivityCategoryRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.spi.ActivityTypeService;
import org.callistasoftware.netcare.model.entity.AccessLevel;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.EstimationTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the activity type service
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 * 
 */
@Service
@Transactional
public class ActivityTypeServiceImpl extends ServiceSupport implements ActivityTypeService {

	private static final Logger log = LoggerFactory.getLogger(ActivityTypeServiceImpl.class);

	@Autowired
	private ActivityCategoryRepository catRepo;

	@Autowired
	private ActivityTypeRepository repo;

	@Autowired
	private CareUnitRepository cuRepo;

	@Override
	public ServiceResult<ActivityType[]> loadAllActivityTypes(final String hsaId) {
		log.info("Loading all activity types from repository belongin to {}...", hsaId);
		final List<ActivityTypeEntity> all = this.repo.findByCareUnit(hsaId);

		if (!all.isEmpty()) {
			this.verifyReadAccess(all.get(0));
		}

		return ServiceResultImpl.createSuccessResult(ActivityTypeImpl.newFromEntities(all,
				LocaleContextHolder.getLocale()), new ListEntitiesMessage(ActivityTypeEntity.class, all.size()));
	}

	@Override
	public ServiceResult<ActivityCategory[]> loadAllActivityCategories() {
		log.info("Load all activity categories from repository...");
		final List<ActivityCategoryEntity> cats = this.catRepo.findAll();

		log.debug("Found {} activity categories in repository. Converting to dtos", cats.size());
		final ActivityCategory[] dtos = new ActivityCategory[cats.size()];
		for (int i = 0; i < cats.size(); i++) {
			dtos[i] = ActivityCategoryImpl.newFromEntity(cats.get(i));
		}

		return ServiceResultImpl.createSuccessResult(dtos, new ListEntitiesMessage(ActivityCategoryEntity.class,
				dtos.length));
	}

	@Override
	public ServiceResult<ActivityCategory> createActivityCategory(ActivityCategory dto) {

		log.info("Creating new activity category: {}", dto.getName());

		log.debug("Checking if there already exist a category with the name {}", dto.getName());
		final ActivityCategoryEntity existing = this.catRepo.findByName(dto.getName().trim());
		if (existing != null) {
			log.debug("The name already exists... Abort.");
			return ServiceResultImpl
					.createFailedResult(new EntityNotUniqueMessage(ActivityCategoryEntity.class, "name"));
		}

		log.debug("No category with the specified name... creating new entity.");
		final ActivityCategoryEntity ent = ActivityCategoryEntity.newEntity(dto.getName());
		final ActivityCategoryEntity savedEntity = this.catRepo.save(ent);

		return ServiceResultImpl.createSuccessResult(ActivityCategoryImpl.newFromEntity(savedEntity),
				new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ActivityType[]> searchForActivityTypes(String searchString) {
		log.info("Finding activity types. Search string is: " + searchString);
		final List<ActivityTypeEntity> result = this.repo.findByNameLike(new StringBuilder().append("%")
				.append(searchString).append("%").toString());

		return ServiceResultImpl.createSuccessResult(ActivityTypeImpl.newFromEntities(result,
				LocaleContextHolder.getLocale()), new ListEntitiesMessage(ActivityTypeEntity.class, result.size()));
	}

	@Override
	public ServiceResult<ActivityType> createActivityType(ActivityType dto, CareActorBaseView careActor) {
		log.info("Creating new activity type. Name: {}", dto.getName());

		final ActivityCategoryEntity category = this.catRepo.findOne(dto.getCategory().getId());
		if (category == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityCategoryEntity.class, dto
					.getCategory().getId()));
		}

		final CareUnitEntity careUnit = this.cuRepo.findByHsaId(careActor.getCareUnit().getHsaId());

		ActivityTypeEntity activityTypeEntity = ActivityTypeEntity.newEntity(dto.getName(), category, careUnit,
				AccessLevel.CAREUNIT);

		for (final ActivityItemType type : dto.getActivityItems()) {
			if (type instanceof MeasurementType) {
				MeasurementType measurementType = (MeasurementType) type;
				final MeasurementTypeEntity entity = MeasurementTypeEntity.newEntity(activityTypeEntity,
						measurementType.getName(),
						MeasurementValueType.valueOf(measurementType.getValueType().getCode()),
						MeasureUnit.valueOf(measurementType.getUnit().getCode()), measurementType.isAlarm());
				activityTypeEntity.addActivityItemType(entity);
				log.debug("Adding measurement type {}", type.getName());
			} else if (type instanceof EstimationType) {
				EstimationType estimationType = (EstimationType) type;
				final EstimationTypeEntity entity = EstimationTypeEntity.newEntity(activityTypeEntity,
						estimationType.getName(), estimationType.getMinScaleText(), estimationType.getMaxScaleText());
				activityTypeEntity.addActivityItemType(entity);
				log.debug("Adding estimation type {}", type.getName());
			}
		}

		final ActivityTypeEntity savedEntity = this.repo.save(activityTypeEntity);

		return ServiceResultImpl.createSuccessResult(
				(ActivityType) ActivityTypeImpl.newFromEntity(savedEntity, LocaleContextHolder.getLocale()),
				new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ActivityType> getActivityType(String idAsString) {
		log.info("Finding activity type by id: " + idAsString);
		Long id = Long.valueOf(idAsString);
		final ActivityTypeEntity result = this.repo.findOne(id);
		if (result == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityTypeEntity.class, id));
		}
		System.out.println("Result type: " + result.getName());
		//TODO Do we have to check access rights here?
		return ServiceResultImpl.createSuccessResult(
				ActivityTypeImpl.newFromEntity(result, LocaleContextHolder.getLocale()), new GenericSuccessMessage());
	}
}
