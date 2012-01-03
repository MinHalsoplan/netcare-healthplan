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


/**
 * Keeps track of time in hours and minutes.
 * 
 * @author Peter
 *
 */
public class FrequencyTime {
	static final String REC_SEP = ":";
	/** hour 0-23 */
	private int hour;
	/** minute 0-59 */
	private int minute;
	
	
	private FrequencyTime() {}
	
	FrequencyTime(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}
	
	public static FrequencyTime newFrequencyTime(int hour, int minute) {
		return new FrequencyTime(hour, minute);
	}
	
	public static String marshal(final FrequencyTime frequencyTime) {
		return String.format("%02d%s%02d", frequencyTime.getHour(), REC_SEP, frequencyTime.getMinute());
	}
	
	public static FrequencyTime unmarshal(String frequencyTime) {
		FrequencyTime ft = new FrequencyTime();
		if (frequencyTime.indexOf(REC_SEP) == -1) {
			frequencyTime += REC_SEP + "0";
		}
		String[] arr = frequencyTime.split(REC_SEP);
		if (arr.length != 2) {
			throw new IllegalArgumentException("Invalid frequency format, expected [hour:minute]: " + frequencyTime);
		}
		ft.setHour(Integer.valueOf(arr[0]));
		ft.setMinute(Integer.valueOf(arr[1]));

		return ft;
	}

	static void validate(int value, int min, int max) {
		if (value < 0 || value > max) {
			throw new IllegalArgumentException(String.format("Number argument %d is out of range (%d-%d)", value, min, max));			
		}
	}
	
	private void setHour(int hour) {
		validate(hour, 0, 23);
		this.hour = hour;
	}
	
	public int getHour() {
		return hour;
	}
	
	private void setMinute(int minute) {
		validate(minute, 0, 59);
		this.minute = minute;
	}
	
	public int getMinute() {
		return minute;
	}
	
	@Override
	public String toString() {
		return String.format("FrequencyTime [ hour: %d, minute: %d ]", hour, minute);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof FrequencyTime)) {
			return false;
		} else if (this == o) {
			return true;
		}
		FrequencyTime r = (FrequencyTime)o;
		return (this.getHour() == r.getHour() && this.getMinute() == r.getMinute());
	}	
}
