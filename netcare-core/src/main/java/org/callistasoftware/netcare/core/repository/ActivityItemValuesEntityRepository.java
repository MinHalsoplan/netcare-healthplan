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
package org.callistasoftware.netcare.core.repository;

import java.util.List;

import org.callistasoftware.netcare.model.entity.ActivityItemValuesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityItemValuesEntityRepository extends JpaRepository<ActivityItemValuesEntity, Long> {

	@Query("select values from ActivityItemValuesEntity as values "
			+ "where values.scheduledActivity.reportedTime is not null "
            + "and values.scheduledActivity.status = 0 "
			+ "and activityItemDefinitionEntity.id = :itemId "
			+ "order by values.scheduledActivity.actualTime")
	List<ActivityItemValuesEntity> findValuesByActivityItemId(@Param("itemId") final Long itemId);

}
