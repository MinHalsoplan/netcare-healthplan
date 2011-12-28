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
				
				var name = "<c:out value="${sessionScope.currentPatient.name}" />";
				if (name.length != 0) {
					new NC.Util().updateCurrentPatient(name);
				}
				
				var units = new NC.Support();
				units.loadLatestReportedActivities('reportedActivities');
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
			
			<section id="aktiviteter">
				<h2>:Genomförda aktiviteter</h2>
				<p>
					<span class="label notice">Information</span>
					Nedan visas en översikt över de patienter som har rapporterat och genomfört
					aktiviteter det senaste dygnet.
				</p>
				
				<div id="reportedActivities">
					<p id="noReportedActivities" style="display: none;"><spring:message code="noReportedActivities" /></p>
					<table class="bordered-table zebra-striped" style="display: none">
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
				
			</section>
			
			<section id="alarms">
				<h2>:Larmöversikt</h2>
				<p>
					<span class="label notice">Information</span>
					Nedan visas en översikt över de patienter som har rapporterat en aktivitet
					och fallit utan för de rekommenderade värdena.
				</p>
			</section>
			
		 
			
			<%--<form id="activityTypeForm" method="post" action="#">
				<fieldset>
					<legend><spring:message code="addActivityType" /></legend>
					<div class="clearfix">
						<label for="activityName"><spring:message code="name" /></label>
						<div class="input">
							<input name="activityName" class="xlarge" size="30" type="text" />
						</div>
					</div>
					
					<div class="clearfix">
						<label for="activityUnit"><spring:message code="unit" /></label>
						<div class="input">
							<select name="unit" class="medium">
							</select>
						</div>
					</div>
					
				</fieldset>
			</form>
			 --%>
		</netcare:content>
		<netcare:menu>
		
		</netcare:menu>
		
	</netcare:body>	
</netcare:page>