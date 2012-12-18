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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.callistasoftware.netcare.core.api.ActivityComment;
import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.CareActorBaseView;
import org.callistasoftware.netcare.core.api.CareUnit;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.ReportingValues;
import org.callistasoftware.netcare.core.api.ScheduledActivity;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.HealthPlanImpl;
import org.callistasoftware.netcare.core.api.statistics.HealthPlanStatistics;
import org.callistasoftware.netcare.core.api.util.DateUtil;
import org.callistasoftware.netcare.core.spi.HealthPlanService;
import org.callistasoftware.netcare.core.spi.ReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/healthplans")
public class HealthPlanApi extends ApiSupport {

	private static final Logger log = LoggerFactory.getLogger(HealthPlanApi.class);
	
	@Autowired 
	private HealthPlanService service;
	
	@Autowired
	private ReportingService reportingService;
	
	@RequestMapping(value="", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public ServiceResult<HealthPlan> createHealthPlan(@RequestBody final HealthPlanImpl dto, final Authentication auth) {
		this.logAccess("create", "health plan");
		return this.service.createNewHealthPlan(dto, (CareActorBaseView) auth.getPrincipal(), dto.getPatient().getId());
	}
	
	@RequestMapping(value="", method=RequestMethod.GET)
	@ResponseBody
	public ServiceResult<HealthPlan[]> listHealthPlans(@RequestParam(value="patient") final Long patient, final Authentication auth) {
		this.logAccess("list", "healthplan");
		final ServiceResult<HealthPlan[]> ordinations = this.service.loadHealthPlansForPatient(patient);
		
		log.debug("Found {} for patient {}", ordinations.getData().length, patient);
		return ordinations;
	}
	
	@RequestMapping(value="/{healthPlan}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<HealthPlan> loadHealthPlan(@PathVariable(value="healthPlan") final Long healthPlan) {
		this.logAccess("load", "health plan");
		return this.service.loadHealthPlan(healthPlan);
	}
	
    @RequestMapping(value="/{healthPlan}", method=RequestMethod.DELETE, produces="application/json")
	@ResponseBody
	public ServiceResult<HealthPlan> deleteHealthPlan(@PathVariable(value="healthPlan") final Long healthPlan) {
		this.logAccess("delete", "health plan");
		return this.service.deleteHealthPlan(healthPlan);
	}
    
    @RequestMapping(value="/{healthPlan}/renew", method=RequestMethod.POST, produces="application/json")
 	@ResponseBody
 	public ServiceResult<HealthPlan> healthPlanRenewal(@PathVariable(value="healthPlan") final Long healthPlan) {
 		this.logAccess("renewal", "health plan");
 		return this.service.healthPlanRenewal(healthPlan, false);
 	}
    
    @RequestMapping(value="/{healthPlan}/stopAutoRenewal", method=RequestMethod.POST, produces="application/json")
 	@ResponseBody
 	public ServiceResult<HealthPlan> stopHealthPlanAutoRenewal(@PathVariable(value="healthPlan") final Long healthPlan) {
 		this.logAccess("renewal", "health plan");
 		return this.service.healthPlanRenewal(healthPlan, true);
 	}
	
	@RequestMapping(value="/activity/reported/{patient}/comments", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ActivityComment[]> loadCommentsForPatient(@PathVariable(value="patient") final Long patient) {
		this.logAccess("load", "comments");
		return this.service.loadCommentsForPatient();
	}
	
	@RequestMapping(value="/activity/reported/latest", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ScheduledActivity[]> loadLatestReportedActivities() {
		this.logAccess("load", "reported activities");
		
		long n = System.currentTimeMillis();
		// three
		final Date start = new Date(n - 3*DateUtil.MILLIS_PER_DAY);
		final Date end = new Date(n);
		final CareUnit unit = ((CareActorBaseView)this.getUser()).getCareUnit();
		return this.service.loadLatestReportedForAllPatients(unit, start, end);
	}
	
	@RequestMapping(value = "/activity/reported/filter", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ServiceResult<ScheduledActivity[]> filterReportedActivities(
			@RequestParam(value = "personnummer") final String personnummer,
			@RequestParam(value = "dateFrom") final String dateFrom, @RequestParam(value = "dateTo") final String dateTo) {
		this.logAccess("filter", "reported activities");

		Date start = null;
		Date end = null;

		try {
			if (StringUtils.hasText(dateFrom)) {
				start = new SimpleDateFormat("yyyyMMdd").parse(dateFrom);
			} else {
				start = new Date(System.currentTimeMillis() - 3 * DateUtil.MILLIS_PER_DAY);
			}
			if (StringUtils.hasText(dateTo)) {
				end = new SimpleDateFormat("yyyyMMdd").parse(dateTo);
			} else {
				end = new Date();
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		if (StringUtils.hasText(personnummer) && !isPersonnummer(personnummer)) {
			throw new RuntimeException("Personnummer har fel format");
		}

		final CareUnit unit = ((CareActorBaseView) this.getUser()).getCareUnit();
		return this.service.filterReportedActivities(unit, personnummer, start, end);
	}
	
	protected boolean isPersonnummer(String personnummer) {
		//TODO Validate personnummer
		return true;
	}

	@RequestMapping(value="/activity/reported/all", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ScheduledActivity[]> loadAllReportedActivities() {
		this.logAccess("load", "reported activities");
		return this.service.loadLatestReportedForAllPatients(((CareActorBaseView)this.getUser()).getCareUnit(), null, null);
	}
	
    @RequestMapping(value="/activity/reported/comment/{comment}/reply", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public ServiceResult<ActivityComment> sendCommentReply(@PathVariable(value="comment") final Long comment, @RequestParam(value="reply") final String reply) {
		this.logAccess("reply", "comment");
		return this.service.replyToComment(comment, reply);
	}
	
	@RequestMapping(value="/activity/{activity}/load", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ScheduledActivity> loadScheduledActivity(@PathVariable(value="activity") final Long activity) {
		this.logAccess("load", "scheduled activity");
		return this.service.loadScheduledActivity(activity);
	}
	
    @RequestMapping(value="/activity/{activity}/comment", produces="application/json", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<ScheduledActivity> commentActivity(@PathVariable(value="activity") final Long activity, @RequestParam(value="comment") final String comment) {
		this.logAccess("comment", "activity");
		log.debug("Comment is: {}", comment);
		return this.service.commentOnPerformedActivity(activity, comment);
	}
	
    @RequestMapping(value="/activity/{activity}/like", produces="application/json", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<ScheduledActivity> likeActivity(@PathVariable(value="activity") final Long activity, @RequestParam(value="like") final boolean like) {
		this.logAccess("like", "activity");
		return this.service.likePerformedActivity(activity, like);
	}
	
    @RequestMapping(value="/activity/{activity}/read", produces="application/json", method=RequestMethod.POST)
	@ResponseBody
	public ServiceResult<ScheduledActivity> markActivityAsRead(@PathVariable(value="activity") final Long activity, @RequestParam(value="read") final boolean hasBeenRead) {
		this.logAccess("markAsRead", "activity");
		return this.service.markPerformedActivityAsRead(activity, hasBeenRead);
	}
	
	@RequestMapping(value="/activity/reported/comments/newreplies", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ActivityComment[]> loadReplies() {
		this.logAccess("list", "replies");
		return this.service.loadRepliesForCareActor();
	}
	
    @RequestMapping(value="/activity/reported/comments/{comment}/delete", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public ServiceResult<ActivityComment> deleteComment(@PathVariable(value="comment") final Long comment) {
		this.logAccess("delete", "comment");
		return this.service.deleteComment(comment);
	}
	
	@RequestMapping(value="/{healthPlanId}/activity/list", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<ActivityDefinition[]> loadActivityDefinitions(@PathVariable(value="healthPlanId") final Long healthPlan) {
		this.logAccess("list", "activity definitions");
		return this.service.loadActivitiesForHealthPlan(healthPlan);
	}
	
	@RequestMapping(value="/{healthPlanId}/statistics", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ServiceResult<HealthPlanStatistics> loadReportedActivitites(@PathVariable(value="healthPlanId") final Long healthPlanId) {
		this.logAccess("list", "scheduled activities");
		return this.service.getStatisticsForHealthPlan(healthPlanId);
	}

	@RequestMapping(value = "/activity/item/{itemDefId}/statistics", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ServiceResult<ReportingValues> getActivitites(@PathVariable(value = "itemDefId") final Long itemDefId) {
		this.logAccess("list", "statistics");
		return this.reportingService.getAllValuesByActivityItemDefId(getUser(), itemDefId);
	}
}
