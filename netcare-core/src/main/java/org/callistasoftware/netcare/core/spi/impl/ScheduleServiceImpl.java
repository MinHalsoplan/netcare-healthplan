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

import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ScheduledActivityImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.repository.ScheduledActivityRepository;
import org.callistasoftware.netcare.core.spi.ScheduleService;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityStatus;
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
}
