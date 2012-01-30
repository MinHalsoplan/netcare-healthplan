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

public interface MeasurementType {

	/**
	 * Returns id.
	 * 
	 */
	Long getId();


	/**
	 * Get the name of this measurement
	 * @return
	 */
	String getName();
	
	/**
	 * Get the type of value for this measurement
	 * @return
	 */
	Option getValueType();
	
	/**
	 * Get the unit of this activity type
	 */
	Option getUnit();
	
	/**
	 * If this measurement is an interval, this flag tells
	 * whether an alarm should be sent if the reported value
	 * is outside the boundaries of this interval
	 * @return
	 */
	boolean isAlarm();	
}
