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

import javax.annotation.PostConstruct;

import org.callistasoftware.netcare.core.api.Alarm;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.spi.AlarmService;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlarmServiceImpl extends ServiceSupport implements AlarmService {

	private static Logger log = LoggerFactory.getLogger(AlarmServiceImpl.class);
	
	@Autowired
	private CareUnitRepository cuRepo;
	
	@PostConstruct
	public void initialize() {
		this.run();
	}
	
	@Async
	@Scheduled(fixedRate=3600000)
	@Override
	public void run() {
		log.info("======== ALARM JOB STARTED =========");
		
		
		
		log.info("======== ALARM JOB COMPLETED =========");
	}

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
