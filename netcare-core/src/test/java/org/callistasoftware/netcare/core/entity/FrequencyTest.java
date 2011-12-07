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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FrequencyTest {
	
	static Frequency createDailyFrequency() {
		Frequency f = new Frequency(FrequencyUnit.DAILY);
		assertEquals(FrequencyUnit.DAILY, f.getFrequencyUnit());
		
		FrequencyValue v = new FrequencyValue();
		v.setHour(14);
		v.setMinute(00);
		f.getValues().add(v);
		
		v = new FrequencyValue();
		v.setHour(8);
		v.setMinute(30);
		f.getValues().add(v);
		
		v = new FrequencyValue();
		v.setHour(21);
		v.setMinute(30);
		f.getValues().add(v);
		
		assertEquals(3, f.getValues().size());
		return f;
	}
	
	
	static Frequency createWeeklyFrequency() {
		Frequency f = new Frequency(FrequencyUnit.WEEKLY);
		assertEquals(FrequencyUnit.WEEKLY, f.getFrequencyUnit());
		
		FrequencyValue v = new FrequencyValue();
		v.setHour(14);
		v.setMinute(00);
		v.setDay(FrequencyDay.MONDAY);
		f.getValues().add(v);
		
		v = new FrequencyValue();
		v.setHour(8);
		v.setMinute(30);
		v.setDay(FrequencyDay.WEDNESDAY);
		f.getValues().add(v);
		
		v = new FrequencyValue();
		v.setHour(21);
		v.setMinute(30);
		v.setDay(FrequencyDay.FRIDAY);
		f.getValues().add(v);
		
		assertEquals(3, f.getValues().size());
		return f;
	}
	
	
	@Test
	public void testDaily() throws Exception {
		createDailyFrequency();
	}
	
	@Test
	public void testDailyMarshal() {
		Frequency f = createDailyFrequency();
		String s = Frequency.marshal(f);
		Frequency r = Frequency.unmarshal(s);
		assertEquals(f.getFrequencyUnit(),r.getFrequencyUnit());
		assertEquals(f.getValues().size(),r.getValues().size());
		for (int i = 0; i < f.getValues().size(); i++) {
			FrequencyValue lv = f.getValues().get(i);
			FrequencyValue rv = r.getValues().get(i);
			assertEquals(lv.getHour(), rv.getHour());
			assertEquals(lv.getMinute(), rv.getMinute());
		}
	}

	@Test
	public void testWeeklyMarshal() {
		Frequency f = createWeeklyFrequency();
		String s = Frequency.marshal(f);
		Frequency r = Frequency.unmarshal(s);
		assertEquals(f.getFrequencyUnit(),r.getFrequencyUnit());
		assertEquals(f.getValues().size(),r.getValues().size());
		for (int i = 0; i < f.getValues().size(); i++) {
			FrequencyValue lv = f.getValues().get(i);
			FrequencyValue rv = r.getValues().get(i);
			assertEquals(lv.getHour(), rv.getHour());
			assertEquals(lv.getMinute(), rv.getMinute());
			assertEquals(lv.getDay(), rv.getDay());
		}
	}

}
