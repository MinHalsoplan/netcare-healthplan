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
package org.callistasoftware.netcare.core.spi;

import org.callistasoftware.netcare.core.api.ActivityCategory;
import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;

/**
 * Interface defining service methods for
 * managing activity types
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public interface ActivityTypeService {
	
	/**
	 * Create a new activity type
	 * @param dto
	 * @param careActor the user creating this type.
	 * @return
	 */
	ServiceResult<ActivityType> createActivityType(final ActivityType dto, final CareActorBaseView careActor);
	
	/**
	 * Searches for activity types
	 * @param searchString
	 * @return
	 */
	ServiceResult<ActivityType[]> searchForActivityTypes(final String searchString);
	
	/**
	 * Load all activity categories
	 * @return
	 */
	ServiceResult<ActivityCategory[]> loadAllActivityCategories();
	
	/**
	 * Creates a new activity category
	 * @param dto
	 * @return
	 */
	ServiceResult<ActivityCategory> createActivityCategory(final ActivityCategory dto);
	
	/**
	 * Load all activity types that exist in
	 * the system
	 * @return
	 */
	ServiceResult<ActivityType[]> loadAllActivityTypes(final String hsaId);
}
