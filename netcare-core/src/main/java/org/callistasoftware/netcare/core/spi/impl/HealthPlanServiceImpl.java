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
package org.callistasoftware.netcare.core.spi.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.CareUnit;
import org.callistasoftware.netcare.core.api.DayTime;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityDefintionImpl;
import org.callistasoftware.netcare.core.api.impl.HealthPlanImpl;
import org.callistasoftware.netcare.core.api.impl.ScheduledActivityImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.DefaultSystemMessage;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.messages.ListEntitiesMessage;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of service defintion
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@Service
@Transactional
public class HealthPlanServiceImpl implements HealthPlanService {

	private static final Logger log = LoggerFactory.getLogger(HealthPlanServiceImpl.class);
	
	@Autowired
	private HealthPlanRepository repo;
	
	@Autowired
	private ActivityTypeRepository activityTypeRepository;
	
	@Autowired
	private CareGiverRepository careGiverRepository;
	
	@Autowired
	private CareUnitRepository careUnitRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private ActivityDefinitionRepository activityDefintionRepository;
	
	@Autowired
	private ScheduledActivityRepository scheduledActivityRepository;
	
	
	@Override
	public ServiceResult<HealthPlan[]> loadHealthPlansForPatient(Long patientId) {
		final PatientEntity forPatient = patientRepository.findOne(patientId);
		final List<HealthPlanEntity> entities = this.repo.findByForPatient(forPatient);
		
		final HealthPlan[] dtos = new HealthPlan[entities.size()];
		int count = 0;
		for (final HealthPlanEntity ent : entities) {
			final HealthPlanImpl dto = HealthPlanImpl.newFromEntity(ent, LocaleContextHolder.getLocale());
			dtos[count++] = dto;
		}
		
		return ServiceResultImpl.createSuccessResult(dtos, new GenericSuccessMessage());
	}
	
	public ServiceResult<ScheduledActivity[]> getActivitiesForPatient(PatientBaseView patient) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(1);
		
		c.add(Calendar.WEEK_OF_YEAR, -1);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		
		Date startDate = ApiUtil.floor(c).getTime();

		c.add(Calendar.WEEK_OF_YEAR, 3);
		Date endDate = ApiUtil.ceil(c).getTime();

		PatientEntity forPatient = patientRepository.findOne(patient.getId());
		List<ScheduledActivityEntity> scheduledActivities = scheduledActivityRepository.findByPatientAndScheduledTimeBetween(forPatient, startDate, endDate);
		Collections.sort(scheduledActivities);
		
		ScheduledActivity[] arr = new ScheduledActivity[scheduledActivities.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = ScheduledActivityImpl.newFromEntity(scheduledActivities.get(i));
		}
		return ServiceResultImpl.createSuccessResult(arr, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<HealthPlan> createNewHealthPlan(final HealthPlan o, final CareGiverBaseView careGiver, final Long patientId) {		
		log.info("Creating new ordination {}", o.getName());
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			final Date start = sdf.parse(o.getStartDate());
			final DurationUnit du = DurationUnit.valueOf(o.getDurationUnit().getCode());
			
			final CareGiverEntity cg = this.careGiverRepository.findByHsaId(careGiver.getHsaId());
			
			final PatientEntity patient = this.patientRepository.findOne(patientId);
			
			final HealthPlanEntity newEntity = HealthPlanEntity.newEntity(cg, patient, o.getName(), start, o.getDuration(), du);
			
			final HealthPlanEntity saved = this.repo.save(newEntity);
			final HealthPlan dto = HealthPlanImpl.newFromEntity(saved, null);
			
			return ServiceResultImpl.createSuccessResult(dto, new GenericSuccessMessage());
			
		} catch (ParseException e1) {
			throw new IllegalArgumentException("Could not parse date.", e1);
		}
	}

	@Override
	public ServiceResult<HealthPlan> deleteHealthPlan(Long ordinationId) {
		log.info("Deleting ordination {}", ordinationId);
		this.repo.delete(ordinationId);
		
		return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<HealthPlan> loadHealthPlan(Long ordinationId,
			PatientBaseView patient) {
		final HealthPlanEntity entity = this.repo.findOne(ordinationId);
		if (entity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, ordinationId));
		}
		
		if (!entity.getForPatient().getId().equals(patient.getId())) {
			return ServiceResultImpl.createFailedResult(new DefaultSystemMessage("Du har inte behörigheten att se denna ordination"));
		}
		
		final HealthPlan dto = HealthPlanImpl.newFromEntity(entity, null);
		return ServiceResultImpl.createSuccessResult(dto, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<HealthPlan> addActvitiyToHealthPlan(
			Long ordinationId, final ActivityDefinition dto) {
		log.info("Adding activity defintion to existing ordination with id {}", ordinationId);
		final HealthPlanEntity entity = this.repo.findOne(ordinationId);
		if (entity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, ordinationId));
		}
		
		log.debug("Ordination entity found and resolved.");

