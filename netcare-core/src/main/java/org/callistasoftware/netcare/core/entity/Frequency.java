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

public class Frequency {
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
	

	public static String marshal(Frequency frequency) {
		StringBuffer sb = new StringBuffer();
		sb.append(FrequencyDay.marshal(frequency.getFrequencyDay()));
		for (FrequencyTime v : frequency.getTimes()) {
			sb.append(REC_SEP);
			sb.append(v.getHour());
			sb.append(VAL_SEP);
			sb.append(v.getMinute());
		}
		return sb.toString();
	}
	
	public static Frequency unmarshal(String s) {
		if (s == null || s.length() == 0) {
			return null;
		}
		StringTokenizer tokenizer = new StringTokenizer(s, REC_SEP);
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

	public void setFrequencyDay(FrequencyDay frequencyDay) {
		this.frequencyDay = frequencyDay;
	}

	public FrequencyDay getFrequencyDay() {
		return frequencyDay;
	}
}
