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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.callistasoftware.netcare.core.api.*;
import org.callistasoftware.netcare.core.api.impl.ActivityCategoryImpl;
import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.api.messages.EntityNotUniqueMessage;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.messages.ListEntitiesMessage;
import org.callistasoftware.netcare.core.api.messages.NoAccessMessage;
import org.callistasoftware.netcare.core.repository.ActivityCategoryRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.MeasureUnitRepository;
import org.callistasoftware.netcare.core.spi.ActivityTypeService;
import org.callistasoftware.netcare.model.entity.*;
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
	private EntityManagerFactory eMan;

	@Autowired
	private ActivityCategoryRepository catRepo;

	@Autowired
	private ActivityTypeRepository repo;

	@Autowired
	private CareUnitRepository cuRepo;
	
	@Autowired
	private MeasureUnitRepository measureRepo;

	public ServiceResult<ActivityType[]> loadAllActivityTypes(final CareUnitEntity careUnit) {
		log.info("Loading all activity templates accessible from care unit {}", careUnit.getHsaId());
		final List<ActivityTypeEntity> all = this.repo.findByCareUnit(careUnit, careUnit.getCountyCouncil());

		/*
		 * Which of these are in use?
		 */
		final List<ActivityTypeEntity> processed = this.processTemplatesInUse(all);
		for (final ActivityTypeEntity ent : processed) {
			log.debug("Is in use? {}", ent.getInUse());
		}

		return ServiceResultImpl.createSuccessResult(ActivityTypeImpl.newFromEntities(processed,
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
		final ActivityCategoryEntity existing = this.catRepo.findByNameOrderByNameAsc(dto.getName().trim());
		if (existing != null) {
			log.debug("The name already exists... Abort.");
			return ServiceResultImpl
					.createFailedResult(new EntityNotUniqueMessage(ActivityCategoryEntity.class, "name"));
		}

		log.debug("No category with the specified name... creating new entity.");
		final ActivityCategoryEntity ent = ActivityCategoryEntity.newEntity(dto.getName());
		verifyWriteAccess(ent);
		
		final ActivityCategoryEntity savedEntity = this.catRepo.save(ent);

		return ServiceResultImpl.createSuccessResult(ActivityCategoryImpl.newFromEntity(savedEntity),
				new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ActivityType[]> searchForActivityTypes(final String searchString, final String category,
			final String level) {
		log.info("Finding activity types. Search string is: " + searchString);

		// Default find all
		if (searchString.isEmpty() && category.equals("all") && level.equals("all")) {
			return this.loadAllActivityTypes(getCareActor().getCareUnit());
		}

		boolean includeAnd = true;
		boolean includeWhere = false;

		final StringBuilder query = new StringBuilder();
		query.append("select e from ActivityTypeEntity as e ");
		if (!searchString.isEmpty()) {
			log.debug("Using name {}", searchString);
			query.append("where lower(e.name) like ").append("'%").append(searchString.toLowerCase()).append("%' ");
		} else {
			includeWhere = true;
			includeAnd = false;
		}

		if (!category.equals("all")) {
			log.debug("Using category {}", category);
			query.append(includeWhere ? " where " : "")
					.append(includeAnd ? "and e.category.name = " : "e.category.id = ").append(Long.valueOf(category));
			includeAnd = true;
			includeWhere = false;
		}

		if (!level.equals("all")) {
			log.debug("Using level {}", level);
			query.append(includeWhere ? " where " : "")
					.append(includeAnd ? "and e.accessLevel = " : "e.accessLevel = ").append("'")
					.append(AccessLevel.valueOf(level)).append("'");
			includeAnd = true;
		}

		log.debug("Search query is: {}", query.toString());

		@SuppressWarnings("unchecked")
		final List<ActivityTypeEntity> results = eMan.createEntityManager().createQuery(query.toString())
				.getResultList();

		log.debug("Now filter result set...");

		/*
		 * Make sure we do not get other county councils templates
		 */
		final CareUnitEntity cu = getCareActor().getCareUnit();
		final CountyCouncilEntity cce = getCareActor().getCareUnit().getCountyCouncil();
		final List<ActivityTypeEntity> filteredResults = new ArrayList<ActivityTypeEntity>();
		for (final ActivityTypeEntity ent : results) {

			if (ent.getAccessLevel().equals(AccessLevel.COUNTY_COUNCIL)
					&& !ent.getCountyCouncil().getId().equals(cce.getId())) {
				log.debug("Found a template for county council {} but the user belongs to {}", ent.getCountyCouncil()
						.getId(), cce.getId());
				continue;
			}

			if (ent.getAccessLevel().equals(AccessLevel.CAREUNIT) && !ent.getCareUnit().getId().equals(cu.getId())) {
				log.debug("Found a template for care unit {} but the user belongs to {}", ent.getCareUnit().getHsaId(),
						cu.getHsaId());
				continue;
			}

			filteredResults.add(ent);
		}
		final List<ActivityTypeEntity> processed = this.processTemplatesInUse(filteredResults);
		for (final ActivityTypeEntity ent : processed) {
			log.debug("Is in use? {}", ent.getInUse());
		}

		return ServiceResultImpl.createSuccessResult(ActivityTypeImpl.newFromEntities(processed,
				LocaleContextHolder.getLocale()),
				new ListEntitiesMessage(ActivityTypeEntity.class, filteredResults.size()));
	}

	@Override
	public ServiceResult<ActivityType> createActivityType(ActivityType dto) {
		log.info("Creating new activity type. Name: {}", dto.getName());

		final ActivityCategoryEntity category = this.catRepo.findOne(dto.getCategory().getId());
		if (category == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityCategoryEntity.class, dto
					.getCategory().getId()));
		}

		final CareUnitEntity careUnit = this.cuRepo.findByHsaId(getCareActor().getCareUnit().getHsaId());

		ActivityTypeEntity activityTypeEntity = ActivityTypeEntity.newEntity(dto.getName(), category, careUnit,
				AccessLevel.valueOf(dto.getAccessLevel().getCode()));

		for (final ActivityItemType type : dto.getActivityItems()) {
			activityTypeEntity.addActivityItemType(createNewItemEntity(type, activityTypeEntity));
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
		// TODO Do we have to check access rights here?
		return ServiceResultImpl.createSuccessResult(
				(ActivityType) ActivityTypeImpl.newFromEntity(result, LocaleContextHolder.getLocale()),
				new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ActivityType> updateActivityType(ActivityTypeImpl dto) {
		Long id = dto.getId();
		ActivityTypeEntity repoItem = this.repo.findOne(id);
		if (repoItem == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityTypeEntity.class, id));
		}
		final ActivityCategoryEntity category = this.catRepo.findOne(dto.getCategory().getId());
		if (category == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityCategoryEntity.class, dto
					.getCategory().getId()));
		}

		repoItem.setName(dto.getName());
		
		if (dto.getAccessLevel().getCode().equals(AccessLevel.CAREUNIT.name())) {
            CareActorEntity careActor = getCareActor();
			if(!repoItem.getCountyCouncil().getId().equals(careActor.getCareUnit().getCountyCouncil().getId())) {
                throw new IllegalStateException("Care actor " + careActor.getHsaId() + " (county council: "
                        + careActor.getCareUnit().getCountyCouncil().getId()
                        + ") tried to change activity type with id "
                        + repoItem.getId()
                        + ". County council must be the same for activity type and care unit. Current county council: "
                        + repoItem.getCountyCouncil().getId());
            }
            getLog().debug("Access level set to care unit on activity. Setting current user's care unit on entity");
			repoItem.setCareUnit(getCareActor().getCareUnit());
		}
		
		repoItem.setAccessLevel(AccessLevel.valueOf(dto.getAccessLevel().getCode()));
		if (repoItem.getCategory().getId() != category.getId()) {
			repoItem.setCategory(category);
		}
		updateItemsWithDtoList(repoItem, dto.getActivityItems());

		final ActivityTypeEntity savedEntity = this.repo.save(repoItem);

		return ServiceResultImpl.createSuccessResult(
				(ActivityType) ActivityTypeImpl.newFromEntity(savedEntity, LocaleContextHolder.getLocale()),
				new GenericSuccessMessage());
	}

	protected void updateItemsWithDtoList(ActivityTypeEntity repoItem, ActivityItemType[] dtoItems) {
		checkForDeletions(repoItem.getActivityItemTypes(), dtoItems);
		updateItemsInType(repoItem, dtoItems);
	}

	protected void updateItemsInType(ActivityTypeEntity repoItem, ActivityItemType[] dtoItems) {
		ActivityItemType dtoItem = null;
		for(int i=0;i<dtoItems.length;i++) {
			dtoItem = dtoItems[i];
			updateOrCreateItem(repoItem, dtoItem, i);
		}
	}

	protected void updateOrCreateItem(ActivityTypeEntity repoItem, ActivityItemType dtoItem, int newSeqNo) {
		if (dtoItem.getId() >= 0) {
			updateItemWithDtoValues(findEntityItem(dtoItem.getId(), repoItem.getActivityItemTypes()), dtoItem, newSeqNo);
		} else {
			createNewItemEntity(dtoItem, repoItem);
		}
	}

	protected void updateItemWithDtoValues(ActivityItemTypeEntity itemEntity, ActivityItemType dtoItem, int newSeqNo) {
		
		itemEntity.setName(dtoItem.getName());
		itemEntity.setSeqno(newSeqNo);
		if (itemEntity instanceof MeasurementTypeEntity) {
			final MeasureUnitEntity mue = this.resolveMeasureUnit(dtoItem.getUnit().getId(), false);
			MeasurementTypeEntity entity = (MeasurementTypeEntity) itemEntity;
			entity.setValueType(MeasurementValueType.valueOf(dtoItem.getValueType().getCode()));
			entity.setUnit(mue);
			entity.setAlarmEnabled(dtoItem.isAlarm());
		} else if (itemEntity instanceof EstimationTypeEntity) {
			EstimationTypeEntity entity = (EstimationTypeEntity) itemEntity;
			entity.setSenseLabelHigh(dtoItem.getMaxScaleText());
			entity.setSenseLabelLow(dtoItem.getMinScaleText());
			entity.setSenseValueHigh(dtoItem.getMaxScaleValue());
			entity.setSenseValueLow(dtoItem.getMinScaleValue());
		} else if (itemEntity instanceof YesNoTypeEntity) {
			YesNoTypeEntity entity = (YesNoTypeEntity) itemEntity;
			entity.setQuestion(dtoItem.getQuestion());
		} else if (itemEntity instanceof TextTypeEntity) {
			TextTypeEntity entity = (TextTypeEntity) itemEntity;
			entity.setLabel(dtoItem.getLabel());
		}
	}

	protected ActivityItemTypeEntity findEntityItem(Long id, List<ActivityItemTypeEntity> activityItemTypes) {
		for (ActivityItemTypeEntity entity : activityItemTypes) {
			if (entity.getId().equals(id)) {
				return entity;
			}
		}
		throw new RuntimeException("Entity with id " + id + " not found for update. Aborting update");
	}

	protected ActivityItemTypeEntity createNewItemEntity(ActivityItemType dtoItem, ActivityTypeEntity parent) {
		
		if (dtoItem.getActivityItemTypeName().equals(ActivityItemType.MEASUREMENT_ITEM_TYPE)) {
			
			// Resolve measure unit
			final MeasureUnitEntity mue = resolveMeasureUnit(dtoItem.getUnit().getId(), false);
			
			return MeasurementTypeEntity.newEntity(parent, dtoItem.getName(),
					MeasurementValueType.valueOf(dtoItem.getValueType().getCode()),
					mue, dtoItem.isAlarm(), dtoItem.getSeqno());
		} else if (dtoItem.getActivityItemTypeName().equals(ActivityItemType.ESTIMATION_ITEM_TYPE)) {
			return EstimationTypeEntity.newEntity(parent, dtoItem.getName(), dtoItem.getMinScaleText(),
					dtoItem.getMaxScaleText(), dtoItem.getMinScaleValue(), dtoItem.getMaxScaleValue(),
					dtoItem.getSeqno());
		} else if (dtoItem.getActivityItemTypeName().equals(ActivityItemType.TEXT_ITEM_TYPE)) {
			return TextTypeEntity.newEntity(parent, dtoItem.getName(), dtoItem.getLabel(), dtoItem.getSeqno());
		} else if (dtoItem.getActivityItemTypeName().equals(ActivityItemType.YESNO_ITEM_TYPE)) {
			return YesNoTypeEntity.newEntity(parent, dtoItem.getName(), dtoItem.getQuestion(), dtoItem.getSeqno());
		} else {
			throw new RuntimeException("Could not create activity type item. Missing attribute [activityTypeName]");
		}
	}

	protected void checkForDeletions(List<ActivityItemTypeEntity> repoList, ActivityItemType[] activityItems) {
		Iterator<ActivityItemTypeEntity> iter = repoList.iterator();
		while (iter.hasNext()) {
			ActivityItemTypeEntity entity = iter.next();
			if (!entityInDtoList(entity.getId(), activityItems)) {
				entity.setActivityType(null);
				iter.remove();
			}
		}
	}

	protected boolean entityInDtoList(Long id, ActivityItemType[] activityItems) {
		for (ActivityItemType item : activityItems) {
			if (item.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	List<ActivityTypeEntity> processTemplatesInUse(final List<ActivityTypeEntity> entities) {
		log.debug("Processing templates in use...");
		if (entities.isEmpty()) {
			log.debug("No entities to process.");
			return Collections.emptyList();
		}
		
		final Collection<Long> ids = new ArrayList<Long>(entities.size());
		for (final ActivityTypeEntity ent : entities) {
			ids.add(ent.getId());
		}

		final List<ActivityTypeEntity> inUse = repo.findInUse(ids);

		log.debug("There are {} templates in use of the specified entities...", inUse.size());
		entLoop: for (final ActivityTypeEntity ent : inUse) {

			for (final ActivityTypeEntity one : entities) {
				if (ent.getId().equals(one.getId())) {
					log.debug("Updating entity with in use = true.");
					one.setInUse(true);
					continue entLoop;
				}
			}
		}

		return entities;
	}

	@Override
	public ServiceResult<ActivityType> deleteActivityTemplate(Long id) {
		log.debug("Deleting template {}", id);
		final ActivityTypeEntity one = repo.findOne(id);
		if (one == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityTypeEntity.class, id));
		}
		
		if (one.isWriteAllowed(getCareActor())) {
			repo.delete(one);
			return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
		} else {
			return ServiceResultImpl.createFailedResult(new NoAccessMessage());
		}
	}
	
	private MeasureUnitEntity resolveMeasureUnit(final Long id, final boolean write) {
		final MeasureUnitEntity mue = measureRepo.findOne(id);
		if (id == null) {
			throw new IllegalStateException("Could not find measure unit with id " + id);
		}
		
		if (write) {
			this.verifyWriteAccess(mue);
		} else {
			this.verifyReadAccess(mue);
		}
		
		return mue;
	}
}
