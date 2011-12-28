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
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityDefintionImpl;
import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.callistasoftware.netcare.core.api.impl.CareGiverBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.DayTimeImpl;
import org.callistasoftware.netcare.core.api.impl.HealthPlanImpl;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.support.TestSupport;
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
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class HealthPlanServiceTest extends TestSupport {
	
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
	private ScheduledActivityRepository schedRepo;
	
	@Autowired
	private HealthPlanService service;

	private HealthPlanImpl createHealthPlan(String name, String startDate, int duration, String unit) {
		final HealthPlanImpl o = new HealthPlanImpl();
		o.setName(name);
		o.setStartDate(startDate);
		o.setDuration(duration);
		o.setDurationUnit(new Option(unit,  LocaleContextHolder.getLocale()));		
		return o;
	}
	
	private ServiceResult<HealthPlan> createHealthPlan(HealthPlan o) {
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		this.cuRepo.save(cu);
		
		final CareGiverEntity cg = CareGiverEntity.newEntity("Test Testgren", "hsa-123-id", cu);
		this.cgRepo.save(cg);
		
		final CareGiverBaseViewImpl cgDto = new CareGiverBaseViewImpl();
		cgDto.setHsaId("hsa-123-id");
				
		final PatientEntity patient = PatientEntity.newEntity("Peter Larsson", "611028");
		patientRepo.save(patient);
		
		final ServiceResult<HealthPlan> saved = this.service.createNewHealthPlan(o, cgDto, patient.getId());
		
		return saved;
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCreateHealthPlan() throws Exception {
		
		final HealthPlanImpl o = createHealthPlan("Test", "2011-12-12", 12, DurationUnit.WEEKS.name());

		final ServiceResult<HealthPlan> saved = createHealthPlan(o);
		
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

		// the date and duration can't be changed without breaking the test, see further below.
		final HealthPlanEntity ord = HealthPlanEntity.newEntity(savedCg, savedPatient, "Test", ApiUtil.parseDate("2012-12-01"), 12, DurationUnit.WEEKS);
		final HealthPlanEntity savedOrd = this.ordinationRepo.save(ord);
		
		final ActivityTypeImpl typeImpl = new ActivityTypeImpl();
		typeImpl.setId(savedType.getId());
		typeImpl.setName("Löpning");
		typeImpl.setUnit(new Option(MeasureUnit.KILOMETERS.name(), LocaleContextHolder.getLocale()));
		
		final ActivityDefintionImpl impl = new ActivityDefintionImpl();
		
		impl.setType(typeImpl);
		impl.setGoal(12);
		impl.setActivityRepeat(2);
		
		// Monday and saturday
		final DayTimeImpl dt = new DayTimeImpl();
		dt.setDay("monday");
		dt.setTimes(new String[]{"12:15", "18:45"});
		
		final DayTimeImpl dt2 = new DayTimeImpl();
		dt2.setDay("saturday");
		
		dt2.setTimes(new String[]{"12:15", "18:45"});
		final DayTimeImpl[] dts = new DayTimeImpl[2];
		dts[0] = dt;
		dts[1] = dt2;
		
		impl.setDayTimes(dts);
		
		final ServiceResult<HealthPlan> result = this.service.addActvitiyToHealthPlan(savedOrd.getId(), impl, CareGiverBaseViewImpl.newFromEntity(cg));
		assertTrue(result.isSuccess());
		
		final ActivityDefintionImpl impl2 = new ActivityDefintionImpl();
		impl2.setType(typeImpl);
		impl2.setGoal(10);
		impl2.setActivityRepeat(0);
		impl2.setStartDate("2013-01-30");
		
		// Wednesday (1 time activity)
		final DayTimeImpl dt3 = new DayTimeImpl();
		dt3.setDay("wednesday");
		dt3.setTimes(new String[] { "07:15", "12:00", "22:45"});
		impl2.setDayTimes(new DayTimeImpl[] { dt3 });		
		final ServiceResult<HealthPlan> result2 = this.service.addActvitiyToHealthPlan(savedOrd.getId(), impl2, CareGiverBaseViewImpl.newFromEntity(cg));
		assertTrue(result2.isSuccess());
		
		this.schedRepo.flush();
		this.ordinationRepo.flush();
		
		final HealthPlanEntity after = this.ordinationRepo.findOne(savedOrd.getId());
		final ActivityDefinitionEntity ent = after.getActivityDefinitions().get(0);
		
		assertEquals(MeasureUnit.KILOMETERS, ent.getActivityType().getUnit());
		assertEquals("Löpning", ent.getActivityType().getName());
		assertEquals(12, ent.getActivityTarget());
		
		final Frequency fr = ent.getFrequency();
		final List<FrequencyTime> times = fr.getDay(Calendar.MONDAY).getTimes();
		assertEquals(2, times.size());
		
		assertEquals("2012-12-01", ApiUtil.formatDate(after.getStartDate()));
		assertEquals(12, times.get(0).getHour());
		assertEquals(15, times.get(0).getMinute());
		
		assertEquals(18, times.get(1).getHour());
		assertEquals(45, times.get(1).getMinute());
		
		assertTrue(fr.isDaySet(Calendar.MONDAY));
		assertTrue(fr.isDaySet(Calendar.SATURDAY));
		assertEquals(2, fr.getWeekFrequency());
		
		assertEquals("7,12:15,18:45", FrequencyDay.marshal(fr.getDay(Calendar.SATURDAY)));
		
		
		// check 
		Date startDate = after.getStartDate();
		Date endDate = after.getEndDate();
		List<ScheduledActivityEntity> scheduledActivities = schedRepo.findByPatientAndScheduledTimeBetween(savedPatient, startDate, endDate);
		Collections.sort(scheduledActivities);

		int n = scheduledActivities.size();
		// 26 (every other week * 2) + 3 (single day * 3)
		assertEquals(29, n);
		assertEquals("2013-02-23", ApiUtil.formatDate(scheduledActivities.get(n-1).getScheduledTime()));
	}

}
