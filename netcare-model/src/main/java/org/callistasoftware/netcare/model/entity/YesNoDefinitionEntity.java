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
package org.callistasoftware.netcare.model.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("yesno")
public class YesNoDefinitionEntity extends ActivityItemDefinitionEntity {

	YesNoTypeEntity getYesNoType() {
		return (YesNoTypeEntity) getActivityItemType();
	}

	public static ActivityItemDefinitionEntity newEntity(ActivityDefinitionEntity activityDefEntity,
			ActivityItemTypeEntity activityItemType) {
		YesNoDefinitionEntity entity = new YesNoDefinitionEntity();
		entity.setActivityDefinition(activityDefEntity);
		entity.setActivityItemType(activityItemType);
		return entity;
	}
}
