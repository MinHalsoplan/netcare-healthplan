<%--

    Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>

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
				var support = NC.Support();
				support.loadDurations($('#createHealthPlanForm select'));
				
				var healthPlans = NC.HealthPlan('ordinationDescription'
						, 'ordinationTable');
				
				healthPlans.list(<c:out value="${sessionScope.currentPatient.id}" />);
				
				/*
				 * Bind date picker to start date field
				 */
				$('#createHealthPlanForm input[name="startDate"]').datepicker({
					dateFormat : 'yy-mm-dd',
					firstDay : 1,
					minDate : +0
				});
				
				support.loadMonths(function(data) {
					$('#createHealthPlanForm input[name="startDate"]').datepicker('option', 'monthNames', data);
				});
				
				support.loadWeekdays(function(data) {
					$('#createHealthPlanForm input[name="startDate"]').datepicker('option', 'dayNamesMin', data);
				});
				
				/*
				 * Bind create button
				 */
				$('#createHealthPlanForm :submit').click(function(event) {
					console.log("Submitting form...");
					event.preventDefault();
					
					var formData = new Object();
					formData.name = $('#createHealthPlanForm input[name="name"]').val();
					formData.startDate = $('#createHealthPlanForm input[name="startDate"]').val();
					formData.duration = $('#createHealthPlanForm input[name="duration"]').val();
					formData.durationUnit = new Object();
					formData.durationUnit.code = $('#createHealthPlanForm select option:selected').attr('value');
					formData.durationUnit.value = $('#createHealthPlanForm select option:selected').val();
					
					var jsonObj = JSON.stringify(formData);
					console.log("JSON: " + jsonObj.toString());
					
					healthPlans.create(jsonObj, <c:out value="${sessionScope.currentPatient.id}" />, function(data){
						$('#createHealthPlanForm :reset').click();
					});
					
					$('#createHealthPlanForm').hide();
				});
			
				$('#showCreateForm').click(function(even) {
					$('#createHealthPlanForm').toggle();
				});
				
				$('#createHealthPlanForm').hide();
				
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2><spring:message code="healthPlans" /> för <c:out value="${sessionScope.currentPatient.name}" /></h2>
			<p>
				<span class="label notice">Information</span>
				Den här sidan visar hälsoplaner för <c:out value="${sessionScope.currentPatient.name}" />. Du kan även skapa
				till en ny hälsoplan genom att klicka på "Skapa hälsoplan" länken nedan.
			</p>
			
			<spring:message code="newHealthPlan" var="title" scope="page"/>
			<spring:message code="clear" var="clear" scope="page" />
			<spring:message code="duration" var="duration" scope="page" />
			<spring:message code="name" var="name" scope="page" />
			<spring:message code="type" var="type" scope="page" />
			<spring:message code="startDate" var="startDate" scope="page" />
			
			<p style="text-align: right; padding-right: 20px;">
				<a id="showCreateForm" class="btn"><netcare:image name="bullet_add" /> <c:out value="${title}" /></a>
			</p>
			<netcare:form id="createHealthPlanForm" classes="form-stacked">
				<fieldset>
					<legend>${title}</legend>
					<netcare:field name="name" label="${name}">
						<input type="text" name="name" class="xlarge" />
					</netcare:field>
					
					<netcare:field name="startDate" label="${startDate}">
						<input type="text" name="startDate" class="xlarge" />
					</netcare:field>
					
					<div class="row">
						<div class="span6">
							<div class="row">
								<div class="span3">
									<netcare:field name="duration" label="${duration}">
										<input type="number" min="1" name="duration" class="medium" />
									</netcare:field>
								</div>
								<div class="span3">
									<netcare:field name="type" label="${type}">
										<select name="type"></select>
									</netcare:field>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
				
				<div class="actions">
					<input type="submit" class="btn primary" value="${title}" />
					<input type="reset" class="btn" value="${clear}" />
				</div>
				
			</netcare:form>
			
			<table id="ordinationTable" class="bordered-table zebra-striped">
				<thead>
					<tr>
						<th><spring:message code="name" /></th>
						<th><spring:message code="duration" /></th>
						<th><spring:message code="startDate" /></th>
						<th><spring:message code="issuedBy" /></th>
						<th>&nbsp;</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			
		</netcare:content>
		<netcare:menu />
	</netcare:body>
</netcare:page>