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
package org.callistasoftware.netcare.model.entity;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines a week based frequency in days and times. <p>
 * 
 * Format [version;weekFrequency;FrequencyDay1;FrequencyDay2;....;FrequencyDayN]
 * 
 * @author Peter
 *
 */
public class Frequency {
	static final String VERSION = "1";
	static final String REC_SEP = ";";
	
	/** Frequency of week in numbers: Between 1 and 53 */
	private int weekFrequency;
	/** Day bitmask (derived from {@link FrequencyTime} to check if a day is set. */
	private int dayMask = 0;
	/** Times. */
	private List<FrequencyDay> days;
	
	public Frequency() {
		this.days = new LinkedList<FrequencyDay>();
	}
	
	//
	public List<FrequencyDay> getDays() {
		return Collections.unmodifiableList(days);
	}

	/**
	 * Returns the actual day of week, or null if not set.
	 * 
	 * @param d the day in week as specified in {@link Calendar}
	 * @return the day or null if not set.
	 */
	public FrequencyDay getDay(int d) {
		for (FrequencyDay day : days) {
			if (day.getDay() == d) {
				return day;
			}
		}
		return null;
	}
	
	//
	public void addDay(FrequencyDay day) {
		days.add(day);
		addDayMask(day);
	}
	
	//
	public boolean removeDay(FrequencyDay day) {
		if (days.remove(day)) {
			removeDayMask(day);
			return true;
		}
		return false;
	}	
		
	/**
	 * Marshals a frequency into a string representation.
	 * 
	 * @param frequency the frequency to marshal.
	 * @return a string representation.
	 */
	public static String marshal(Frequency frequency) {
		StringBuffer sb = new StringBuffer();
		sb.append(VERSION);
		sb.append(REC_SEP);
		sb.append(frequency.getWeekFrequency());
		for (FrequencyDay day : frequency.getDays()) {
			sb.append(REC_SEP);
			sb.append(FrequencyDay.marshal(day));
		}
		return sb.toString();
	}
	
	/**
	 * Unmarshals a frequency from a string representation.
	 * 
	 * @param stringFrequency the string representation.
	 * @return a corresponding frequency object.
	 */
	
	public static Frequency unmarshal(String stringFrequency) {
		if (stringFrequency == null || stringFrequency.length() == 0) {
			return null;
		}
		final String[] fields = stringFrequency.split(REC_SEP);
		int n = 0;
		String version = fields[n++];
		if (!VERSION.equals(version)){
			throw new IllegalArgumentException("Invalid frequency version: " + version + ", expected: " + VERSION);
		}
		Frequency frequency = new Frequency();
		frequency.setWeekFrequency(Integer.valueOf(fields[n++]));
		for (; n < fields.length; n++) {
			FrequencyDay day = FrequencyDay.unmarshal(fields[n]);
			frequency.addDay(day);
		}
		return frequency;
	}

	public void setWeekFrequency(int weekFrequency) {
		if (weekFrequency < 0 || weekFrequency > 53) {
			throw new IllegalArgumentException("Invalid week frequency, expected (0-53): " + weekFrequency);
		}
		this.weekFrequency = weekFrequency;
	}

	public int getWeekFrequency() {
		return weekFrequency;
	}

	/**
	 * Returns if date is set.
	 * 
	 * @param calendar the date.
	 * @return true if set, otherwise false.
	 */
	public boolean isDaySet(Calendar calendar) {
		return isDaySet(calendar.get(Calendar.DAY_OF_WEEK));
	}
	/**
	 * Returns if a discrete day or all days are set.
	 * 
	 * @param day any of the defined day constants form this class (MON-SAT)
	 * @return 
	 */
	public boolean isDaySet(int day) {
		return (dayMask & (1<<day)) != 0;
	}

	/**
	 * Adds a discrete day or all days to the bitmask.
	 * 
	 * @param day as specified in DAY_OF_WEEK in {@link Calendar}
	 */
	private void addDayMask(FrequencyDay day) {		
		validateDay(day.getDay());
		dayMask |= (1<<day.getDay());
	}

	private void removeDayMask(FrequencyDay day) {
		dayMask &= ~(1<<day.getDay());
	}

	/**
	 * Returns if the parameter is a valid discrete day or all days.
	 * 
	 * @param day the day bits.
	 */
	public static void validateDay(int day) {
		if (day < 1 || day > 7) {
			throw new IllegalArgumentException("Invalid day, expected (1-7): " + day);
		}
	}
	
	@Override
	public String toString() {
		return marshal(this);
	}
}
