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

import org.callistasoftware.netcare.core.api.impl.ScheduledActivityImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Keeps scheduled activity information, used to display a list of activities.
 * 
 * @author Peter
 */
@JsonDeserialize(as=ScheduledActivityImpl.class)
public interface ScheduledActivity extends Serializable {
	/**
	 * Returns the id.
	 * 
	 * @return the system unique id.
	 */
	long getId();

	/**
	 * Returns the day in week (monday, ...)
	 * 
	 * @return the day as an option.
	 */
	Option getDay();
	
	/**
	 * Return the day in week when activity was reported
	 * @return
	 */
	Option getReportedDay();

	/**
	 * Returns if this activity has been rejected by the user.
	 * 
	 * @return true if rejected, otherwise false.
	 */
	boolean isRejected();

	/**
	 * Returns the scheduled time (hh:mm)
	 * 
	 * @return the time.
	 */
	String getTime();
	
	/**
	 * Returns the reported time
	 * @return
	 */
	String getReportedTime();

	/**
	 * Returns the scheduled date (yyyy-mm-dd)
	 * 
	 * @return the date.
	 */
	String getDate();
	
	/**
	 * Returns the reported date
	 * @return
	 */
	String getReportedDate();

	/**
	 * Returns the associated {@link ActivityDefinition}
	 * 
	 * @return the definition.
	 */
	ActivityDefinition getActivityDefinition();

	/**
	 * Returns if this activity is due.
	 * <p>
	 * 
	 * An activity is marked as due if it's not reported on during the same day
	 * (date) as scheduled.
	 * 
	 * @return true if due, otherwise false.
	 */
	boolean isDue();

	/**
	 * Returns the date and time this was reported (yyyy-mm-dd hh:mm).
	 * 
	 * @return the reported time, or null if none.
	 */
	String getReported();

	/**
	 * Returns the actual time this activity was carried out (yyyy-dd-dd hh:mm).
	 * 
	 * @return the actual time, or null if not yet reported.
	 */
	String getActualTime();

	/**
	 * Returns the patient for this activity.
	 * 
	 * @return the patient.
	 */
	PatientBaseView getPatient();

	/**
	 * Returns the associated patient notice.
	 * 
	 * @return the patient note.
	 */
	String getNote();

	/**
	 * Returns activityItemValues.
	 */
	ActivityItemValues[] getActivityItemValues();
	
	/**
	 * Returns the comments for this activity.
	 * @return
	 */
	ActivityComment[] getComments();
	
	/**
	 * Whether reporting is possible or not for this activity
	 * @return
	 */
	boolean isReportingPossible();
	
	/**
	 * Whether this activity was created by the patient.
	 * @return
	 */
	boolean isExtra();
}
