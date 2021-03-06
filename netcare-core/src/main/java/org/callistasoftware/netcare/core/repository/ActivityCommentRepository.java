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

import org.callistasoftware.netcare.model.entity.ActivityCommentEntity;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityCommentRepository extends JpaRepository<ActivityCommentEntity, Long> {

	@Query(value = "select e from ActivityCommentEntity as e inner join " + "e.activity as a inner join "
			+ "a.activityDefinition as ad inner join "
			+ "ad.healthPlan as hp where hp.forPatient = :patient and e.hiddenByPatient = false")
	List<ActivityCommentEntity> findCommentsForPatient(@Param("patient") final PatientEntity patient);

	@Query(value = "select e from ActivityCommentEntity as e where e.commentedBy = :careActor "
			+ "and e.repliedAt is not null " + "and e.activity.activityDefinition.healthPlan.careUnit = :careUnit "
			+ "and e.hiddenByAdmin = false")
	List<ActivityCommentEntity> findRepliesForCareActor(@Param("careActor") final CareActorEntity careActor,
			@Param("careUnit") final CareUnitEntity careUnit);
}
