/**
 * Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.repository.AlarmRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PdlLogRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.callistasoftware.netcare.core.spi.PushNotificationService;
import org.callistasoftware.netcare.core.spi.impl.HealthPlanServiceImpl;
import org.callistasoftware.netcare.model.entity.AlarmCause;
import org.callistasoftware.netcare.model.entity.AlarmEntity;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.PdlLogEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Runs system jobs.
 * 
 * 
 * @author Peter
 */
@Component
@Transactional
public class SystemAlarmJob {

	private static Logger log = LoggerFactory.getLogger(SystemAlarmJob.class);

	@Autowired
	private PushNotificationService notificationService;
	
	@Value("${reminder.time}")
	private int reminderTime;

	@Autowired
	private ScheduledActivityRepository saRepo;
	
	@Autowired
	private HealthPlanRepository hpRepo;
	
	@Autowired
	private AlarmRepository alRepo;
		
	@Autowired
	private MessageSource messageBundle;

    @Autowired
    private HealthPlanService service;

    @Autowired
    private PdlLogRepository pdlLogRepository;


	public void init() {
		alarmJob();
		reminderJob();
	}

	// 2 times per day, analyse pdlLog entries from last 24 hours
    @Scheduled(fixedRate = 43200000)
    public void compressPdlLog() {
        log.info("======== COMPRESS PDL LOG JOB STARTED =========");
		Calendar fromCal = Calendar.getInstance();
		fromCal.add(Calendar.DATE, -1);
		Calendar toCal = Calendar.getInstance();
		toCal.add(Calendar.MINUTE, -2);
		
        log.info("select log records between" + fromCal.getTime() + " : " +  toCal.getTime());
		
		
        // Order By hsaId, civiId, action, healthPlanName,  date
		final List<PdlLogEntity> entities = pdlLogRepository.findByDateBetween(fromCal.getTime(), toCal.getTime());
        log.debug("remove duplicate pdlLog number of candidates:" + entities.size());        		
		List<PdlLogEntity> duplicates = new ArrayList<PdlLogEntity>();
        PdlLogEntity lastPdlLog = null;
		for (final PdlLogEntity pdlLog : entities) {
			if ( lastPdlLog == null){
		       	lastPdlLog = pdlLog;
				continue;
			}

        	if ( pdlLog.getHsaId().equals(lastPdlLog.getHsaId()) &&
        			pdlLog.getCivicId().equals(lastPdlLog.getCivicId())&&
        			pdlLog.getAction().equals(lastPdlLog.getAction()) &&
        			pdlLog.getHealtPlanName().equals(lastPdlLog.getHealtPlanName())){
        		duplicates.add(pdlLog);
                log.debug("remove duplicate pdlLog:" + pdlLog.getId());        		
        	}
        	lastPdlLog = pdlLog;
        }
        log.debug("remove duplicate pdlLog number of items:" + duplicates.size());        		
    	pdlLogRepository.delete(duplicates);
        log.info("======== COMPRESS PDL LOG JOB COMPLETED =========");
    }

    @Scheduled(fixedRate = 3600000)
    public void inactiveExpiredHealthPlans() {
        log.info("======== HEALTH PLAN EXPIRATION JOB STARTED =========");
        final List<HealthPlanEntity> entities = hpRepo.findByEndDateLessThanAndActiveTrueAndAutoRenewalFalse(new Date());
        for (final HealthPlanEntity ent : entities) {
            service.inactivateHealthPlan(ent.getId(), true);
        }
        log.info("======== HEALTH PLAN EXPIRATION JOB COMPLETED =========");
    }

    @Scheduled(fixedRate = 3600000)
    public void autoRenewHealthPlans() {
        log.info("========= HEALTH PLAN AUTO RENEW JOB STARTED =========");
        final List<HealthPlanEntity> hpl = hpRepo.findByEndDateLessThanAndActiveTrueAndAutoRenewalTrue(new Date());

        log.debug("Found {} expired health plans that needs to be auto renewed.", hpl.size());
        for (HealthPlanEntity hpe : hpl) {
            log.debug("Perform auto-renewal: health-plan {} for patient {}", hpe.getName(), hpe.getForPatient().getFirstName() + " " + hpe.getForPatient().getSurName());
            service.activateHealthPlan(hpe.getId(), true);
        }
        log.info("========= HEALTH PLAN AUTO RENEW JOB COMPLETED =========");
    }
	
	@Scheduled(fixedRate=3600000)
	public void alarmJob() {
		log.info("======== ALARM JOB STARTED =========");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -(HealthPlanServiceImpl.SCHEMA_HISTORY_DAYS));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.add(Calendar.DATE, -1);
		ApiUtil.dayEnd(cal);

        /*
         * Create alarm for health plans that is about to expire
         */
		plans(cal.getTime());

        /*
         * Create alarm for activities that is about to expire
         */
		activities(cal.getTime());
		
