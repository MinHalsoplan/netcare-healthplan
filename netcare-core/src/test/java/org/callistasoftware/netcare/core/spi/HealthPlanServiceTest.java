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

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityDefintionImpl;
import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.callistasoftware.netcare.core.api.impl.CareGiverBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.HealthPlanImpl;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.FrequencyDay;
import org.callistasoftware.netcare.model.entity.FrequencyTime;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/netcare-config.xml")
public class HealthPlanServiceTest {
	
	@Autowired
	private CareGiverRepository cgRepo;
	@Autowired
	private PatientRepository patientRepo;
	@Autowired
	private HealthPlanRepository ordinationRepo;
	@Autowired
	private ActivityTypeRepository typeRepo;
	@Autowired
	private CareUnitRepository cuRepo;
	
	@Autowired
	private HealthPlanService service;

	@Test
	@Transactional
	@Rollback(true)
	public void testCreateOrdination() throws Exception {
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		this.cuRepo.save(cu);
		
		final CareGiverEntity cg = CareGiverEntity.newEntity("Test Testgren", "hsa-123-id", cu);
		this.cgRepo.save(cg);
		
		final CareGiverBaseViewImpl cgDto = new CareGiverBaseViewImpl();
		cgDto.setHsaId("hsa-123-id");
		
		final HealthPlanImpl o = new HealthPlanImpl();
		o.setName("Test");
		o.setStartDate("2011-12-12");
		o.setDuration(12);
		o.setDurationUnit(new Option(DurationUnit.WEEKS.name(), null));
		
		final PatientEntity patient = PatientEntity.newEntity("Peter Larsson", "611028");
		patientRepo.save(patient);
		
		final ServiceResult<HealthPlan> saved = this.service.createNewHealthPlan(o, cgDto, patient.getId());
		
		assertEquals(o.getName(), saved.getData().getName());
		assertEquals(o.getStartDate(), saved.getData().getStartDate());
		assertEquals(o.getDuration(), saved.getData().getDuration());
		assertEquals(o.getDurationUnit().getCode(), saved.getData().getDurationUnit().getCode());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testAddActivityDefintion() throws Exception {
		final ActivityTypeEntity type = ActivityTypeEntity.newEntity("Löpning", MeasureUnit.KILOMETERS);
		final ActivityTypeEntity savedType = typeRepo.save(type);

		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		this.cuRepo.save(cu);
		
		final CareGiverEntity cg = CareGiverEntity.newEntity("Test Testgren", "hsa-123", cu);
		final CareGiverEntity savedCg = this.cgRepo.save(cg);
		
		final PatientEntity patient = PatientEntity.newEntity("Marcus Krantz", "123456789004");
		final PatientEntity savedPatient = this.patientRepo.save(patient);
		
		final HealthPlanEntity ord = HealthPlanEntity.newEntity(savedCg, savedPatient, "Test", new Date(), 12, DurationUnit.WEEKS);
		final HealthPlanEntity savedOrd = this.ordinationRepo.save(ord);
		
		final ActivityTypeImpl typeImpl = new ActivityTypeImpl();
		typeImpl.setId(savedType.getId());
		typeImpl.setName("Löpning");
		typeImpl.setUnit(new Option(MeasureUnit.KILOMETERS.name(), null));
		
		final ActivityDefintionImpl impl = new ActivityDefintionImpl();
		// Monday and wednesday
		impl.setDays(new int[]{0, 2});
		impl.setGoal(12);
		impl.setTimes(new String[] { "12:15", "18:45"});
		impl.setType(typeImpl);
		
		final ServiceResult<HealthPlan> result = this.service.addActvitiyToHealthPlan(savedOrd.getId(), (ActivityDefinition) impl);
		assertTrue(result.isSuccess());
		
		final HealthPlanEntity after = this.ordinationRepo.findOne(savedOrd.getId());
		final ActivityDefinitionEntity ent = after.getActivityDefinitions().get(0);
		
		assertEquals(MeasureUnit.KILOMETERS, ent.getActivityType().getUnit());
		assertEquals("Löpning", ent.getActivityType().getName());
		assertEquals(12, ent.getActivityTarget());
		
		final Frequency fr = ent.getFrequency();
		final List<FrequencyTime> times = fr.getTimes();
		assertEquals(2, times.size());
		
		assertEquals(12, times.get(0).getHour());
		assertEquals(15, times.get(0).getMinute());
		
		assertEquals(18, times.get(1).getHour());
		assertEquals(45, times.get(1).getMinute());
		
		final FrequencyDay frDay = fr.getFrequencyDay();
		assertTrue(frDay.isMonday());
		assertTrue(frDay.isWedbesday());
		
		assertEquals("101", FrequencyDay.marshal(frDay));
	}

}
