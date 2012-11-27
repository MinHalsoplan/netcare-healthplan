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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.callistasoftware.netcare.core.api.ActivityItemValues;
import org.callistasoftware.netcare.core.api.ApiUtil;
import org.callistasoftware.netcare.core.api.Estimation;
import org.callistasoftware.netcare.core.api.Measurement;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.Text;
import org.callistasoftware.netcare.core.api.YesNo;
import org.callistasoftware.netcare.core.api.impl.ScheduledActivityImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityItemValuesEntityRepository;
import org.callistasoftware.netcare.core.repository.AlarmRepository;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.spi.ScheduleService;
import org.callistasoftware.netcare.model.entity.ActivityItemValuesEntity;
import org.callistasoftware.netcare.model.entity.AlarmCause;
import org.callistasoftware.netcare.model.entity.AlarmEntity;
import org.callistasoftware.netcare.model.entity.EstimationEntity;
import org.callistasoftware.netcare.model.entity.MeasurementDefinitionEntity;
import org.callistasoftware.netcare.model.entity.MeasurementEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityStatus;
import org.callistasoftware.netcare.model.entity.TextEntity;
import org.callistasoftware.netcare.model.entity.YesNoEntity;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScheduleServiceImpl extends ServiceSupport implements ScheduleService {

	@Autowired
	private EntityManagerFactory eMan;
	
	@Autowired private ActivityDefinitionRepository defRepo;
	@Autowired private ScheduledActivityRepository repo;
	
	@Autowired
	private ActivityItemValuesEntityRepository valueRepository;
	
	@Autowired
	private AlarmRepository alarmRepo;
	
	@Override
	public ServiceResult<ScheduledActivity[]> load(final boolean includeReported
			, final boolean includeDue
			, Long start
			, Long end) {
		
		if (start == null || end == null) {
			start = new DateTime().withMillisOfDay(0).toDate().getTime();
			end = new DateTime().withMillisOfDay(0).plusDays(1).toDate().getTime();
		}
		
		final DateTime s = new DateTime(start);
		final DateTime e = new DateTime(end);
		
		if (s.getDayOfYear() == e.getDayOfYear()) {
			start = s.withMillisOfDay(0).toDate().getTime();
			end = s.withMillisOfDay(0).plusDays(1).toDate().getTime();
		}
		
		final List<ScheduledActivityEntity> list = this.repo.findByPatientAndScheduledTimeBetween(getPatient(), new Date(start), new Date(end));
		final List<ScheduledActivityEntity> filtered = new ArrayList<ScheduledActivityEntity>();
		getLog().debug("Query found {} in interval {} - {}", new Object[] { list.size(), new Date(start), new Date(end) });
		
		for (final ScheduledActivityEntity sae : list) {
			
			// Ensure read access
			sae.isReadAllowed(getPatient());
			
			boolean dueCheck = sae.getReportedTime() == null && sae.getStatus().equals(ScheduledActivityStatus.OPEN) && sae.getScheduledTime().before(new Date(System.currentTimeMillis()));
			boolean reportCheck = sae.getReportedTime() != null && (sae.getStatus().equals(ScheduledActivityStatus.OPEN) || sae.getStatus().equals(ScheduledActivityStatus.REJECTED));
			
			getLog().debug("due check: {}, report check: {}", dueCheck, reportCheck);
			
			if ((includeDue && dueCheck) || (includeReported && reportCheck)) {
				filtered.add(sae);
			} else {
				
				boolean openCheck = sae.getReportedTime() == null && sae.getStatus().equals(ScheduledActivityStatus.OPEN); 
				if (openCheck) {
					filtered.add(sae);
				}
			}
		}
		
		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntities(filtered), new GenericSuccessMessage());
		
	}

	@Override
	public ServiceResult<ScheduledActivity> loadLatestForDefinition(
			Long definitionId) {
		final List<ScheduledActivityEntity> all = this.repo.findLatestScheduledActivityForActivity(definitionId);
		if (all.isEmpty()) {
			throw new IllegalStateException("Could not find any scheduled activities for definition " + definitionId + ". This method should never be invoked at this stage.");
		}
		
		final ScheduledActivityEntity act = all.get(0);
		act.isReadAllowed(getPatient());
		
		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntity(act), new GenericSuccessMessage());
	}
	
	@Override
	public ServiceResult<ScheduledActivity> reportReady(final ScheduledActivity report) {
		getLog().info("Report done for scheduled activity {}", report.getId());
		
		ScheduledActivityEntity entity = repo.findOne(report.getId());
		if (report.isExtra()) {
			
			getLog().debug("Creating extra report for activity");
			
			entity = ScheduledActivityEntity.newEntity(entity.getActivityDefinitionEntity(), new Date());
			entity.setExtra(true);
		}
		
		entity.setReportedTime(new Date());
		entity.setStatus(report.isRejected() ? ScheduledActivityStatus.REJECTED : ScheduledActivityStatus.OPEN);
		entity.setNote(report.getNote());
		for (final ActivityItemValues value : report.getActivityItemValues()) {
			
			final ActivityItemValuesEntity valueEntity = this.valueRepository.findOne(value.getId());
			
			if (valueEntity instanceof MeasurementEntity) {
				
				final Measurement m = (Measurement) value;
				final MeasurementEntity me = (MeasurementEntity) valueEntity;
				
				// Set reported value
				me.setReportedValue(m.getReportedValue());
				
				// Update goal values at this point in time
				MeasurementDefinitionEntity definition = (MeasurementDefinitionEntity) me.getActivityItemDefinitionEntity();
				MeasurementValueType valueType = definition.getMeasurementType().getValueType();

				switch (valueType) {
				case INTERVAL:
					me.setMaxTarget(definition.getMaxTarget());
					me.setMinTarget(definition.getMinTarget());
					break;
				case SINGLE_VALUE:
					me.setTarget(definition.getTarget());
					break;
				}

				getLog().debug("Alarm status: enabled {} raised {}", definition.getMeasurementType().isAlarmEnabled(),
						me.isAlarm());
				
				if (!report.isRejected() && me.isAlarm()) {
					AlarmEntity ae = AlarmEntity.newEntity(AlarmCause.LIMIT_BREACH, entity
							.getActivityDefinitionEntity().getHealthPlan().getForPatient(), entity
							.getActivityDefinitionEntity().getHealthPlan().getCareUnit().getHsaId(), me.getId());
					
					ae.setInfo(definition.getMeasurementType().getName() + ": " + me.getReportedValue() + " "
							+ definition.getMeasurementType().getUnit().getName());
					
					alarmRepo.save(ae);
				}
			} else if (valueEntity instanceof EstimationEntity) {
				
				final Estimation e = (Estimation) value;
				final EstimationEntity ee = (EstimationEntity) valueEntity;
				
				ee.setPerceivedSense(e.getPerceivedSense());
				
			} else if (valueEntity instanceof YesNoEntity) {
				
				final YesNo yn = (YesNo) value;
				final YesNoEntity yne = (YesNoEntity) valueEntity;
				
				yne.setAnswer(yn.getAnswer());
				
			} else if (valueEntity instanceof TextEntity) {
				
				final Text t = (Text) value;
				final TextEntity te = (TextEntity) valueEntity;
				
				te.setTextComment(t.getTextComment());
				
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		Date d = ApiUtil.parseDateTime(report.getActualTime());
		entity.setActualTime(d);
		entity = repo.save(entity);

		getLog().debug("Reported time for activity is: {}", entity.getReportedTime());

		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntity(entity),
				new GenericSuccessMessage());
	}
}