		log.info("======== ALARM JOB COMPLETED =========");
	}
	
	/**
	 * Notifies mobile users about it's time to perform an activity.
	 */
	@Scheduled(fixedDelay=300000)
	public void reminderJob() {
		log.info("======== REMINDER JOB STARTED =========");
		HashMap<PatientEntity, Integer> patients = new HashMap<PatientEntity, Integer>();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, reminderTime);
		
		Date start = cal.getTime();
		log.debug("Find all scheduled activity with scheduled time less than: {} and that is not already reported.");
		
		List<ScheduledActivityEntity> list = saRepo.findByScheduledTimeLessThanAndReportedTimeIsNull(start);
		log.debug("Reminder: {} candidates found", list.size());
		for (ScheduledActivityEntity sae : list) {
			
			/*
			 * Don't send reminder unless the patient
			 * wants it
			 */
			if (!sae.getActivityDefinitionEntity().isReminder() || !sae.getActivityDefinitionEntity().getHealthPlan().isActive()) {
				continue;
			}
			
			PatientEntity patient = sae.getActivityDefinitionEntity().getHealthPlan().getForPatient();
			log.debug("Reminder: for patient {}, activity {}", patient.getFirstName(), sae.getActivityDefinitionEntity().getActivityType().getName());
			
			log.debug("==== PUSH CHECKS ====");
			log.debug("Already reminded: {}", sae.isReminderDone());
			log.debug("Is activity already reported: {}", sae.getReportedTime() != null);
			log.debug("Is patient push enabled: {}", patient.isPushEnbaled());
			log.debug("=====================");
			
			if (!sae.isReminderDone() && sae.getReportedTime() == null && patient.isPushEnbaled()) {
				Integer i = patients.get(patient);
				log.debug("Reminder: for patient {} -- add to send list", patient.getFirstName());
				patients.put(patient, (i == null) ? 1 : i.intValue()+1);
				
				sae.setReminderDone(true);
				saRepo.save(sae);
			}
			
		}
		log.debug("Reminder: {} to send", patients.size());
		for (Map.Entry<PatientEntity, Integer> p : patients.entrySet()) {
			log.debug("Reminder: send {} new events reminder to patient {}", p.getValue(), p.getKey().getFirstName());
			sendReminder(p.getKey(), p.getValue());
		}
		
		log.info("======== REMINDER JOB COMPLETED =========");
	}
	
	private void sendReminder(PatientEntity to, int n) {
		final String title = messageBundle.getMessage("system.name", new Object[0], LocaleContextHolder.getLocale());
		String message = messageBundle.getMessage("system.push.single", new Object[]{}, LocaleContextHolder.getLocale());
		if(n>1) {
			message = messageBundle.getMessage("system.push", new Object[]{new Integer(n)}, LocaleContextHolder.getLocale());
		}
		this.notificationService.sendPushNotification(title, message, to);
	}
	
	private void activities(Date endDate) {
		List<ScheduledActivityEntity> sal = saRepo.findByScheduledTimeLessThanAndReportedTimeIsNull(endDate);
		log.info("Alarm activity job: {} activities over due ({})", sal.size(), endDate);
		List<AlarmEntity> al = new LinkedList<AlarmEntity>();
		List<ScheduledActivityEntity> saSave = new LinkedList<ScheduledActivityEntity>();
		HashSet<Long> patients = new HashSet<Long>();
		for (ScheduledActivityEntity sae : sal) {
			PatientEntity patient = sae.getActivityDefinitionEntity().getHealthPlan().getForPatient();
			if (!patients.contains(patient.getId())) {
				AlarmEntity ae = AlarmEntity.newEntity(AlarmCause.UNREPORTED_ACTIVITY, patient,
						sae.getActivityDefinitionEntity().getHealthPlan().getCareUnit().getHsaId(), 
						sae.getId());
				ae.setInfo(sae.getActivityDefinitionEntity().getHealthPlan().getName());
				al.add(ae);
				
				patients.add(patient.getId());
			}
			sae.setStatus(ScheduledActivityStatus.CLOSED);
			sae.setReportedTime(new Date());
			sae.setNote("St��ngd per automatik.");
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
		List<HealthPlanEntity> hpl = hpRepo.findByEndDateLessThanAndActiveTrue(endDate);
		List<AlarmEntity> al = new LinkedList<AlarmEntity>();
		for (HealthPlanEntity hpe : hpl) {
			if (!hpe.isReminderDone() && !hpe.isAutoRenewal()) {
				AlarmEntity ae = AlarmEntity.newEntity(AlarmCause.PLAN_EXPIRES, hpe.getForPatient(), hpe.getCareUnit().getHsaId(), hpe.getId());
				ae.setInfo(hpe.getName());
				al.add(ae);
				hpe.setReminderDone(true);
				hpRepo.save(hpe);
			}
		}
		log.info("Alarm plan job ready: {} new plan alarms!", al.size());
		if (al.size() > 0) {
			alRepo.save(al);
		}		
	}
}
