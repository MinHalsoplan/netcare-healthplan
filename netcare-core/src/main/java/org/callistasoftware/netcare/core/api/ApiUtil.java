/**
 * Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//
public class ApiUtil {
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	static Map<String, Integer> intDay = new HashMap<String, Integer>();
	static Map<Integer, String> stringDay = new HashMap<Integer, String>();
	static {
		intDay.put("monday", Calendar.MONDAY);
		intDay.put("tuesday", Calendar.TUESDAY);
		intDay.put("wednesday", Calendar.WEDNESDAY);
		intDay.put("thursday", Calendar.THURSDAY);
		intDay.put("friday", Calendar.FRIDAY);
		intDay.put("saturday", Calendar.SATURDAY);
		intDay.put("sunday", Calendar.SUNDAY);
		for (Map.Entry<String, Integer> entry : intDay.entrySet()) {
			stringDay.put(entry.getValue(), entry.getKey());
		}
	}

	public static int toIntDay(String day) {
		Integer d = intDay.get(day);
		if (d == null) {
			throw new IllegalArgumentException("No such day: " + day);
		}
		return d;
	}
	
	public static String toStringDay(int day) {
		String s = stringDay.get(day);
		if (s == null) {
			throw new IllegalArgumentException("No such day: " + day);
		}
		return s;
	}	
	
	/**
	 * Returns a date from a string date in the format yyyy-MM-dd
	 * 
	 * @param date the string according to yyyy-MM-dd
	 * @return the date or null if input date is null
	 */
	public static Date toDate(String date) {
		try {
			return (date == null) ? null : dateFormat.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Returns a string from a date.
	 * 
	 * @param date the date.
	 * @return the string representation as 'yyyy-MM-dd' or null
	 */
	public static String toString(Date date) {
		return (date == null) ? null : dateFormat.format(date);
	}
	
	//
	public static Calendar floor(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	//
	public static Calendar ceil(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal;
	}
}
