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
package org.callistasoftware.netcare.web.listener;

import javax.servlet.ServletContextEvent;

import org.callistasoftware.netcare.core.entity.CareGiverEntity;
import org.callistasoftware.netcare.core.entity.PatientEntity;
import org.callistasoftware.netcare.core.repository.CareGiverRepository;
import org.callistasoftware.netcare.core.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ApplicationListener extends ContextLoaderListener {

	private static final Logger log = LoggerFactory.getLogger(ApplicationListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		
		log.info("======== NETCARE STARTED ========");
		
		final WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		final PatientRepository bean = wc.getBean(PatientRepository.class);
		final CareGiverRepository cgRepo = wc.getBean(CareGiverRepository.class);
		
		final CareGiverEntity cg1 = CareGiverEntity.newEntity("Dr. Test Testgren", "hsa-id-1234");
		
		cgRepo.save(cg1);

		final CareGiverEntity cg = CareGiverEntity.newEntity("Doctor Hook", "12345-67");
		cgRepo.save(cg);
		
		cgRepo.flush();
		
		final PatientEntity p1 = PatientEntity.newEntity("Marcus Krantz", "198205134656", cg1);
		bean.save(p1);
		
		final PatientEntity p2 = PatientEntity.newEntity("Peter Larsson", "191212121212", cg1);
		bean.save(p2);
		
		final PatientEntity p3 = PatientEntity.newEntity("Arne Andersson", "123456789003", cg1);
		p3.getProperties().put("testKey", "[Test Value]");
		bean.save(p3);
		
		final PatientEntity p4 = PatientEntity.newEntity("Anders Arnesson", "123456789004", cg1);
		bean.save(p4);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
		
		log.info("======== NETCARE STOPPED ========");
	}
}
