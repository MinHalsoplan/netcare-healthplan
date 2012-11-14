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
package org.callistasoftware.netcare.web.util;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;

import org.callistasoftware.netcare.core.repository.ActivityCategoryRepository;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityItemValuesEntityRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareActorRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.CountyCouncilRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.repository.RoleRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.callistasoftware.netcare.model.entity.AccessLevel;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityItemDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityItemValuesEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.EstimationEntity;
import org.callistasoftware.netcare.model.entity.EstimationTypeEntity;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnitEntity;
import org.callistasoftware.netcare.model.entity.MeasurementDefinitionEntity;
import org.callistasoftware.netcare.model.entity.MeasurementEntity;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.RoleEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public final class WebUtil {

	private static final Logger log = LoggerFactory.getLogger(WebUtil.class);

	public static final WebApplicationContext getWebRequest(final ServletContext sc) {
		return WebApplicationContextUtils.getWebApplicationContext(sc);
	}

	public static final String[] getActiveProfiles(final ServletContext sc) {
		return WebUtil.getWebRequest(sc).getEnvironment().getActiveProfiles();
	}

	public static final boolean isProfileActive(final ServletContext sc, final String name) {
		final String[] profiles = WebUtil.getActiveProfiles(sc);
		for (final String s : profiles) {
			if (s.equals(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Creates test data for app.store tests (app. approval process).
	 * 
	 * @param sc
	 *            the servlet context.
	 */
	public static final void setupIosTestData(final ServletContext sc) {
		final WebApplicationContext wc = getWebRequest(sc);

		final String careActorHsa = "app-test-giver";
		
		final TestDataHelper td = new TestDataHelper(sc);

		final PatientRepository bean = wc.getBean(PatientRepository.class);
		final CareActorRepository careActorRepo = wc.getBean(CareActorRepository.class);
		final CareUnitRepository cuRepo = wc.getBean(CareUnitRepository.class);
		final ActivityCategoryRepository catRepo = wc.getBean(ActivityCategoryRepository.class);
		final ActivityTypeRepository atRepo = wc.getBean(ActivityTypeRepository.class);
		final ActivityDefinitionRepository adRepo = wc.getBean(ActivityDefinitionRepository.class);
		final HealthPlanRepository hpRepo = wc.getBean(HealthPlanRepository.class);
		final HealthPlanService hps = wc.getBean(HealthPlanService.class);
		final CountyCouncilRepository ccRepo = wc.getBean(CountyCouncilRepository.class);
		final RoleRepository roleRepo = wc.getBean(RoleRepository.class);
		
		
		if (careActorRepo.findByHsaId(careActorHsa) != null) {
			log.info("Test data already setup. Aborting...");
			return;
		}
		
		final RoleEntity careActorRole = td.newCareActorRole();
		final CountyCouncilEntity jkpg = td.newCountyCouncil("Landstinget i Jönköpings Län");
		final MeasureUnitEntity mue = td.newMeasureUnit("m", "Meter");

		final ActivityCategoryEntity cat = catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		final CareUnitEntity cu = CareUnitEntity.newEntity("ap-test-unit", jkpg);
		cu.setName("Jönköpings vårdcentral");
		cuRepo.save(cu);
		cuRepo.flush();

		ActivityTypeEntity ate = ActivityTypeEntity.newEntity("Löpning", cat, cu, AccessLevel.CAREUNIT);
		MeasurementTypeEntity.newEntity(ate, "Distans", MeasurementValueType.SINGLE_VALUE, mue, false, 0);
		EstimationTypeEntity.newEntity(ate, "Känsla", "Lätt", "Tufft", 0, 10, 1);
		atRepo.save(ate);
		atRepo.flush();

		final CareActorEntity ca1 = CareActorEntity.newEntity("Test", "Läkare", careActorHsa, cu);
		ca1.addRole(careActorRole);
		careActorRepo.save(ca1);
		careActorRepo.flush();

		final PatientEntity p2 = PatientEntity.newEntity("AppDemo", "Patient", "191112121212");
		p2.setPhoneNumber("0700000000");
		bean.save(p2);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		HealthPlanEntity hp = HealthPlanEntity.newEntity(ca1, p2, "Auto", cal.getTime(), 6, DurationUnit.MONTH);
		hpRepo.save(hp);

		Frequency frequency = Frequency.unmarshal("1;1;2,18:15;6,07:00,19:00");
		ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, ate, frequency, ca1);
		for (ActivityItemDefinitionEntity aid : ad.getActivityItemDefinitions()) {
			MeasurementDefinitionEntity md = (MeasurementDefinitionEntity) aid;
			if (md.getMeasurementType().getName().equals("Distans")) {
				md.setTarget(1200);
			}
		}
		adRepo.save(ad);
		hps.scheduleActivities(ad);
	}

	public static final void setupTestData(final ServletContext sc) {
		final WebApplicationContext wc = getWebRequest(sc);

		final String careActorHsa = "hsa-cg-1";

		final PatientRepository bean = wc.getBean(PatientRepository.class);
		final CareActorRepository careActorRepo = wc.getBean(CareActorRepository.class);
		final CareUnitRepository cuRepo = wc.getBean(CareUnitRepository.class);
		final ActivityCategoryRepository catRepo = wc.getBean(ActivityCategoryRepository.class);
		final ActivityTypeRepository atRepo = wc.getBean(ActivityTypeRepository.class);
		final ActivityDefinitionRepository adRepo = wc.getBean(ActivityDefinitionRepository.class);
		final HealthPlanRepository hpRepo = wc.getBean(HealthPlanRepository.class);
		final HealthPlanService hps = wc.getBean(HealthPlanService.class);
		final ScheduledActivityRepository sar = wc.getBean(ScheduledActivityRepository.class);
		final ActivityItemValuesEntityRepository actRepo = wc.getBean(ActivityItemValuesEntityRepository.class);
		final CountyCouncilRepository ccRepo = wc.getBean(CountyCouncilRepository.class);

		if (careActorRepo.findByHsaId(careActorHsa) != null) {
			log.info("Test data already setup. Aborting...");
			return;
		}
		
		final TestDataHelper td = new TestDataHelper(sc);
		
		/*
		 * County councils
		 */
		final CountyCouncilEntity jkpg = td.newCountyCouncil("Landstinget i Jönköpings län");
		final CountyCouncilEntity hall = td.newCountyCouncil("Region Halland");
		final CountyCouncilEntity vbott = td.newCountyCouncil("Västerbottens läns landsting");

		/*
		 * Care units
		 */
		final CareUnitEntity jkpg_cu_1 = td.newCareUnit("hsa-cu-1", "Rosenhälsan i Huskvarna", jkpg);
		final CareUnitEntity jkpg_cu_2 = td.newCareUnit("hsa-cu-2", "Dialysmottagningen Ryhov", jkpg);
		final CareUnitEntity hall_cu_1 = td.newCareUnit("hall-cu-1", "Vårdcentralen Getinge", hall);
		final CareUnitEntity hall_cu_2 = td.newCareUnit("hall-cu-2", "Tudorkliniken Allmänläkarmottagning", hall);
		final CareUnitEntity vbott_cu_1 = td.newCareUnit("vbott-cu-1", "Hudläkaren i Umeå AB", vbott);
		final CareUnitEntity vbott_cu_2 = td.newCareUnit("vbott-cu-2", "Norsjö Rehab Center", vbott);
		
		/*
		 * System categories
		 */
		final ActivityCategoryEntity cat = catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		final ActivityCategoryEntity cat2 = catRepo.save(ActivityCategoryEntity.newEntity("Mental träning"));
		final ActivityCategoryEntity cat3 = catRepo.save(ActivityCategoryEntity.newEntity("Provtagning"));
		
		/*
		 * Rosenhälsan
		 */
		final MeasureUnitEntity meter = td.newMeasureUnit("meter", "Meter");
		final MeasureUnitEntity kilogram = td.newMeasureUnit("kg", "Kilogram");
		final MeasureUnitEntity minute = td.newMeasureUnit("m", "Minuter");
		final MeasureUnitEntity pressure = td.newMeasureUnit("mmHg", "Tryck");
		
		
		final ActivityTypeEntity t1 = ActivityTypeEntity.newEntity("Löpning", cat, jkpg_cu_1, AccessLevel.COUNTY_COUNCIL);
		MeasurementTypeEntity.newEntity(t1, "Distans", MeasurementValueType.SINGLE_VALUE, meter, false, 1);
		MeasurementTypeEntity me = MeasurementTypeEntity.newEntity(t1, "Vikt", MeasurementValueType.INTERVAL,
				kilogram, true, 2);
		EstimationTypeEntity.newEntity(t1, "Känsla", "Lätt", "Tufft", 1, 5, 3);
		atRepo.saveAndFlush(t1);


		final ActivityTypeEntity t2 = ActivityTypeEntity.newEntity("Promenad (skattning)", cat2, jkpg_cu_1,
				AccessLevel.NATIONAL);

		MeasurementTypeEntity
				.newEntity(t2, "Varaktighet", MeasurementValueType.SINGLE_VALUE, minute, false, 1);
		EstimationTypeEntity.newEntity(t2, "Känsla", "Lätt", "Tufft", 1, 5, 2);

		atRepo.saveAndFlush(t2);

		/*
		 * Dialys Ryhov
		 */
		final ActivityTypeEntity t3 = ActivityTypeEntity.newEntity("Blodtryck (enkelt)", cat3, jkpg_cu_2,
				AccessLevel.COUNTY_COUNCIL);
		MeasurementTypeEntity.newEntity(t3, "Övertryck", MeasurementValueType.INTERVAL, pressure, true, 1);
		MeasurementTypeEntity.newEntity(t3, "Undertryck", MeasurementValueType.INTERVAL, pressure, true, 2);

		atRepo.saveAndFlush(t3);
		
		/*
		 * Norsjö Rehab Center
		 */
		final ActivityTypeEntity t4 = ActivityTypeEntity.newEntity("Yoga", cat, vbott_cu_2, AccessLevel.COUNTY_COUNCIL);
		MeasurementTypeEntity.newEntity(t4, "Varaktighet", MeasurementValueType.SINGLE_VALUE, minute, false, 1);

		atRepo.saveAndFlush(t4);

		
		final CareActorEntity ca1 = CareActorEntity.newEntity("Peter", "Abrahamsson", careActorHsa, vbott_cu_2);
		ca1.addRole(td.newCareActorRole());
		careActorRepo.saveAndFlush(ca1);

		final CareActorEntity ca = CareActorEntity.newEntity("Marcus", "Hansson", "hsa-cg-2", jkpg_cu_1);
		ca.addRole(td.newCareActorRole());
		careActorRepo.saveAndFlush(ca);
		
		final CareActorEntity ca3 = CareActorEntity.newEntity("Mikael", "Andersson", "hsa-lj-admin", jkpg_cu_2);
		ca3.addRole(td.newCareActorRole());
		ca3.addRole(td.newCountyAdminRole());
		careActorRepo.saveAndFlush(ca3);
		
		
		final CareActorEntity ca4 = CareActorEntity.newEntity("Johanna", "Niklasson", "hsa-swe-admin", hall_cu_1);
		ca4.addRole(td.newCareActorRole());
		ca4.addRole(td.newCountyAdminRole());
		ca4.addRole(td.newNationAdminRole());
		careActorRepo.saveAndFlush(ca4);

		final PatientEntity p2 = PatientEntity.newEntity("Tolvan", "Tolvansson", "191212121212");
		p2.setPhoneNumber("0733 - 39 87 45");
		bean.save(p2);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -90);
		HealthPlanEntity hp = HealthPlanEntity.newEntity(ca, p2, "Viktprogram", cal.getTime(), 6, DurationUnit.MONTH);
		hpRepo.save(hp);

		Frequency frequency = Frequency.unmarshal("1;1;2,18:15;6,07:00,19:00");
		ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, t1, frequency, ca);
		// FIXME: multi-values
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
		ad.setStartDate(hp.getStartDate());
		adRepo.save(ad);
		
		hps.scheduleActivities(ad);

		Date now = new Date();
		log.debug("Create history reports for scheduled activities: " + ad.getScheduledActivities().size());
		int currentWeight = 0;
		int distance = 1000;
		int runNumber = 0;
		int modulo = 5;
		for (ScheduledActivityEntity sce : ad.getScheduledActivities()) {
			if (sce.getScheduledTime().compareTo(now) < 0) {
				
				log.debug("Creating report for {}", sce.getScheduledTime());
				
				sce.setNote("Anteckning");
				Calendar c = Calendar.getInstance();
				c.setTime(sce.getScheduledTime());
				int offset = (int) Math.round(Math.random() * 240) + 30;
				c.add(Calendar.MINUTE, offset);
				sce.setReportedTime(c.getTime());
				sce.setStatus(ScheduledActivityStatus.OPEN);
				sce.setActualTime(sce.getScheduledTime());
				for (ActivityItemValuesEntity aiv : sce.getActivities()) {
					
					log.debug("Processing value {}", aiv.getActivityItemDefinitionEntity().getActivityItemType().getName());
					
					if (aiv instanceof MeasurementEntity) {
						MeasurementEntity m = (MeasurementEntity) aiv;
						MeasurementDefinitionEntity md = (MeasurementDefinitionEntity) m
								.getActivityItemDefinitionEntity();
						int target;
						int diff = 0;
						if (md.getMeasurementType().getName().equals("Distans")) {
							target = distance;
							runNumber++;
							if ((runNumber % modulo) == 0) {
								distance += 100;
							}
							m.setTarget(md.getTarget());
							if (target > 1400) {
								md.setTarget(1600);
							}
						} else {
							currentWeight = (int) ((currentWeight == 0) ? ((MeasurementDefinitionEntity) m
									.getActivityItemDefinitionEntity()).getMaxTarget() + 2 : currentWeight - 1);
							target = currentWeight;
							diff = (Math.random() < 0.3) ? -1 : 1;
						}
						m.setReportedValue(target + diff);
						log.debug("Reported value: " + m.getReportedValue());
					} else if (aiv instanceof EstimationEntity) {
						EstimationEntity est = (EstimationEntity) aiv;
						int sense = (int) Math.round(Math.random() * 10);
						est.setPerceivedSense(sense == 0 ? 1 : sense);
					}
				}
				actRepo.save(sce.getActivities());
				sar.save(sce);
			}
		}
		sar.flush();
		adRepo.flush();

		Frequency frequency2 = Frequency.unmarshal("1;2;3,16:30");
		ActivityDefinitionEntity ad2 = ActivityDefinitionEntity.newEntity(hp, t3, frequency2, ca);
		
		((MeasurementDefinitionEntity) ad2.getActivityItemDefinitions().get(0)).setMinTarget(90.0f);
		((MeasurementDefinitionEntity) ad2.getActivityItemDefinitions().get(0)).setMaxTarget(140.0f);

		((MeasurementDefinitionEntity) ad2.getActivityItemDefinitions().get(0)).setMinTarget(70.0f);
		((MeasurementDefinitionEntity) ad2.getActivityItemDefinitions().get(1)).setMaxTarget(90.0f);
		
		adRepo.save(ad2);
		hps.scheduleActivities(ad2);
	}
}
