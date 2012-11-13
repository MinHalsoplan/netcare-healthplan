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
package org.callistasoftware.netcare.core.support;

import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.impl.CareActorBaseViewImpl;
import org.callistasoftware.netcare.core.repository.CareActorRepository;
import org.callistasoftware.netcare.core.repository.CareUnitRepository;
import org.callistasoftware.netcare.core.repository.CountyCouncilRepository;
import org.callistasoftware.netcare.core.repository.MeasureUnitRepository;
import org.callistasoftware.netcare.core.repository.RoleRepository;
import org.callistasoftware.netcare.model.entity.CareActorEntity;
import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.callistasoftware.netcare.model.entity.CountyCouncilEntity;
import org.callistasoftware.netcare.model.entity.MeasureUnitEntity;
import org.callistasoftware.netcare.model.entity.RoleEntity;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/netcare-config.xml")
@ActiveProfiles(profiles={"db-embedded","test"}, inheritProfiles=true)
public abstract class TestSupport {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private CareActorRepository caRepo;
	
	@Autowired
	private CareUnitRepository cuRepo;
	
	@Autowired
	private CountyCouncilRepository ccRepo;
	
	@Autowired
	private MeasureUnitRepository measureUnitRepository;
	
	protected void runAs(final UserBaseView user) {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
	}
	
	protected final void authenticatedUser(final String hsaId
			, final String careUnitHsa
			, final String countyCouncil
			, final RoleEntity... roles) {
		
		CountyCouncilEntity councilEntity = getCountyCouncilRepository().findByName(countyCouncil);
		if (councilEntity == null) {
			councilEntity = ccRepo.saveAndFlush(CountyCouncilEntity.newEntity(countyCouncil));
		}
		
		CareUnitEntity unitEntity = getCareUnitRepository().findByHsaId(careUnitHsa);
		if (unitEntity == null) {
			unitEntity = cuRepo.saveAndFlush(CareUnitEntity.newEntity(careUnitHsa, councilEntity));
		}
		
		CareActorEntity entity = getCareActorRepository().findByHsaId(hsaId);
		if (entity == null) {
			entity = caRepo.saveAndFlush(CareActorEntity.newEntity("Test", "Torsksson", hsaId, unitEntity));
		}
		
		for (final RoleEntity r : roles) {
			entity.addRole(r);
		}
		
		final CareActorBaseView ca = CareActorBaseViewImpl.newFromEntity(entity);
		runAs(ca);
	}
	
	protected final MeasureUnitEntity newMeasureUnit(final String name, final String dn, final CountyCouncilEntity cce) {
		MeasureUnitEntity mue = getMeasureUnitRepository().findByDnOrderByDnAsc(dn);
		if (mue == null) {
			mue = getMeasureUnitRepository().saveAndFlush(MeasureUnitEntity.newEntity(name, dn));
		}
		
		return mue;
	}
	
	protected final CountyCouncilEntity newCountyCouncil(final String name) {
		CountyCouncilEntity cc = getCountyCouncilRepository().findByName(name);
		if (cc == null) {
			cc = getCountyCouncilRepository().saveAndFlush(CountyCouncilEntity.newEntity(name));
		}
		
		return cc;
	}
	
	protected final RoleEntity newRole(final String name) {
		RoleEntity r = getRoleRepository().findByDn(name);
		if (r == null) {
			r = getRoleRepository().saveAndFlush(RoleEntity.newRole(name));
		}
		
		return r;
	}
	
	protected final RoleRepository getRoleRepository() {
		return roleRepository;
	}
	
	protected final MeasureUnitRepository getMeasureUnitRepository() {
		return measureUnitRepository;
	}
	
	protected final CareActorRepository getCareActorRepository() {
		return this.caRepo;
	}
	
	protected final CareUnitRepository getCareUnitRepository() {
		return this.cuRepo;
	}
	
	protected final CountyCouncilRepository getCountyCouncilRepository() {
		return this.ccRepo;
	}
}
