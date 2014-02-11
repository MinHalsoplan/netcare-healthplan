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
package org.callistasoftware.netcare.core.spi.impl;

import org.callistasoftware.netcare.core.api.PdlLog;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.PdlLogImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.repository.PdlLogRepository;
import org.callistasoftware.netcare.core.spi.PdlLogService;
import org.callistasoftware.netcare.model.entity.PdlLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PdlLogServiceImpl extends ServiceSupport implements PdlLogService {

	@Autowired
	private PdlLogRepository repo;

	@Override
	public ServiceResult<PdlLog> createPdlLog(final PdlLog pdlLog) {

		PdlLogEntity newPdlLog = PdlLogEntity.newEntity(pdlLog.getAction(), pdlLog.getCareActorName(),
				pdlLog.getCivicId(), pdlLog.getHsaId(), pdlLog.getPatientName(), pdlLog.getHealtPlanName());

		final PdlLogEntity saved = this.repo.save(newPdlLog);
		return ServiceResultImpl.createSuccessResult(PdlLogImpl.newFromEntity(saved), new GenericSuccessMessage());

	}

}
