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

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			$(function() {
				
				var _support = new NC.Support();
				
				var msgs;
				var _ra;
				_support.loadMessages('report.reject, activity.reported.none', function(messages) {
					msgs = messages;
					_ra = new NC.ReportedActivities(msgs);
				});
				
				_ra.loadData('all', function(data) {
					_ra.loadUI(data, function(row) {
						$('#activity-table tbody').append(row);
					},
					function() {
						$('#list-empty').append('<p>').html(msgs['activity.reported.none']).show();
						$('#activity-table').hide();
					});
				});
				
			});
		</script>
	</netcare:header>
	<netcare:body>
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
						</tr>
					</thead>
					<tbody></tbody>
				</netcare:table>
			</netcare:list-content>
		</netcare:content>
		
	</netcare:body>	
</netcare:page>