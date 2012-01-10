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
package org.callistasoftware.netcare.core.spi.impl;


import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.callistasoftware.netcare.core.api.Alarm;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.repository.AlarmRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.spi.AlarmService;
import org.callistasoftware.netcare.model.entity.AlarmCause;
import org.callistasoftware.netcare.model.entity.AlarmEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlarmServiceImpl extends ServiceSupport implements AlarmService {

	private static Logger log = LoggerFactory.getLogger(AlarmServiceImpl.class);
	
	@Autowired
	private CareUnitRepository cuRepo;
	
	@Autowired
	private ScheduledActivityRepository saRepo;
	
	@Autowired
	private HealthPlanRepository hpRepo;
	
	@Autowired
	private AlarmRepository alRepo;
	
	@PostConstruct
	public void initialize() {
		this.run();
	}
	
	@Override
	public void run() {
		log.info("======== ALARM JOB STARTED =========");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -(HealthPlanServiceImpl.SCHEMA_HISTORY_DAYS));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.add(Calendar.DATE, -1);
		
		plans(cal.getTime());
		
		activities(cal.getTime());
		
		
		log.info("======== ALARM JOB COMPLETED =========");
	}
	
	private void activities(Date endDate) {
		List<ScheduledActivityEntity> sal = saRepo.findByScheduledTimeLessThanAndReportedTimeIsNull(endDate);
		List<AlarmEntity> al = new LinkedList<AlarmEntity>();
		List<ScheduledActivityEntity> saSave = new LinkedList<ScheduledActivityEntity>();
		for (ScheduledActivityEntity sae : sal) {
			al.add(AlarmEntity.newEntity(AlarmCause.UNREPORTED_ACTIVITY, 
					sae.getActivityDefinitionEntity().getHealthPlan().getCareUnit().getHsaId(), 
					sae.getId()));
			sae.setRejected(true);
			sae.setReportedTime(new Date());
			sae.setNote("Stängd automatiskt.");
			sae.setActualValue(0);
			saSave.add(sae);
		}
		if (al.size() > 0) {
			alRepo.save(al);
			saRepo.save(saSave);
		}		
	}
	
	private void plans(Date endDate) {
		List<HealthPlanEntity> hpl = hpRepo.findByEndDateLessThan(endDate);
		List<AlarmEntity> al = new LinkedList<AlarmEntity>();
		for (HealthPlanEntity hpe : hpl) {
			al.add(AlarmEntity.newEntity(AlarmCause.PLAN_EXPIRES, hpe.getCareUnit().getHsaId(), hpe.getId()));
		}
		if (al.size() > 0) {
			alRepo.save(al);
		}		
	}
	
	@Transactional
	@Override
	public ServiceResult<Alarm[]> getCareUnitAlarms(String hsaId) {
		
		final CareUnitEntity cu = this.cuRepo.findByHsaId(hsaId);
		if (cu == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(CareUnitEntity.class, hsaId));
		}
		
		this.verifyReadAccess(cu);
		
		return null;
		
	}

}
