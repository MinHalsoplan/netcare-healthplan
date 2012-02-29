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
package org.callistasoftware.spring.mvk.authentication.service.impl;

import java.net.URL;

import javax.xml.ws.BindingProvider;

import mvk.asb.common.base._1.MvkRequestHeaderType;
import mvk.asb.sso.v100.pushid.ExportsPushIdCertFacadePushIdInterfaceHttpService;
import mvk.asb.sso.v100.pushid.PushIdInterface;
import mvk.asb.sso.v100.pushidresponder.PushIdResponseType;
import mvk.asb.sso.v100.pushidresponder.PushIdType;

import org.callistasoftware.spring.mvk.authentication.service.MvkTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MvkTokenServiceImpl implements MvkTokenService {
	
	@Value("${mvk.pushid.address}")
	private String endpointAddress;
	
	@Override
	public String createAuthenticationToken() {
		final PushIdResponseType response = this.getService(endpointAddress).pushId(new MvkRequestHeaderType(), new PushIdType());
		return response.getGuid();
	}
	
	private PushIdInterface getService(final String address) {
		
		try {
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			final URL wsdl = loader.getResource("schemas/SSO/interactions/pushid/Mvk_SSOService_CertAuth_PushId_Cert_Facade.wsdl");
			
			final ExportsPushIdCertFacadePushIdInterfaceHttpService service = new ExportsPushIdCertFacadePushIdInterfaceHttpService(wsdl);
			final PushIdInterface serviceInterface = service.getExportsPushIdCertFacadePushIdInterfaceHttpPort();
			
			((BindingProvider) serviceInterface).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
			return serviceInterface;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}	
	}

}
