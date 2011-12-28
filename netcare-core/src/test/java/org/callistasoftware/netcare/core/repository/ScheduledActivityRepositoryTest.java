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

import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

public class ScheduledActivityRepositoryTest extends TestSupport {

	@Autowired
	private CareUnitRepository cuRepo;
	
	@Autowired
	private CareGiverRepository cgRepo;
	
	@Autowired
	private PatientRepository pRepo;
	
	@Autowired
	private ActivityCategoryRepository catRepo;
	
	@Autowired
	private ActivityTypeRepository atRepo;
	
	@Autowired
	private HealthPlanRepository hpRepo;
	
	@Autowired
	private ActivityDefinitionRepository adRepo;
	
	@Autowired
	private ScheduledActivityRepository repo;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testFindByCareUnit() throws Exception {
		
		final CareUnitEntity cu = CareUnitEntity.newEntity("hsa-id-4321");
		final CareUnitEntity savedCu = cuRepo.save(cu);
		
		final CareGiverEntity cg = CareGiverEntity.newEntity("Marcus", "hsa-id-1234", savedCu);
		final CareGiverEntity savedCg = this.cgRepo.save(cg);
		
		final PatientEntity p = PatientEntity.newEntity("Marcus", "123456789002");
		final PatientEntity savedPatient = this.pRepo.save(p);
		
		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		
		final ActivityTypeEntity at = ActivityTypeEntity.newEntity("Löpning", cat, MeasureUnit.KILOMETERS);
		final ActivityTypeEntity savedAt = this.atRepo.save(at);
		
		final HealthPlanEntity hp = HealthPlanEntity.newEntity(savedCg, savedPatient, "Health plan", new Date(), 12, DurationUnit.MONTHS);
		final HealthPlanEntity savedHp = this.hpRepo.save(hp);
		
		final ActivityDefinitionEntity def = ActivityDefinitionEntity.newEntity(savedHp, savedAt, Frequency.unmarshal("1;1"));
		final ActivityDefinitionEntity savedDef = this.adRepo.save(def);
		
		final ScheduledActivityEntity e = ScheduledActivityEntity.newEntity(savedDef, new Date());
		e.setReportedTime(new Date());
		this.repo.save(e);
		
		final List<ScheduledActivityEntity> result = this.repo.findByCareUnit("hsa-id-4321");
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("hsa-id-4321", result.get(0).getActivityDefinitionEntity().getHealthPlan().getCareUnit().getHsaId());
	}
}
