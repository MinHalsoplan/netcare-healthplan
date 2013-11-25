/**
 * Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>
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

public class CountyCouncilEntityTest {

	@Test
	public void testMeta() {
		CountyCouncilEntity cc = new CountyCouncilEntity(CountyCouncil.VASTRA_GOTALAND);
		assertEquals(CountyCouncil.VASTRA_GOTALAND, cc.getMeta());
	}

}
