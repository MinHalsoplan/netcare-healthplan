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

import org.callistasoftware.netcare.core.api.MeasureUnit;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.MeasureUnitImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.EntityNotFoundMessage;
import org.callistasoftware.netcare.core.api.messages.EntityNotUniqueMessage;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.messages.ListEntitiesMessage;
import org.callistasoftware.netcare.core.repository.MeasureUnitRepository;
import org.callistasoftware.netcare.core.spi.UnitService;
import org.callistasoftware.netcare.model.entity.MeasureUnitEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnitServiceImpl extends ServiceSupport implements UnitService {

	@Autowired private MeasureUnitRepository repo;
	
	@Override
	public ServiceResult<MeasureUnit[]> loadUnits() {
		final List<MeasureUnitEntity> set = repo.findAll();
		return ServiceResultImpl.createSuccessResult(MeasureUnitImpl.newFromEntities(set)
				, new ListEntitiesMessage(MeasureUnitEntity.class, set.size()));
	}

	@Override
	public ServiceResult<MeasureUnit> saveUnit(MeasureUnit measureUnit) {
		
		final Long id = measureUnit.getId();
		
		MeasureUnitEntity mue = resolveEntity(id);
		if (id.longValue() == -1L && mue != null) {
			getLog().warn("Measure unit with dn {} did already exist in the system.", measureUnit.getDn());
			return ServiceResultImpl.createFailedResult(new EntityNotUniqueMessage(MeasureUnitEntity.class, "unikt namn"));
		}
		
		if (id.longValue() != -1L && mue == null) {
			getLog().warn("Measure unit {} was expected to be in the database but was not", id);
			return ServiceResultImpl.createFailedResult(new EntityNotFoundMessage(MeasureUnitEntity.class, id));
		}
		
		if (mue == null) {
			
			mue = MeasureUnitEntity.newEntity(measureUnit.getName(), measureUnit.getDn());
			
			verifyWriteAccess(mue);
			
			final MeasureUnitEntity saved = repo.save(mue);
			return ServiceResultImpl.createSuccessResult(MeasureUnitImpl.newFromEntity(saved)
					, new GenericSuccessMessage());
		} else {
			
			mue.setDn(measureUnit.getDn());
			mue.setName(measureUnit.getName());
			
			return ServiceResultImpl.createSuccessResult(MeasureUnitImpl.newFromEntity(mue)
					, new GenericSuccessMessage());
		}
		
	}
	
	private MeasureUnitEntity resolveEntity(final Long id) {
		if (id.longValue() == -1L) {
			return null;
		}
		
		return repo.findOne(id);
	}

}
