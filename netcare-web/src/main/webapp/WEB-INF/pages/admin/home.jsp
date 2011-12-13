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
				var util = new NC.Util();
				var patient = new NC.Patient();
				var patientSearchInput = $('#pickPatientForm input[name="pickPatient"]'); 
				
				patientSearchInput.autocomplete({
					source : function(request, response) {
						
						/*
						 * Call find patients. Pass in the search value as well as
						 * a function that should be executed upon success.
						 */
						patient.findPatients(request.term, function(data) {
							console.log("Found " + data.data.length + " patients.");
							response($.map(data.data, function(item) {
								console.log("Processing item: " + item.name);
								return { label : item.name, value : item.name, patientId : item.id };
							}));
						});
					},
					select : function(event, ui) {
						console.log("Setting hidden field value to: " + ui.item.patientId);
						$('#pickPatientForm input[name="selectedPatient"]').attr('value', ui.item.patientId);
					}
				});
				
				var selectPatientSuccess = function(data) {
					var name = data.data.name;
					util.updateCurrentPatient(name);
				};
				
				var selectPatient = function(event) {
					patient.selectPatient($('#pickPatientForm input[name="selectedPatient"]').val(), selectPatientSuccess);
					event.preventDefault();
				}
				
				/*
				 * When the user presses enter in the search field will cause
				 * the form to submit
				 */
				patientSearchInput.keypress(function(e) {
					if (e.which == 13) {
						selectPatient(e);
					}
				});
				
				/*
				 * When the user clicks on the submit button, we perform
				 * an ajax call that selects the patient in the session.
				 */
				$('#pickPatientForm').submit(function(event) {
					selectPatient(event);
				});
				
				var units = new NC.Support();
				units.loadOptions($('#activityTypeForm select[name="unit"]'));
				
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h1>Välkommen</h1>
		 
			<form id="pickPatientForm">
				<fieldset>
					<legend><spring:message code="pickPatient" /></legend>
				</fieldset>
				<div class="clearfix">
					<label for="pickPatient"><spring:message code="search" /></label>
					<div class="input">
						<input name="pickPatient" class="xlarge" size="30" type="text" />
						
						<spring:message code="pick" var="pick" scope="page" />
						<input name="pickSubmit" type="submit" value="${pageScope.pick}" class="btn primary"/>
						<input name="selectedPatient" type="hidden" />
					</div>
				</div>
			</form>
			
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