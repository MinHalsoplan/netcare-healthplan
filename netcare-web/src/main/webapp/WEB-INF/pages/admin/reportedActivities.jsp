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
						$('#commentActivity a.btn-primary').click(function(e) {
							e.preventDefault();
							
							var val = $('#commentActivity input[name="comment"]').val();
							
							_ra.sendComment(data.id, val, function() {
								$('#commentActivity input[name="comment"]').val('');
								$('#commentActivity').modal('hide');
								
								$('#commentActivity a.btn-primary').unbind('click');
							});
						});
					});
				}, 
				function() {
					$('#activity-table').hide();
					$('#list-empty').append($('<p>').html(msgs['activity.reported.none'])).show();
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
		
	</netcare:body>	
</netcare:page>