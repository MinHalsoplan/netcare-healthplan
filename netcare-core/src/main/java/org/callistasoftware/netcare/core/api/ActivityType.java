/**
 * Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>
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

import java.io.Serializable;

import org.callistasoftware.netcare.core.api.impl.ActivityTypeImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Definition of an activity type
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@JsonDeserialize(as=ActivityTypeImpl.class)
public interface ActivityType extends Serializable {

	/**
	 * Get the id of this activity type
	 */
	Long getId();
	
	/**
	 * Wether the activity is used somewhere
	 * @return
	 */
	boolean isInUse();
	
	/**
	 * The access level of this template
	 * @return
	 */
	Option getAccessLevel();
	
	/**
	 * Get the name of this activity type
	 */
	String getName();
	
	/**
	 * Get the name of the category where this type belongs
	 */
	ActivityCategory getCategory();
	
	/**
	 * The measure values bound to this activity type
	 * @return
	 */
	ActivityItemType[] getActivityItems();
}
