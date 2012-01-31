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

import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.FrequencyDay;
import org.callistasoftware.netcare.model.entity.FrequencyTime;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.UserEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class HealthPlanRepositoryTest extends TestSupport {

	@Autowired
	private HealthPlanRepository repo;
	@Autowired
	private ActivityDefinitionRepository actRepo;
	@Autowired
	private ActivityCategoryRepository catRepo;
	@Autowired
	private ActivityTypeRepository typeRepo;
	@Autowired
	private CareGiverRepository cgRepo;
	@Autowired
	private PatientRepository patientRepo;
	@Autowired
	private CareUnitRepository cuRepo;
	
	ActivityDefinitionEntity createActivityDefinition(HealthPlanEntity healthPlan, UserEntity user) {
		Frequency freq = new Frequency();
		freq.setWeekFrequency(1);
		FrequencyDay day = FrequencyDay.newFrequencyDay(Calendar.MONDAY);
		FrequencyTime time = FrequencyTime.unmarshal("10:00");
		day.addTime(time);
		
		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		
		final ActivityTypeEntity type = ActivityTypeEntity.newEntity("test", cat);
		MeasurementTypeEntity.newEntity(type, "Distans", MeasurementValueType.SINGLE_VALUE, MeasureUnit.METER);
		MeasurementTypeEntity me = MeasurementTypeEntity.newEntity(type, "Vikt", MeasurementValueType.INTERVAL, MeasureUnit.KILOGRAM);
		me.setAlarmEnabled(true);
		typeRepo.save(type);
		typeRepo.flush();

		return ActivityDefinitionEntity.newEntity(healthPlan, type, freq, user);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testInsertFind() throws Exception {
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		this.cuRepo.save(cu);
		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67", cu);
		cgRepo.save(cg);
		cgRepo.flush();
		
		final PatientEntity patient = PatientEntity.newEntity("Peter", "123456");
		patientRepo.save(patient);
		patientRepo.flush();
		
		final HealthPlanEntity e1 = HealthPlanEntity.newEntity(cg, patient, "Hälsoplan B", new Date(), 20, DurationUnit.WEEK);
		
		ActivityDefinitionEntity ad =  createActivityDefinition(e1, cg);
		
		actRepo.save(ad);

		repo.save(e1);
		repo.flush();
		
		final List<HealthPlanEntity> all = repo.findAll();
		assertNotNull(all);
		assertEquals(1, all.size());
		
		HealthPlanEntity e2 = all.get(0);
		assertEquals("Hälsoplan B", e2.getName());
		assertEquals(DurationUnit.WEEK, e2.getDurationUnit());
		assertEquals(20, e2.getDuration());
		
		Calendar c = Calendar.getInstance();
		c.setTime(e2.getStartDate());
		c.add(Calendar.WEEK_OF_YEAR, e2.getDuration());
		assertEquals(ApiUtil.dayEnd(c).getTime(), e2.getEndDate());
		
		assertEquals(1, e2.getActivityDefinitions().size());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testFindByForPatient() throws Exception {
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		this.cuRepo.save(cu);
		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67", cu);
		cgRepo.save(cg);
		cgRepo.flush();
		
		final PatientEntity patient = PatientEntity.newEntity("Peter", "123456");
		patientRepo.save(patient);
		patientRepo.flush();
		
		repo.save(HealthPlanEntity.newEntity(cg, patient, "Hälsoplan B", new Date(), 20, DurationUnit.WEEK));
		repo.save(HealthPlanEntity.newEntity(cg, patient, "Hälsoplan A", new Date(), 3, DurationUnit.MONTH));
		repo.flush();
		
		List<HealthPlanEntity> list = repo.findByForPatient(patient);
		
		assertEquals(2, list.size());
		
		assertEquals("Hälsoplan B", list.get(0).getName());
		assertEquals(DurationUnit.WEEK, list.get(0).getDurationUnit());
		assertEquals(20, list.get(0).getDuration());
		assertEquals(DurationUnit.MONTH, list.get(1).getDurationUnit());
		assertEquals(3, list.get(1).getDuration());
		assertEquals(cg, list.get(0).getIssuedBy());
		assertEquals(cg, list.get(1).getIssuedBy());
		assertEquals(patient, list.get(0).getForPatient());
		assertEquals(patient, list.get(1).getForPatient());
	}
}
