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

import org.callistasoftware.netcare.core.entity.CareGiverEntity;
import org.callistasoftware.netcare.core.entity.PatientEntity;
import org.junit.Test;

/** Concept doesn't work. */
public class ApiUtilTest {

	@Test
	public void testProxy() throws Exception {
		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67");
		final PatientEntity p = PatientEntity.newEntity("Arne", "123456789004", cg);
		p.getProperties().put("prop1", "val1");
		p.getProperties().put("prop2", "val2");
		
		final CareGiverEntity c = CareGiverEntity.newEntity("doktor peter", "123456Id");
		p.setPrimaryCareGiver(c);
		
		Patient pi = ApiUtil.copy(Patient.class, p);
		
		assertEquals(pi.getName(), p.getName());
		assertEquals(p.getProperties(), pi.getProperties());
		assertEquals(p.getPrimaryCareGiver().getHsaId(), pi.getPrimaryCareGiver().getHsaId());
	}
}
