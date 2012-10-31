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
package org.callistasoftware.netcare.web.util;

import javax.servlet.ServletContext;

import org.callistasoftware.netcare.core.repository.CareActorRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.CountyCouncilRepository;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class TestDataHelper {

	private CareActorRepository careActorRepository;
	private CareUnitRepository careUnitRepository;
	private CountyCouncilRepository countyCouncilRepository;
	
	public TestDataHelper() {
		
	}
	
	public TestDataHelper(final ServletContext sc) {
		final WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(sc);
		setCareActorRepository(wc.getBean(CareActorRepository.class));
		setCareUnitRepository(wc.getBean(CareUnitRepository.class));
		setCountyCouncilRepository(wc.getBean(CountyCouncilRepository.class));
	}
	
	public void setCareActorRepository(CareActorRepository careActorRepository) {
		this.careActorRepository = careActorRepository;
	}
	
	public void setCareUnitRepository(CareUnitRepository careUnitRepository) {
		this.careUnitRepository = careUnitRepository;
	}
	
	public void setCountyCouncilRepository(
			CountyCouncilRepository countyCouncilRepository) {
		this.countyCouncilRepository = countyCouncilRepository;
	}
	
	public CareUnitRepository getCareUnitRepository() {
		return careUnitRepository;
	}
	
	public CareActorRepository getCareActorRepository() {
		return careActorRepository;
	}
	
	public CountyCouncilRepository getCountyCouncilRepository() {
		return countyCouncilRepository;
	}
	
	public CountyCouncilEntity newCountyCouncil(final String name) {
		return getCountyCouncilRepository().saveAndFlush(CountyCouncilEntity.newEntity(name));
	}
	
	public CareUnitEntity newCareUnit(final String hsaId, final String name, final CountyCouncilEntity countyCouncil) {
		final CareUnitEntity cu = CareUnitEntity.newEntity(hsaId, countyCouncil);
		cu.setName(name);
		getCareUnitRepository().saveAndFlush(cu);
		
		return cu;
	}
}
