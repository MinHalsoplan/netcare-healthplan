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
import org.callistasoftware.netcare.core.api.impl.EstimationImpl;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonTypeName(ActivityItemTypeImpl.ESTIMATION_ITEM_TYPE)
@JsonDeserialize(as=EstimationImpl.class)
public interface Estimation extends ActivityItemValues {

	/**
	 * Get the reported value.
	 * 
	 * @return The value.
	 */
	Integer getPerceivedSense();
}
