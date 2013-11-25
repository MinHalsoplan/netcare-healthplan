/**
 * Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.AccessLevel;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityCommentEntity;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncil;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.UserEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class PatientRepositoryTest extends TestSupport {

	@Autowired
	private PatientRepository repo;
	@Autowired
	private CareActorRepository careActorRepo;
	@Autowired
	private HealthPlanRepository hpRepo;
	@Autowired
	private CareUnitRepository cuRepo;
	@Autowired
	private CountyCouncilRepository ccRepo;
	@Autowired
	private ScheduledActivityRepository schRepo;
	@Autowired
	private ActivityDefinitionRepository adRepo;
	@Autowired
	private ActivityCommentRepository acRepo;
	@Autowired
	private ActivityTypeRepository atRepo;
	@Autowired
	private ActivityCategoryRepository acatRepo;
	@Autowired
	private UserRepository userRepo;
	

	@Test
	@Transactional
	@Rollback(true)
	public void testInsertFind() throws Exception {
		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity(CountyCouncil.STOCKHOLM));
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu", cc);
		cuRepo.save(cu);

		final CareActorEntity ca = CareActorEntity.newEntity("Doctor Hook", "", "12345-67", cu);
		careActorRepo.save(ca);

		final PatientEntity p1 = PatientEntity.newEntity("Marcus", "", "19121212-1212");

		this.repo.save(p1);

		final List<PatientEntity> all = this.repo.findAll();

		assertNotNull(all);
		assertEquals(1, all.size());
		assertEquals(all.get(0).getCivicRegistrationNumber(), "191212121212");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testProperties() {
		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity(CountyCouncil.STOCKHOLM));
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu", cc);
		cuRepo.save(cu);

		final CareActorEntity ca = CareActorEntity.newEntity("Doctor Hook", "", "12345-67", cu);
		careActorRepo.save(ca);

		final PatientEntity p = PatientEntity.newEntity("Arne", "", "123456789004");
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
		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity(CountyCouncil.STOCKHOLM));
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu", cc);
		this.cuRepo.save(cu);
		final CareActorEntity ca = CareActorEntity.newEntity("Doctor Hook", "", "12345-67", cu);
		careActorRepo.save(ca);

		final PatientEntity p1 = PatientEntity.newEntity("Arne", "", "123456789004");
		final PatientEntity p2 = PatientEntity.newEntity("Bjarne", "", "123456789005");
		final PatientEntity p3 = PatientEntity.newEntity("Peter", "", "123456789006");
		final PatientEntity p4 = PatientEntity.newEntity("Marcus", "", "123456789007");

		ents.add(p1);
		ents.add(p2);
		ents.add(p3);
		ents.add(p4);

		this.repo.save(ents);

		long count = this.repo.count();
		assertEquals(4, count);

		String search = "%123%";
		List<PatientEntity> result = this.repo.findPatients(search);
		assertNotNull(result);
		assertEquals(4, result.size());

		search = "%004%";
		result = this.repo.findPatients(search);
		assertNotNull(result);
		assertEquals(1, result.size());

		search = "%rne%";
		result = this.repo.findPatients(search);
		assertNotNull(result);
		assertEquals(2, result.size());

		search = "%Arn%";
		result = this.repo.findPatients(search);
		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void findPatientsWithHealthPlansAtCareUnit() throws Exception {

		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity(CountyCouncil.STOCKHOLM));
		final CareUnitEntity cu = this.cuRepo.save(CareUnitEntity.newEntity("hsa-id", cc));
		final CareActorEntity ca = this.careActorRepo.save(CareActorEntity.newEntity("Test", "", "hsa-2", cu));
		final PatientEntity patient = this.repo.save(PatientEntity.newEntity("Marcus", "", "123456789004"));
		final PatientEntity patient2 = this.repo.save(PatientEntity.newEntity("Peter", "", "123456789005"));

		this.hpRepo.save(HealthPlanEntity.newEntity(ca, patient, "Testplan", new Date(), 12, DurationUnit.WEEK));
		this.hpRepo.save(HealthPlanEntity.newEntity(ca, patient2, "Testplan", new Date(), 12, DurationUnit.WEEK));

		final List<PatientEntity> patients = this.repo.findByCareUnit("hsa-id");
		assertNotNull(patients);
		assertEquals(2, patients.size());
	}
}
