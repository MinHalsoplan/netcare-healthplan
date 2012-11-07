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
package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.ActivityItemType;
import org.callistasoftware.netcare.core.api.ActivityItemValuesDefinition;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="valueType")
@JsonSubTypes({
      @JsonSubTypes.Type(value=EstimationDefinitionImpl.class, name=ActivityItemTypeImpl.ESTIMATION_ITEM_TYPE),
      @JsonSubTypes.Type(value=MeasurementDefinitionImpl.class, name=ActivityItemTypeImpl.MEASUREMENT_ITEM_TYPE),
      @JsonSubTypes.Type(value=TextDefinitionImpl.class, name=ActivityItemTypeImpl.TEXT_ITEM_TYPE),
      @JsonSubTypes.Type(value=YesNoDefinitionImpl.class, name=ActivityItemTypeImpl.YESNO_ITEM_TYPE)
}) 
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActivityItemValuesDefinitionImpl implements ActivityItemValuesDefinition {
	private Long id;
	private ActivityItemType activityItemType;

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public ActivityItemType getActivityItemType() {
		return this.activityItemType;
	}

	public void setActivityItemType(ActivityItemType activityItemType) {
		this.activityItemType = activityItemType;
	}

}
