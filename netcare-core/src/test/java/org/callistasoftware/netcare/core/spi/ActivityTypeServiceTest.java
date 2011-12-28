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
package org.callistasoftware.netcare.core.spi;

import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.repository.ActivityCategoryRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

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
		
		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		
		for (int i = 0; i < 10; i++) {
			this.repo.save(ActivityTypeEntity.newEntity("Type-" + i, cat, MeasureUnit.KILOMETERS));
		}
		
		final ServiceResult<ActivityType[]> result = this.service.loadAllActivityTypes();
		assertTrue(result.isSuccess());
		assertEquals(10, result.getData().length);
	}
}
