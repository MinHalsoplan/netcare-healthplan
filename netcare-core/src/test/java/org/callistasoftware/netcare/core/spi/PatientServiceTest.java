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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.callistasoftware.netcare.model.entity.CareGiverEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/netcare-config.xml")
public class PatientServiceTest {

	@Autowired
	private PatientRepository patientRepository;
	@Autowired
	private CareGiverRepository cgRepo;

	
	@Autowired
	private PatientService service;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testFindPatients() {
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67", cu);
		cgRepo.save(cg);
		
		final PatientEntity p1 = PatientEntity.newEntity("Marcus", "123456789001", cg);
		final PatientEntity p2 = PatientEntity.newEntity("Peter", "123456789002", cg);
		
		this.patientRepository.save(p1);
		this.patientRepository.save(p2);
		
		ServiceResult<PatientBaseView[]> result = this.service.findPatients("Mar");
		assertTrue(result.isSuccess());
		assertEquals(1, result.getData().length);
		
		final PatientBaseView p = result.getData()[0];
		assertEquals(p1.getName(), p.getName());
		assertEquals(p1.getCivicRegistrationNumber(), p.getCivicRegistrationNumber());
		assertEquals(p1.getId(), p.getId());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testLoadPatient() throws Exception {
		final CareUnitEntity cu = CareUnitEntity.newEntity("cu");
		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67", cu);
		cgRepo.save(cg);
		
		final PatientEntity p1 = PatientEntity.newEntity("Marcus", "123456789004", cg);
		final PatientEntity saved = this.patientRepository.save(p1);
		
		final ServiceResult<PatientBaseView> bv = this.service.loadPatient(saved.getId());
		assertNotNull(bv);
		assertTrue(bv.isSuccess());
		assertNotNull(bv.getData());
		
		final PatientBaseView data = bv.getData();
		assertEquals(saved.getName(), data.getName());
		assertEquals(saved.getId(), data.getId());
		assertEquals(saved.getCivicRegistrationNumber(), data.getCivicRegistrationNumber());
	}
}
