package org.callistasoftware.netcare.api.rest;

import org.callistasoftware.netcare.core.api.ServiceResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/ordination")
public class OrdinationApi {

	@RequestMapping(value="/{patient}/create", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public ServiceResult createOrdination() {
		return null;
	}
}
