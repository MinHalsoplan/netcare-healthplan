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
	
	private FrequencyUnit frequencyUnit;
	private List<FrequencyValue> values;
		
	Frequency() {
		this.values = new LinkedList<FrequencyValue>();	
	}
	
	public Frequency(FrequencyUnit frequencyUnit) {
		this();
		this.frequencyUnit = frequencyUnit;
	}

	public List<FrequencyValue> getValues() {
		return values;
	}
	
	public FrequencyUnit getFrequencyUnit() {
		return frequencyUnit;
	}

	public static String marshal(Frequency frequency) {
		StringBuffer sb = new StringBuffer(frequency.getFrequencyUnit().toString());
		for (FrequencyValue v : frequency.getValues()) {
			sb.append(REC_SEP);
			if (frequency.getFrequencyUnit() == FrequencyUnit.DAILY) {
				sb.append("");
			} else {
				sb.append(v.getDay().toString());
			}
			sb.append(VAL_SEP);
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
		StringTokenizer t = new StringTokenizer(s, REC_SEP);
		FrequencyUnit unit = FrequencyUnit.valueOf(t.nextToken());
		Frequency f = new Frequency(unit);
		while (t.hasMoreTokens()) {
			FrequencyValue value = new FrequencyValue();
			String[] values = t.nextToken().split(VAL_SEP);
			if (unit == FrequencyUnit.WEEKLY) {
				value.setDay(FrequencyDay.valueOf(values[0]));
			}
			value.setHour(Integer.valueOf(values[1]));
			value.setMinute(Integer.valueOf(values[2]));

			f.getValues().add(value);
		}
		return f;
	}
}
