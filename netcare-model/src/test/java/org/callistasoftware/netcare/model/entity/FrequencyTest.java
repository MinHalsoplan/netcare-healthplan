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
package org.callistasoftware.netcare.model.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FrequencyTest {
	
	static Frequency createFrequency() {
		Frequency f = new Frequency();
		
		f.getFrequencyDay().addDay(FrequencyDay.TUE);
		f.getFrequencyDay().addDay(FrequencyDay.WED);
		f.getFrequencyDay().addDay(FrequencyDay.FRI);
		f.getFrequencyDay().addDay(FrequencyDay.SAT);

		FrequencyTime v = new FrequencyTime();
		v.setHour(14);
		v.setMinute(00);
		f.getTimes().add(v);
		
		v = new FrequencyTime();
		v.setHour(8);
		v.setMinute(30);
		f.getTimes().add(v);
		
		v = new FrequencyTime();
		v.setHour(21);
		v.setMinute(30);
		f.getTimes().add(v);
		
		assertEquals(3, f.getTimes().size());
		
		assertEquals(true, f.getFrequencyDay().isTuesday());
		assertEquals(false, f.getFrequencyDay().isSunday());
		
		f.getFrequencyDay().removeDay(FrequencyDay.TUE);
		assertEquals(false, f.getFrequencyDay().isTuesday());
		assertEquals(true, f.getFrequencyDay().isWedbesday());

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
		Frequency r = Frequency.unmarshal(s);
		assertEquals(f.getTimes().size(),r.getTimes().size());
		assertEquals(f.getFrequencyDay().getDays(), f.getFrequencyDay().getDays());
		for (int i = 0; i < f.getTimes().size(); i++) {
			FrequencyTime lv = f.getTimes().get(i);
			FrequencyTime rv = r.getTimes().get(i);
			assertEquals(lv.getHour(), rv.getHour());
			assertEquals(lv.getMinute(), rv.getMinute());
		}
	}
}
