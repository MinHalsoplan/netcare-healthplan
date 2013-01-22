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
import java.util.Date;

import org.callistasoftware.netcare.core.api.impl.ActivityDefinitionImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Defines an activity definition how it is represented
 * in the UI.
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
@JsonDeserialize(as=ActivityDefinitionImpl.class)
public interface ActivityDefinition extends Serializable {
	/**
	 * The id of this activity definition
	 * @return
	 */
	Long getId();

	/**
	 * The desired goal values of this definition
	 */
	ActivityItemValuesDefinition[] getGoalValues();
	
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
	 * Get the id of the health plan this definition belongs to
	 * @return
	 */
	Long getHealthPlanId();
	
	/**
	 * Returns health plan name.
	 * 
	 * @return the health plan name.
	 */
	String getHealthPlanName();
	
	/**
	 * The start date of the health plan
	 * @return
	 */
	Date getHealthPlanStartDate();
	
	/**
	 * Returns issued by.
	 */
	CareActorBaseView getIssuedBy();
	
	/**
	 * returns the health plans care unit
	 * @return
	 */
	CareUnit getHealthPlanCareUnit();
	
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
	
	int getNumExtra();
	
	/**
	 * Whether the activity is active or not
	 * @return
	 */
	boolean isActive();
	
	/**
	 * Whether a reminder for the definition is
	 * enabled or not
	 * @return
	 */
	boolean isReminder();
}