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
package org.callistasoftware.netcare.api.rest;

import org.callistasoftware.netcare.core.api.ActivityComment;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.callistasoftware.netcare.model.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/comments", produces=MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize(RoleEntity.PATIENT)
public class CommentsApi extends ApiSupport {

	@Autowired private HealthPlanService service;
	
	@RequestMapping(value="", method=RequestMethod.GET)
	@ResponseBody
	public ServiceResult<ActivityComment[]> list() {
		logAccess("list", "comments");
		return service.loadCommentsForPatient();
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public ServiceResult<ActivityComment> remove(@PathVariable(value="id") final Long id) {
		logAccess("remove", "comment");
		return service.deleteComment(id);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<ActivityComment> update(@RequestBody final ActivityComment comment) {
		logAccess("update", "comment");
		return service.replyToComment(comment.getId(), comment.getReply());
	}
	
}
