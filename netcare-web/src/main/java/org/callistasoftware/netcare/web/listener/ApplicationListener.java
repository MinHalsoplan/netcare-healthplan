/**
 * Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>
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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.callistasoftware.netcare.web.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

public class ApplicationListener extends ContextLoaderListener {

	private static final Logger log = LoggerFactory.getLogger(ApplicationListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		
		final ServletContext sc = event.getServletContext();
		
		if (WebUtil.isProfileActive(sc, "ios-testdata")) {
			WebUtil.setupIosTestData(sc);			
		} else if (WebUtil.isProfileActive(sc, "db-embedded") || (WebUtil.isProfileActive(sc, "test") && WebUtil.isProfileActive(sc, "db-psql"))) {
			log.debug("Setting up application test data...");
			WebUtil.setupTestData(sc);
			log.debug("Test data setup completed.");
		}
		
		if (WebUtil.isProfileActive(sc, "prod")) {
			WebUtil.setProdData(sc);
		}
		
		log.info("======== NETCARE STARTED ========");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
		log.info("======== NETCARE STOPPED ========");
	}
}
