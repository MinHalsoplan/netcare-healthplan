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
package org.callistasoftware.netcare.mvk.authentication.service;

import junit.framework.TestCase;

import org.callistasoftware.netcare.mvk.authentication.service.MvkAuthenticationService;
import org.callistasoftware.netcare.mvk.authentication.service.MvkTokenService;
import org.callistasoftware.netcare.mvk.authentication.service.api.AuthenticationRequest;
import org.callistasoftware.netcare.mvk.authentication.service.api.AuthenticationResult;
import org.callistasoftware.netcare.mvk.authentication.service.api.impl.AuthenticationRequestImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/mvk-test-config.xml")
@ActiveProfiles("qa")
public class WebServiceInteractionTest extends TestCase {
	
	@Autowired
	private MvkTokenService service;
	
	@Autowired
	private MvkAuthenticationService authService;
	
	@Test
	public void testWebServiceInteraction() throws Exception {
		final String token = this.service.createAuthenticationTokenForPatient("1234-mvk");
		assertNotNull(token);
		assertEquals("1234-mvk", token);
		
		final AuthenticationRequest req = AuthenticationRequestImpl.newAuthenticationRequest(token);
		final AuthenticationResult result = this.authService.authenticate(req);
		
		assertFalse(result.isCareGiver());
		assertEquals("191212121212", result.getUsername());
	}
}
