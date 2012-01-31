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

import java.io.Serializable;

/**
 * Defines an activity definition how it is represented
 * in the UI.
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public interface ActivityDefinition extends Serializable {
	/**
	 * The id of this activity definition
	 * @return
	 */
	Long getId();

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
	
	/**
	 * Returns end date.
	 * 
	 * @return the end date (yyyy-mm-dd)
	 */
	String getEndDate();
	
	/**
	 * Returns health plan name.
	 * 
	 * @return the health plan name.
	 */
	String getHealthPlanName();
	
	/**
	 * Returns issued by.
	 */
	CareGiverBaseView getIssuedBy();
	
	/**
	 * Returns measurememnt defintions.
	 */
	MeasurementDefinition[] getMeasurementDefinitions();
	
	/**
	 * Returns number of times an activity should have been carried out.
	 */
	int getNumTarget();

	/**
	 * Returns number of activities.
	 */
	int getNumTotal();
	
	/**
	 * Returns number of done activities
	 */
	int getNumDone();	
}