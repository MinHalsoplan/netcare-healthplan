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
package org.callistasoftware.netcare.core.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Defines a week based frequency in days and times.
 * 
 * @author Peter
 *
 */
public class Frequency {
	static final String VERSION = "1";
	static final String REC_SEP = ";";
	static final String VAL_SEP = ":";
	
	private List<FrequencyTime> times;
	private FrequencyDay frequencyDay;
		
	public Frequency() {
		this.times = new LinkedList<FrequencyTime>();
		this.frequencyDay = new FrequencyDay();
	}
	
	public List<FrequencyTime> getTimes() {
		return times;
	}
		
	public void setFrequencyDay(FrequencyDay frequencyDay) {
		this.frequencyDay = frequencyDay;
	}

	public FrequencyDay getFrequencyDay() {
		return frequencyDay;
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
		sb.append(FrequencyDay.marshal(frequency.getFrequencyDay()));
		for (FrequencyTime v : frequency.getTimes()) {
			sb.append(REC_SEP);
			sb.append(v.getHour());
			sb.append(VAL_SEP);
			sb.append(v.getMinute());
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
		StringTokenizer tokenizer = new StringTokenizer(stringFrequency, REC_SEP);
		String version = tokenizer.nextToken();
		if (!VERSION.equals(version)){
			throw new IllegalArgumentException("Invalid string frequency version: " + version + ", expetced: " + VERSION);
		}
		Frequency frequency = new Frequency();
		frequency.setFrequencyDay(FrequencyDay.unmarshal(tokenizer.nextToken()));
		while (tokenizer.hasMoreTokens()) {
			FrequencyTime time = new FrequencyTime();
			String[] values = tokenizer.nextToken().split(VAL_SEP);
			time.setHour(Integer.valueOf(values[0]));
			time.setMinute(Integer.valueOf(values[1]));
			frequency.getTimes().add(time);
		}
		return frequency;
	}

}
