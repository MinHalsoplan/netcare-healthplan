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
package org.callistasoftware.netcare.core.api;

/**
 * Defines an activity definition how it is represented
 * in the UI.
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public interface ActivityDefinition {

	/**
	 * The desired goal of this definition
	 * @return
	 */
	int getGoal();
	
	/**
	 * The activity's type
	 * @return
	 */
	ActivityType getType();
	
	/**
	 * The days of week the activity should be
	 * performed.
	 * 
	 * Valid values are: 0-6 (where 0 = monday)
	 * 
	 * @return
	 */
	DayTime[] getDayTimes();
	
	/**
	 * Get the start date of this activity
	 * @return
	 */
	String getStartDate();
	
	/**
	 * Returns week frequency. <p>
	 * 
	 * A value of 0 indicates single occurrence, otherwise repeating each N week.
	 * 
	 * @return the week frequency.
	 */
	int getActivityRepeat();
}
