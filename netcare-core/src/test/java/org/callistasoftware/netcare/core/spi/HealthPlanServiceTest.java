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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.PatientEvent;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityDefintionImpl;
import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.callistasoftware.netcare.core.api.impl.CareGiverBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.DayTimeImpl;
import org.callistasoftware.netcare.core.api.impl.HealthPlanImpl;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.repository.ActivityCategoryRepository;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
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
	private ActivityCategoryRepository catRepo;
	@Autowired
	private ActivityTypeRepository typeRepo;
	@Autowired
	private CareUnitRepository cuRepo;
	@Autowired
	private ScheduledActivityRepository schedRepo;
	@Autowired
	private ActivityDefinitionRepository defRepo;
	
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
	

	private ActivityDefinitionEntity createActivityDefinitionEntity() {
		final CareUnitEntity cu = CareUnitEntity.newEntity("care-unit-hsa-123");
		cu.setName("Jönköpings vårdcentral");
		cuRepo.save(cu);
		cuRepo.flush();
		
		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67", cu);
		cgRepo.save(cg);

		final PatientEntity p2 = PatientEntity.newEntity("Peter Larsson", "191212121212");
		patientRepo.save(p2);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		HealthPlanEntity hp = HealthPlanEntity.newEntity(cg, p2, "Auto", cal.getTime(), 6, DurationUnit.MONTH);
		ordinationRepo.save(hp);
		final ActivityCategoryEntity cat = catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));

		ActivityTypeEntity at = ActivityTypeEntity.newEntity("Löpning", cat);
		at.setMeasuringSense(true);
		at.setSenseLabelHigh("Mycket Trötthet");
		at.setSenseLabelLow("Väldigt Lätt");
		typeRepo.save(at);
		Frequency frequency = Frequency.unmarshal("1;1;2,18:15;5,07:00,19:00");
		ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, at, frequency, cg);
		ad.setActivityTarget(1200);
		defRepo.save(ad);
		
		schedRepo.save(ad.scheduleActivities());
		
		return ad;
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
		
		final HealthPlanImpl o = createHealthPlan("Test", "2011-12-12", 12, DurationUnit.WEEK.name());

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
		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		
		final ActivityTypeEntity type = ActivityTypeEntity.newEntity("Löpning", cat);
		final ActivityTypeEntity savedType = typeRepo.save(type);

		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		this.cuRepo.save(cu);
		
		final CareGiverEntity cg = CareGiverEntity.newEntity("Test Testgren", "hsa-123", cu);
		final CareGiverEntity savedCg = this.cgRepo.save(cg);
		
		final PatientEntity patient = PatientEntity.newEntity("Marcus Krantz", "123456789004");
		final PatientEntity savedPatient = this.patientRepo.save(patient);

		// the date and duration can't be changed without breaking the test, see further below.
		final HealthPlanEntity ord = HealthPlanEntity.newEntity(savedCg, savedPatient, "Test", ApiUtil.parseDate("2012-12-01"), 12, DurationUnit.WEEK);
		final HealthPlanEntity savedOrd = this.ordinationRepo.save(ord);
		
		final ActivityTypeImpl typeImpl = new ActivityTypeImpl();
		typeImpl.setId(savedType.getId());
		typeImpl.setName("Löpning");
		//typeImpl.setUnit(new Option(MeasureUnit.METER.name(), LocaleContextHolder.getLocale()));
		
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
		
		final CareGiverBaseView cgbv = CareGiverBaseViewImpl.newFromEntity(savedCg);
		this.runAs(cgbv);
		
		final ServiceResult<HealthPlan> result = this.service.addActvitiyToHealthPlan(savedOrd.getId(), impl, cgbv);
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
		
		/*
		 * FIXME
		 */
		//assertEquals(MeasureUnit.METER, ent.getActivityType().getUnit());
		//assertEquals("Löpning", ent.getActivityType().getName());
		//assertEquals(12, ent.getActivityTarget());
		
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

	@Test
	@Transactional
	@Rollback(true)
	public void iCalendar() {
		ActivityDefinitionEntity ad = createActivityDefinitionEntity();
		String cal = service.getICalendarEvents(PatientBaseViewImpl.newFromEntity(ad.getHealthPlan().getForPatient()));
		final String expect = "BEGIN:VCALENDAR\r\n"
		      + "VERSION:2.0\r\n"
		      + "PRODID:-//Callista Enterprise//NONSGML NetCare//EN\r\n"
		      + "BEGIN:VEVENT\r\n"
		      + "UID:cae745bc-1278-4828-96f5-86b221af99db@MO.0\r\n"
		      + "DTSTAMP;TZID=Europe/Stockholm:20120105T175109\r\n"
		      + "DTSTART;TZID=Europe/Stockholm:20111206T181500\r\n"
		      + "DURATION:PT30M\r\n"
		      + "SUMMARY:Löpning 1200 METER\r\n"
		      + "TRANSP:TRANSPARENT\r\n"
		      + "CLASS:CONFIDENTIAL\r\n"
		      + "CATEGORIES:FYSIK,PERSONLIGT,PLAN,HÄLSA\r\n"
		      + "RRULE:FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=MO;UNTIL=20120606T235959\r\n"
		      + "END:VEVENT\r\n"
		      + "BEGIN:VEVENT\r\n"
		      + "UID:cae745bc-1278-4828-96f5-86b221af99db@TH.0\r\n"
		      + "DTSTAMP;TZID=Europe/Stockholm:20120105T175109\r\n"
		      + "DTSTART;TZID=Europe/Stockholm:20111206T070000\r\n"
		      + "DURATION:PT30M\r\n"
		      + "SUMMARY:Löpning 1200 METER\r\n"
		      + "TRANSP:TRANSPARENT\r\n"
		      + "CLASS:CONFIDENTIAL\r\n"
		      + "CATEGORIES:FYSIK,PERSONLIGT,PLAN,HÄLSA\r\n"
		      + "RRULE:FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=TH;UNTIL=20120606T235959\r\n"
		      + "END:VEVENT\r\n"
		      + "BEGIN:VEVENT\r\n"
		      + "UID:cae745bc-1278-4828-96f5-86b221af99db@TH.1\r\n"
		      + "DTSTAMP;TZID=Europe/Stockholm:20120105T175109\r\n"
		      + "DTSTART;TZID=Europe/Stockholm:20111206T190000\r\n"
		      + "DURATION:PT30M\r\n"
		      + "SUMMARY:Löpning 1200 METER\r\n"
		      + "TRANSP:TRANSPARENT\r\n"
		      + "CLASS:CONFIDENTIAL\r\n"
		      + "CATEGORIES:FYSIK,PERSONLIGT,PLAN,HÄLSA\r\n"
		      + "RRULE:FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=TH;UNTIL=20120606T235959\r\n"
		      + "END:VEVENT\r\n"
		      + "END:VCALENDAR\r\n";
		
		// FIXME: better test needed! The generated UID makes it impossible to make a straight comparison.
		assertEquals(expect.split("\r\n").length, cal.split("\r\n").length);
		
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void events() {
		ActivityDefinitionEntity ad = createActivityDefinitionEntity();
		ServiceResult<PatientEvent> sr = service.getActualEventsForPatient(PatientBaseViewImpl.newFromEntity(ad.getHealthPlan().getForPatient()));
		PatientEvent event = sr.getData();
		assertEquals(true, event.getDueReports() > 0);
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		assertEquals(true, (event.getNumReports() > 0 && (day == 2 || day == 5)) || event.getNumReports() == 0);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void deleteActivityDefintion() throws Exception {
		
		final PatientEntity p = this.createPatient(null);
		final PatientBaseView pb = PatientBaseViewImpl.newFromEntity(p);
		
		this.runAs(pb);
		
		final HealthPlanEntity hp = HealthPlanEntity.newEntity(this.createCareGiver(null, null), p, "Health Plan", new Date(), 6, DurationUnit.MONTH);
		this.ordinationRepo.save(hp);
		
		final Frequency frequency = Frequency.unmarshal("1;1;2,18:15;5,07:00,19:00");
		
		final ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, this.createActivityType(), frequency, hp.getIssuedBy());
		
		final ActivityDefinitionEntity saved = this.defRepo.save(ad);
		this.schedRepo.save(saved.scheduleActivities());
		
		this.service.deleteActivity(saved.getId());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteActivityDefintionWithNoAccess() throws Exception {
		final HealthPlanEntity hp = HealthPlanEntity.newEntity(this.createCareGiver(null, null), this.createPatient(null), "Health Plan", new Date(), 6, DurationUnit.MONTH);
		this.ordinationRepo.save(hp);
		
		final PatientBaseView pb = PatientBaseViewImpl.newFromEntity(this.patientRepo.save(this.createPatient("198205133333")));
		this.runAs(pb);
		
		final Frequency frequency = Frequency.unmarshal("1;1;2,18:15;5,07:00,19:00");
		final ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, this.createActivityType(), frequency, hp.getIssuedBy());
		
		final ActivityDefinitionEntity saved = this.defRepo.save(ad);
		this.schedRepo.save(saved.scheduleActivities());
		
		try {
			this.service.deleteActivity(saved.getId());
			fail("Should not be possible to delete as another patient.");
		} catch (Exception e) {
			assertTrue(e instanceof SecurityException);
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteHealthPlan() throws Exception {
		
		final CareGiverEntity cg = this.createCareGiver(null, null);
		final PatientEntity p = this.createPatient("123");
		
		final HealthPlanEntity hp = HealthPlanEntity.newEntity(cg, p, "Test", new Date(), 12, DurationUnit.MONTH);
		final HealthPlanEntity saved = this.ordinationRepo.save(hp);
		
		final ActivityTypeEntity at = this.createActivityType();
		final Frequency frequency = Frequency.unmarshal("1;1;2,18:15;5,07:00,19:00");
		this.defRepo.save(ActivityDefinitionEntity.newEntity(hp, at, frequency, hp.getIssuedBy()));
		this.defRepo.save(ActivityDefinitionEntity.newEntity(hp, at, frequency, hp.getIssuedBy()));
		
		this.runAs(CareGiverBaseViewImpl.newFromEntity(cg));
		
		this.service.deleteHealthPlan(saved.getId());
		
		assertEquals(null, this.ordinationRepo.findOne(saved.getId()));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteHealthPlanAsUnathorized() throws Exception {
		final CareGiverEntity cg = this.createCareGiver("hsa", null);
		final PatientEntity p = this.createPatient("123");
		final PatientEntity p2 = this.createPatient("345");
		
		final HealthPlanEntity hp = HealthPlanEntity.newEntity(cg, p, "Test", new Date(), 12, DurationUnit.MONTH);
		final HealthPlanEntity saved = this.ordinationRepo.save(hp);
		
		final ActivityTypeEntity at = this.createActivityType();
		final Frequency frequency = Frequency.unmarshal("1;1;2,18:15;5,07:00,19:00");
		this.defRepo.save(ActivityDefinitionEntity.newEntity(hp, at, frequency, hp.getIssuedBy()));
		this.defRepo.save(ActivityDefinitionEntity.newEntity(hp, at, frequency, hp.getIssuedBy()));
		
		this.runAs(PatientBaseViewImpl.newFromEntity(p2));
		
		try {
			this.service.deleteHealthPlan(saved.getId());
			fail("Patients' must not be able to delete each others health plans.");
		} catch (Exception e) {
			assertTrue(e instanceof SecurityException);
		}
		
		final CareUnitEntity cu = this.createCareUnit("another-hsa");
		final CareGiverEntity cg2 = this.createCareGiver("another-hsa-2", cu);
		
		this.runAs(CareGiverBaseViewImpl.newFromEntity(cg2));
		
		try {
			this.service.deleteHealthPlan(saved.getId());
			fail("Patients' must not be able to delete each others health plans.");
		} catch (Exception e) {
			assertTrue(e instanceof SecurityException);
		}
	}
	
	private ActivityCategoryEntity createActivityCategory() {
		final ActivityCategoryEntity ac = ActivityCategoryEntity.newEntity("Fysisk aktivitet");
		return this.catRepo.save(ac);
	}
	
	private ActivityTypeEntity createActivityType() {
		final ActivityTypeEntity at = ActivityTypeEntity.newEntity("Yoga", this.createActivityCategory());
		return this.typeRepo.save(at);
	}
	
	private PatientEntity createPatient(final String cnr) {
		final PatientEntity p = PatientEntity.newEntity("Kalle Anka", cnr == null ? "191212121212" : cnr);
		return this.patientRepo.save(p);
	}
	
	private CareUnitEntity createCareUnit(final String hsaId) {
		final CareUnitEntity cu = CareUnitEntity.newEntity(hsaId == null ? "hsa-cu-123" : hsaId);
		return this.cuRepo.save(cu);
	}
	
	private CareGiverEntity createCareGiver(final String hsaId, final CareUnitEntity careUnit) {
		final CareGiverEntity cg = CareGiverEntity.newEntity("Care Giver", hsaId == null ? "hsa-id-123" : hsaId, careUnit == null ? this.createCareUnit(null) : careUnit);
		return this.cgRepo.save(cg);
	}
}
