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

import mvk.asb.common.base._1.MvkRequestHeaderType;
import mvk.asb.common.base._1.ResultCodeEnum;
import mvk.asb.sso.v100.core.HealthCareFacilityType;
import mvk.asb.sso.v100.core.SSOObjectType;
import mvk.asb.sso.v100.pushid.ExportsPushIdCertFacadePushIdInterfaceHttpService;
import mvk.asb.sso.v100.pushid.PushIdInterface;
import mvk.asb.sso.v100.pushidresponder.PushIdResponseType;
import mvk.asb.sso.v100.pushidresponder.PushIdType;

import org.callistasoftware.netcare.mvk.authentication.service.MvkTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MvkTokenServiceImpl implements MvkTokenService {
	
	private static final Logger log = LoggerFactory.getLogger(MvkTokenServiceImpl.class);
	
	@Value("${mvk.pushid.address}")
	private String endpointAddress;
	
	@Override
	public String createAuthenticationToken() {
		log.info("Getting MVK authentication token from url: {}", this.endpointAddress);
		
		final SSOObjectType sso = new SSOObjectType();
		sso.setSysId("MVK");
		sso.setAuthMethod("3");
		sso.setTokenTimeout("10000");
		sso.setCert("");
		
		final HealthCareFacilityType hcf = new HealthCareFacilityType();
		hcf.setHealthCareFacilityId("hsa-test");
		hcf.setHealthCareFacilityName("jkpg");
		hcf.setResourceId("");
		hcf.setResourceName("");
		
		sso.setHealthCareFacility(hcf);
		
		final PushIdType pushId = new PushIdType();
		pushId.setTokenSize("12");
		pushId.setSsoObject(sso);
		
		final PushIdResponseType response = this.getService(endpointAddress).pushId(new MvkRequestHeaderType(), pushId);
		
		if (response.getStatus().getResultCode().equals(ResultCodeEnum.ERROR)) {
			log.error("The response from MVK indicated errors...");
			log.error("Error message: {}", response.getError().getErrorMessage());
			log.error("Error cause: {}", response.getError().getCauseErrorMessage());
			
			throw new RuntimeException("Error while exchanging information with MVK. Please see error log.");
		}
		
		log.debug("Got response from MVK. Token is: {}", response.getGuid());
		
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
