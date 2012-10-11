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

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>

<mvk:page>
	<mvk:header title="Netcare 2.0" resourcePath="/netcare/resources" contextPath="${pageContext.request.contextPath}">
		<link rel="stylesheet" href="<c:url value="/css/netcare.css" />" />
		<netcare:js />
		<script type="text/javascript">
			$(function() {
				
				var _support = new NC.Support();
				
				var _ra
				var msgs;
				_support.loadMessages('report.reject,healthplan.icons.result,healthplan.icons.edit,activity.reported.none,comments.sendComment', function(messages) {
					msgs = messages;
					_ra = new NC.ReportedActivities(msgs);
				});
				
				_ra.loadData('latest', function(data) {
					
					_ra.loadUI(data, function(row) {
						NC.log('Appending row');
						$('#activity-table tbody').append(row);	
					},
					function(data) {
						$('#commentActivity').modal('show');
						$('#commentActivity a.btn-info').click(function(e) {
							e.preventDefault();
							
							var val = $('#commentActivity input[name="comment"]').val();
							
							_ra.sendComment(data.id, val, function() {
								$('#commentActivity input[name="comment"]').val('');
								$('#commentActivity').modal('hide');
								
								$('#commentActivity a.btn-info').unbind('click');
							});
						});
					});
				}, 
				function() {
					$('#activity-table').hide();
					$('#list-empty').html(msgs['activity.reported.none']).show();
				});
				
			});
		</script>
	</mvk:header>
	<mvk:body>
		<mvk:pageHeader title="Min hälsoplan - Profil" loggedInUser="Testar Test" loggedInAsText="Inloggad som : "
			logoutUrl="/netcare/security/logout" logoutText="Logga ut" />

		<mvk:pageContent>
			<mvk:leftMenu>
				<c:url value="/netcare/admin/home" var="start" />
				<c:url value="/netcare/admin/patients" var="patients" />
				<c:url value="/netcare/admin/activitytypes" var="activitytypes" />
				<c:url value="/netcare/admin/categories" var="categories" />

				<mvk:menuItem label="Startsida" url="${start}" />
				<mvk:menuItem active="true" label="Patienter" url="${patients}" />
				<mvk:menuItem label="Skapa ny aktivitet" url="${activitytypes}" />
				<mvk:menuItem label="Skapa ny aktivitetskategori" url="${categories}" />
			</mvk:leftMenu>
		<mvk:content title="Genomförda aktiviteter">
		<netcare:content>
			<netcare:list-content descriptionCode="activity.reported.desc2" titleCode="activity.reported.title">
				<netcare:table id="activity-table">
					<thead>
						<tr>
							<th><spring:message code="activity.reported.patient" /></th>
							<th><spring:message code="activity.reported.type" /></th>
							<th><spring:message code="activity.reported.healthplan" /></th>
							<th><spring:message code="activity.reported.value" /></th>
							<th><spring:message code="activity.reported.when" /></th>
							<th>&nbsp;</th>
						</tr>
					</thead>
					<tbody></tbody>
				</netcare:table>
				
				<netcare:modal confirmCode="comments.sendComment" titleCode="comments.comment" id="commentActivity">
					<input type="text" name="comment" class="xlarge" />
				</netcare:modal>
			</netcare:list-content>
		</netcare:content>
		</mvk:content>
		</mvk:pageContent>
	</mvk:body>	
</mvk:page>
