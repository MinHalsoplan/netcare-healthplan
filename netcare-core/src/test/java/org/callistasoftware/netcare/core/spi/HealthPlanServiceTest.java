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
package org.callistasoftware.netcare.core.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.PatientEvent;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityDefinitionImpl;
import org.callistasoftware.netcare.core.api.impl.ActivityItemTypeImpl;
import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.callistasoftware.netcare.core.api.impl.CareActorBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.DayTimeImpl;
import org.callistasoftware.netcare.core.api.impl.HealthPlanImpl;
import org.callistasoftware.netcare.core.api.impl.MeasurementDefinitionImpl;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.repository.ActivityCategoryRepository;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareActorRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.CountyCouncilRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.AccessLevel;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityItemDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncil;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.EstimationTypeEntity;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.FrequencyDay;
import org.callistasoftware.netcare.model.entity.FrequencyTime;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.MeasurementDefinitionEntity;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class HealthPlanServiceTest extends TestSupport {

	@Autowired
	private CareActorRepository careActorRepo;
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
	private CountyCouncilRepository ccRepo;

	@Autowired
	private HealthPlanService service;

	private HealthPlanImpl createHealthPlan(String name, String startDate, int duration, String unit) {
		final HealthPlanImpl o = new HealthPlanImpl();
		o.setName(name);
		o.setStartDate(startDate);
		o.setDuration(duration);
		o.setDurationUnit(new Option(unit, LocaleContextHolder.getLocale()));
		return o;
	}

	private ActivityDefinitionEntity createActivityDefinitionEntity() {
		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity(CountyCouncil.STOCKHOLM));
		final CareUnitEntity cu = CareUnitEntity.newEntity("care-unit-hsa-123", cc);
		cu.setName("Jönköpings vårdcentral");
		cuRepo.save(cu);
		cuRepo.flush();

		final CareActorEntity ca = CareActorEntity.newEntity("Doctor Hook", "", "12345-67", cu);
		careActorRepo.save(ca);

		final PatientEntity p2 = PatientEntity.newEntity("Peter Larsson", "", "191212121212");
		patientRepo.save(p2);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		HealthPlanEntity hp = HealthPlanEntity.newEntity(ca, p2, "Auto", cal.getTime(), 6, DurationUnit.MONTH);
		hp.setAutoRenewal(true);
		ordinationRepo.save(hp);
		final ActivityCategoryEntity cat = catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));

		ActivityTypeEntity at = ActivityTypeEntity.newEntity("Löpning", cat, cu, AccessLevel.CAREUNIT);
		MeasurementTypeEntity.newEntity(at, "Distans", MeasurementValueType.SINGLE_VALUE, newMeasureUnit("m", "Meter", cc), false, 0);
		MeasurementTypeEntity.newEntity(at, "Vikt", MeasurementValueType.INTERVAL, newMeasureUnit("kg", "Kilogram", cc), true, 1);
		EstimationTypeEntity.newEntity(at, "Känsla", "Väldigt lätt", "Mycket Trötthet", 0, 10, 2);
		typeRepo.save(at);
		Frequency frequency = Frequency.unmarshal("1;1;2,18:15;5,07:00,19:00");
		ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, at, frequency, ca);
		for (ActivityItemDefinitionEntity aid : ad.getActivityItemDefinitions()) {
			if (aid instanceof MeasurementDefinitionEntity) {
				MeasurementDefinitionEntity md = (MeasurementDefinitionEntity) aid;
				if (md.getMeasurementType().getName().equals("Vikt")) {
					md.setMinTarget(80);
					md.setMaxTarget(120);
				}
				if (md.getMeasurementType().getName().equals("Distans")) {
					md.setTarget(1200);
				}
			}
		}
		defRepo.save(ad);

		schedRepo.save(ad.scheduleActivities());

		return ad;
	}

	private ServiceResult<HealthPlan> createHealthPlan(HealthPlan o) {
		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity(CountyCouncil.STOCKHOLM));
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu", cc);
		this.cuRepo.save(cu);

		final CareActorEntity ca = CareActorEntity.newEntity("Test Testgren", "", "hsa-123-id", cu);
		this.careActorRepo.save(ca);

		final CareActorBaseViewImpl caDto = new CareActorBaseViewImpl();
		caDto.setHsaId("hsa-123-id");

		final PatientEntity patient = PatientEntity.newEntity("Peter Larsson", "", "611028");
		patientRepo.save(patient);

		final ServiceResult<HealthPlan> saved = this.service.createNewHealthPlan(o, caDto, patient.getId());

		return saved;
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testCreateHealthPlan() throws Exception {

		final HealthPlanImpl o = createHealthPlan("Test", "2011-12-12", 12, DurationUnit.WEEK.name());
		o.setAutoRenewal(true);
		final ServiceResult<HealthPlan> saved = createHealthPlan(o);

		assertEquals(o.getName(), saved.getData().getName());
		assertEquals(o.getStartDate(), saved.getData().getStartDate());
		assertEquals(o.getDuration(), saved.getData().getDuration());
		assertEquals(o.getDurationUnit().getCode(), saved.getData().getDurationUnit().getCode());
		assertEquals(o.isAutoRenewal(), saved.getData().isAutoRenewal());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testAddActivityDefintion() throws Exception {
		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity(CountyCouncil.STOCKHOLM));
		final ActivityCategoryEntity cat = this.catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu", cc);
		this.cuRepo.save(cu);

		final ActivityTypeEntity type = ActivityTypeEntity.newEntity("Löpning", cat, cu, AccessLevel.CAREUNIT);
		MeasurementTypeEntity
				.newEntity(type, "Distans", MeasurementValueType.SINGLE_VALUE, newMeasureUnit("m", "Meter", cc), false, 0);
		MeasurementTypeEntity me = MeasurementTypeEntity.newEntity(type, "Vikt", MeasurementValueType.INTERVAL,
				newMeasureUnit("kg", "Kilogram", cc), true, 1);

		final ActivityTypeEntity savedType = typeRepo.saveAndFlush(type);
		final CareActorEntity ca = CareActorEntity.newEntity("Test Testgren", "", "hsa-123", cu);
		final CareActorEntity savedCa = this.careActorRepo.save(ca);

		final PatientEntity patient = PatientEntity.newEntity("Marcus Krantz", "", "123456789004");
		final PatientEntity savedPatient = this.patientRepo.save(patient);

		// the date and duration can't be changed without breaking the test, see
		// further below.
		final HealthPlanEntity ord = HealthPlanEntity.newEntity(savedCa, savedPatient, "Test",
				ApiUtil.parseDate("2012-12-01"), 12, DurationUnit.WEEK);
		final HealthPlanEntity savedOrd = this.ordinationRepo.save(ord);

		final ActivityTypeImpl typeImpl = new ActivityTypeImpl();
		typeImpl.setId(savedType.getId());
		typeImpl.setName("Löpning");

		final ActivityItemTypeImpl mdType = new ActivityItemTypeImpl();
		mdType.setId(me.getId());
		mdType.setName(me.getName());
		
		final MeasurementDefinitionImpl md = new MeasurementDefinitionImpl();
		md.setTarget(1200);
		md.setActivityItemType(mdType);

		final ActivityDefinitionImpl impl = new ActivityDefinitionImpl();

		impl.setHealthPlanId(savedOrd.getId());
		impl.setType(typeImpl);
		impl.setActivityRepeat(2);
		impl.setGoalValues(new MeasurementDefinitionImpl[] { md });

		// Monday and saturday
		final DayTimeImpl dt = new DayTimeImpl();
		dt.setDay("monday");
		dt.setTimes(new String[] { "12:15", "18:45" });

		final DayTimeImpl dt2 = new DayTimeImpl();
		dt2.setDay("saturday");

		dt2.setTimes(new String[] { "12:15", "18:45" });
		final DayTimeImpl[] dts = new DayTimeImpl[2];
		dts[0] = dt;
		dts[1] = dt2;

		impl.setDayTimes(dts);

		final CareActorBaseView cabv = CareActorBaseViewImpl.newFromEntity(savedCa);
		this.runAs(cabv);

		final ServiceResult<ActivityDefinition> result = this.service.addActvitiyToHealthPlan(impl);
		assertTrue(result.isSuccess());

		final ActivityDefinitionImpl impl2 = new ActivityDefinitionImpl();
		impl2.setHealthPlanId(savedOrd.getId());
		impl2.setType(typeImpl);
		impl2.setActivityRepeat(0);
		impl2.setStartDate("2013-01-30");
		impl2.setGoalValues(new MeasurementDefinitionImpl[] { md });

		// Wednesday (1 time activity)
		final DayTimeImpl dt3 = new DayTimeImpl();
		dt3.setDay("wednesday");
		dt3.setTimes(new String[] { "07:15", "12:00", "22:45" });
		impl2.setDayTimes(new DayTimeImpl[] { dt3 });
		
		this.runAs(CareActorBaseViewImpl.newFromEntity(ca));
		
		final ServiceResult<ActivityDefinition> result2 = this.service.addActvitiyToHealthPlan(impl2);
		assertTrue(result2.isSuccess());

		this.schedRepo.flush();
		this.ordinationRepo.flush();

		final HealthPlanEntity after = this.ordinationRepo.findOne(savedOrd.getId());
		final ActivityDefinitionEntity ent = after.getActivityDefinitions().get(0);
		// FIXME: multi-values
		assertEquals(newMeasureUnit("m", "Meter", cc), ((MeasurementTypeEntity) ent.getActivityItemDefinitions().get(0)
				.getActivityItemType()).getUnit());
		assertEquals("Löpning", ent.getActivityType().getName());
		// FIXME: multi-values
		// assertEquals(12, ent.getActivityTarget());

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
		List<ScheduledActivityEntity> scheduledActivities = schedRepo.findByPatientAndScheduledTimeBetween(
				savedPatient, startDate, endDate);
		Collections.sort(scheduledActivities);

		int n = scheduledActivities.size();
		// 26 (every other week * 2) + 3 (single day * 3)
		assertEquals(29, n);
		assertEquals("2013-02-23", ApiUtil.formatDate(scheduledActivities.get(n - 1).getScheduledTime()));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void deleteActivityDefintion() throws Exception {

		final PatientEntity p = this.createPatient(null);
		final PatientBaseView pb = PatientBaseViewImpl.newFromEntity(p);

		this.runAs(pb);

		final HealthPlanEntity hp = HealthPlanEntity.newEntity(this.createCareActor(null, null), p, "Health Plan",
				new Date(), 6, DurationUnit.MONTH);
		this.ordinationRepo.save(hp);

		final Frequency frequency = Frequency.unmarshal("1;1;2,18:15;5,07:00,19:00");

		final ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, this.createActivityType(),
				frequency, hp.getIssuedBy());

		final ActivityDefinitionEntity saved = this.defRepo.save(ad);
		this.schedRepo.save(saved.scheduleActivities());

		this.service.inactivateActivity(saved.getId());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteActivityDefintionWithNoAccess() throws Exception {
		final HealthPlanEntity hp = HealthPlanEntity.newEntity(this.createCareActor(null, null),
				this.createPatient(null), "Health Plan", new Date(), 6, DurationUnit.MONTH);
		this.ordinationRepo.save(hp);

		final PatientBaseView pb = PatientBaseViewImpl.newFromEntity(this.patientRepo.save(this
				.createPatient("198205133333")));
		this.runAs(pb);

		final Frequency frequency = Frequency.unmarshal("1;1;2,18:15;5,07:00,19:00");
		final ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, this.createActivityType(),
				frequency, hp.getIssuedBy());

		final ActivityDefinitionEntity saved = this.defRepo.save(ad);
		this.schedRepo.save(saved.scheduleActivities());

		try {
			this.service.inactivateActivity(saved.getId());
			fail("Should not be possible to delete as another patient.");
		} catch (Exception e) {
			assertTrue(e instanceof SecurityException);
		}
	}

	private ActivityCategoryEntity createActivityCategory() {
		final ActivityCategoryEntity ac = ActivityCategoryEntity.newEntity("Fysisk aktivitet");
		return this.catRepo.save(ac);
	}

	private ActivityTypeEntity createActivityType() {
		
		final CareUnitEntity cu = createCareUnit("123");
		
		final ActivityTypeEntity at = ActivityTypeEntity.newEntity("Yoga", this.createActivityCategory(),
				cu, AccessLevel.CAREUNIT);
		MeasurementTypeEntity.newEntity(at, "Tid", MeasurementValueType.SINGLE_VALUE, newMeasureUnit("m", "Meter", cu.getCountyCouncil()), false, 0);
		return this.typeRepo.save(at);
	}

	private PatientEntity createPatient(final String cnr) {
		final PatientEntity p = PatientEntity.newEntity("Kalle Anka", "", cnr == null ? "191212121212" : cnr);
		return this.patientRepo.save(p);
	}

	private CareUnitEntity createCareUnit(final String hsaId) {
		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity(CountyCouncil.STOCKHOLM));
		final CareUnitEntity cu = CareUnitEntity.newEntity(hsaId == null ? "hsa-cu-123" : hsaId, cc);
		return this.cuRepo.save(cu);
	}

	private CareActorEntity createCareActor(final String hsaId, final CareUnitEntity careUnit) {
		final CareActorEntity ca = CareActorEntity.newEntity("Care Giver", "", hsaId == null ? "hsa-id-123" : hsaId,
				careUnit == null ? this.createCareUnit(null) : careUnit);
		return this.careActorRepo.save(ca);
	}
}
