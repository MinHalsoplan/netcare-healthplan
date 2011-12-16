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
package org.callistasoftware.netcare.core.repository;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

import org.callistasoftware.netcare.model.entity.CareGiverDelegationEntity;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/netcare-config.xml")
public class CareGiverDelegationRepositoryTest {
	@Autowired
	private CareGiverDelegationRepository dr;
	@Autowired
	private CareGiverRepository cr;
	
	/**
	 * 2 doctors delegates to the same nurse.
	 * 
	 * @throws Exception
	 */
	@Test
	@Transactional
	@Rollback(true)
	public void testInsertFind() throws Exception {
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		CareGiverEntity cd1 =  CareGiverEntity.newEntity("doctor peter", "2-14", cu);
		cr.save(cd1);

		CareGiverEntity cd2 =  CareGiverEntity.newEntity("doctor mikael", "32-14", cu);
		cr.save(cd2);

		CareGiverEntity cn =  CareGiverEntity.newEntity("nurse emma", "63-14", cu);
		cr.save(cn);
		cr.flush();
		
		CareGiverDelegationEntity de1 = CareGiverDelegationEntity.newEntity(cd1, cn, new Date(), new Date());
		dr.save(de1);
		
		CareGiverDelegationEntity de2 = CareGiverDelegationEntity.newEntity(cd2, cn, new Date(), new Date());
		dr.save(de2);
		dr.flush();
		
		List<CareGiverDelegationEntity> list = dr.findByCareGiverDelegatee(cn);
		assertEquals(2, list.size());
		
		assertEquals(cn, list.get(0).getCareGiverDelegatee());
	}
}
