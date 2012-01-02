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
 * Keeps information about an activity report. <p>
 * 
 * Used when reporting back.
 * 
 * @author Peter
 */
public interface ActivityReport extends Serializable{
	/**
	 * Returns the reported value.
	 * 
	 * @return the actual value reported.
	 */
	int getActualValue();
	
	/**
	 * Returns the actual date when the activity was performed.
	 * 
	 * @return the date as (yyyy-mm-dd), or null.
	 */
	String getActualDate();
	
	/**
	 * Return the actual time when the activity was performed.
	 * 
	 * @return the time as (hh:mm)
	 */
	String getActualTime();
	
	/**
	 * Returns the perceived sense.
	 * 
	 * @return the sense.
	 */
	int getSense();
	
	/**
	 * Returns a note.
	 * 
	 * @return the note, or null if not set.
	 */
	String getNote();
}
