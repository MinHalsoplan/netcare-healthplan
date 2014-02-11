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

/**
 * Defines a day with times
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public interface DayTime {

	/**
	 * The week day.
	 * 
	 * @return
	 */
	String getDay();
	
	/**
	 * The localized week day.
	 * 
	 * @return the localized week day.
	 */
	Option getDayCaption();
	
	/**
	 * Get the times in a string array, the format of each
	 * element is HH:MM
	 * @return
	 */
	String[] getTimes();
}
