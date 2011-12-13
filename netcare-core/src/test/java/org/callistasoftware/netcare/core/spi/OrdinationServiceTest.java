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
package org.callistasoftware.netcare.core.spi;

import static org.junit.Assert.assertEquals;

import org.callistasoftware.netcare.core.api.Ordination;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.CareGiverBaseViewImpl;
import org.callistasoftware.netcare.core.api.impl.OrdinationImpl;
import org.callistasoftware.netcare.core.entity.CareGiverEntity;
import org.callistasoftware.netcare.core.entity.DurationUnit;
import org.callistasoftware.netcare.core.entity.PatientEntity;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/netcare-config.xml")
public class OrdinationServiceTest {
	
	@Autowired
	private CareGiverRepository cgRepo;
	@Autowired
	private PatientRepository patientRepo;
	
	@Autowired
	private OrdinationService service;

	@Test
	@Transactional
	@Rollback(true)
	public void testCreateOrdination() throws Exception {
		
		final CareGiverEntity cg = CareGiverEntity.newEntity("Test Testgren", "hsa-123-id");
		this.cgRepo.save(cg);
		
		final CareGiverBaseViewImpl cgDto = new CareGiverBaseViewImpl();
		cgDto.setHsaId("hsa-123-id");
		
		final OrdinationImpl o = new OrdinationImpl();
		o.setName("Test");
		o.setStartDate("2011-12-12");
		o.setDuration(12);
		o.setDurationUnit(DurationUnit.WEEK.getCode());
		
		final PatientEntity patient = PatientEntity.newEntity("Peter Larsson", "611028", cg);
		patientRepo.save(patient);
		
		final ServiceResult<Ordination> saved = this.service.createNewOrdination(o, cgDto, patient.getId());
		
		assertEquals(o.getName(), saved.getData().getName());
		assertEquals(o.getStartDate(), saved.getData().getStartDate());
		assertEquals(o.getDuration(), saved.getData().getDuration());
		assertEquals(o.getDurationUnit(), saved.getData().getDurationUnit());
	}

}
