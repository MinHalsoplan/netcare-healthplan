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

import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareUnitRepository extends JpaRepository<CareUnitEntity, Long> {

	/**
	 * Find the care unit with the specified hsa id
	 * @param hsaId
	 * @return
	 */
	CareUnitEntity findByHsaId(final String hsaId);
}
