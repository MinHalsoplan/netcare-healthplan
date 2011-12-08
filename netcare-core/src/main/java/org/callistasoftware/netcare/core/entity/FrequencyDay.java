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
 * Keeps track of days using a bitmask datatype.
 * 
 * @author Peter
 *
 */
public class FrequencyDay {
	public static final int MON = (1<<0);
	public static final int TUE = (1<<1);
	public static final int WED = (1<<2);
	public static final int THU = (1<<3);
	public static final int FRI = (1<<4);
	public static final int SAT = (1<<5);
	public static final int SUN = (1<<6);
	public static final int ALL = (MON | TUE | WED | THU | FRI | SAT | SUN);
	private int dayMask;
	
	public FrequencyDay() {
		dayMask = 0;
	}
	
	/**
	 * Sets the day bitmask.
	 * 
	 * @param dayMask
	 */
	public void setDays(int dayMask) {
		this.dayMask = dayMask;
	}
	
	/**
	 * Returns the day bitmask.
	 * 
	 * @return the bitmask of days.
	 */
	public int getDays() {
		return dayMask;
	}
	
	/**
	 * Adds a discrete day or all days to the bitmask.
	 * 
	 * @param day any of the defined day constants form this class (MON-SAT and ALL)
	 */
	public void addDay(int day) {		
		assert(isValidDay(day));
		dayMask |= day;
	}
	
	/**
	 * Removes a discrete day or all days from the bitmask.
	 * 
	 * @param day any of the defined day constants form this class (MON-SAT and ALL)
	 */
	public void removeDay(int day) {
		assert(isValidDay(day));
		dayMask &= ~day;
	}
	
	/**
	 * Returns if a discrete day or all days are set.
	 * 
	 * @param day any of the defined day constants form this class (MON-SAT)
	 * @return 
	 */
	public boolean isSet(int day) {
		assert(isValidDay(day));
		return (dayMask & day) != 0;
	}

	/**
	 * Returns if Monday is set.
	 * 
	 * @return true if set, otherwise false.
	 */
	public boolean isMonday() {
		return isSet(MON);
	}

	/**
	 * Returns if Tuesday is set.
	 * 
	 * @return true if set, otherwise false.
	 */
	public boolean isTuesday() {
		return isSet(TUE);
	}
	
	/**
	 * Returns if Wednesday is set.
	 * 
	 * @return true if set, otherwise false.
	 */
	public boolean isWedbesday() {
		return isSet(WED);
	}
	
	/**
	 * Returns if Thursday is set.
	 * 
	 * @return true if set, otherwise false.
	 */
	public boolean isThursday() {
		return isSet(THU);
	}
	
	/**
	 * Returns if Friday is set.
	 * 
	 * @return true if set, otherwise false.
	 */
	public boolean isFriday() {
		return isSet(FRI);
	}
	
	/**
	 * Returns if Saturday is set.
	 * 
	 * @return true if set, otherwise false.
	 */
	public boolean isSaturday() {
		return isSet(SAT);
	}
	
	/**
	 * Returns if Sunday is set.
	 * 
	 * @return true if set, otherwise false.
	 */
	public boolean isSunday() {
		return isSet(SUN);
	}
	
	/**
	 * Returns if the parameter is a valid discrete day.
	 * 
	 * @param day the day bit.
	 * 
	 * @return true if its valid, otherwise false.
	 */
	protected boolean isValidDay(int day) {
		switch (day) {
		case MON: case TUE: case WED:
		case THU: case FRI: case SAT:
		case SUN: case ALL:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Returns a string representation.
	 * 
	 * @param frequencyDay the frequency.
	 * @return a corresponding {@link String} representation.
	 */
	public static String marshal(FrequencyDay frequencyDay) {
		return Integer.toBinaryString(frequencyDay.getDays());
	}
	
	/**
	 * Returns the object representation.
	 * 
	 * @param frequencyDay the string.
	 * @return a corresponding {@link FrequencyDay} object.
	 */
	public static FrequencyDay unmarshal(String frequencyDay) {
		FrequencyDay f = new FrequencyDay();
		f.setDays(Integer.parseInt(frequencyDay, 2));
		return f;
	}
}
