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
package org.callistasoftware.netcare.core.api;

import org.callistasoftware.netcare.core.api.impl.ActivityItemTypeImpl;
import org.callistasoftware.netcare.core.api.impl.MeasurementImpl;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonTypeName(ActivityItemTypeImpl.MEASUREMENT_ITEM_TYPE)
@JsonDeserialize(as=MeasurementImpl.class)
public interface Measurement extends ActivityItemValues {

	/**
	 * Returns target if not an interval.
	 */
	float getTarget();

	/**
	 * Returns min target if there is an interval.
	 */
	float getMinTarget();

	/**
	 * Returns max target if there is an interval.
	 * 
	 */
	float getMaxTarget();
	
	/**
	 * Returns reported value.
	 */
	float getReportedValue();
}
