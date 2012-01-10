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


import java.util.List;

import org.callistasoftware.netcare.core.api.Alarm;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.AlarmImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.api.messages.ListEntitiesMessage;
import org.callistasoftware.netcare.core.repository.AlarmRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.spi.AlarmService;
import org.callistasoftware.netcare.model.entity.AlarmEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlarmServiceImpl extends ServiceSupport implements AlarmService {
	
	@Autowired
	private CareUnitRepository cuRepo;
	
	@Autowired
	private AlarmRepository alarmRepo;
	
	@Transactional
	@Override
	public ServiceResult<Alarm[]> getCareUnitAlarms(String hsaId) {
		
		this.getLog().info("Get alarms for care unit {}", hsaId);
		
		final CareUnitEntity cu = this.cuRepo.findByHsaId(hsaId);
		if (cu == null) {
			this.getLog().warn("Care unit {} was not found in the system.", hsaId);
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(CareUnitEntity.class, hsaId));
		}
		
		this.verifyReadAccess(cu);
		
		final List<AlarmEntity> alarms = this.alarmRepo.findByResolvedTimeIsNullAndCareUnitHsaIdLike(hsaId, new Sort(Sort.Direction.DESC, "createdTime"));
		this.getLog().debug("Found {} alarms for care unit {}", alarms.size(), hsaId);
		
		return ServiceResultImpl.createSuccessResult(AlarmImpl.newFromEntities(alarms, LocaleContextHolder.getLocale()), new ListEntitiesMessage(AlarmEntity.class, alarms.size()));
	}

}
