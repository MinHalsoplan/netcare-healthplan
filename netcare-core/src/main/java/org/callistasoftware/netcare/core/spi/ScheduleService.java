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

import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;

public interface ScheduleService {

	ServiceResult<ScheduledActivity[]> load(boolean includeReported
			, boolean includeDue
			, final Long startTs
			, final Long endTs);
	
	/**
	 * Load the latest scheduled activity for a specific activity definition
	 * @param definitionId
	 * @return
	 */
	ServiceResult<ScheduledActivity> loadLatestForDefinition(final Long definitionId);
	
	/**
	 * Reports on an activity and returns the update.
	 * 
	 * @param scheduledActivityId the id.
	 * @param value the value.
	 * @return an updated {@link ScheduledActivity}
	 */
	ServiceResult<ScheduledActivity> reportReady(final ScheduledActivity report);
}
