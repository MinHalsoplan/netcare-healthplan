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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ScheduledActivityImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.spi.ScheduleService;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScheduleServiceImpl extends ServiceSupport implements ScheduleService {

	@Autowired
	private EntityManagerFactory eMan;
	
	@Autowired private ScheduledActivityRepository repo;
	
	@Override
	public ServiceResult<ScheduledActivity[]> loadToday(final boolean includeReported
			, final boolean includeDue
			, Long start
			, Long end) {
		
		if (start == null || end == null) {
			start = new DateTime().withMillisOfDay(0).toDate().getTime();
			end = new DateTime().withMillisOfDay(0).plusDays(1).toDate().getTime();
		}
		
		final StringBuilder query = new StringBuilder();
		query.append("select e from ScheduledActivityEntity as e inner join e.activityDefinition as ad "); 
		query.append("inner join ad.healthPlan as hp where hp.forPatient = :patient and ad.removedFlag = 'false' ");
		
		if (includeDue && includeReported) {
			query.append("and ((e.scheduledTime between :start and :end ");
			query.append("or e.scheduledTime < :end and e.reportedTime is null) ");
			query.append("or (e.reportedTime is not null and e.scheduledTime < :end))");
		}
		
		if (includeDue && !includeReported) {
			query.append("and (e.scheduledTime between :start and :end ");
			query.append("or e.scheduledTime < :end and e.reportedTime is null) ");
		}
		
		if (!includeDue && includeReported) {
			query.append("and (e.reportedTime is not null and e.scheduledTime < :end))");
		}
			
		if (!includeDue && !includeReported) {
			query.append("and ((e.reportedTime is not null) and (e.scheduledTime < :end and e.reportedTime is not null))");
		}
		
		query.append("order by e.scheduledTime asc");
		
		final Query q = eMan.createEntityManager().createQuery(query.toString());
		q.setParameter("patient", getPatient());
		q.setParameter("start", new Date(start));
		q.setParameter("end", new Date(end));
		
		@SuppressWarnings("unchecked")
		List<ScheduledActivityEntity> resultList = q.getResultList();
		return ServiceResultImpl.createSuccessResult(ScheduledActivityImpl.newFromEntities(resultList), new GenericSuccessMessage());
	}
}
