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
				var support = NC.Support();
				support.loadDurations($('#createHealthPlanForm select'));
				
				var updateDescription = function(count) {
					NC.log("Updating ordination table description");
					if (count == 0) {
						$('#healthPlanContainer div').show();
						$('#ordinationTable').hide();
					} else {
						$('#healthPlanContainer div').hide();
						$('#ordinationTable').show();
					}
				};
				
				var healthPlans = NC.HealthPlan();
				var listCallback = function(data) {
					NC.log("Success. Processing results...");
					
					/* Empty the result list */
					$('#ordinationTable tbody > tr').empty();
					
					$.each(data.data, function(index, value) {
						NC.log("Processing index " + index + " value: " + value.name);
						
						var util = NC.Util();
						
						var resultIcon = util.createIcon('results', 24, function() {
							healthPlans.results(value.id);
						});
						
						var editIcon = util.createIcon('edit', 24, function() {
							healthPlans.view(value.id);
						});
						
						var deleteIcon = util.createIcon('trash', 24, function() {
							healthPlans.remove(value.id, function(data) {
								healthPlans.list(value.patient.id, listCallback);
							});
						});
						
						var actionCol = $('<td>');
						actionCol.css('text-align', 'right');
						
						resultIcon.appendTo(actionCol);
						editIcon.appendTo(actionCol);
						deleteIcon.appendTo(actionCol);
						
						$('#ordinationTable tbody').append(
								$('<tr>').append(
									$('<td>').html(value.name)).append(
										$('<td>').html(value.duration + ' ' + value.durationUnit.value)).append(
												$('<td>').html(value.startDate)).append(
														$('<td>').html(value.issuedBy.name)).append(
																actionCol));
					});
					
					NC.log("Updating description");
					updateDescription(data.data.length);
				}
				
				healthPlans.list(<c:out value="${sessionScope.currentPatient.id}" />, function(data) {
					listCallback(data);
				});
				
				updateDescription(0);
				
				/*
				 * Bind date picker to start date field
				 */
				$('#createHealthPlanForm input[name="startDate"]').datepicker({
					dateFormat : 'yy-mm-dd',
					firstDay : 1,
					minDate : +0,
					buttonImage : '/netcare-web/img/icons/16/date.png',
					buttonImageOnly : true
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
					NC.log("Submitting form...");
					event.preventDefault();
					
					var formData = new Object();
					formData.name = $('#createHealthPlanForm input[name="name"]').val();
					formData.startDate = $('#createHealthPlanForm input[name="startDate"]').val();
					formData.duration = $('#createHealthPlanForm input[name="duration"]').val();
					formData.durationUnit = new Object();
					formData.durationUnit.code = $('#createHealthPlanForm select option:selected').attr('value');
					formData.durationUnit.value = $('#createHealthPlanForm select option:selected').val();
					
					var jsonObj = JSON.stringify(formData);
					NC.log("JSON: " + jsonObj.toString());
					
					healthPlans.create(jsonObj, <c:out value="${sessionScope.currentPatient.id}" />, function(data){
						$('#createHealthPlanForm :reset').click();
						healthPlans.view(data.data.id);
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
				<span class="label label-info">Information</span>
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
				<a id="showCreateForm" class="btn addButton"><c:out value="${title}" /></a>
			</p>
			<netcare:form id="createHealthPlanForm">
				<fieldset>
					<legend>${title}</legend>
					<netcare:field name="name" label="${name}">
						<input type="text" name="name" class="xlarge" />
					</netcare:field>
					
					<netcare:field name="startDate" label="${startDate}">
						<input type="text" name="startDate" class="xlarge"/>
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
				
				<div class="form-actions">
					<input type="submit" class="btn btn-primary" value="${title}" />
					<input type="reset" class="btn" value="${clear}" />
				</div>
				
			</netcare:form>
			
			<div id="healthPlanContainer">
				<div style="display: none;" class="alert alert-info">
					<p><spring:message code="noHealthPlans" /></p>
				</div>
				<netcare:table id="ordinationTable">
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
				</netcare:table>
			</div>
		</netcare:content>
	</netcare:body>
</netcare:page>