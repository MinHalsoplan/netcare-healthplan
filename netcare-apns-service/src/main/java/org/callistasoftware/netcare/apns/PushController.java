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
package org.callistasoftware.netcare.apns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PushController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PushService service;

	@RequestMapping(value = "/alert", method = RequestMethod.POST)
	@ResponseBody
	public String alert(@RequestParam final String token, @RequestParam final String message) {
		log.debug("Received push request. Token: " + token + " Message: " + message);
		try {
			service.sendPushAlert(new PushMessage(token, message));
		} catch (RuntimeException e) {
			log.error("Error occured while trying to send push message.", e);
			throw new RuntimeException("Could not send push message.");
		}
		return "success";
	}

}
