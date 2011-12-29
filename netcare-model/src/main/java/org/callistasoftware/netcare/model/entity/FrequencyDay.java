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
package org.callistasoftware.netcare.model.entity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps track of days.
 * 
 * @author Peter
 *
 */
public class FrequencyDay {
	static final String REC_SEP = ",";
	private int day;
	private List<FrequencyTime> times;
	
	private FrequencyDay() {
		this.times = new LinkedList<FrequencyTime>();
	}
	
	public static FrequencyDay newFrequencyDay(int day) {
		FrequencyDay fd = new FrequencyDay();
		fd.setDay(day);
		return fd;
	}
	
	//
	public List<FrequencyTime> getTimes() {
		return Collections.unmodifiableList(times);
	}
	
	//
	public boolean addTime(FrequencyTime time) {
		return times.add(time);
	}
	
	//
	public boolean removeTime(FrequencyTime time) {
		return times.remove(time);
	}


	/**
	 * Returns a string representation.
	 * 
	 * @param frequencyDay the frequency.
	 * @return a corresponding {@link String} representation.
	 */
	public static String marshal(FrequencyDay frequencyDay) {
		StringBuffer sb = new StringBuffer();
		sb.append(frequencyDay.getDay());
		for (FrequencyTime time : frequencyDay.getTimes()) {
			sb.append(REC_SEP);
			sb.append(FrequencyTime.marshal(time));
		}
		return sb.toString();
	}
	
	/**
	 * Returns the object representation.
	 * 
	 * @param frequencyDay the string.
	 * @return a corresponding {@link FrequencyDay} object.
	 */
	public static FrequencyDay unmarshal(String frequencyDay) {
		String[] arr = frequencyDay.split(REC_SEP);
		if (arr.length == 0) {
			throw new IllegalArgumentException("Invalid frequency day format: " + frequencyDay);
		}
		int n = 0;
		FrequencyDay fd = newFrequencyDay(Integer.valueOf(arr[n++]));
		for (; n < arr.length; n++) {
			FrequencyTime ft = FrequencyTime.unmarshal(arr[n]);
			fd.addTime(ft);
		}
		return fd;
	}

	private void setDay(int day) {
		Frequency.validateDay(day);
		this.day = day;
	}

	public int getDay() {
		return day;
	}

}
