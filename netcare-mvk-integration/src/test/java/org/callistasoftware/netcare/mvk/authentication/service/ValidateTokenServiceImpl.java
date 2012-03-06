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

import mvk.asb.interaction.validateidresponder._1._0.ValidateIdResponseType;
import mvk.asb.mvksso.ssoobjekttype._1._0.SSOObjectType;
import mvk.asb.mvksso.validateidresponder._1.ValidateIDType;
import mvk.asb.mvksso.validatetoken._1._0.mvkasb10.ValidateToken;

public class ValidateTokenServiceImpl implements ValidateToken {

	@Override
	public ValidateIdResponseType validate(ValidateIDType requestpayload) {
		final String guid = requestpayload.getGuid();
		
		final ValidateIdResponseType resp = new ValidateIdResponseType();
		resp.setStatusCode("OK");
		final SSOObjectType sso = new SSOObjectType();
		sso.setGuid(guid);
		sso.setUserType("INV");
		sso.setUserId("191212121212");
		
		resp.setSsoobject(sso);
		return resp;
	}

}
