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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityComment;
import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.ActivityReport;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.CareUnit;
import org.callistasoftware.netcare.core.api.DayTime;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.MeasurementDefinition;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.PatientEvent;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.Value;
import org.callistasoftware.netcare.core.api.impl.ActivityCommentImpl;
import org.callistasoftware.netcare.core.api.impl.ActivityDefintionImpl;
import org.callistasoftware.netcare.core.api.impl.HealthPlanImpl;
import org.callistasoftware.netcare.core.api.impl.PatientEventImpl;
import org.callistasoftware.netcare.core.api.impl.ScheduledActivityImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.EntityDeletedMessage;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.messages.ListEntitiesMessage;
import org.callistasoftware.netcare.core.api.messages.NoAccessMessage;
import org.callistasoftware.netcare.core.api.statistics.ActivityCount;
import org.callistasoftware.netcare.core.api.statistics.HealthPlanStatistics;
import org.callistasoftware.netcare.core.api.statistics.MeasuredValue;
import org.callistasoftware.netcare.core.api.statistics.ReportedActivity;
import org.callistasoftware.netcare.core.api.statistics.ReportedValue;
import org.callistasoftware.netcare.core.api.util.DateUtil;
import org.callistasoftware.netcare.core.repository.ActivityCommentRepository;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.AlarmRepository;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.repository.UserRepository;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.callistasoftware.netcare.model.entity.ActivityCommentEntity;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.callistasoftware.netcare.model.entity.AlarmCause;
import org.callistasoftware.netcare.model.entity.AlarmEntity;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.DurationUnit;
import org.callistasoftware.netcare.model.entity.EntityUtil;
import org.callistasoftware.netcare.model.entity.Frequency;
import org.callistasoftware.netcare.model.entity.FrequencyDay;
import org.callistasoftware.netcare.model.entity.FrequencyTime;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;
import org.callistasoftware.netcare.model.entity.MeasurementDefinitionEntity;
import org.callistasoftware.netcare.model.entity.MeasurementEntity;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityStatus;
import org.callistasoftware.netcare.model.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
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
public class HealthPlanServiceImpl extends ServiceSupport implements HealthPlanService {
	
	/**
	 * Days back when fetching patient plan (schema).
	 */
	public static int SCHEMA_HISTORY_DAYS = 7;
	/**
	 * Days forward when fetching patient plan (schema).
	 */
	public static int SCHEMA_FUTURE_DAYS  = (2*SCHEMA_HISTORY_DAYS);
	/**
	 * Always get full weeks when fetching patient plan (schema), and weeks starts on Mondays.
	 */
	public static int SCHEMA_DAY_ALIGN = Calendar.MONDAY;
		
	/**
	 * CSV End of Line
	 */
	public static String CSV_EOL = "\r\n";
	
	@org.springframework.beans.factory.annotation.Value("${csv.delimiter}")
	private String CSV_SEP;

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
	private UserRepository userRepository;

	@Autowired
	private ActivityDefinitionRepository activityDefintionRepository;
	
	@Autowired
	private ScheduledActivityRepository scheduledActivityRepository;
	
	@Autowired
	private ActivityCommentRepository commentRepository;
	
	@Autowired
	private AlarmRepository alarmRepo;

	
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
		
