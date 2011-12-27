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
package org.callistasoftware.netcare.core.api;
/*
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
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

//
public class ApiUtilTest {

	@Test
	public void testIntDays() throws Exception {
		assertEquals(Calendar.MONDAY, ApiUtil.toIntDay("monday"));
		assertEquals(Calendar.TUESDAY, ApiUtil.toIntDay("tuesday"));
		assertEquals(Calendar.WEDNESDAY, ApiUtil.toIntDay("wednesday"));
		assertEquals(Calendar.THURSDAY, ApiUtil.toIntDay("thursday"));
		assertEquals(Calendar.FRIDAY, ApiUtil.toIntDay("friday"));
		assertEquals(Calendar.SATURDAY, ApiUtil.toIntDay("saturday"));
		assertEquals(Calendar.SUNDAY, ApiUtil.toIntDay("sunday"));
	}
	
	@Test
	public void testStringDays() throws Exception {
		assertEquals("monday", ApiUtil.toStringDay(Calendar.MONDAY));
		assertEquals("tuesday", ApiUtil.toStringDay(Calendar.TUESDAY));
		assertEquals("wednesday", ApiUtil.toStringDay(Calendar.WEDNESDAY));
		assertEquals("thursday", ApiUtil.toStringDay(Calendar.THURSDAY));
		assertEquals("friday", ApiUtil.toStringDay(Calendar.FRIDAY));
		assertEquals("saturday", ApiUtil.toStringDay(Calendar.SATURDAY));
		assertEquals("sunday", ApiUtil.toStringDay(Calendar.SUNDAY));		
	}
	
	@Test
	public void dateTest() {
		final String d = "2012-12-29";
		assertEquals(d, ApiUtil.toString(ApiUtil.toDate(d)));
	}
}
