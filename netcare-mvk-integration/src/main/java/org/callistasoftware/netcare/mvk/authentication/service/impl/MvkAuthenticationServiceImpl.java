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
package org.callistasoftware.netcare.mvk.authentication.service.impl;

import java.net.URL;

import javax.xml.ws.BindingProvider;

import mvk.asb.interaction.validateidresponder._1._0.ValidateIdResponseType;
import mvk.asb.mvksso.validateidresponder._1.ValidateIDType;
import mvk.asb.mvksso.validatetoken._1._0.mvkasb10.ValidateToken;
import mvk.asb.mvksso.validatetoken._1._0.mvkasb10.ValidateTokenValidateTokenHttpService;

import org.callistasoftware.netcare.mvk.authentication.service.MvkAuthenticationService;
import org.callistasoftware.netcare.mvk.authentication.service.api.AuthenticationRequest;
import org.callistasoftware.netcare.mvk.authentication.service.api.AuthenticationResult;
import org.callistasoftware.netcare.mvk.authentication.service.api.impl.AuthenticationResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MvkAuthenticationServiceImpl implements MvkAuthenticationService {
	
	private static final Logger log = LoggerFactory.getLogger(MvkAuthenticationServiceImpl.class);
	
	@Value("${mvk.validatetoken.address}")
	private String endpointAddress;
	
	@Override
	public AuthenticationResult authenticate(AuthenticationRequest request) {
		log.info("Validating token {} by calling MVK validate token service at: {}", request.getAuthenticationToken(), this.endpointAddress);
		final ValidateIDType req = new ValidateIDType();
		req.setGuid(request.getAuthenticationToken());
		
		final ValidateIdResponseType response = this.getService(this.endpointAddress).validate(req);
		
		if (response.getStatusCode().equals("ERROR")) {
			log.error("The response from MVK indicated errors...");
			log.error("Error message: {}", response.getStatusText());
			
			return null;
		}
		
		return AuthenticationResultImpl.createFromResponse(response);
	}
	
	private ValidateToken getService(final String address) {
		
		try {
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			final URL wsdl = loader.getResource("schemas/ValidateID/wsdl/interactions/MvkAsb_ValidateID_ValidateToken.wsdl");
			
			final ValidateTokenValidateTokenHttpService service = new ValidateTokenValidateTokenHttpService(wsdl);
			final ValidateToken serviceInterface = service.getExportsValidateTokenValidateTokenHttpPort();
			
			((BindingProvider) serviceInterface).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
			
			return serviceInterface;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}	
	}
	
}
