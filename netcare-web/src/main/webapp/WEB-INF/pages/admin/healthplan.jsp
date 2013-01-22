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

<c:set var="patientId" value="${sessionScope.currentPatient.id}" scope="page" />
<c:set var="patient" value="${sessionScope.currentPatient.name}" scope="page" />

<hp:view>
	<hp:viewHeader>
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				
				var lang = {
					active : '<spring:message code="healthplan.active" />',
					autoRenew : '<spring:message code="healthplan.autoRenew" />',
					ends : '<spring:message code="healthplan.ends" />',
					noActivities : '<spring:message code="healthplan.noActivities" />'
				}
				
				var params = {
					healthplanId : -1,
					patientId : '<c:out value="${patientId}" />',
					lang : lang,
					showForm : '<c:out value="${param.showForm}" />'
				};
				
				NC_MODULE.HEALTH_PLAN.init(params);
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Hälsoplaner" plain="true">
		<spring:message code="clear" var="clear" scope="page" />
		<spring:message code="healthplan.duration" var="duration" scope="page" />
		<spring:message code="healthplan.name" var="name" scope="page" />
		<spring:message code="healthplan.type" var="type" scope="page" />
		<spring:message code="healthplan.start" var="startDate" scope="page" />
		<spring:message code="healthplan.issuedBy" var="issuedBy" scope="page" />
		<spring:message code="healthplan.autoRenewal" var="autoRenewal" scope="page" />
		
		<c:set var="curPatient" value="${sessionScope.currentPatient.name}" scope="page" />
		<spring:message code="healthplan.new" var="newHealthPlan" scope="page"/>
		
		<button id="showCreateHealthPlan" class="btn" style="margin-top: 20px; margin-bottom: 20px;">
			<spring:message code="healthplan.new" />
		</button>
		
		<mvk:sheet id="createHealthPlanSheet" style="display: none;">
		<form id="createHealthPlanForm" action="#" method="post">
			<fieldset>
				<netcare:field containerId="nameContainer" name="name" label="${name}">
					<input type="text" id="name" name="name" class="xlarge required" />
				</netcare:field>
				
				<netcare:field containerId="startDateContainer" name="startDate" label="${startDate}">
					<netcare:dateInput id="startDate" name="startDate" classes="dateISO required"/>
				</netcare:field>
				
				<netcare:row>
					<netcare:col span="6">
						<netcare:field containerId="durationContainer" name="duration" label="${duration}">
							<input type="number" min="1" id="duration" name="duration" class="medium digits" value="1"/>
						</netcare:field>
					</netcare:col>
				</netcare:row>
				
				<netcare:row>
					<netcare:col span="6">
						<netcare:field name="type" label="${type}">
							<select name="type"></select>
						</netcare:field>
					</netcare:col>
				</netcare:row>

				<netcare:field name="autoRenewal" label="${autoRenewal}">
					<input type="checkbox" name="autoRenewal" value="true"/>
				</netcare:field>
			</fieldset>
			
			<div class="form-actions">
				<input type="submit" class="btn btn-info" value="${newHealthPlan}" />
				<input type="reset" class="btn" value="${clear}" />
			</div>
			
		</form>
		</mvk:sheet>
		
		<h3 class="title"><spring:message code="healthplan.title" arguments="${curPatient}"/></h3>
		<mvk:touch-list id="healthPlanContainer">
		</mvk:touch-list>
	</hp:viewBody>
</hp:view>
