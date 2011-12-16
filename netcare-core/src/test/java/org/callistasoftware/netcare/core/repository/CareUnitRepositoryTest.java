package org.callistasoftware.netcare.core.repository;

import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/netcare-config.xml")
public class CareUnitRepositoryTest {

	@Autowired
	private CareUnitRepository repo;
	
	@Test
	@Transactional
	@Rollback
	public void testFindByHsaId() throws Exception {
		final CareUnitEntity cu1 = CareUnitEntity.newEntity("hsa-123");
		final CareUnitEntity cu2 = CareUnitEntity.newEntity("hsa-432");
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
