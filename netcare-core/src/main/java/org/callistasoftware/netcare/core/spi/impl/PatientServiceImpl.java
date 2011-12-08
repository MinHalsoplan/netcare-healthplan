/**
 * Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>
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
import java.util.List;

import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.DefaultSystemMessage;
import org.callistasoftware.netcare.core.api.impl.PatientBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.entity.PatientEntity;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.core.spi.PatientService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

	@Autowired
	private PatientRepository patientRepository;
	
	@Override
	public ServiceResult<PatientBaseView[]> findPatients(String freeTextSearch) {
		if (freeTextSearch.length() < 3) {
			throw new IllegalArgumentException("Method cannot be invoked if the search string does not contain at least 3 charcaters");
		}
		
		final StringBuilder search = new StringBuilder().append("%").append(freeTextSearch).append("%"); 
		final List<PatientEntity> hits = this.patientRepository.findByNameLikeOrEmailLikeOrCivicRegistrationNumberLike(search.toString(), search.toString(), search.toString());
		
		final List<PatientBaseView> dtos = new ArrayList<PatientBaseView>(hits.size());
		for (final PatientEntity ent : hits) {
			final PatientBaseViewImpl dto = new PatientBaseViewImpl();
			BeanUtils.copyProperties(ent, dto);
			
			dtos.add(dto); 
		}
		
		return ServiceResultImpl.createSuccessResult(dtos.toArray(new PatientBaseView[dtos.size()]), new DefaultSystemMessage("Found " + dtos.size() + " patients that matched your critera."));
	}

	@Override
	public ServiceResult<PatientBaseView> loadPatient(Long id) {
		final PatientEntity ent = this.patientRepository.findOne(id);
		final PatientBaseView dto = new PatientBaseViewImpl();
		
		BeanUtils.copyProperties(ent, dto);
		
		return ServiceResultImpl.createSuccessResult(dto, null);
	}

}
