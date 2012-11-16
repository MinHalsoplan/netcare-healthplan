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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.AccessLevel;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class ScheduledActivityRepositoryTest extends TestSupport {

	@Autowired
	private CareUnitRepository cuRepo;

	@Autowired
	private CareActorRepository careActorRepo;

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

	@Autowired
	private CountyCouncilRepository ccRepo;

	private ScheduledActivityEntity setup() {
		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity("SLL"));
		final CareUnitEntity cu = CareUnitEntity.newEntity("hsa-id-4321", cc);
		final CareUnitEntity savedCu = cuRepo.save(cu);

		final CareActorEntity ca = CareActorEntity.newEntity("Marcus", "", "hsa-id-1234", savedCu);
		final CareActorEntity savedCa = this.careActorRepo.save(ca);

		final PatientEntity p = PatientEntity.newEntity("Marcus", "", "123456789002");
		final PatientEntity savedPatient = this.pRepo.save(p);

		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));

		final ActivityTypeEntity at = ActivityTypeEntity.newEntity("Löpning", cat, cu, AccessLevel.CAREUNIT);
		MeasurementTypeEntity.newEntity(at, "Distans", MeasurementValueType.SINGLE_VALUE, newMeasureUnit("m", "Meter", cc), false, 0);
		MeasurementTypeEntity.newEntity(at, "Vikt", MeasurementValueType.INTERVAL, newMeasureUnit("kg", "Kilogram", cc), true, 0);
		final ActivityTypeEntity savedAt = this.atRepo.save(at);

		final HealthPlanEntity hp = HealthPlanEntity.newEntity(savedCa, savedPatient, "Health plan", new Date(), 12,
				DurationUnit.MONTH);
		final HealthPlanEntity savedHp = this.hpRepo.save(hp);

		final ActivityDefinitionEntity def = ActivityDefinitionEntity.newEntity(savedHp, savedAt,
				Frequency.unmarshal("1;1"), ca);
		final ActivityDefinitionEntity saved = this.adRepo.save(def);

		ScheduledActivityEntity e = ScheduledActivityEntity.newEntity(saved, new Date());

		e = this.repo.save(e);

		return e;
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testFindByCareUnit() {

		ScheduledActivityEntity e = setup();
		e.setReportedTime(new Date());
		e = this.repo.save(e);

		final List<ScheduledActivityEntity> result = this.repo.findByCareUnit("hsa-id-4321");
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("hsa-id-4321", result.get(0).getActivityDefinitionEntity().getHealthPlan().getCareUnit()
				.getHsaId());
		assertEquals(result.get(0).getActivities().get(0).getActivityItemDefinitionEntity().getActivityItemType()
				.getSeqno(), 0);
		e.setReportedTime(null);
		e = this.repo.save(e);

		Calendar cal = Calendar.getInstance();
		assertEquals(1, repo.findByScheduledTimeLessThanAndReportedTimeIsNull(cal.getTime()).size());
		cal.add(Calendar.DATE, -1);
		assertEquals(0, repo.findByScheduledTimeLessThanAndReportedTimeIsNull(cal.getTime()).size());

		e.setReportedTime(new Date());

		this.repo.save(e);
		cal.add(Calendar.DATE, 1);
		assertEquals(0, repo.findByScheduledTimeLessThanAndReportedTimeIsNull(cal.getTime()).size());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testFindByCareUnitPatientBetween() {

		String careUnit = "hsa-id-4321";

		ScheduledActivityEntity e = setup();
		e.setReportedTime(new Date());
		e = this.repo.save(e);

		Date zero = new Date(0);
		Date now = new Date();

		PatientEntity patient = e.getActivityDefinitionEntity().getHealthPlan().getForPatient();

		final List<ScheduledActivityEntity> result = this.repo.findByCareUnitPatientBetween(careUnit, patient, zero,
				now);

		assertNotNull(result);
		assertEquals(1, result.size());

		assertEquals("hsa-id-4321", result.get(0).getActivityDefinitionEntity().getHealthPlan().getCareUnit()
				.getHsaId());
		assertEquals(patient.getCivicRegistrationNumber(), result.get(0).getActivityDefinitionEntity().getHealthPlan()
				.getForPatient().getCivicRegistrationNumber());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testFindByScheduledTimeLessThanAndReportedTimeIsNull() {
		setup();

		final List<ScheduledActivityEntity> result = repo.findByScheduledTimeLessThanAndReportedTimeIsNull(new Date());

		assertEquals(1, result.size());
		assertEquals(false, result.get(0).isReminderDone());

	}

}
