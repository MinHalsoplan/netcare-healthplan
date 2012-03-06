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
				
				var util = new NC.Util();
				
				var name = "<c:out value="${sessionScope.currentPatient.name}" />";
				if (name.length != 0) {
					util.updateCurrentPatient(name);
				}
				
				var hp = new NC.HealthPlan();
				hp.loadLatestReportedActivities('reportedActivities');
				
				var alarms = new NC.Alarm();
				
				var loadAlarms = function() {
					alarms.loadAlarms(function(data) {
						
						if (data.data.length == 0) {
							$('#alarmContainer table').hide();
							$('#alarmContainer div').show();
						}
						
						$('#alarmContainer table tbody').empty();
						
						$.each(data.data, function(index, value) {
							NC.log("Processing " + value.id + "...");
							var tr = $('<tr>');
							var created = $('<td>' + value.createdTime + '</td>');
							var patient = $('<td>' + value.patient.name + '<br/>' + util.formatCnr(value.patient.civicRegistrationNumber)  + '</td>');
							var contact = $('<td>' + value.patient.phoneNumber + '</td>');
							var info =  value.cause.value;
							if (value.info != null) {
								info += '<br/>' + value.info;
							}
							var cause = $('<td>' + info + '</td>');
							
							var processIcon = util.createIcon('trash', '24', function() {
								NC.log("Resolving alarm...");
								alarms.resolve(value.id, loadAlarms);
							}, 'alarm.delete');
							
							var actionCol = $('<td>').css('text-align', 'right');
							
							actionCol.append(processIcon);
							
							var table = $('#alarmContainer table tbody');
							tr.append(patient);
							tr.append(contact);
							tr.append(cause);
							tr.append(created);
							tr.append(actionCol);
							
							table.append(tr);
							
							$('#alarmContainer div').hide();
							$('#alarmContainer table').show();
							
						});
					});
				};
				
				loadAlarms();
				
				//$('#commentActivity').modal();
				$('#commentActivity').bind('shown', function() {
					$('#commentActivity input[name="comment"]').focus();
				});
				
				var loadReplies = function() {
					hp.loadNewReplies(function(data) {
						if (data.data.length > 0) {
							$('#noReplyId').hide();
							$('#replyTableId').show();
						} else {
							$('#noReplyId').show();
							$('#replyTableId').hide();
						}
						
						$('#replyTableId tbody > tr').empty();
						
						$.each(data.data, function(index, value) {
							var tr = $('<tr>');
							tr.append($('<td>').css('font-style', 'italic').html(value.comment));
							tr.append($('<td>').css('font-style', 'italic').html(value.reply));
							tr.append($('<td>').html(value.repliedBy));
							tr.append($('<td>').html(value.activityName));
							
							var deleteIcon = util.createIcon('trash', 24, function() {
								hp.deleteComment(value.id, function(data) {
									loadReplies();
								});
							}, 'comments.delete');
							
							var actionCol = $('<td>').css('text-align', 'right');
							actionCol.append(deleteIcon);
							
							tr.append(actionCol);
							
							$('#replyTableId tbody').append(tr);
							
						});
						
					});
				};
				
				loadReplies();
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			
			<section id="dashboard">
				<h2>Start</h2>
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
				<div id="noReplyId" class="alert alert-info">
					<p><spring:message code="comments.noReplies" />
				</div>
				
				<netcare:table id="replyTableId">
					<thead>
						<tr>
							<th><spring:message code="comments.comment" /></th>
							<th><spring:message code="comments.reply" /></th>
							<th><spring:message code="comments.from" />
							<th><spring:message code="comments.activity" /></th>
							<th>&nbsp;</th>
						</tr>
					</thead>
					<tbody></tbody>
				</netcare:table>
			</section>
			
			<br />
			
			<section id="aktiviteter">
				<h2><spring:message code="activity.reported.title" /></h2>
				<p>
					<span class="label label-info"><spring:message code="information" /></span>
					<spring:message code="activity.reported.desc" />
				</p>
				
				<div id="reportedActivities">
					<div id="noReportedActivities" style="display: none;" class="alert alert-info">
						<p><spring:message code="activity.reported.none" /></p>
					</div>
					<netcare:table style="display: none;">
						<thead>
							<tr>
								<th><spring:message code="activity.reported.patient" /></th>
								<th><spring:message code="activity.reported.type" /></th>
								<th><spring:message code="activity.reported.value" /></th>
								<th><spring:message code="activity.reported.when" /></th>
								<th>&nbsp;</th>
						</thead>
						<tbody></tbody>
					</netcare:table>
				</div>
				
				<div id="commentActivity" class="modal hide fade" style="display: none;">
					<div class="modal-header">
						<a href="#" class="close" data-dismiss="modal">x</a>
						<h3><spring:message code="comments.comment" /></h3>
					</div>

					<div class="modal-body">
						<form action="post">
						<input type="text" name="comment" class="xlarge" />
					</div>
					<div class="modal-footer">
						<button class="btn btn-primary"><spring:message code='comments.sendComment' /></button>
						</form>
					</div>
				</div>
				
			</section>
			
			<br />
			
			<section id="alarms">
				<h2><spring:message code="alarm.title" /></h2>
				<p>
					<span class="label label-info"><spring:message code="information" /></span>
					<spring:message code="alarm.desc" />
				</p>
				
				<div id="alarmContainer">					
					<div class="alert alert-info" style="display:none;">
						<p><spring:message code="alarm.none" /></p>
					</div>
					<netcare:table style="display: none;">
						<thead>
							<tr>
								<th><spring:message code="alarm.patient" /></th>
								<th><spring:message code="alarm.contact" /></th>
								<th><spring:message code="alarm.cause" /></th>
								<th><spring:message code="alarm.created" /></th>
								<th>&nbsp;</th>
							</tr>
						</thead>
						<tbody></tbody>
					</netcare:table>
				</div>
			</section>
		</netcare:content>
	</netcare:body>	
</netcare:page>