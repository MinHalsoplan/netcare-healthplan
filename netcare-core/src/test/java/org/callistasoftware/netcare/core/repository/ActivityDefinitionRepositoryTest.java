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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.callistasoftware.netcare.core.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.core.entity.Frequency;
import org.callistasoftware.netcare.core.entity.FrequencyUnit;
import org.callistasoftware.netcare.core.entity.FrequencyValue;
import org.callistasoftware.netcare.core.entity.ScheduledActivityEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/netcare-config.xml")

public class ActivityDefinitionRepositoryTest {
	@Autowired
	private ActivityDefinitionRepository repo;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testInsertFind() throws Exception {
		ActivityDefinitionEntity entity = new ActivityDefinitionEntity();
		Frequency freq = new Frequency(FrequencyUnit.DAILY);
		FrequencyValue fval = new FrequencyValue();
		fval.setHour(10);
		fval.setMinute(0);
		freq.getValues().add(fval);
		entity.setFrequency(freq);
		ScheduledActivityEntity sa = new ScheduledActivityEntity();
		sa.setScheduledTime(new Date());
		entity.getScheduledActivities().add(sa);
		repo.save(entity);
		repo.flush();
		
		final List<ActivityDefinitionEntity> all = repo.findAll();
		assertNotNull(all);
		assertEquals(1, all.size());
		assertEquals(true, all.get(0).getFrequency().getValues().get(0).getHour() == 10);
	}
}
