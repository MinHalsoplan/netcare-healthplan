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

import java.util.*;

import org.callistasoftware.netcare.core.api.ActivityComment;
import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.ActivityItemValuesDefinition;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.CareActorBaseView;
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
import org.callistasoftware.netcare.core.api.impl.ActivityCommentImpl;
import org.callistasoftware.netcare.core.api.impl.ActivityDefinitionImpl;
import org.callistasoftware.netcare.core.api.impl.HealthPlanImpl;
import org.callistasoftware.netcare.core.api.impl.MeasureUnitImpl;
import org.callistasoftware.netcare.core.api.impl.PatientEventImpl;
import org.callistasoftware.netcare.core.api.impl.ScheduledActivityImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.*;
import org.callistasoftware.netcare.core.api.statistics.ActivityCount;
import org.callistasoftware.netcare.core.api.statistics.HealthPlanStatistics;
import org.callistasoftware.netcare.core.api.statistics.MeasuredValue;
import org.callistasoftware.netcare.core.api.statistics.ReportedActivity;
import org.callistasoftware.netcare.core.api.statistics.ReportedValue;
import org.callistasoftware.netcare.core.api.util.DateUtil;
import org.callistasoftware.netcare.core.repository.ActivityCommentRepository;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.repository.CareActorRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.HealthPlanRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.repository.UserRepository;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.callistasoftware.netcare.model.entity.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implementation of service definition
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
	public static int SCHEMA_FUTURE_DAYS = (2 * SCHEMA_HISTORY_DAYS);
	/**
	 * Always get full weeks when fetching patient plan (schema), and weeks
	 * starts on Mondays.
	 */
	public static int SCHEMA_DAY_ALIGN = Calendar.MONDAY;

	@Autowired
	private HealthPlanRepository repo;

	@Autowired
	private ActivityTypeRepository activityTypeRepository;

	@Autowired
	private CareActorRepository careActorRepository;

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

	@Override
	public ServiceResult<HealthPlan[]> loadHealthPlansForPatient(Long patientId) {
		final PatientEntity forPatient = patientRepository.findOne(patientId);
		final List<HealthPlanEntity> entities = this.repo.findByForPatient(forPatient);

		List<HealthPlan> plans = new LinkedList<HealthPlan>();
		for (final HealthPlanEntity ent : entities) {
			if (ent.isReadAllowed(this.getCurrentUser())) {
				final HealthPlanImpl dto = HealthPlanImpl.newFromEntity(ent, LocaleContextHolder.getLocale());
				plans.add(dto);
			}
		}

		return ServiceResultImpl.createSuccessResult(plans.toArray(new HealthPlan[plans.size()]),
				new ListEntitiesMessage(HealthPlanEntity.class, plans.size()));
	}

	@Override
	public ServiceResult<HealthPlan> createNewHealthPlan(final HealthPlan o, final CareActorBaseView careActor,
			final Long patientId) {
		getLog().info("Creating new ordination {}", o.getName());

		final Date start = ApiUtil.parseDate(o.getStartDate());
		final DurationUnit du = DurationUnit.valueOf(o.getDurationUnit().getCode());

		final CareActorEntity ca = this.careActorRepository.findByHsaId(careActor.getHsaId());

		final PatientEntity patient = this.patientRepository.findOne(patientId);

		final HealthPlanEntity newEntity = HealthPlanEntity.newEntity(ca, patient, o.getName(), start, o.getDuration(),
				du);
		newEntity.setAutoRenewal(o.isAutoRenewal());

		final HealthPlanEntity saved = this.repo.save(newEntity);
		final HealthPlan dto = HealthPlanImpl.newFromEntity(saved, null);

		return ServiceResultImpl.createSuccessResult(dto, new GenericSuccessMessage());
	}

    @Override
    public ServiceResult<HealthPlan> inactivateHealthPlan(Long healthPlanId, boolean sysUser) {
        getLog().info("Inactivating health plan {}", healthPlanId);
        return this.setActiveFlagOnHealthPlan(healthPlanId, false, sysUser);
    }

    @Override
    public ServiceResult<HealthPlan> activateHealthPlan(Long healthPlanId, boolean sysUser) {
        getLog().info("Activating health plan {}", healthPlanId);
        return this.setActiveFlagOnHealthPlan(healthPlanId, true, sysUser);
    }

    protected ServiceResult<HealthPlan> setActiveFlagOnHealthPlan(Long healthPlanId, boolean active, boolean sysUser) {
        final HealthPlanEntity hp = this.repo.findOne(healthPlanId);
        if (hp == null) {
            return ServiceResultImpl
                    .createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
        }

        if (!sysUser) {
            this.verifyWriteAccess(hp);
        }

        hp.setActive(active);
        if (active) {

            getLog().debug("Healthplan {} is being activated by {}", hp.getId(), sysUser ? "system job" : getCareActor().getHsaId());

            hp.setStartDate(new DateTime().toDate());
            getLog().debug("Start and end date have been recalculated. New date span is {} - {}", hp.getStartDate(), hp.getEndDate());

            // Reset alarm status
            hp.setReminderDone(false);

            int count = 0;
            final List<ScheduledActivityEntity> scheduledActivityRepositoryScheduledActivitiesForHealthPlan = scheduledActivityRepository.findScheduledActivitiesForHealthPlan(healthPlanId);
            for (final ScheduledActivityEntity sae : scheduledActivityRepositoryScheduledActivitiesForHealthPlan) {
                if (sae.getScheduledTime().after(hp.getStartDate()) && sae.getReportedTime() == null) {
                    sae.setStatus(ScheduledActivityStatus.CLOSED);
                    sae.setNote("Closed when health plan was re-activated.");
                    count++;
                }
            }

            getLog().debug("Closed {} scheduled activities due to re-activation.", count);

            for (final ActivityDefinitionEntity ad : hp.getActivityDefinitions()) {
                ad.setStartDate(hp.getStartDate());

                final List<ScheduledActivityEntity> schedule = ad.scheduleActivities();

                this.scheduledActivityRepository.save(schedule);
                getLog().debug("Scheduled {} activities for definition {}", schedule.size(), ad.getActivityType().getName());
            }
        }

        this.repo.save(hp);
        return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
    }

    @Override
	public ServiceResult<HealthPlan> loadHealthPlan(Long healthPlanId) {
		final HealthPlanEntity entity = this.repo.findOne(healthPlanId);
		if (entity == null) {
			return ServiceResultImpl
					.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}

		this.verifyReadAccess(entity);

		final HealthPlan dto = HealthPlanImpl.newFromEntity(entity, LocaleContextHolder.getLocale());
		return ServiceResultImpl.createSuccessResult(dto, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ActivityDefinition> addActvitiyToHealthPlan(final ActivityDefinition dto) {
		getLog().info("Adding activity defintion to existing ordination with id {}", dto.getHealthPlanId());

		final HealthPlanEntity entity = this.repo.findOne(dto.getHealthPlanId());
		if (entity == null) {
			return ServiceResultImpl
					.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, dto.getHealthPlanId()));
		}

		this.verifyWriteAccess(entity);

		getLog().debug("Health plan entity found and resolved. Id is {}", entity.getId());

		final ActivityTypeEntity typeEntity = this.activityTypeRepository.findOne(dto.getType().getId());
		if (typeEntity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityTypeEntity.class, dto
					.getType().getId()));
		}

		getLog().debug("Activity type entity found and resolved. Id is {}", typeEntity.getId());
		final ActivityDefinitionEntity newEntity = ActivityDefinitionEntity.newEntity(entity, typeEntity, createFrequency(dto), getCareActor());

		/*
		 * Process measurement definitions
		 */
		this.updateActivityItems(newEntity, dto);

		if (dto.getStartDate() != null) {
			newEntity.setStartDate(ApiUtil.parseDate(dto.getStartDate()));
		}

		ActivityDefinitionEntity savedEntity = activityDefintionRepository.save(newEntity);

		getLog().debug("Activity defintion saved.");

		scheduleActivities(savedEntity);

		this.repo.save(entity);
		getLog().debug("Health plan saved");

		getLog().debug("Creating result. Success!");
		final ActivityDefinition def = ActivityDefinitionImpl.newFromEntity(savedEntity);
		return ServiceResultImpl.createSuccessResult(def, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<HealthPlan> healthPlanRenewal(Long healthPlanId, boolean stop) {
		final HealthPlanEntity entity = this.repo.findOne(healthPlanId);
		if (entity == null) {
			return ServiceResultImpl
					.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}

        verifyWriteAccess(entity);

        if (!entity.isActive()) {
            throw new IllegalStateException("User: " + getCurrentUser().getId() + " tried to renew inactive health plan with id: " + entity.getId());
        }

        entity.setAutoRenewal(!stop);

		return ServiceResultImpl.createSuccessResult((HealthPlan) HealthPlanImpl.newFromEntity(entity, LocaleContextHolder.getLocale()), new GenericSuccessMessage());
	}

	@Override
	public void scheduleActivities(ActivityDefinitionEntity activityDefinition) {
		List<ScheduledActivityEntity> scheduleActivities = activityDefinition.scheduleActivities();
		getLog().debug("Scheduled {} activities.", scheduleActivities.size());
		scheduledActivityRepository.save(scheduleActivities);
	}

	@Override
	public ServiceResult<ActivityDefinition[]> loadActivitiesForHealthPlan(Long healthPlanId) {
		getLog().info("Loading health plan activities for health plan {}", healthPlanId);
		final HealthPlanEntity entity = this.repo.findOne(healthPlanId);
		if (entity == null) {
			return ServiceResultImpl
					.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}

		this.verifyReadAccess(entity);

		getLog().debug("Found {} health plan activities for health plan {}", entity.getActivityDefinitions().size(),
				healthPlanId);
		return ServiceResultImpl.createSuccessResult(ActivityDefinitionImpl.newFromEntities(entity
				.getActivityDefinitions()), new ListEntitiesMessage(ActivityDefinitionEntity.class, entity
				.getActivityDefinitions().size()));
	}

	@Override
	public ServiceResult<ScheduledActivity[]> loadLatestReportedForAllPatients(final CareUnit careUnit,
			final Date start, final Date end) {
		getLog().info("Loading latest reported activities for all patients belonging to care unit {}", careUnit.getHsaId());

		final CareUnitEntity entity = this.careUnitRepository.findByHsaId(careUnit.getHsaId());
		if (entity == null) {
			ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(CareUnitEntity.class, -1L));
		}

		this.verifyReadAccess(entity);

		getLog().info("latest reports: start: {}, end: {}, unit: \"{}\"", new Object[] { start, end, entity.getHsaId() });

		final List<ScheduledActivityEntity> activities;
		if (start == null || end == null) {
			activities = this.scheduledActivityRepository.findByCareUnit(entity.getHsaId());
		} else {
			activities = this.scheduledActivityRepository.findByCareUnitBetween(entity.getHsaId(), start, end);
		}

		getLog().info("latest reports: found {} activities", activities.size());

		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntities(activities),
				new ListEntitiesMessage(ScheduledActivityEntity.class, activities.size()));
	}

	@Override
	public ServiceResult<ScheduledActivity[]> filterReportedActivities(final CareUnit careUnit, final String personnummer,
			final Date start, final Date end) {
		getLog().info("Loading a filtered out list of reported activities belonging to care unit {}", careUnit.getHsaId());

		final CareUnitEntity entity = this.careUnitRepository.findByHsaId(careUnit.getHsaId());
		if (entity == null) {
			ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(CareUnitEntity.class, -1L));
		}
		final PatientEntity patient = patientRepository.findByCivicRegistrationNumber(personnummer);
		this.verifyReadAccess(entity);

		getLog().info("filtered reports: pnr: {}, start: {}, end: {}, unit: \"{}\"", new Object[] { personnummer, start, end, entity.getHsaId() });

		final List<ScheduledActivityEntity> activities;
		if (StringUtils.hasText(personnummer)) {
			activities = this.scheduledActivityRepository.findByCareUnitPatientBetween(entity.getHsaId(), patient, start, end);
		} else {
			activities = this.scheduledActivityRepository.findByCareUnitBetween(entity.getHsaId(), start, end);
		}

		getLog().info("filter reports: found {} activities", activities.size());

		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntities(activities),
				new ListEntitiesMessage(ScheduledActivityEntity.class, activities.size()));
	}

	@Override
	public ServiceResult<ActivityDefinition[]> getPlannedActivitiesForPatient(final Long patientId) {
		
		final UserEntity currentUser = getCurrentUser();
		if (patientId == null && currentUser.isCareActor()) {
			throw new IllegalStateException("Don't know which patient to load...");
		}
		
		final PatientEntity patient;
		if (currentUser.isCareActor()) {
			patient = patientRepository.findOne(patientId);
		} else {
			patient = patientRepository.findOne(getPatient().getId());
		}
		
		final Date now = new Date();
		final List<ActivityDefinitionEntity> defs = activityDefintionRepository.findByPatientAndNow(patient, now);
		
		if (currentUser.isCareActor()) {
			getLog().debug("Filter definitions and include only definitions that the care actor are allowed to see.");
			final Long careUnit = getCareActor().getCareUnit().getId();
			final List<ActivityDefinitionEntity> filter = new ArrayList<ActivityDefinitionEntity>();
			
			for (final ActivityDefinitionEntity ent : defs) {
				if (ent.getHealthPlan().getCareUnit().getId().equals(careUnit)) {
					filter.add(ent);
				}
			}
			
			return ServiceResultImpl.createSuccessResult(ActivityDefinitionImpl.newFromEntities(filter), new GenericSuccessMessage());
		}
		
		if (defs.size() > 0) {
			this.verifyReadAccess(defs.get(0));
		}
		
		return ServiceResultImpl.createSuccessResult(ActivityDefinitionImpl.newFromEntities(defs), new GenericSuccessMessage());
	}

	public ServiceResult<ScheduledActivity[]> getScheduledActivitiesForHealthPlan(Long healthPlanId) {
		getLog().info("Get scheduled activities for health plan {}", healthPlanId);
		final HealthPlanEntity ad = this.repo.findOne(healthPlanId);
		if (ad == null) {
			return ServiceResultImpl
					.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}

		this.verifyReadAccess(ad);

		final List<ScheduledActivityEntity> entities = this.scheduledActivityRepository
				.findScheduledActivitiesForHealthPlan(healthPlanId);
		getLog().debug("Found {} scheduled activities", entities.size());

		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntities(entities),
				new ListEntitiesMessage(ScheduledActivityEntity.class, entities.size()));
	}

	@Override
	public ServiceResult<HealthPlanStatistics> getStatisticsForHealthPlan(Long healthPlanId) {
		getLog().info("Getting statistics for health plans...");

		final HealthPlanStatistics stats = new HealthPlanStatistics();

		final HealthPlanEntity healthPlan = this.repo.findOne(healthPlanId);
		if (healthPlan == null) {
			return ServiceResultImpl
					.createFailedResult(new EntityNotFoundMessage(HealthPlanEntity.class, healthPlanId));
		}

		this.verifyReadAccess(healthPlan);

		getLog().debug("Calculating health plan overview...");
		final ScheduledActivity[] activities = this.getScheduledActivitiesForHealthPlan(healthPlanId).getData();
		final List<ActivityCount> activityCount = new ArrayList<ActivityCount>();

		for (final ScheduledActivity ac : activities) {
			final String name = ac.getActivityDefinition().getType().getName();
			final ActivityCount act = new ActivityCount(name);
			final ActivityCount existing = this.findActivityCount(name, activityCount);

			if (existing == null) {
				getLog().debug("Activity count not in list. Adding {}", act.getName());
				activityCount.add(act);
			}

			this.findActivityCount(name, activityCount).increaseCount();
		}
		stats.setActivities(activityCount);
		getLog().debug("Health plan overview calculated.");

		/*
		 * Get all reported activities
		 */
		getLog().debug("Calculating reported activities...");
		final List<ScheduledActivityEntity> ents = this.scheduledActivityRepository
				.findReportedActivitiesForHealthPlan(healthPlanId, healthPlan.getStartDate(), new Date(), new Sort(
						Sort.Direction.ASC, "scheduledTime"));

		final List<MeasuredValue> measuredValues = new ArrayList<MeasuredValue>();
		for (final ScheduledActivityEntity schedActivityEntity : ents) {
			final ReportedActivity ra = new ReportedActivity();
			ra.setName(schedActivityEntity.getActivityDefinitionEntity().getActivityType().getName());
			ra.setNote(schedActivityEntity.getNote());
			ra.setReportedAt(DateUtil.toDateTime(schedActivityEntity.getReportedTime()));
			ra.setLabel(DateUtil.toDateTime(schedActivityEntity.getScheduledTime()));

			final List<ActivityItemValuesEntity> activityItemValues = schedActivityEntity.getActivities();
			for (final ActivityItemValuesEntity valueEntity : activityItemValues) {
				if (valueEntity instanceof MeasurementEntity) {
					MeasurementEntity m = (MeasurementEntity) valueEntity;
					MeasurementTypeEntity type = (MeasurementTypeEntity) m.getActivityItemDefinitionEntity()
							.getActivityItemType();
					final String measurementName = type.getName();
					MeasuredValue mv = this.findMeasuredValue(measurementName, measuredValues);
					if (mv == null) {
						mv = new MeasuredValue();
						mv.setName(schedActivityEntity.getActivityDefinitionEntity().getActivityType().getName());
						mv.setDefinitionId(schedActivityEntity.getActivityDefinitionEntity().getId());
						mv.setValueType(new Option(measurementName, null));
						mv.setUnit(MeasureUnitImpl.newFromEntity(type.getUnit()));
						mv.setInterval(type.equals(MeasurementValueType.INTERVAL));
						measuredValues.add(mv);
					}

					final ReportedValue rv = new ReportedValue();
					switch (type.getValueType()) {
					case INTERVAL:
						rv.setMaxTargetValue((float) m.getMaxTarget());
						rv.setMinTargetValue((float) m.getMinTarget());
						break;
					case SINGLE_VALUE:
						rv.setTargetValue((float) m.getTarget());
						break;
					}

					rv.setReportedValue((float) m.getReportedValue());
					rv.setReportedAt(DateUtil.toDateTime(schedActivityEntity.getScheduledTime()));

					mv.getReportedValues().add(rv);
				}
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
    public ServiceResult<ActivityDefinition> inactivateActivity(Long activityDefinitionId) {

        getLog().info("Inactivating activity definition {}", activityDefinitionId);
        final ActivityDefinitionEntity ent = this.activityDefintionRepository.findOne(activityDefinitionId);
        if (ent == null) {
            getLog().warn("The activity definition {} could not be found.", activityDefinitionId);
            return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityDefinitionEntity.class,
                    activityDefinitionId));
        }

        this.verifyWriteAccess(ent);

        ent.setRemovedFlag(true);

        this.removeScheduledActivities(ent);

        getLog().debug("Activity definition with id {} marked as inactivated", activityDefinitionId);
        this.activityDefintionRepository.save(ent);

        return ServiceResultImpl.createSuccessResult(ActivityDefinitionImpl.newFromEntity(ent),
                new ActivityActivatedMessage(ActivityDefinitionEntity.class, activityDefinitionId));
    }

    protected void removeScheduledActivities(ActivityDefinitionEntity activityDefinition) {
        Iterator<ScheduledActivityEntity> iter = activityDefinition.getScheduledActivities().iterator();
        List<ScheduledActivityEntity> tbr = new ArrayList<ScheduledActivityEntity>();
        for(ScheduledActivityEntity scheduledActivity : activityDefinition.getScheduledActivities()) {
            if (scheduledActivity.getStatus().equals(ScheduledActivityStatus.OPEN) && scheduledActivity.getReportedTime() == null) {
                tbr.add(scheduledActivity);
            }
        }
        activityDefinition.removeScheduledActivities(tbr);
    }


    @Override
    public ServiceResult<ActivityDefinition> activateActivity(Long activityDefinitionId) {

        getLog().info("Re-activating activity definition {}", activityDefinitionId);
        final ActivityDefinitionEntity ent = this.activityDefintionRepository.findOne(activityDefinitionId);
        if (ent == null) {
            getLog().warn("The activity definition {} could not be found.", activityDefinitionId);
            return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityDefinitionEntity.class,
                    activityDefinitionId));
        }

        this.verifyWriteAccess(ent);

        ent.setRemovedFlag(false);
        ent.setStartDate(new Date());
        List<ScheduledActivityEntity> scheduledActivityEntities = ent.scheduleActivities();
        scheduledActivityRepository.save(scheduledActivityEntities);

        /*
         * TODO: Ska vi re-schedule aktiviteter här från nu till hälsoplanens slut? Förutsatt att
         * vi gör en hard delete när vi inaktiverar schemaläggningen.
         */

        getLog().debug("Activity definition with id {} marked as activated", activityDefinitionId);
        this.activityDefintionRepository.save(ent);

        return ServiceResultImpl.createSuccessResult(ActivityDefinitionImpl.newFromEntity(ent),
                new EntityDeletedMessage(ActivityDefinitionEntity.class, activityDefinitionId));
    }

	@Override
	public ServiceResult<ScheduledActivity> commentOnPerformedActivity(Long activityId, String comment) {
		return createOrUpdateCommentOnPerformedActivity(activityId, comment, null, null);
	}

	@Override
	public ServiceResult<ScheduledActivity> likePerformedActivity(Long activityId, boolean like) {
		return createOrUpdateCommentOnPerformedActivity(activityId, null, like, null);
	}

	@Override
	public ServiceResult<ScheduledActivity> markPerformedActivityAsRead(Long activityId, boolean hasBeenRead) {
		return createOrUpdateCommentOnPerformedActivity(activityId, null, null, hasBeenRead);
	}

	protected ServiceResult<ScheduledActivity> createOrUpdateCommentOnPerformedActivity(Long activityId, String comment, Boolean like, Boolean hasBeenRead) {
		final ScheduledActivityEntity ent = this.scheduledActivityRepository.findOne(activityId);
		if (ent == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ScheduledActivityEntity.class,
					activityId));
		}

		this.verifyWriteAccess(ent);

		final UserEntity user = this.getCurrentUser();
		if (user.isCareActor()) {
			final CareActorEntity ca = (CareActorEntity) user;
			
			ActivityCommentEntity commentEntity = null; 
			if(ent.getComments().size()>0) {
				commentEntity = ent.getComments().get(0);
			} else {
				commentEntity = ActivityCommentEntity.newEntity("", ca, ent);
				ent.getComments().add(commentEntity);
			}
			
			if(StringUtils.hasText(comment)) {
				commentEntity.setComment(comment);
			} else if(like!=null) {
				commentEntity.setLike(like);
			} else if(hasBeenRead!=null) {
				commentEntity.setMarkedAsRead(hasBeenRead);
			}

			return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntity(ent),
					new GenericSuccessMessage());
		} else {
			throw new SecurityException("A patient is not allow to comment his own activity");
		}
	}

	
	@Override
	public ServiceResult<ActivityComment[]> loadCommentsForPatient() {
		final PatientEntity patient = this.getPatient();
		
		final List<ActivityCommentEntity> entities = this.commentRepository.findCommentsForPatient(patient);
		return ServiceResultImpl.createSuccessResult(ActivityCommentImpl.newFromEntities(entities),
				new ListEntitiesMessage(ActivityCommentEntity.class, entities.size()));
	}

	@Override
	public ServiceResult<ActivityComment> replyToComment(Long comment, final String reply) {
		final ActivityCommentEntity ent = this.commentRepository.findOne(comment);
		if (ent == null) {
			return ServiceResultImpl
					.createFailedResult(new EntityNotFoundMessage(ActivityCommentEntity.class, comment));
		}

		this.verifyWriteAccess(getPatient());

		ent.setReply(reply);
		ent.setRepliedAt(new Date());

		return ServiceResultImpl.createSuccessResult(ActivityCommentImpl.newFromEntity(ent),
				new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ActivityComment[]> loadRepliesForCareActor() {
		final CareActorEntity ca = this.getCareActor();
		getLog().info("Loading replies for care giver {}", ca.getFirstName());

		final List<ActivityCommentEntity> comments = this.commentRepository.findRepliesForCareActor(ca,
				ca.getCareUnit());
		return ServiceResultImpl.createSuccessResult(ActivityCommentImpl.newFromEntities(comments),
				new ListEntitiesMessage(ActivityCommentEntity.class, comments.size()));
	}

	@Override
	public ServiceResult<ActivityComment> deleteComment(Long commentId) {
		final UserEntity user = this.getCurrentUser();
		getLog().info("Care giver {} is deleting comment {}", user.getId(), commentId);

		final ActivityCommentEntity ent = this.commentRepository.findOne(commentId);
		if (ent == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityCommentEntity.class,
					commentId));
		}

		this.verifyWriteAccess(ent);
		this.commentRepository.delete(ent);

		return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ActivityComment> hideComment(Long commentId, boolean isAdmin) {
		final UserEntity user = this.getCurrentUser();
		getLog().info("User {} is hiding comment {}", user.getId(), commentId);

		final ActivityCommentEntity ent = this.commentRepository.findOne(commentId);
		if (ent == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityCommentEntity.class,
					commentId));
		}

		this.verifyWriteAccess(ent);
		if(isAdmin) {
			ent.setHiddenByAdmin(true);
		} else {
			ent.setHiddenByPatient(true);
		}
		this.commentRepository.save(ent);

		return ServiceResultImpl.createSuccessResult(null, new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ScheduledActivity> loadScheduledActivity(Long activity) {
		final ScheduledActivityEntity sae = this.scheduledActivityRepository.findOne(activity);
		if (sae == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ScheduledActivityEntity.class,
					activity));
		}

		this.verifyReadAccess(sae);

		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntity(sae),
				new GenericSuccessMessage());
	}

	private static String quotedString(String s) {
		return String.format("\"%s\"", s);
	}

	@Override
	public ServiceResult<ActivityDefinition> updateActivity(ActivityDefinition dto) {
		this.getLog().info("Updating activity definition {}", dto.getId());

		final ActivityDefinitionEntity entity = this.activityDefintionRepository.findOne(dto.getId());
		if (entity == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityDefinitionEntity.class, dto
					.getId()));
		}

		this.verifyWriteAccess(entity);

		/*
		 * Update measure values
		 */
		this.updateActivityItems(entity, dto);
		
		/*
		 * Update frequency
		 */
		entity.setFrequency(createFrequency(dto));
		
		/*
		 * Update reminder
		 */
		entity.setReminder(dto.isReminder());
		
		/*
		 * Remove all future scheduled activties and add
		 * new ones
		 */
		getLog().debug("Rescheduling activities... Before: {}", entity.getScheduledActivities().size());
		entity.reschedule();
		getLog().debug("After rescheduling {}", entity.getScheduledActivities().size());

		
		final ActivityDefinitionEntity one = activityDefintionRepository.findOne(entity.getId());
		getLog().debug("After loaded: {} activities.", one.getScheduledActivities().size());
		
		return ServiceResultImpl.createSuccessResult(ActivityDefinitionImpl.newFromEntity(entity),
				new GenericSuccessMessage());
	}

	private void updateActivityItems(final ActivityDefinitionEntity entity, final ActivityDefinition dto) {
		/*
		 * Process measurement defintions
		 */
		getLog().debug("Updating activity items...");
		entLoop: for (final ActivityItemDefinitionEntity aid : entity.getActivityItemDefinitions()) {
			
			for (final ActivityItemValuesDefinition aivDefinition : dto.getGoalValues()) {
				if (aid.getActivityItemType().getId().equals(aivDefinition.getActivityItemType().getId())) {
					
					getLog().debug((aivDefinition.isActive() ? "Including " : "Excluding") + " measure value {}", aid.getActivityItemType().getName());
					aid.setActive(aivDefinition.isActive());
					
					// Check types
					if (aid instanceof MeasurementDefinitionEntity) {
						final MeasurementDefinitionEntity mde = (MeasurementDefinitionEntity) aid;
						
						getLog().debug("Processing measure value {} for activity type {}", mde.getMeasurementType()
								.getName(), mde.getMeasurementType().getActivityType().getName());

						MeasurementDefinition md = (MeasurementDefinition) aivDefinition;
						switch (mde.getMeasurementType().getValueType()) {
						case INTERVAL:
							getLog().debug("Setting values for measure defintion: {}-{}", md.getMinTarget(),
									md.getMaxTarget());
							mde.setMaxTarget(md.getMaxTarget());
							mde.setMinTarget(md.getMinTarget());
							break;
						case SINGLE_VALUE:
							getLog().debug("Setting values for measure defintion: {}", md.getTarget());
							mde.setTarget(md.getTarget());
							break;
						}
					}
					
					continue entLoop;
				}
			}
		}
	}
	
	private Frequency createFrequency(final ActivityDefinition dto) {
		/*
		 * Create the day frequency based on what the user selected.
		 */
		getLog().debug("Processing the day and time frequence...");

		final Frequency frequency = new Frequency();
		frequency.setWeekFrequency(dto.getActivityRepeat());
		
		getLog().debug("Week frequency is: {}", dto.getActivityRepeat());
		
		for (final DayTime dt : dto.getDayTimes()) {
			FrequencyDay fd = FrequencyDay.newFrequencyDay(ApiUtil.toIntDay(dt.getDay()));
			for (String time : dt.getTimes()) {
				fd.addTime(FrequencyTime.unmarshal(time));
			}
			
			getLog().debug("ADDING TIME {}", fd);
			frequency.addDay(fd);
		}
		
		getLog().debug("Frequency: {}", Frequency.marshal(frequency));
		return frequency;
	}

	@Override
	public ServiceResult<ActivityDefinition> loadDefinition(Long definitionId) {
		final ActivityDefinitionEntity one = activityDefintionRepository.findOne(definitionId);
		if (one == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityDefinitionEntity.class, definitionId));
		}
		
		one.isReadAllowed(getCurrentUser());
		
		return ServiceResultImpl.createSuccessResult(ActivityDefinitionImpl.newFromEntity(one), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<ActivityDefinition> updateReminder(final Long id, final boolean reminderOn) {
		final ActivityDefinitionEntity def = this.activityDefintionRepository.findOne(id);
		if (def == null) {
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(ActivityDefinitionEntity.class, id));
		}
		
		verifyWriteAccess(getPatient());
		
		getLog().debug("Setting reminder for {} to {}", def.getActivityType().getName(), reminderOn);
		def.setReminder(reminderOn);
		
		return ServiceResultImpl.createSuccessResult(ActivityDefinitionImpl.newFromEntity(def), new GenericSuccessMessage());
	}
}