		final ActivityTypeEntity typeEntity = this.activityTypeRepository.findOne(dto.getType().getId());
		if (typeEntity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityTypeEntity.class, dto.getType().getId()));
		}
		
		log.debug("Activity type entity found and resolved");
		
		/*
		 * Create the day frequence based on what the user
		 * selected.
		 */
		log.debug("Processing the day and time frequence...");
		
		final Frequency frequency = new Frequency();
		frequency.setWeekFrequency(dto.getWeekFrequency());
		for (final DayTime dt : dto.getDayTimes()) {
			FrequencyDay fd = FrequencyDay.newFrequencyDay(ApiUtil.toIntDay(dt.getDay()));
			for (String time : dt.getTimes()) {
				fd.addTime(FrequencyTime.unmarshal(time));
			}
			frequency.addDay(fd);
		}
		
		final ActivityDefinitionEntity newEntity = ActivityDefinitionEntity.newEntity(entity, typeEntity, frequency);
		newEntity.setActivityTarget(dto.getGoal());
		if (dto.getStartDate() != null) {
			newEntity.setStartDate(ApiUtil.toDate(dto.getStartDate()));		
		}
		ActivityDefinitionEntity savedEntity = activityDefintionRepository.save(newEntity);
		
		log.debug("Activity defintion saved.");
		
		scheduleActivities(savedEntity);
		
		final HealthPlanEntity savedOrdination = this.repo.save(entity);
		log.debug("Ordination saved");
		
		log.debug("Creating result. Success!");
		final HealthPlan result = HealthPlanImpl.newFromEntity(savedOrdination, LocaleContextHolder.getLocale());
		return ServiceResultImpl.createSuccessResult(result, new GenericSuccessMessage());
	}
	
	
	/**
	 * Schedules activities.
	 * 
	 * @param activityDefinition the activity defintion.
	 */
	private void scheduleActivities(ActivityDefinitionEntity activityDefinition) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(activityDefinition.getStartDate());
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);	
		Frequency freq = activityDefinition.getFrequency();
		log.debug("Schedule activities for: {}, frequency {}", activityDefinition, Frequency.marshal(freq));
		while (cal.getTime().compareTo(activityDefinition.getHealthPlan().getEndDate()) <= 0) {
			if (freq.isDaySet(cal)) {
				log.debug("Schedule day: {}", cal.getTime());
				List<ScheduledActivityEntity> list = scheduleActivity(activityDefinition, cal, freq.getDay(cal.get(Calendar.DAY_OF_WEEK)));
				scheduledActivityRepository.save(list);
				// single event.
				if (freq.getWeekFrequency() == 0) {
					break;
				}
			}
			if (freq.getWeekFrequency() > 1 && cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY &&
					cal.getTime().compareTo(activityDefinition.getStartDate()) > 0) {
				cal.add(Calendar.DATE, 8*(freq.getWeekFrequency()-1));
			} else {
				cal.add(Calendar.DATE, 1);
			}
		}
	}

	/**
	 * Returns a list with scheduled activities, might be empty if none is applicable.
	 * 
	 * @param entity the acitivty definiton to schedule.
	 * @param day the actual day.
	 * @return a list of scheduled activities, empty if none is applicable.
	 */
	private List<ScheduledActivityEntity> scheduleActivity(ActivityDefinitionEntity entity, Calendar day, FrequencyDay fday) {
		List<ScheduledActivityEntity> list = new LinkedList<ScheduledActivityEntity>();
		for (FrequencyTime t : fday.getTimes()) {
			day.set(Calendar.HOUR_OF_DAY, t.getHour());
			day.set(Calendar.MINUTE, t.getMinute());
			ScheduledActivityEntity scheduledActivity = entity.createScheduledActivityEntity(day.getTime());
			scheduledActivity.setTargetValue(entity.getActivityTarget());
			list.add(scheduledActivity);
		}
		log.debug("{} activities scheduled: {}", list.size());
		return list;
	}

	@Override
	public ServiceResult<ActivityDefinition[]> loadActivitiesForHealthPlan(
			Long healthPlanId) {
		log.info("Loading health plan activities for health plan {}", healthPlanId);
		final HealthPlanEntity entity = this.repo.findOne(healthPlanId);
		if (entity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}
		
		log.debug("Found {} health plan activities for health plan {}", entity.getActivityDefinitions().size(), healthPlanId);
		return ServiceResultImpl.createSuccessResult(ActivityDefintionImpl.newFromEntities(entity.getActivityDefinitions()), new ListEntitiesMessage(ActivityDefinitionEntity.class, entity.getActivityDefinitions().size()));
	}

	@Override
	public ServiceResult<ScheduledActivity> reportReady(
			Long scheduledActivityId, int value) {
		log.info("Report done for scheduled activity {} value {}", scheduledActivityId, value);
		ScheduledActivityEntity entity = scheduledActivityRepository.findOne(scheduledActivityId);
		entity.setReportedTime(new Date());
		entity.setActualValue(value);
		scheduledActivityRepository.save(entity);
		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntity(entity), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ScheduledActivity[]> loadLatestReportedForAllPatients(final CareUnit careUnit) {
		log.info("Loading latest reported activities for all patients belonging to care unit {}", careUnit.getHsaId());
		
		final CareUnitEntity entity = this.careUnitRepository.findByHsaId(careUnit.getHsaId());
		if (entity == null) {
			ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(CareUnitEntity.class, -1L));
		}
		
		final List<ScheduledActivityEntity> activities = this.scheduledActivityRepository.findByCareUnit(entity.getHsaId());
		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntities(activities), new ListEntitiesMessage(ScheduledActivityEntity.class, activities.size()));
	}
}
