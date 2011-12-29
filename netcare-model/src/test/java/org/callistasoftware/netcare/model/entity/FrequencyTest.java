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

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class FrequencyTest {
	
	static Frequency createFrequency() {
		Frequency f = new Frequency();
		f.setWeekFrequency(1);
		FrequencyDay day1 = FrequencyDay.newFrequencyDay(Calendar.MONDAY);
		
		day1.addTime(FrequencyTime.newFrequencyTime(7, 0));
		day1.addTime(FrequencyTime.newFrequencyTime(12, 0));
		day1.addTime(FrequencyTime.newFrequencyTime(18, 0));
		f.addDay(day1);

		FrequencyDay day2 = FrequencyDay.newFrequencyDay(Calendar.SATURDAY);
		day2.addTime(FrequencyTime.newFrequencyTime(9, 0));
		day2.addTime(FrequencyTime.newFrequencyTime(18, 15));
		f.addDay(day2);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		
		assertEquals(true, f.isDaySet(Calendar.MONDAY));
		assertEquals(true, f.isDaySet(Calendar.SATURDAY));
		assertEquals(true, f.isDaySet(cal));
		assertEquals(false, f.isDaySet(Calendar.TUESDAY));
		assertEquals(false, f.isDaySet(Calendar.WEDNESDAY));
		assertEquals(false, f.isDaySet(Calendar.THURSDAY));
		assertEquals(false, f.isDaySet(Calendar.FRIDAY));
		assertEquals(false, f.isDaySet(Calendar.SUNDAY));

		return f;
	}
	
	
	@Test
	public void testBasic() throws Exception {
		createFrequency();
	}
	
	@Test
	public void testMarshal() {
		Frequency f = createFrequency();
		String s = Frequency.marshal(f);
		System.out.println(s);
		Frequency r = Frequency.unmarshal(s);
		assertEquals(f.getDays().size(), f.getDays().size());
		for (int i = 0; i < f.getDays().size(); i++) {
			FrequencyDay lv = f.getDays().get(i);
			FrequencyDay rv = r.getDays().get(i);
			assertEquals(lv.getTimes().get(0).getHour(), rv.getTimes().get(0).getHour());
			assertEquals(lv.getTimes().get(0).getMinute(), rv.getTimes().get(0).getMinute());
		}
	}
}
