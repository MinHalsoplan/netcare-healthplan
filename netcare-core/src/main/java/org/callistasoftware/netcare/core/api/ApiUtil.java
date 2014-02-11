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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.callistasoftware.netcare.model.entity.EntityUtil;

/**
 * Various utility & conversion functions.
 * 
 * @author Peter
 */
public class ApiUtil {
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd H:m");

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

	/**
	 * Returns the corresponding day as an integer.
	 * 
	 * @param day the string as specified in message properties (key)
	 * @return the corresponding integer as specified in {@link Calendar}
	 */
	public static int toIntDay(String day) {
		Integer d = intDay.get(day);
		if (d == null) {
			throw new IllegalArgumentException("No such day: " + day);
		}
		return d;
	}
	
	/**
	 * Returns the corresponing day as string.
	 * 
	 * @param day the day as specified in {@link Calendar}
	 * @return the corresponfing string representation as specified in message.properties (key)
	 */
	public static String toStringDay(int day) {
		String s = stringDay.get(day);
		if (s == null) {
			throw new IllegalArgumentException("No such day: " + day);
		}
		return s;
	}	
	
	/**
	 * Returns a date from a string date and time.
	 * 
	 * @param date the date as (yyyy-mm-dd)
	 * @param time (hh:mi)
	 * @return the date.
	 */
	public static Date parseDateTime(String date, String time) {
		try {
			String dateTime = String.format("%s %s", date, time);
			return (date == null) ? null : dateTimeFormat.parse(dateTime);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static Date parseDateTime(final String dateTime) {
		try {
			return dateTime == null ? null : dateTimeFormat.parse(dateTime);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not construct date from: " + dateTime);
		}
	}
	
	/**
	 * Returns a date from a string date in the format yyyy-MM-dd
	 * 
	 * @param date the string according to yyyy-MM-dd
	 * @return the date or null if input date is null
	 */
	public static Date parseDate(String date) {
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
	public static String formatDate(Date date) {
		return (date == null) ? null : dateFormat.format(date);
	}
	
	/**
	 * Returns a string time from a date.
	 * 
	 * @param time the time.
	 * @return the string formatted as as HH24:MM
	 */
	public static String formatTime(Date time) {
		return String.format("%1$tR", time);		
	}
	
	
	/**
	 * Truncates time to zero, keeps date.
	 * 
	 * @param cal the calendar to truncate.
	 * @return the altered  calendar.
	 */
	public static Calendar dayBegin(Calendar cal) {
		return EntityUtil.dayBegin(cal);
	}

	/**
	 * Sets time to 23:59:59:999, at end of day, keeps date.
	 * 
	 * @param cal the calendar to set to end of day.
	 * @return the altered calendar.
	 */
	public static Calendar dayEnd(Calendar cal) {
		return EntityUtil.dayEnd(cal);
	}
}