		return ServiceResultImpl.createSuccessResult(dtos, new ListEntitiesMessage(HealthPlanEntity.class, dtos.length));
	}
	
	public ServiceResult<ScheduledActivity[]> getActivitiesForPatient(PatientBaseView patient) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(1);
		
		c.add(Calendar.DATE, -(SCHEMA_HISTORY_DAYS));
		c.set(Calendar.DAY_OF_WEEK, SCHEMA_DAY_ALIGN);
		
		Date startDate = ApiUtil.dayBegin(c).getTime();

		c.add(Calendar.DATE, SCHEMA_HISTORY_DAYS + SCHEMA_FUTURE_DAYS);
		Date endDate = ApiUtil.dayEnd(c).getTime();

		PatientEntity forPatient = patientRepository.findOne(patient.getId());
		List<ScheduledActivityEntity> entities = scheduledActivityRepository.findByPatientAndScheduledTimeBetween(forPatient, startDate, endDate);
		Collections.sort(entities);
		
		ScheduledActivity[] arr = ScheduledActivityImpl.newFromEntities(entities);
		
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
	public ServiceResult<HealthPlan> deleteHealthPlan(Long healthPlanId) {
		log.info("Deleting health plan {}", healthPlanId);
		final HealthPlanEntity hp = this.repo.findOne(healthPlanId);
		if (hp == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}
		
		this.verifyWriteAccess(hp);
		
		this.repo.delete(healthPlanId);
		
		return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<HealthPlan> loadHealthPlan(Long healthPlanId) {
		final HealthPlanEntity entity = this.repo.findOne(healthPlanId);
		if (entity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}
		
		this.verifyReadAccess(entity);
		
		final HealthPlan dto = HealthPlanImpl.newFromEntity(entity, LocaleContextHolder.getLocale());
		return ServiceResultImpl.createSuccessResult(dto, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<HealthPlan> addActvitiyToHealthPlan(
			final Long healthPlanId, final ActivityDefinition dto, final UserBaseView user) {
		log.info("Adding activity defintion to existing ordination with id {}", healthPlanId);
		final HealthPlanEntity entity = this.repo.findOne(healthPlanId);
		if (entity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}
		
		this.verifyWriteAccess(entity);
		
		log.debug("Health plan entity found and resolved.");

		final ActivityTypeEntity typeEntity = this.activityTypeRepository.findOne(dto.getType().getId());
		if (typeEntity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityTypeEntity.class, dto.getType().getId()));
		}
		
		log.debug("Activity type entity found and resolved");
		
		/*
		 * Create the day frequency based on what the user
		 * selected.
		 */
		log.debug("Processing the day and time frequence...");
		
		final Frequency frequency = new Frequency();
		frequency.setWeekFrequency(dto.getActivityRepeat());
		for (final DayTime dt : dto.getDayTimes()) {
			FrequencyDay fd = FrequencyDay.newFrequencyDay(ApiUtil.toIntDay(dt.getDay()));
			for (String time : dt.getTimes()) {
				fd.addTime(FrequencyTime.unmarshal(time));
			}
			frequency.addDay(fd);
		}
		log.debug("Frequency: {}", Frequency.marshal(frequency));

		final UserEntity userEntity = user.isCareGiver() ? careGiverRepository.findOne(user.getId()) : patientRepository.findOne(user.getId());
		final ActivityDefinitionEntity newEntity = ActivityDefinitionEntity.newEntity(entity, typeEntity, frequency, userEntity);
		
		log.debug("Setting public definition to {}", dto.isPublicDefinition());
		newEntity.setPublicDefinition(dto.isPublicDefinition());
		
		/*
		 * Process measurement defintions
		 */
		for (final MeasurementDefinitionEntity mde : newEntity.getMeasurementDefinitions()) {
			for (final MeasurementDefinition md : dto.getGoalValues()) {
				if (mde.getMeasurementType().getId().equals(md.getMeasurementType().getId())) {	
					
					log.debug("Processing measure value {} for activity type {}", mde.getMeasurementType().getName(), mde.getMeasurementType().getActivityType().getName());
					
					switch (mde.getMeasurementType().getValueType()) {
					case INTERVAL:
						log.debug("Setting values for measure defintion: {}-{}", md.getMinTarget(), md.getMaxTarget());
						mde.setMaxTarget(md.getMaxTarget());
						mde.setMinTarget(md.getMinTarget());
						break;
					case SINGLE_VALUE:
						log.debug("Setting values for measure defintion: {}", md.getTarget());
						mde.setTarget(md.getTarget());
						break;
					}
				}
			}
		}
		
		if (dto.getStartDate() != null) {
			newEntity.setStartDate(ApiUtil.parseDate(dto.getStartDate()));		
		}
		
		ActivityDefinitionEntity savedEntity = activityDefintionRepository.save(newEntity);
		
		log.debug("Activity defintion saved.");
		
		scheduleActivities(savedEntity);
		
		final HealthPlanEntity savedOrdination = this.repo.save(entity);
		log.debug("Health plan saved");
		
		log.debug("Creating result. Success!");
		final HealthPlan result = HealthPlanImpl.newFromEntity(savedOrdination, LocaleContextHolder.getLocale());
		return ServiceResultImpl.createSuccessResult(result, new GenericSuccessMessage());
	}
	
	
	@Override
	public void scheduleActivities(ActivityDefinitionEntity activityDefinition) {
		scheduledActivityRepository.save(activityDefinition.scheduleActivities());
	}

	@Override
	public ServiceResult<ActivityDefinition[]> loadActivitiesForHealthPlan(
			Long healthPlanId) {
		log.info("Loading health plan activities for health plan {}", healthPlanId);
		final HealthPlanEntity entity = this.repo.findOne(healthPlanId);
		if (entity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}
		
		this.verifyReadAccess(entity);
		
		log.debug("Found {} health plan activities for health plan {}", entity.getActivityDefinitions().size(), healthPlanId);
		return ServiceResultImpl.createSuccessResult(ActivityDefintionImpl.newFromEntities(entity.getActivityDefinitions()), new ListEntitiesMessage(ActivityDefinitionEntity.class, entity.getActivityDefinitions().size()));
	}

	@Override
	public ServiceResult<ScheduledActivity> reportReady(
			Long scheduledActivityId, ActivityReport report) {
		log.info("Report done for scheduled activity {}", scheduledActivityId);
		ScheduledActivityEntity entity = scheduledActivityRepository.findOne(scheduledActivityId);
		entity.setReportedTime(new Date());
		entity.setStatus(report.isRejected() ? ScheduledActivityStatus.REJECTED : ScheduledActivityStatus.OPEN);
		entity.setNote(report.getNote());
		entity.setPerceivedSense(report.getSense());
		for (Value value : report.getValues()) {
			MeasurementEntity me = entity.lookupMeasurement(value.getSeqno()); 
			me.setReportedValue(value.getValue());
			log.debug("Alarm status: enabled {} raised {}", me.getMeasurementDefinition().getMeasurementType().isAlarmEnabled(), me.isAlarm());
			if (!report.isRejected() && me.isAlarm()) {
				AlarmEntity ae = AlarmEntity.newEntity(AlarmCause.LIMIT_BREACH, 
						entity.getActivityDefinitionEntity().getHealthPlan().getForPatient(), 
						entity.getActivityDefinitionEntity().getHealthPlan().getCareUnit().getHsaId(), me.getId());
					Option o = new Option(me.getMeasurementDefinition().getMeasurementType().getUnit().name(), LocaleContextHolder.getLocale());
					ae.setInfo(me.getMeasurementDefinition().getMeasurementType().getName() + ": " + me.getReportedValue() + " " + o.getValue());
				alarmRepo.save(ae);
			}
		}
		Date d = ApiUtil.parseDateTime(report.getActualDate(), report.getActualTime());
		entity.setActualTime(d);
		entity = scheduledActivityRepository.save(entity);
		
		log.debug("Reported time for activity is: {}", entity.getReportedTime());
		
		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntity(entity), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ScheduledActivity[]> loadLatestReportedForAllPatients(final CareUnit careUnit) {
		log.info("Loading latest reported activities for all patients belonging to care unit {}", careUnit.getHsaId());
		
		final CareUnitEntity entity = this.careUnitRepository.findByHsaId(careUnit.getHsaId());
		if (entity == null) {
			ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(CareUnitEntity.class, -1L));
		}
		
		this.verifyReadAccess(entity);
		
		final List<ScheduledActivityEntity> activities = this.scheduledActivityRepository.findByCareUnit(entity.getHsaId());
		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntities(activities), new ListEntitiesMessage(ScheduledActivityEntity.class, activities.size()));
	}

	@Override
	public ServiceResult<ActivityDefinition[]> getPlannedActivitiesForPatient(
			PatientBaseView patient) {
		PatientEntity forPatient = patientRepository.findOne(patient.getId());
		Date now = new Date();
		List<ActivityDefinitionEntity> defs = activityDefintionRepository.findByPatientAndNow(forPatient, now);
		ActivityDefinition[] arr = ActivityDefintionImpl.newFromEntities(defs);
		return ServiceResultImpl.createSuccessResult(arr,new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<PatientEvent> getActualEventsForPatient(PatientBaseView patientView) {
		PatientEntity patient = patientRepository.findOne(patientView.getId());
		Calendar cal = Calendar.getInstance();
		Date today = ApiUtil.dayBegin(cal).getTime();
		cal.add(Calendar.DATE, -(SCHEMA_HISTORY_DAYS));
		cal.set(Calendar.DAY_OF_WEEK, SCHEMA_DAY_ALIGN);
		Date start = ApiUtil.dayBegin(cal).getTime();
		cal.setTime(today);
		Date end = ApiUtil.dayEnd(cal).getTime();
		final List<ScheduledActivityEntity> activities = scheduledActivityRepository.findByPatientAndScheduledTimeBetween(patient, start, end);
		int num = 0;
		int due = 0;
		for (ScheduledActivityEntity sc : activities) {
			if (sc.getReportedTime() == null) {
				if (sc.getScheduledTime().compareTo(today) < 0) {
					due++;
				} else {
					num++;
				}
			}
		}
		PatientEvent event = PatientEventImpl.newPatientEvent(num, due);
		ServiceResult<PatientEvent> sr;
		sr = ServiceResultImpl.createSuccessResult(event, new GenericSuccessMessage());
		return sr;
	}
	
	public ServiceResult<ScheduledActivity[]> getScheduledActivitiesForHealthPlan(
			Long healthPlanId) {
		log.info("Get scheduled activities for health plan {}", healthPlanId);
		final HealthPlanEntity ad = this.repo.findOne(healthPlanId);
		if (ad == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}
		
		this.verifyReadAccess(ad);
		
		final List<ScheduledActivityEntity> entities = this.scheduledActivityRepository.findScheduledActivitiesForHealthPlan(healthPlanId);
		log.debug("Found {} scheduled activities", entities.size());
		
		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntities(entities), new ListEntitiesMessage(ScheduledActivityEntity.class, entities.size()));
	}

	@Override
	public ServiceResult<HealthPlanStatistics> getStatisticsForHealthPlan(
			Long healthPlanId) {
		log.info("Getting statistics for health plans...");
		
		final HealthPlanStatistics stats = new HealthPlanStatistics();
		
		final HealthPlanEntity healthPlan = this.repo.findOne(healthPlanId);
		if (healthPlan == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}
		
		this.verifyReadAccess(healthPlan);
		
		log.debug("Calculating health plan overview...");
		final ScheduledActivity[] activities = this.getScheduledActivitiesForHealthPlan(healthPlanId).getData();
		final List<ActivityCount> activityCount = new ArrayList<ActivityCount>();
		
		actLoop: for(final ScheduledActivity ac : activities) {
			
			if (!ac.getDefinition().isPublicDefinition() && this.getCurrentUser().isCareGiver()) {
				log.debug("Skip activity because the care giver was not allowed to see it.");
				continue actLoop;
			}
			
			final String name = ac.getDefinition().getType().getName();
			final ActivityCount act = new ActivityCount(name);
			final ActivityCount existing = this.findActivityCount(name, activityCount);
			
			if (existing == null) {
				log.debug("Activity count not in list. Adding {}", act.getName());
				activityCount.add(act);
			}
			
			this.findActivityCount(name, activityCount).increaseCount();
		}
		stats.setActivities(activityCount);
		log.debug("Health plan overview calculated.");
		
		
		/*
		 * Get all reported activities
		 */
		log.debug("Calculating reported activities...");
		final List<ScheduledActivityEntity> ents = this.scheduledActivityRepository.findReportedActivitiesForHealthPlan(
				healthPlanId
				, healthPlan.getStartDate()
				, new Date()
				, new Sort(Sort.Direction.ASC, "scheduledTime"));
		
		final List<MeasuredValue> measuredValues = new ArrayList<MeasuredValue>();
		for (final ScheduledActivityEntity e : ents) {
			
			if (!e.getActivityDefinitionEntity().isPublicDefinition() && this.getCurrentUser().isCareGiver()) {
				log.debug("Skip activity because the care giver was not allowed to see it.");
				continue;
			}
			
			final ReportedActivity ra = new ReportedActivity();
			ra.setName(e.getActivityDefinitionEntity().getActivityType().getName());
			ra.setNote(e.getNote());
			ra.setReportedAt(DateUtil.toDateTime(e.getReportedTime()));
			ra.setLabel(DateUtil.toDateTime(e.getScheduledTime()));
			
			final List<MeasurementEntity> measurements = e.getMeasurements();
			for (final MeasurementEntity m : measurements) {	
				final String measurementName = m.getMeasurementDefinition().getMeasurementType().getName(); 
				MeasuredValue mv = this.findMeasuredValue(measurementName, measuredValues);
				if (mv == null) {
					mv = new MeasuredValue();
					mv.setName(e.getActivityDefinitionEntity().getActivityType().getName());
					mv.setDefinitionId(e.getActivityDefinitionEntity().getId());
					mv.setValueType(new Option(measurementName, null));
					mv.setUnit(new Option(m.getMeasurementDefinition().getMeasurementType().getUnit().name(), LocaleContextHolder.getLocale()));
					mv.setInterval(m.getMeasurementDefinition().getMeasurementType().getValueType().equals(MeasurementValueType.INTERVAL));
					measuredValues.add(mv);
				}
				
				final ReportedValue rv = new ReportedValue();
				switch (m.getMeasurementDefinition().getMeasurementType().getValueType()) {
				case INTERVAL:
					rv.setMaxTargetValue((float) m.getMaxTarget());
					rv.setMinTargetValue((float) m.getMinTarget());
					break;
				case SINGLE_VALUE:
					rv.setTargetValue((float) m.getTarget());
					break;
				}
				
				rv.setReportedValue((float) m.getReportedValue());
				rv.setReportedAt(DateUtil.toDateTime(e.getScheduledTime()));
				
				mv.getReportedValues().add(rv);
			}
			ra.setMeasures(measuredValues);
		}
		
		stats.setMeasuredValues(measuredValues);
		
		return ServiceResultImpl.createSuccessResult(stats, new GenericSuccessMessage());
	}
	
	private MeasuredValue findMeasuredValue(final String measurementName, final List<MeasuredValue> list) {
		for (final MeasuredValue mv : list) {
			if (mv.getValueType().getCode().equals(measurementName)) {
				return mv;
			}
		}
		
		return null;
	}
	
	private ActivityCount findActivityCount(final String name, final List<ActivityCount> list) {
		for (final ActivityCount a : list) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		
		return null;
	}

	@Override
	public ServiceResult<ActivityDefinition> deleteActivity(
			Long activityDefinitionId) {
		
		log.info("Deleteing activity definition {}", activityDefinitionId);
		final ActivityDefinitionEntity ent = this.activityDefintionRepository.findOne(activityDefinitionId);
		if (ent == null) {
			log.warn("The activity definition {} could not be found.", activityDefinitionId);
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityDefinitionEntity.class, activityDefinitionId));
		}
		
		this.verifyWriteAccess(ent);
		
		ent.setRemovedFlag(true);
		
		log.debug("Activity definition with id {} marked as rmeoved", activityDefinitionId);
		this.activityDefintionRepository.save(ent);
		
		return ServiceResultImpl.createSuccessResult(ActivityDefintionImpl.newFromEntity(ent), new EntityDeletedMessage(ActivityDefinitionEntity.class, activityDefinitionId)); 
	}
	
	@Override
	public String getICalendarEvents(PatientBaseView patient) {
		PatientEntity forPatient = patientRepository.findOne(patient.getId());
		Date now = new Date();
		List<ActivityDefinitionEntity> defs = activityDefintionRepository.findByPatientAndNow(forPatient, now);
		final String calPattern =
			"BEGIN:VCALENDAR\r\n"
			+ "VERSION:2.0\r\n"
			+ "PRODID:-//Callista Enterprise//NONSGML NetCare//EN\r\n"
			+ "%s"
			+ "END:VCALENDAR\r\n";
		
		final String eventPattern = 
			"BEGIN:VEVENT\r\n"
			+ "UID:%s@%s.%d\r\n"
			+ "DTSTAMP;TZID=Europe/Stockholm:%s\r\n"
			+ "DTSTART;TZID=Europe/Stockholm:%s\r\n"
			+ "DURATION:%s\r\n"
			+ "SUMMARY:%s\r\n"
			+ "TRANSP:OPAQUE\r\n"
			+ "CLASS:CONFIDENTIAL\r\n"
			+ "CATEGORIES:PERSONLIGT,PLAN,HÄLSA\r\n"
			+ "%s"
			+ "END:VEVENT\r\n";
		
		StringBuffer events = new StringBuffer();
		for (ActivityDefinitionEntity ad : defs) {
			String stamp = EntityUtil.formatCalTime(ad.getCreatedTime());
			String summary = ad.getActivityType().getName();
			Frequency fr = ad.getFrequency();
			String duration = toICalDuration(ad);
			for (FrequencyDay day : fr.getDays()) {
				StringBuffer rrule = new StringBuffer();
				String wday = toICalDay(day);
				if (fr.getWeekFrequency() > 0) {
					rrule.append("RRULE:FREQ=WEEKLY");
					rrule.append(";INTERVAL=").append(ad.getFrequency().getWeekFrequency());
					rrule.append(";WKST=MO");
					rrule.append(";BYDAY=").append(wday);
					rrule.append(";UNTIL=").append(EntityUtil.formatCalTime(ad.getHealthPlan().getEndDate()));
					rrule.append("\r\n");
				}
				
				int timeIndex = 0;
				for (FrequencyTime time : day.getTimes()) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(ad.getStartDate());
					cal.set(Calendar.HOUR, time.getHour());
					cal.set(Calendar.MINUTE, time.getMinute());
					String start = EntityUtil.formatCalTime(cal.getTime());
					events.append(String.format(eventPattern, ad.getUUID(), wday, timeIndex++, stamp, start, duration, summary, rrule.toString()));
				}
			}
		}
		String r = String.format(calPattern, events.toString());
		return r;

	}
	
	
	/**
	 * Converts amount and units into minutes. <p>
	 * 
	 * Steps are converted into slow walking, and meter into slow jog. <p>
	 * 
	 * Minutes are rounded to half-hour precision.
	 * 
	 * @param the activity deftinion. 
	 * @return the ical duration.
	 */
	private static String toICalDuration(ActivityDefinitionEntity ad) {	
		int minutes = 30;
		for (MeasurementDefinitionEntity md : ad.getMeasurementDefinitions()) {
			int target = md.getMeasurementType().getValueType().equals(MeasurementValueType.INTERVAL) ? md.getMaxTarget() : md.getTarget();
			switch (md.getMeasurementType().getUnit()) {
			case STEP:
				minutes = Math.max(target / 50, minutes);
				break;
			case METER:
				minutes = Math.max(target / 80, minutes);
				break;
			case MINUTE:
				minutes = Math.max(target, minutes);				
				break;
			}
		}
		
		String dur = "PT";
		if (minutes > 60) {
			int hours = minutes / 60;
			dur += (hours + "H");
			minutes = (minutes % 60);
		}
		minutes = (minutes < 30) ? 30 : 60;

		dur += (minutes + "M");

		return dur;
	}
	
	//
	private static String toICalDay(FrequencyDay day) {
		switch (day.getDay()) {
		case Calendar.MONDAY:
			return "MO";
		case Calendar.TUESDAY:
			return "TU";
		case Calendar.WEDNESDAY:
			return "WE";
		case Calendar.THURSDAY:
			return "TH";
		case Calendar.FRIDAY:
			return "FR";
		case Calendar.SATURDAY:
			return "SA";
		case Calendar.SUNDAY:
			return "SU";
		}
		throw new IllegalArgumentException("Invalid day: " + day.getDay());
	}

	@Override
	public ServiceResult<ScheduledActivity> commentOnPerformedActivity(
			Long activityId, String comment) {
		final ScheduledActivityEntity ent = this.scheduledActivityRepository.findOne(activityId);
		if (ent == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ScheduledActivityEntity.class, activityId));
		}
		
		this.verifyWriteAccess(ent);
		
		final UserEntity user = this.getCurrentUser();
		if (user.isCareGiver()) {
			final CareGiverEntity cg = (CareGiverEntity) user;
			ent.getComments().add(ActivityCommentEntity.newEntity(comment, cg, ent));
			
			return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntity(ent), new GenericSuccessMessage());
		} else {
			throw new SecurityException("A patient is not allow to comment his own activity");
		}
	}

	@Override
	public ServiceResult<ActivityComment[]> loadCommentsForPatient() {
		final PatientEntity patient = this.getPatient();
		final List<ActivityCommentEntity> entities = this.commentRepository.findCommentsForPatient(patient);
		
		return ServiceResultImpl.createSuccessResult(ActivityCommentImpl.newFromEntities(entities), new ListEntitiesMessage(ActivityCommentEntity.class, entities.size()));
	}

	@Override
	public ServiceResult<ActivityComment> replyToComment(Long comment, final String reply) {
		final ActivityCommentEntity ent = this.commentRepository.findOne(comment);
		if (ent == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityCommentEntity.class, comment));
		}
		
		this.verifyWriteAccess(getPatient());
		
		ent.setReply(reply);
		ent.setRepliedAt(new Date());
		
		return ServiceResultImpl.createSuccessResult(ActivityCommentImpl.newFromEntity(ent), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ActivityComment[]> loadRepliesForCareGiver() {
		final CareGiverEntity cg = this.getCareGiver();
		log.info("Loading replies for care giver {}", cg.getName());
		
		final List<ActivityCommentEntity> comments = this.commentRepository.findRepliesForCareGiver(cg);
		return ServiceResultImpl.createSuccessResult(ActivityCommentImpl.newFromEntities(comments), new ListEntitiesMessage(ActivityCommentEntity.class, comments.size()));
	}

	@Override
	public ServiceResult<ActivityComment> deleteComment(Long commentId) {
		final UserEntity user = this.getCurrentUser();
		log.info("Care giver {} is deleting comment {}", user.getId(), commentId);
		
		final ActivityCommentEntity ent = this.commentRepository.findOne(commentId);
		if (ent == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityCommentEntity.class, commentId));
		}
		
		this.verifyWriteAccess(ent);
		this.commentRepository.delete(ent);
		
		return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ScheduledActivity> loadScheduledActivity(Long activity) {
		final ScheduledActivityEntity sae = this.scheduledActivityRepository.findOne(activity);
		if (sae == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ScheduledActivityEntity.class, activity));
		}
		
		this.verifyReadAccess(sae);
		
		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntity(sae), new GenericSuccessMessage());
	}

	private static String quotedString(String s) {
		return String.format("\"%s\"", s);
	}
	
	// FIXME: Requires single activity definitions
	@Override
	public String getPlanReports(UserBaseView user, Long activityDeifntionId) {
		ActivityDefinitionEntity entity = activityDefintionRepository.findOne(activityDeifntionId);
		UserEntity ue = userRepository.findOne(user.getId());
		if (!entity.isReadAllowed(ue)) {
			return new NoAccessMessage().getMessage();
		}
		List<ScheduledActivityEntity> list = entity.getScheduledActivities();
		StringBuffer hb = new StringBuffer();
		hb.append("Aktivitet");
		hb.append(CSV_SEP).append("Planerad datum");
		hb.append(CSV_SEP).append("Planerad tid");
		hb.append(CSV_SEP).append("Utförd datum");
		hb.append(CSV_SEP).append("Utförd tid");
		hb.append(CSV_SEP).append("Känsla");
		hb.append(CSV_SEP).append("Kommentar");
		
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (ScheduledActivityEntity sc : list) {
			if (sc.getReportedTime() == null) {
				continue;
			}
			sb.append(quotedString(sc.getActivityDefinitionEntity().getActivityType().getName()));
			sb.append(CSV_SEP).append(ApiUtil.formatDate(sc.getScheduledTime()));
			sb.append(CSV_SEP).append(ApiUtil.formatTime(sc.getScheduledTime()));
			sb.append(CSV_SEP).append(sc.getActualTime() != null ? ApiUtil.formatDate(sc.getActualTime()) : "");
			sb.append(CSV_SEP).append(sc.getActualTime() != null ? ApiUtil.formatTime(sc.getActualTime()) : "");
			sb.append(CSV_SEP).append(sc.getPerceivedSense());
			sb.append(CSV_SEP).append(quotedString(sc.getNote()));
			for (MeasurementEntity me : sc.getMeasurements()) {
				MeasurementTypeEntity type = me.getMeasurementDefinition().getMeasurementType();
				String name = type.getName();
				if (first) {
					Option unit = new Option(type.getUnit().name(), LocaleContextHolder.getLocale());
					hb.append(CSV_SEP).append(quotedString(name + " [" + unit.getValue() + "]"));
					if (type.getValueType().equals(MeasurementValueType.INTERVAL)) {
						hb.append(CSV_SEP).append(quotedString(name + " - min"));
						hb.append(CSV_SEP).append(quotedString(name + " - max"));
					} else {
						hb.append(CSV_SEP).append(quotedString(name + " - mål"));						
					}
				}
				sb.append(CSV_SEP).append(me.getReportedValue());
				if (type.getValueType().equals(MeasurementValueType.INTERVAL)) {
					sb.append(CSV_SEP).append(me.getMinTarget());
					sb.append(CSV_SEP).append(me.getMaxTarget());
				} else {
					sb.append(CSV_SEP).append(me.getTarget());
				}
			}
			sb.append(CSV_EOL);
			first = false;
		}
		hb.append(CSV_EOL);
		
		return hb.append(sb).toString();
	}
}
