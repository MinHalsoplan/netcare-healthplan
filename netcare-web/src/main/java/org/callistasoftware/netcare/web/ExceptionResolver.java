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
package org.callistasoftware.netcare.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.callistasoftware.netcare.core.spi.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class ExceptionResolver extends SimpleMappingExceptionResolver {

	private static final Logger log = LoggerFactory.getLogger(ExceptionResolver.class);
	
	@Autowired
	private EmailNotificationService emailService;
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
	
		log.info("Resolving exception. Deliver mail to support about the exception.");
		final StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		log.error("=== END OF ERROR ===");
		
		log.error("Internal error", ex);
		this.emailService.sendSupportEmail(sw.toString());
		
		final ModelAndView mav = super.resolveException(request, response, handler, ex);
		mav.setViewName("error/error");
		
		return mav;	
	}
}
