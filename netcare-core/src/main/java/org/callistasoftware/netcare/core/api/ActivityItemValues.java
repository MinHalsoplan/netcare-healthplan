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
	@JsonSubTypes.Type(value=Estimation.class),
    @JsonSubTypes.Type(value=Measurement.class),
    @JsonSubTypes.Type(value=Text.class),
    @JsonSubTypes.Type(value=YesNo.class)
})
public interface ActivityItemValues {

	/**
	 * Returns id.
	 * 
	 */
	Long getId();

	/**
	 * Get the definition by its base interface ActivityItemValuesDefinition.
	 * 
	 * @return The definition.
	 */
	ActivityItemValuesDefinition getDefinition();
}
