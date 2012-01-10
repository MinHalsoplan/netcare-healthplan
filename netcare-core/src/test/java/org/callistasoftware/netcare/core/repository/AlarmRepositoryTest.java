package org.callistasoftware.netcare.core.repository;

import static org.junit.Assert.assertEquals;

import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.AlarmCause;
import org.callistasoftware.netcare.model.entity.AlarmEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class AlarmRepositoryTest extends TestSupport {

	@Autowired
	private AlarmRepository repo;
	
	
	@Test
	@Transactional
	@Rollback(true)
	public void findByReportedTimeIsNotNull() {
		AlarmEntity e = AlarmEntity.newEntity(AlarmCause.PLAN_EXPIRES, "hsa-123", 42L);
		
		e = repo.save(e);
		
		assertEquals(0, repo.findByResolvedTimeIsNotNull().size());

		e.resolve("peter");
		
		e = repo.save(e);
		repo.flush();
		
		assertEquals(1, repo.findByResolvedTimeIsNotNull().size());
	}
}
