<%--

    Copyright (C) 2011,2012 Callista Enterprise AB <info@callistaenterprise.se>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<hp:view>
	<hp:viewHeader>
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				var msgs = {
					like : '<spring:message code="activity.reported.like" />',
					liked : '<spring:message code="activity.reported.liked" />',
					markasread : '<spring:message code="activity.reported.markasread" />',
					markedasread : '<spring:message code="activity.reported.markedasread" />'
				};
				
				NC_MODULE.REPLIES.init();
				NC_MODULE.REPORTED_ACTIVITIES.init(msgs);
				NC_MODULE.ALARM.init();
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Startsida" plain="true">
		<section id="alarms">
			<spring:message code="alarm.title" var="alarms" />
			<mvk:heading title="${alarms}">
				<spring:message code="alarm.desc" />
			</mvk:heading>
			<mvk:touch-list id="alarmContainer"></mvk:touch-list>
		</section>

		<br />
		
		<section id="replies">
			<spring:message code="comments.replies" var="replies"/>
			<mvk:heading title="${replies}">
				<spring:message code="comments.repliesDescription" />
			</mvk:heading>
			<mvk:touch-list id="repliesContainer"></mvk:touch-list>
		</section>
		
		<br />
		
		<section id="aktiviteter">
			<spring:message code="activity.reported.title" var="reportedTitle"/>
			<mvk:heading title="${reportedTitle}">
				<spring:message code="activity.reported.desc" />
			</mvk:heading>
			
			<a href="<spring:url value="/netcare/admin/activity/list" />" class="btn"><spring:message code="activity.reported.list" /></a>
			
			<section id="report">
				<div class="sectionLoader" style="display: none;">
					<img src="<c:url value="/netcare/resources/images/loaders/ajax-loader-medium.gif" />" />
					<span class="loaderMessage"></span>
				</div>
				<div id="reportContainer" style="display: none;">
					<mvk:touch-list id="latestActivitiesContainer"></mvk:touch-list>
					
					<div id="riPagination" class="pagination pagination-centered">
						<ul></ul>
					</div>
				</div>
			</section>
		</section>
		
	</hp:viewBody>
</hp:view>