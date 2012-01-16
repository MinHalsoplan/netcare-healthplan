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
package org.callistasoftware.netcare.core.job;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.repository.AlarmRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.spi.impl.HealthPlanServiceImpl;
import org.callistasoftware.netcare.model.entity.AlarmCause;
import org.callistasoftware.netcare.model.entity.AlarmEntity;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SystemAlarmJob {

	private static Logger log = LoggerFactory.getLogger(SystemAlarmJob.class);
	
	@Autowired
	private ScheduledActivityRepository saRepo;
	
	@Autowired
	private HealthPlanRepository hpRepo;
	
	@Autowired
	private AlarmRepository alRepo;

	public void init() {
		this.run();
	}
	
	@Scheduled(fixedRate=3600000)
	public void run() {
		log.info("======== ALARM JOB STARTED =========");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -(HealthPlanServiceImpl.SCHEMA_HISTORY_DAYS));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.add(Calendar.DATE, -1);
		ApiUtil.dayEnd(cal);
		
		plans(cal.getTime());
		
		activities(cal.getTime());
		

		log.info("======== ALARM JOB COMPLETED =========");
	}
	
	//
	private void activities(Date endDate) {
		List<ScheduledActivityEntity> sal = saRepo.findByScheduledTimeLessThanAndReportedTimeIsNull(endDate);
		log.info("Alarm activity job: {} activities over due ({})", sal.size(), endDate);
		List<AlarmEntity> al = new LinkedList<AlarmEntity>();
		List<ScheduledActivityEntity> saSave = new LinkedList<ScheduledActivityEntity>();
		HashSet<Long> patients = new HashSet<Long>();
		for (ScheduledActivityEntity sae : sal) {
			PatientEntity patient = sae.getActivityDefinitionEntity().getHealthPlan().getForPatient();
			if (!patients.contains(patient.getId())) {
				al.add(AlarmEntity.newEntity(AlarmCause.UNREPORTED_ACTIVITY, patient,
						sae.getActivityDefinitionEntity().getHealthPlan().getCareUnit().getHsaId(), 
						sae.getId()));
				patients.add(patient.getId());
			}
			sae.setStatus(ScheduledActivityStatus.CLOSED);
			sae.setReportedTime(new Date());
			sae.setNote("Stängd per automatik.");
			sae.setActualValue(0);
			saSave.add(sae);
		}
		log.info("Alarm activity job: {} new activity alarms!", al.size());
		if (al.size() > 0) {
			alRepo.save(al);
		}
		if (saSave.size() > 0) {
			saRepo.save(saSave);			
		}
	}
	
	//
	private void plans(Date endDate) {
		List<HealthPlanEntity> hpl = hpRepo.findByEndDateLessThan(endDate);
		List<AlarmEntity> al = new LinkedList<AlarmEntity>();
		for (HealthPlanEntity hpe : hpl) {
			al.add(AlarmEntity.newEntity(AlarmCause.PLAN_EXPIRES, hpe.getForPatient(), hpe.getCareUnit().getHsaId(), hpe.getId()));
		}
		log.info("Alarm plan job ready: {} new plan alarms!", al.size());
		if (al.size() > 0) {
			alRepo.save(al);
		}		
	}
}
