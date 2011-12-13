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
				support.loadDurations($('#createOrdinationForm select'));
				
				var ordinations = NC.Ordination('ordinationDescription'
						, 'ordinationTable');
				
				ordinations.list(<c:out value="${sessionScope.currentPatient.id}" />);
				
				/*
				 * Bind date picker to start date field
				 */
				$('#createOrdinationForm input[name="startDate"]').datepicker({
					dateFormat : 'yy-mm-dd',
					firstDay : 1,
					minDate : +0
				});
				
				support.loadMonths(function(data) {
					$('#createOrdinationForm input[name="startDate"]').datepicker('option', 'monthNames', data);
				});
				
				support.loadWeekdays(function(data) {
					$('#createOrdinationForm input[name="startDate"]').datepicker('option', 'dayNamesMin', data);
				});
				
				/*
				 * Bind create button
				 */
				$('#createOrdinationForm :submit').click(function(event) {
					console.log("Submitting form...");
					
					var formData = new Object();
					formData.name = $('#createOrdinationForm input[name="name"]').val();
					formData.startDate = $('#createOrdinationForm input[name="startDate"]').val();
					formData.duration = $('#createOrdinationForm input[name="duration"]').val();
					formData.durationUnit = $('#createOrdinationForm select').val();
					
					var jsonObj = JSON.stringify(formData);
					
					ordinations.create(jsonObj, <c:out value="${sessionScope.currentPatient.id}" />, function(data){
						$('#createOrdinationForm :reset').click();
					});
					event.preventDefault();
				});
				
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2><spring:message code="ordinations" /></h2>
			<p>
				Den här sidan låter dig skapa en ordination för en patient. Ordinationen kan sedan schemaläggas som sedan patient
				kan rapportera på. Beskrivande text... bla bla.
			</p>
			
			<spring:message code="create" var="title" scope="page" />
			<spring:message code="clear" var="clear" scope="page" />
			<spring:message code="duration" var="duration" scope="page" />
			<spring:message code="name" var="name" scope="page" />
			<spring:message code="type" var="type" scope="page" />
			<spring:message code="startDate" var="startDate" scope="page" />
			
			<netcare:form title="${title}" id="createOrdinationForm" classes="form-stacked">
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
									<input type="number" name="duration" class="medium" />
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
				
				<div class="actions">
					<input type="submit" class="btn primary" value="${title}" />
					<input type="reset" class="btn" value="${clear}" />
				</div>
				
			</netcare:form>
			
			<h3>Aktuella ordinationer</h3>
			<p id="ordinationDescription"></p>
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