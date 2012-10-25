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
package org.callistasoftware.netcare.core.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class CareUnitRepositoryTest extends TestSupport {

	@Autowired
	private CareUnitRepository repo;
	@Autowired
	private CountyCouncilRepository ccRepo;

	@Test
	@Transactional
	@Rollback
	public void testFindByHsaId() throws Exception {
		final CountyCouncilEntity cc = ccRepo.save(CountyCouncilEntity.newEntity("SLL"));
		final CareUnitEntity cu1 = CareUnitEntity.newEntity("hsa-123", cc);
		final CareUnitEntity cu2 = CareUnitEntity.newEntity("hsa-432", cc);
		cu2.setName("Vårdcentralen Jönköping");

		this.repo.save(cu1);
		this.repo.save(cu2);

		final CareUnitEntity res1 = this.repo.findByHsaId("hsa-123");
		assertNotNull(res1);
		assertEquals("hsa-123", res1.getHsaId());
		assertNull(res1.getName());

		final CareUnitEntity res2 = this.repo.findByHsaId("hsa-432");
		assertNotNull(res2);
		assertEquals("hsa-432", res2.getHsaId());
		assertEquals("Vårdcentralen Jönköping", res2.getName());
	}
}
