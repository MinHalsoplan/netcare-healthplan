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

import org.callistasoftware.netcare.core.api.CareUnit;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.CareUnitImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.CountyCouncilRepository;
import org.callistasoftware.netcare.core.spi.CareUnitService;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncil;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CareUnitServiceImpl extends ServiceSupport implements CareUnitService {

	@Autowired private CountyCouncilRepository ccRepo;
	@Autowired private CareUnitRepository repo;
	
	@Override
	public ServiceResult<CareUnit[]> listCareUnits() {
		final List<CareUnitEntity> all = repo.findAll();
		return ServiceResultImpl.createSuccessResult(CareUnitImpl.newFromEntities(all), new GenericSuccessMessage());
	}

	@Override
	public ServiceResult<CareUnit> saveCareUnit(CareUnit careUnit) {
		
		boolean isNew = false;
		
		final int ccCode = Integer.valueOf(careUnit.getCountyCouncil().getCode());
		CountyCouncilEntity cce = ccRepo.findByMeta(ccCode);
		
		if (cce == null) {	
			cce = CountyCouncilEntity.newEntity(CountyCouncil.fromCode(ccCode));
			cce = ccRepo.save(cce);
		}
		
		CareUnitEntity cu = repo.findOne(careUnit.getId());
		if (cu == null) {
			isNew = true;
			cu = CareUnitEntity.newEntity(careUnit.getHsaId(), cce);
		}
		
		cu.setHsaId(careUnit.getHsaId());
		cu.setName(careUnit.getName());
		cu.setCountyCouncil(cce);
		
		if (isNew) {
			cu = repo.save(cu);
		}
		
		return ServiceResultImpl.createSuccessResult(
				CareUnitImpl.newFromEntity(cu), new GenericSuccessMessage());
	}

}
