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
package org.callistasoftware.netcare.core.spi;

import org.callistasoftware.netcare.core.api.MeasureUnit;
import org.callistasoftware.netcare.core.api.ServiceResult;

public interface UnitService {

	/**
	 * Load units
	 * @return All units that exist on the user's county council
	 */
	ServiceResult<MeasureUnit[]> loadUnits();
	
	/**
	 * Create a new measure unit
	 * @param measureUnit
	 * @return
	 */
	ServiceResult<MeasureUnit> saveUnit(final MeasureUnit measureUnit);
}
