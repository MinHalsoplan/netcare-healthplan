/**
 * Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>
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
package org.callistasoftware.netcare.web.controller;

import javax.servlet.http.HttpSession;

import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class ControllerSupport {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	protected Logger getLog() {
		return this.log;
	}
	
	protected UserBaseView getLoggedInUser() {
		return (UserBaseView) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	protected PatientBaseView getCurrentPatient(final HttpSession session) {
		return (PatientBaseView) session.getAttribute("currentPatient");
	}
}
