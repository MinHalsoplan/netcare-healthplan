package org.callistasoftware.netcare.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/mobile")
public class MobileController extends ControllerSupport {

	@RequestMapping(value="/start", method=RequestMethod.GET)
	public String displayMobileStartPage() {
		return "mobile/start";
	}
}
