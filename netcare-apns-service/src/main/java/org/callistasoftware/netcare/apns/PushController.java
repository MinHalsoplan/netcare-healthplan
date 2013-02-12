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
