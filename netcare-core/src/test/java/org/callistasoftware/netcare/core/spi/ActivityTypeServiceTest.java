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
import static org.junit.Assert.assertTrue;

import org.callistasoftware.netcare.core.api.ActivityCategory;
import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityCategoryImpl;
import org.callistasoftware.netcare.core.api.impl.CareGiverBaseViewImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotUniqueMessage;
import org.callistasoftware.netcare.core.repository.ActivityCategoryRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
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
	private CareUnitRepository cuRepo;
	
	@Autowired
	private CareGiverRepository cgRepo;
	
	@Autowired
	private ActivityTypeService service;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testLoadAllActivityTypes() throws Exception {
		
		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		final CareUnitEntity cu = this.cuRepo.save(CareUnitEntity.newEntity("hsa-id"));
		
		final CareGiverEntity cg = this.cgRepo.save(CareGiverEntity.newEntity("Dr Marcus", "hsa-id-cg", cu));
		final CareGiverBaseView cgb = CareGiverBaseViewImpl.newFromEntity(cg);
		
		this.runAs(cgb);
		
		for (int i = 0; i < 10; i++) {
			this.repo.save(ActivityTypeEntity.newEntity("Type-" + i, cat, cu));
		}
		
		final ServiceResult<ActivityType[]> result = this.service.loadAllActivityTypes();
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
}
