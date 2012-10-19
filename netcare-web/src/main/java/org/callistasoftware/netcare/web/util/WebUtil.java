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
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareActorRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.ActivityItemValuesEntityRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityItemDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityItemValuesEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.EstimationEntity;
import org.callistasoftware.netcare.model.entity.EstimationTypeEntity;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.callistasoftware.netcare.model.entity.MeasurementDefinitionEntity;
import org.callistasoftware.netcare.model.entity.MeasurementEntity;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.PatientEntity;
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

		final PatientRepository bean = wc.getBean(PatientRepository.class);
		final CareActorRepository careActorRepo = wc.getBean(CareActorRepository.class);
		final CareUnitRepository cuRepo = wc.getBean(CareUnitRepository.class);
		final ActivityCategoryRepository catRepo = wc.getBean(ActivityCategoryRepository.class);
		final ActivityTypeRepository atRepo = wc.getBean(ActivityTypeRepository.class);
		final ActivityDefinitionRepository adRepo = wc.getBean(ActivityDefinitionRepository.class);
		final HealthPlanRepository hpRepo = wc.getBean(HealthPlanRepository.class);
		final HealthPlanService hps = wc.getBean(HealthPlanService.class);

		if (careActorRepo.findByHsaId(careActorHsa) != null) {
			log.info("Test data already setup. Aborting...");
			return;
		}

		final ActivityCategoryEntity cat = catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		final CareUnitEntity cu = CareUnitEntity.newEntity("ap-test-unit");
		cu.setName("Jönköpings vårdcentral");
		cuRepo.save(cu);
		cuRepo.flush();

		ActivityTypeEntity ate = ActivityTypeEntity.newEntity("Löpning", cat, cu);
		MeasurementTypeEntity.newEntity(ate, "Distans", MeasurementValueType.SINGLE_VALUE, MeasureUnit.METER, false);
		EstimationTypeEntity.newEntity(ate, "Känsla", "Lätt", "Tufft");
		atRepo.save(ate);
		atRepo.flush();

		final CareActorEntity ca1 = CareActorEntity.newEntity("Test", "Läkare", careActorHsa, cu);
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

		if (careActorRepo.findByHsaId(careActorHsa) != null) {
			log.info("Test data already setup. Aborting...");
			return;
		}

		final ActivityCategoryEntity cat = catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		final ActivityCategoryEntity cat2 = catRepo.save(ActivityCategoryEntity.newEntity("Mental träning"));
		final ActivityCategoryEntity cat3 = catRepo.save(ActivityCategoryEntity.newEntity("Provtagning"));

		final CareUnitEntity cu = CareUnitEntity.newEntity("hsa-cu-1");
		cu.setName("Rosenhälsan i Huskvarna");
		cuRepo.save(cu);
		cuRepo.flush();

		final CareUnitEntity cu2 = CareUnitEntity.newEntity("hsa-cu-2");
		cu2.setName("Primärvårdsrehab Gibraltar");
		cuRepo.save(cu2);
		cuRepo.flush();

		final ActivityTypeEntity t1 = ActivityTypeEntity.newEntity("Löpning", cat, cu2);
		MeasurementTypeEntity.newEntity(t1, "Distans", MeasurementValueType.SINGLE_VALUE, MeasureUnit.METER, false);
		MeasurementTypeEntity me = MeasurementTypeEntity.newEntity(t1, "Vikt", MeasurementValueType.INTERVAL,
				MeasureUnit.KILOGRAM, true);
		me.setAlarmEnabled(true);
		EstimationTypeEntity.newEntity(t1, "Känsla", "Lätt", "Tufft");
		atRepo.save(t1);
		atRepo.flush();

		final ActivityTypeEntity t2 = ActivityTypeEntity.newEntity("Promenad (skattning)", cat2, cu2);
		MeasurementTypeEntity
				.newEntity(t2, "Varaktighet", MeasurementValueType.SINGLE_VALUE, MeasureUnit.MINUTE, false);
		EstimationTypeEntity.newEntity(t2, "Känsla", "Lätt", "Tufft");
		atRepo.save(t2);
		atRepo.flush();

		final ActivityTypeEntity t3 = ActivityTypeEntity.newEntity("Blodtryck (enkelt)", cat3, cu2);
		MeasurementTypeEntity
				.newEntity(t3, "Övertryck", MeasurementValueType.INTERVAL, MeasureUnit.PRESSURE_MMHG, true);
		MeasurementTypeEntity.newEntity(t3, "Undertryck", MeasurementValueType.INTERVAL, MeasureUnit.PRESSURE_MMHG,
				true);
		atRepo.save(t3);
		atRepo.flush();

		final CareActorEntity ca1 = CareActorEntity.newEntity("Peter", "Abrahamsson", careActorHsa, cu);
		careActorRepo.save(ca1);

		final CareActorEntity ca = CareActorEntity.newEntity("Marcus", "Hansson", "hsa-cg-2", cu2);
		careActorRepo.save(ca);

		careActorRepo.flush();

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
				sce.setNote("Anteckning");
				Calendar c = Calendar.getInstance();
				c.setTime(sce.getScheduledTime());
				int offset = (int) Math.round(Math.random() * 240) + 30;
				c.add(Calendar.MINUTE, offset);
				sce.setReportedTime(c.getTime());
				sce.setStatus(ScheduledActivityStatus.OPEN);
				sce.setActualTime(sce.getScheduledTime());
				for (ActivityItemValuesEntity aiv : sce.getActivities()) {
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

		ActivityTypeEntity at2 = ActivityTypeEntity.newEntity("Yoga", cat, cu2);
		MeasurementTypeEntity.newEntity(at2, "Varaktighet", MeasurementValueType.SINGLE_VALUE, MeasureUnit.MINUTE,
				false);
		atRepo.save(at2);
		Frequency frequency2 = Frequency.unmarshal("1;2;3,16:30");
		ActivityDefinitionEntity ad2 = ActivityDefinitionEntity.newEntity(hp, at2, frequency2, ca);
		((MeasurementDefinitionEntity) ad2.getActivityItemDefinitions().get(0)).setTarget(60);

		adRepo.save(ad2);
		hps.scheduleActivities(ad2);
	}
}
