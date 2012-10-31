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
package org.callistasoftware.netcare.core.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.callistasoftware.netcare.core.api.ActivityCategory;
import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityCategoryImpl;
import org.callistasoftware.netcare.core.api.impl.CareActorBaseViewImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotUniqueMessage;
import org.callistasoftware.netcare.core.repository.ActivityCategoryRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.AccessLevel;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class ActivityTypeServiceTest extends TestSupport {

	@Autowired
	private ActivityCategoryRepository catRepo;

	@Autowired
	private ActivityTypeRepository repo;

	@Autowired
	private ActivityTypeService service;

	@Test
	@Transactional
	@Rollback(true)
	public void testLoadAllActivityTypes() throws Exception {
		final CountyCouncilEntity cc = getCountyCouncilRepository().save(CountyCouncilEntity.newEntity("SLL"));
		final CareUnitEntity cu = CareUnitEntity.newEntity("hsa-id-4321", cc);
		final CareUnitEntity savedCu = getCareUnitRepository().save(cu);
		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));

		final CareActorEntity ca = getCareActorRepository().save(CareActorEntity.newEntity("Dr Marcus", "", "hsa-id-cg", cu));
		final CareActorBaseView cab = CareActorBaseViewImpl.newFromEntity(ca);

		this.runAs(cab);

		for (int i = 0; i < 10; i++) {
			this.repo.save(ActivityTypeEntity.newEntity("Type-" + i, cat, savedCu, AccessLevel.CAREUNIT));
		}

		final ServiceResult<ActivityType[]> result = this.service.loadAllActivityTypes(savedCu.getHsaId());
		assertTrue(result.isSuccess());
		assertEquals(10, result.getData().length);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testCreateNewActivityCategory() throws Exception {

		final ActivityCategory impl = ActivityCategoryImpl.createNewDto("Fysisk aktivitet");
		final ServiceResult<ActivityCategory> result = this.service.createActivityCategory(impl);

		assertTrue(result.isSuccess());
		assertEquals(impl.getName(), result.getData().getName());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testCreateDuplicateActivityCategory() throws Exception {

		final ActivityCategory i1 = ActivityCategoryImpl.createNewDto("Fysisk aktivitet");
		final ServiceResult<ActivityCategory> result = this.service.createActivityCategory(i1);

		assertTrue(result.isSuccess());

		final ServiceResult<ActivityCategory> result2 = this.service.createActivityCategory(i1);
		assertFalse(result2.isSuccess());
		assertEquals(1, result2.getErrorMessages().size());
		assertTrue(result2.getErrorMessages().get(0) instanceof EntityNotUniqueMessage);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetActivityType() throws Exception {
		// First create an entity in the database
		final CountyCouncilEntity cc = getCountyCouncilRepository().save(CountyCouncilEntity.newEntity("SLL"));
		final CareUnitEntity cu = getCareUnitRepository().save(CareUnitEntity.newEntity("hsa-id", cc));

		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Tempkategori"));
		final ActivityTypeEntity ent = ActivityTypeEntity.newEntity("Springa", cat, cu, AccessLevel.CAREUNIT);
		MeasurementTypeEntity.newEntity(ent, "Distans", MeasurementValueType.SINGLE_VALUE, MeasureUnit.METER, false);
		MeasurementTypeEntity.newEntity(ent, "Vikt", MeasurementValueType.INTERVAL, MeasureUnit.KILOGRAM, true);
		final ActivityTypeEntity savedEnt = this.repo.save(ent);
		assertNotNull(savedEnt);

		String id = savedEnt.getId().toString();
		
		// Then the actual test:
		ServiceResult<ActivityType> result = this.service.getActivityType(id);
		assertNotNull("Result should not be null", result);
		assertTrue(result.isSuccess());
		assertEquals("Springa", result.getData().getName());

	}
	
	@Test
	@Rollback(true)
	public void searchTemplates() {
		
		authenticatedUser("hsa-care-actor", "hsa-care-unit", "SLL");

		final CountyCouncilEntity cc = getCountyCouncilRepository().saveAndFlush(CountyCouncilEntity.newEntity("LJ"));
		
		final CareUnitEntity cu = getCareUnitRepository().findByHsaId("hsa-care-unit");
		final CareUnitEntity cu2 = getCareUnitRepository().saveAndFlush(CareUnitEntity.newEntity("hsa-care-unit-2", cc));

		final ActivityCategoryEntity cat = this.catRepo.saveAndFlush(ActivityCategoryEntity.newEntity("Tempkategori"));
		final ActivityCategoryEntity cat2 = this.catRepo.saveAndFlush(ActivityCategoryEntity.newEntity("Tempkategori2"));
		final ActivityCategoryEntity cat3 = this.catRepo.saveAndFlush(ActivityCategoryEntity.newEntity("Tempkategori3"));
		final ActivityCategoryEntity cat4 = this.catRepo.saveAndFlush(ActivityCategoryEntity.newEntity("Tempkategori4"));
		
		final ActivityTypeEntity ent = ActivityTypeEntity.newEntity("Springa", cat, cu, AccessLevel.CAREUNIT);
		MeasurementTypeEntity.newEntity(ent, "Distans", MeasurementValueType.SINGLE_VALUE, MeasureUnit.METER, false);
		MeasurementTypeEntity.newEntity(ent, "Vikt", MeasurementValueType.INTERVAL, MeasureUnit.KILOGRAM, true);
		
		final ActivityTypeEntity savedEnt = this.repo.saveAndFlush(ent);
		assertNotNull(savedEnt);
		
		final ActivityTypeEntity ent2 = ActivityTypeEntity.newEntity("Springa", cat2, cu, AccessLevel.COUNTY_COUNCIL);
		MeasurementTypeEntity.newEntity(ent, "Distans", MeasurementValueType.SINGLE_VALUE, MeasureUnit.METER, false);
		MeasurementTypeEntity.newEntity(ent, "Vikt", MeasurementValueType.INTERVAL, MeasureUnit.KILOGRAM, true);
		
		final ActivityTypeEntity savedEnt2 = this.repo.saveAndFlush(ent2);
		assertNotNull(savedEnt2);

		final ActivityTypeEntity ent3 = ActivityTypeEntity.newEntity("Springa", cat3, cu2, AccessLevel.COUNTY_COUNCIL);
		MeasurementTypeEntity.newEntity(ent, "Distans", MeasurementValueType.SINGLE_VALUE, MeasureUnit.METER, false);
		MeasurementTypeEntity.newEntity(ent, "Vikt", MeasurementValueType.INTERVAL, MeasureUnit.KILOGRAM, true);
		
		final ActivityTypeEntity savedEnt3 = this.repo.saveAndFlush(ent3);
		assertNotNull(savedEnt3);
		
		final ActivityTypeEntity ent4 = ActivityTypeEntity.newEntity("Springa", cat4, cu, AccessLevel.NATIONAL);
		MeasurementTypeEntity.newEntity(ent, "Distans", MeasurementValueType.SINGLE_VALUE, MeasureUnit.METER, false);
		MeasurementTypeEntity.newEntity(ent, "Vikt", MeasurementValueType.INTERVAL, MeasureUnit.KILOGRAM, true);
		
		final ActivityTypeEntity savedEnt4 = this.repo.saveAndFlush(ent4);
		assertNotNull(savedEnt4);
		
		ServiceResult<ActivityType[]> result = this.service.searchForActivityTypes("ring", "all", "all");
		assertEquals(3, result.getData().length);
		
		result = this.service.searchForActivityTypes("", String.valueOf(cat.getId()), "all");
		assertEquals(1, result.getData().length);
		
		result = this.service.searchForActivityTypes("", "all", AccessLevel.COUNTY_COUNCIL.name());
		assertEquals(1, result.getData().length);
		
		result = this.service.searchForActivityTypes("", "all", AccessLevel.NATIONAL.name());
		assertEquals(1, result.getData().length);
	}
}
