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


/**
 * Keeps track of time in hours and minutes.
 * 
 * @author Peter
 *
 */
public class FrequencyTime {
	private int hour;
	private int minute;
	
	/**
	 * Create a frequence time from the give string
	 * @param time
	 * @return
	 */
	public static FrequencyTime fromTimeString(final String time) {
		final FrequencyTime fr = new FrequencyTime();
		
		if (time.length() != 5) {
			throw new IllegalArgumentException("Invalid time format. Requires: XX:XX but was: " + time);
		}
		
		if (!time.contains(":")) {
			throw new IllegalArgumentException("Invalid time format. Requires: XX:XX but was: " + time);
		}
		
		final String[] parts = time.split(":");
		
		fr.setHour(Integer.valueOf(parts[0]));
		fr.setMinute(Integer.valueOf(parts[1]));
		
		return fr;
	}
	
	void setHour(int hour) {
		if (hour < 0 || hour > 23) {
			throw new IllegalArgumentException("Invalid hour: " + hour);
		}
		this.hour = hour;
	}
	
	public int getHour() {
		return hour;
	}
	
	void setMinute(int minute) {
		if (minute < 0 || minute > 59) {
			throw new IllegalArgumentException("Invalid minute: " + minute);
		}
		this.minute = minute;
	}
	
	public int getMinute() {
		return minute;
	}
}
