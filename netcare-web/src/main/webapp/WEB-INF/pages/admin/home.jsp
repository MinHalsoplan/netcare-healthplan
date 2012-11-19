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
					star : '<spring:message code="activity.reported.star" />',
					starred : '<spring:message code="activity.reported.starred" />'
				};
				
				NC_MODULE.REPLIES.init();
				NC_MODULE.REPORTED_ACTIVITIES.init(msgs);
				NC_MODULE.ALARM.init();
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Min hälsoplan - Profil">
		<section id="dashboard">
			<p>
				<spring:message code="admin.home.description" />
			</p>
			<p>
				<span class="label label-important"><spring:message code="important" /></span>
				<spring:message code="admin.home.important" />
			</p>
			
		</section>
		
		<section id="replies">
			<h2><spring:message code="comments.replies" /></h2>
			<p>
				<span class="label label-info"><spring:message code="information" /></span>
				<spring:message code="comments.repliesDescription" />
			</p>
			<mvk:touch-list id="repliesContainer">
		
			</mvk:touch-list>
		</section>
		
		<br />
		
		<section id="aktiviteter">
			<h2><spring:message code="activity.reported.title" /></h2>
			<p>
				<span class="label label-info"><spring:message code="information" /></span>
				<spring:message code="activity.reported.desc" />
			</p>
			
			<netcare:block-message type="warning">
				<a href="<spring:url value="/netcare/admin/activity/list" />"><spring:message code="activity.reported.list" /></a>
			</netcare:block-message>
			
			<div id="reportedActivities">
				<mvk:touch-list id="latestActivitiesContainer">
		
				</mvk:touch-list>
			</div>
			
		
		</section>
		
		<br />
		
		<section id="alarms">
			<h2><spring:message code="alarm.title" /></h2>
			<p>
				<span class="label label-info"><spring:message code="information" /></span>
				<spring:message code="alarm.desc" />
			</p>
			<mvk:touch-list id="alarmContainer">
		
			</mvk:touch-list>
		</section>
	</hp:viewBody>
</hp:view>