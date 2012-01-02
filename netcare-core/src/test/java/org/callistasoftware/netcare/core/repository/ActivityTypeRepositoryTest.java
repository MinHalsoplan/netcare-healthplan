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

import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

public class ActivityTypeRepositoryTest extends TestSupport {

	@Autowired
	private ActivityCategoryRepository catRepo;
	
	@Autowired
	private ActivityTypeRepository repo;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testInsertFind() throws Exception {
		
		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		
		final ActivityTypeEntity ent = ActivityTypeEntity.newEntity("Löpning", cat, MeasureUnit.METER);
		final ActivityTypeEntity savedEnt = this.repo.save(ent);
		
		assertNotNull(savedEnt);
		assertNotNull(savedEnt.getId());
		assertEquals(ent.getName(), savedEnt.getName());
		assertEquals(ent.getUnit(), savedEnt.getUnit());
	}
}
