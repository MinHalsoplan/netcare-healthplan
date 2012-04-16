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

import javax.servlet.ServletContext;

import org.callistasoftware.netcare.core.repository.ActivityCategoryRepository;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.callistasoftware.netcare.model.entity.ActivityCategoryEntity;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnit;
import org.callistasoftware.netcare.model.entity.MeasurementDefinitionEntity;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.PatientEntity;
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
	 * @param sc the servlet context.
	 */
	public static final void setupIosTestData(final ServletContext sc) {
		final WebApplicationContext wc = getWebRequest(sc);
		
		final String careGiverHsa = "app-test-giver";
		
		final PatientRepository bean = wc.getBean(PatientRepository.class);
		final CareGiverRepository cgRepo = wc.getBean(CareGiverRepository.class);
		final CareUnitRepository cuRepo = wc.getBean(CareUnitRepository.class);
		final ActivityCategoryRepository catRepo = wc.getBean(ActivityCategoryRepository.class);
		final ActivityTypeRepository atRepo = wc.getBean(ActivityTypeRepository.class);
		final ActivityDefinitionRepository adRepo = wc.getBean(ActivityDefinitionRepository.class);
		final HealthPlanRepository hpRepo = wc.getBean(HealthPlanRepository.class);
		final HealthPlanService hps = wc.getBean(HealthPlanService.class);

		if (cgRepo.findByHsaId(careGiverHsa) != null) {
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
		ate.setMeasuringSense(true);
		ate.setSenseLabelLow("Lätt");
		ate.setSenseLabelHigh("Tufft");
		atRepo.save(ate);
		atRepo.flush();
		
		final CareGiverEntity cg1 = CareGiverEntity.newEntity("Test", "Läkare", careGiverHsa, cu);
		cgRepo.save(cg1);		
		cgRepo.flush();
				
		final PatientEntity p2 = PatientEntity.newEntity("AppDemo", "Patient", "191112121212");
		p2.setPhoneNumber("0700000000");
		bean.save(p2);
		
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		HealthPlanEntity hp = HealthPlanEntity.newEntity(cg1, p2, "Auto", cal.getTime(), 6, DurationUnit.MONTH);
		hpRepo.save(hp);
		
		Frequency frequency = Frequency.unmarshal("1;1;2,18:15;6,07:00,19:00");
		ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, ate, frequency, cg1);
		for (MeasurementDefinitionEntity md : ad.getMeasurementDefinitions()) {
			if (md.getMeasurementType().getName().equals("Distans")) {
				md.setTarget(1200);
			}
		}
		adRepo.save(ad);
		hps.scheduleActivities(ad);		
	}
	
	public static final void setupTestData(final ServletContext sc) {
		final WebApplicationContext wc = getWebRequest(sc);
		
		final String careGiverHsa = "hsa-cg-1";
		
		final PatientRepository bean = wc.getBean(PatientRepository.class);
		final CareGiverRepository cgRepo = wc.getBean(CareGiverRepository.class);
		final CareUnitRepository cuRepo = wc.getBean(CareUnitRepository.class);
		final ActivityCategoryRepository catRepo = wc.getBean(ActivityCategoryRepository.class);
		final ActivityTypeRepository atRepo = wc.getBean(ActivityTypeRepository.class);
		final ActivityDefinitionRepository adRepo = wc.getBean(ActivityDefinitionRepository.class);
		final HealthPlanRepository hpRepo = wc.getBean(HealthPlanRepository.class);
		final HealthPlanService hps = wc.getBean(HealthPlanService.class);

		if (cgRepo.findByHsaId(careGiverHsa) != null) {
			log.info("Test data already setup. Aborting...");
			return;
		}
		
		final ActivityCategoryEntity cat = catRepo.save(ActivityCategoryEntity.newEntity("Fysisk aktivitet"));
		final ActivityCategoryEntity cat2 = catRepo.save(ActivityCategoryEntity.newEntity("Psykisk aktivitet"));
		final ActivityCategoryEntity cat3 = catRepo.save(ActivityCategoryEntity.newEntity("Provtagning"));
		
		final CareUnitEntity cu = CareUnitEntity.newEntity("hsa-cu-1");
		cu.setName("Rosenhälsan i Huskvarna");
		cuRepo.save(cu);
		cuRepo.flush();
		
		final CareUnitEntity cu2 = CareUnitEntity.newEntity("hsa-cu-2");
		cu2.setName("Primärvårdsrehab Gibraltar");
		cuRepo.save(cu2);

		final ActivityTypeEntity t1 = ActivityTypeEntity.newEntity("Löpning", cat, cu);
		MeasurementTypeEntity.newEntity(t1, "Distans", MeasurementValueType.SINGLE_VALUE, MeasureUnit.METER, false);
		MeasurementTypeEntity me = MeasurementTypeEntity.newEntity(t1, "Vikt", MeasurementValueType.INTERVAL, MeasureUnit.KILOGRAM, true);
		me.setAlarmEnabled(true);
		t1.setMeasuringSense(true);
		t1.setSenseLabelLow("Lätt");
		t1.setSenseLabelHigh("Tufft");
		atRepo.save(t1);
		atRepo.flush();
		
		final ActivityTypeEntity t2 = ActivityTypeEntity.newEntity("Meditation", cat2, cu);
		MeasurementTypeEntity.newEntity(t2, "Varaktighet", MeasurementValueType.SINGLE_VALUE, MeasureUnit.MINUTE, false);
		
		t2.setMeasuringSense(true);
		t2.setSenseLabelLow("Obehagligt");
		t2.setSenseLabelHigh("Behagligt");
		
		atRepo.save(t2);
		
		final ActivityTypeEntity t3 = ActivityTypeEntity.newEntity("Blodtryck", cat3, cu);
		MeasurementTypeEntity.newEntity(t3, "Övertryck", MeasurementValueType.INTERVAL, MeasureUnit.PRESSURE_MMHG, true);
		MeasurementTypeEntity.newEntity(t3, "Undertryck", MeasurementValueType.INTERVAL, MeasureUnit.PRESSURE_MMHG, true);
		
		t3.setMeasuringSense(false);
		
		atRepo.save(t3);
		
		final CareGiverEntity cg1 = CareGiverEntity.newEntity("Peter", "Abrahamsson", careGiverHsa, cu);
		cgRepo.save(cg1);

		final CareGiverEntity cg = CareGiverEntity.newEntity("Marcus", "Hansson", "hsa-cg-2", cu2);
		cgRepo.save(cg);
		
		cgRepo.flush();
		
		final PatientEntity p2 = PatientEntity.newEntity("Tolvan", "Tolvansson", "191212121212");
		p2.setPhoneNumber("0733 - 39 87 45");
		bean.save(p2);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		HealthPlanEntity hp = HealthPlanEntity.newEntity(cg, p2, "Rehabilitering höger axel", cal.getTime(), 6, DurationUnit.MONTH);
		hpRepo.save(hp);
		
		Frequency frequency = Frequency.unmarshal("1;1;2,18:15;6,07:00,19:00");
		ActivityDefinitionEntity ad = ActivityDefinitionEntity.newEntity(hp, t1, frequency, cg);
		// FIXME: multi-values
		for (MeasurementDefinitionEntity md : ad.getMeasurementDefinitions()) {
			if (md.getMeasurementType().getName().equals("Vikt")) {
				md.setMinTarget(80);
				md.setMaxTarget(120);
			} 
			if (md.getMeasurementType().getName().equals("Distans")) {
				md.setTarget(1200);
			}
		}
		adRepo.save(ad);
		hps.scheduleActivities(ad);
		
		ActivityTypeEntity at2 = ActivityTypeEntity.newEntity("Yoga", cat, cu2);
		MeasurementTypeEntity.newEntity(at2, "Varaktighet", MeasurementValueType.SINGLE_VALUE, MeasureUnit.MINUTE, false);
		atRepo.save(at2);
		Frequency frequency2 = Frequency.unmarshal("1;2;3,16:30");
		ActivityDefinitionEntity ad2 = ActivityDefinitionEntity.newEntity(hp, at2, frequency2, cg);
		ad2.getMeasurementDefinitions().get(0).setTarget(60);
		
		adRepo.save(ad2);
		hps.scheduleActivities(ad2);
	}
}
