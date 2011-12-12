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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.callistasoftware.netcare.core.api.Ordination;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.impl.OrdinationImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.entity.OrdinationEntity;
import org.callistasoftware.netcare.core.repository.OrdinationRepository;
import org.callistasoftware.netcare.core.spi.OrdinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of service defintion
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@Service
public class OrdinationServiceImpl implements OrdinationService {

	private static final Logger log = LoggerFactory.getLogger(OrdinationServiceImpl.class);
	
	@Autowired
	private OrdinationRepository repo;
	
	@Override
	public ServiceResult<Ordination[]> loadOrdinationsForPatient(Long patient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResult<Ordination> createNewOrdination(final Ordination ordination) {
		
		log.info("Creating new ordination {}", ordination.getName());
		
		final OrdinationEntity entity = new OrdinationEntity();
		entity.setName(ordination.getName());
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yy-mm-dd");
		try {
			entity.setStartDate(sdf.parse(ordination.getStartDate()));
		} catch (ParseException e1) {
			throw new IllegalArgumentException("Could not parse date.", e1);
		}
		
		final OrdinationEntity saved = this.repo.save(entity);
		final OrdinationImpl dto = new OrdinationImpl();
		
		dto.setName(saved.getName());
		dto.setStartDate(sdf.format(saved.getStartDate()));
		
		return ServiceResultImpl.createSuccessResult((Ordination) dto, new GenericSuccessMessage());
	}

}
