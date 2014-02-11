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

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="valueType")
@JsonSubTypes({
	@JsonSubTypes.Type(value=EstimationDefinition.class),
    @JsonSubTypes.Type(value=MeasurementDefinition.class),
    @JsonSubTypes.Type(value=TextDefinition.class),
    @JsonSubTypes.Type(value=YesNoDefinition.class)
})
public interface ActivityItemValuesDefinition {

	/**
	 * Returns id.
	 * 
	 */
	Long getId();

	/**
	 * Returns the type.
	 * 
	 * @return An subclass of ActivityItemType
	 */
	ActivityItemType getActivityItemType();
	
	/**
	 * Whether this item is enabled in this definition
	 * @return
	 */
	boolean isActive();
}
