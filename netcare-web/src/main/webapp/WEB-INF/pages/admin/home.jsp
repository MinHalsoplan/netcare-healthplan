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
					new NC.Util().updateCurrentPatient(name);
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
							console.log("Processing " + value.id + "...");
							var tr = $('<tr>');
							var created = $('<td>' + value.createdTime + '</td>');
							var patient = $('<td>' + value.patient.name + ' (' + util.formatCnr(value.patient.civicRegistrationNumber)  + ')</td>');
							var contact = $('<td>' + value.patient.phoneNumber + '</td>');
							var cause = $('<td>' + value.cause.value + '</td>');
							
							var processIcon = util.createIcon('clear', '24', function() {
								console.log("Resolving alarm...");
								alarms.resolve(value.id, loadAlarms);
							});
							
							var actionCol = $('<td>');
							
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
				
				$('#commentActivity').modal();
				$('#commentActivity').bind('shown', function() {
					$('#commentActivity input[name="comment"]').focus();
				});
				
				var loadReplies = function() {
					hp.loadNewReplies(function(data) {
						
						if (data.data.length > 0) {
							$('#replies div').hide();
							$('#replies table').show();
						} else {
							$('#replies div').show();
							$('#replies table').hide();
						}
						
						$('#replies table tbody').empty();
						
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
							});
							
							var actionCol = $('<td>');
							actionCol.append(deleteIcon);
							
							tr.append(actionCol);
							
							$('#replies table tbody').append(tr);
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
				<h2>:Start</h2>
				<p>
					Välkommen till planerade hälsotjänster. I menyn till höger anges vem du är
					inloggad som, vilken patient du för närvarande arbetar med samt en meny där
					du anger vad du vill arbeta med.
				</p>
				<p>
					<span class="label important">Viktigt!</span>
					Du måste ha valt en patient att arbeta med innan du kan utföra andra
					aktiviteter i systemet.
				</p>
				
			</section>
			
			<section id="replies">
				<h2>:<spring:message code="comments.replies" /></h2>
				<p>
					<span class="label notice"><spring:message code="information" /></span>
					<spring:message code="comments.repliesDescription" />
				</p>
				<div class="alert-message info">
					<p><spring:message code="comments.noReplies" />
				</div>
				<table class="bordered-table zebra-striped shadow-box">
					<thead>
						<th><spring:message code="comments.comment" /></th>
						<th><spring:message code="comments.reply" /></th>
						<th><spring:message code="comments.from" />
						<th><spring:message code="comments.activity" /></th>
						<th>&nbsp;</th>
					</thead>
					<tbody></tbody>
				</table>
			</section>
			
			<br />
			
			<section id="aktiviteter">
				<h2>:Genomförda aktiviteter</h2>
				<p>
					<span class="label notice"><spring:message code="information" /></span>
					Nedan visas en översikt över de patienter som har rapporterat och genomfört
					aktiviteter det senaste dygnet.
				</p>
				
				<div id="reportedActivities">
					<div id="noReportedActivities" style="display: none;" class="alert-message info">
						<p><spring:message code="noReportedActivities" /></p>
					</div>
					<table class="bordered-table zebra-striped shadow-box" style="display: none">
						<thead>
							<th><spring:message code="patient" /></th>
							<th><spring:message code="type" /></th>
							<th><spring:message code="goal" /></th>
							<th><spring:message code="reportedValue" /></th>
							<th><spring:message code="when" /></th>
						</thead>
						<tbody></tbody>
					</table>
				</div>
				
				<div id="commentActivity" class="modal hide fade" style="display: none;">
					<div class="modal-header">
						<a href="#" class="close">x</a>
						<h3><spring:message code="comments.comment" /></h3>
					</div>
					
					<div class="modal-body">
						<form action="post">
							<input type="text" name="comment" class="xlarge" />
							<input type="submit" class="btn primary" value="<spring:message code='comments.sendComment' />" />
						</form>
					</div>
				</div>
				
			</section>
			
			<br />
			
			<section id="alarms">
				<h2>:<spring:message code="alarm.overview" /></h2>
				<p>
					<span class="label notice"><spring:message code="information" /></span>
					<spring:message code="alarm.information" />
				</p>
				
				<div id="alarmContainer">					
					<div class="alert-message info" style="display:none;">
						<p><spring:message code="alarm.noAlarms" /></p>
					</div>
					<table class="bordered-table zebra-striped shadow-box" style="display: none;">
						<thead>
							<th><spring:message code="patient" /></th>
							<th><spring:message code="contactInformation" /></th>
							<th><spring:message code="alarm.cause" /></th>
							<th><spring:message code="alarm.created" /></th>
							<th>&nbsp;</th>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</section>
		</netcare:content>
	</netcare:body>	
</netcare:page>