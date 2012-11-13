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
package org.callistasoftware.netcare.core.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.AccessLevel;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class ActivityTypeRepositoryTest extends TestSupport {

	@Autowired
	private CareUnitRepository cuRepo;

	@Autowired
	private CountyCouncilRepository ccRepo;

	@Autowired
	private ActivityCategoryRepository catRepo;

	@Autowired
	private ActivityTypeRepository repo;
	
	@Autowired
	private MeasureUnitRepository measureRepo;

	@Test
	@Transactional
	@Rollback(true)
	public void testInsertFind() throws Exception {

		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity("SLL"));
		final CareUnitEntity cu = cuRepo.save(CareUnitEntity.newEntity("hsa-id", cc));

		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		final ActivityTypeEntity ent = ActivityTypeEntity.newEntity("Löpning", cat, cu, AccessLevel.CAREUNIT);
		MeasurementTypeEntity.newEntity(ent, "Distans", MeasurementValueType.SINGLE_VALUE, newMeasureUnit("m", "Meter", cc), false, 0);
		MeasurementTypeEntity.newEntity(ent, "Vikt", MeasurementValueType.INTERVAL, newMeasureUnit("kg", "Kilogram", cc), true, 1);
		final ActivityTypeEntity savedEnt = this.repo.save(ent);

		assertNotNull(savedEnt);
		assertNotNull(savedEnt.getId());
		assertEquals(ent.getName(), savedEnt.getName());
		assertEquals(ent.getActivityItemTypes().size(), savedEnt.getActivityItemTypes().size());
		assertNotNull(savedEnt.getActivityItemTypes());
		assertEquals("Vikt", ent.getActivityItemTypes().get(1).getName());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testInsertFindCountyCouncil() throws Exception {
		CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity("SLL"));
		CountyCouncilEntity ccFind = ccRepo.findOne(cc.getId());
		assertEquals("SLL", ccFind.getName());
	}
}
