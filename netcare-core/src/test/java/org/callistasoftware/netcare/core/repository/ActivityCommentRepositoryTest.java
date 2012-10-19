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

import java.util.List;

import org.callistasoftware.netcare.core.support.TestSupport;
import org.callistasoftware.netcare.model.entity.ActivityCommentEntity;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.ScheduledActivityEntity;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class ActivityCommentRepositoryTest extends TestSupport {

	@Autowired
	private ActivityCommentRepository repo;

	@Autowired
	private CareUnitRepository cuRepo;

	@Autowired
	private CareActorRepository careActorRepo;

	@Test
	@Transactional
	@Rollback(true)
	public void testInsertFind() throws Exception {

		final CareUnitEntity cu = this.cuRepo.save(CareUnitEntity.newEntity("hsa-unit"));

		final CareActorEntity ca = this.careActorRepo.save(CareActorEntity.newEntity("Marcus", "", "hsa", cu));

		final ScheduledActivityEntity ent = Mockito.mock(ScheduledActivityEntity.class);
		Mockito.when(ent.getId()).thenReturn(1L);

		final ActivityCommentEntity comment = ActivityCommentEntity.newEntity("Duktigt.", ca, ent);
		final ActivityCommentEntity saved = this.repo.save(comment);
		assertNotNull(saved);

		assertNotNull(saved.getId());
		assertNotNull(saved.getCommentedAt());
		assertEquals(ca, saved.getCommentedBy());
		assertEquals("Duktigt.", saved.getComment());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testFindComments() {

		final CareUnitEntity cu = this.cuRepo.save(CareUnitEntity.newEntity("hsa-unit"));
		final CareActorEntity ca = this.careActorRepo.save(CareActorEntity.newEntity("Marcus", "", "hsa", cu));

		List<ActivityCommentEntity> replies = this.repo.findRepliesForCareActor(ca, ca.getCareUnit());
		assertNotNull(replies);
		assertEquals(0, replies.size());

	}
}
