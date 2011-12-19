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

import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.ListEntitiesMessage;
import org.callistasoftware.netcare.core.repository.ActivityTypeRepository;
import org.callistasoftware.netcare.core.spi.ActivityTypeService;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the activity type service
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@Service
@Transactional
public class ActivityTypeServiceImpl implements ActivityTypeService {

	private static final Logger log = LoggerFactory.getLogger(ActivityTypeServiceImpl.class);
	
	@Autowired
	private ActivityTypeRepository repo;
	
	@Override
	public ServiceResult<ActivityType[]> loadAllActivityTypes() {
		log.info("Loading all activity types from repository...");
		final List<ActivityTypeEntity> all = this.repo.findAll();
		
		log.debug("Found {} activity types in repository. Converting to dtos", all.size());
		final ActivityType[] types = new ActivityType[all.size()];
		for (int i = 0; i < all.size(); i++) {
			final ActivityTypeEntity ent = all.get(i);
			types[i] = ActivityTypeImpl.newFromEntity(ent, LocaleContextHolder.getLocale());
		}
		
		return ServiceResultImpl.createSuccessResult(types, new ListEntitiesMessage(ActivityTypeEntity.class, types.length));
	}

}
