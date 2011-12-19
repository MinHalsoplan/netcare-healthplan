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
package org.callistasoftware.netcare.core.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class PatientRepositoryTest extends TestSupport {

	@Autowired
	private PatientRepository repo;
	@Autowired
	private CareGiverRepository cgRepo;
	@Autowired
	private CareUnitRepository cuRepo;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testInsertFind() throws Exception {
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		cuRepo.save(cu);
		
		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67", cu);
		cgRepo.save(cg);
		
		final PatientEntity p1 = PatientEntity.newEntity("Marcus", "123456789004", cg);
		
		this.repo.save(p1);
		
		final List<PatientEntity> all = this.repo.findAll();
		assertNotNull(all);
		assertEquals(1, all.size());
		assertFalse(all.get(0).isMobile());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testProperties() {
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		cuRepo.save(cu);
		
		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67", cu);
		cgRepo.save(cg);

		final PatientEntity p = PatientEntity.newEntity("Arne", "123456789004", cg);
		p.getProperties().put("prop1", "val1");
		p.getProperties().put("prop2", "val2");
		repo.save(p);
		repo.flush();
		
		final PatientEntity p2 = repo.findOne(p.getId());
		assertEquals(2, p2.getProperties().size());
		
		p2.getProperties().remove("prop1");
		p2.getProperties().put("prop3", "val2");
		repo.save(p2);
		repo.flush();
		
		final PatientEntity p3 = repo.findOne(p.getId());
		assertEquals(2, p3.getProperties().size());
		assertNotNull(p3.getProperties().get("prop3"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testFindByFreeText() throws Exception {
		final List<PatientEntity> ents = new ArrayList<PatientEntity>();
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		this.cuRepo.save(cu);
		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67", cu);
		cgRepo.save(cg);

		final PatientEntity p1 = PatientEntity.newEntity("Arne", "123456789004", cg);
		final PatientEntity p2 = PatientEntity.newEntity("Bjarne", "123456789005", cg);
		final PatientEntity p3 = PatientEntity.newEntity("Peter", "123456789006", cg);
		final PatientEntity p4 = PatientEntity.newEntity("Marcus", "123456789007", cg);
		
		ents.add(p1);
		ents.add(p2);
		ents.add(p3);
		ents.add(p4);
		
		this.repo.save(ents);
		
		long count = this.repo.count();
		assertEquals(4, count);
		
		String search = "%123%";
		List<PatientEntity> result = this.repo.findByNameLikeOrEmailLikeOrCivicRegistrationNumberLike(search, search, search);
		assertNotNull(result);
		assertEquals(4, result.size());
		
		search = "%004%";
		result = this.repo.findByNameLikeOrEmailLikeOrCivicRegistrationNumberLike(search, search, search);
		assertNotNull(result);
		assertEquals(1, result.size());
		
		search = "%rne%";
		result = this.repo.findByNameLikeOrEmailLikeOrCivicRegistrationNumberLike(search, search, search);
		assertNotNull(result);
		assertEquals(2, result.size());
		
		search = "%Arn%";
		result = this.repo.findByNameLikeOrEmailLikeOrCivicRegistrationNumberLike(search, search, search);
		assertNotNull(result);
		assertEquals(1, result.size());
	}
}
