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
				var support = new NC.Support();
				
				util.bindNotEmptyField($('#nameContainer'), $('input[name="name"]'));
				util.bindNotEmptyField($('#cnrContainer'), $('input[name="cnr"]'));
				util.bindLengthField($('#cnrContainer'), $('input[name="cnr"]'), 12);
				
				var patients = new NC.Patient();
				
				var updatePatientTable = function(data) {
					
					if (data != null) {
						if (data.success) {
							/*
							 * Select the patient and navigate to home
							 */
							patients.selectPatient(data.data.id, function(data) {
								NC.log("Created patient selected. Go to home...");
								window.location = '/netcare-web/netcare/home';
							});
						}
					}
					
					patients.load(function(data) {
						if (data.data.length == 0) {
							$('#patients div').show();
							$('#patientsTable').hide();
						} else {
							$('#patients div').hide();
							$('#patientsTable tbody').empty();
							
							$.each(data.data, function(index, value) {
								
								NC.log("Processing patient " + value.name + "...");
								
								var tr = $('<tr>');
								
								var name = $('<td>' + value.name + '</td>');
								var cnr = $('<td>' + new NC.Util().formatCnr(value.civicRegistrationNumber) + '</td>');
								var phone = $('<td>' + value.phoneNumber + '</td>');
								
								var loginAsIcon = util.createIcon('loginAs', 24, function() {
									new NC.Patient().selectPatient(value.id, function(data) {
										util.updateCurrentPatient(data.data.name);
										
										window.location = '/netcare-web/netcare/home';
									});
								});
								
								var deleteIcon = util.createIcon('trash', 24, function() {
									NC.log("Delete patient.");
									
									support.loadCaptions(null, ['patientDelete'], function(data) {
										var result = confirm(data.patientDelete);
										if (result) {
											NC.log("Deleting patient...");
											patients.deletePatient(value.id, function(data) {
												NC.log("Patient deleted. Reload patients...");
												patients.load(updatePatientTable);
											});
										}
									});
								});
								
								var actionCol = $('<td>').css('text-align', 'right');
								actionCol.append(loginAsIcon);
								actionCol.append(deleteIcon);
								
								tr.append(name).append(cnr).append(phone).append(actionCol);
								
								$('#patientsTable tbody').append(tr);
							});
							
							$('#patientsTable').show();
						}
					});
				};
				
				/*
				 * Load patients
				 */
				updatePatientTable(null);
				
				$('#patientForm :submit').click(function(event) {
					NC.log("Submitting form...");
					event.preventDefault();
					
					var formData = new Object();
					formData.name = $('input[name="name"]').val();
					formData.civicRegistrationNumber = $('input[name="cnr"]').val();
					formData.phoneNumber = $('input[name="phoneNumber"]').val();
					
					var jsonObj = JSON.stringify(formData);
					patients.create(jsonObj, updatePatientTable);
					
					$('#patientForm :reset').click();
				});
				
				$('#showCreateForm').click(function(event) {
					$('#patientForm').toggle();
				});
				
				$('#patientForm').hide();
				
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2><spring:message code="patients" /></h2>
			<p>På den här sidan lägger du till nya patienter. Etc...</p>
			
			<p style="text-align: right; padding-right: 20px;">
				<a id="showCreateForm" class="btn addButton">
					<spring:message code="newPatient" />
				</a>
			</p>
			
			<form id="patientForm" class="form-stacked">
				<fieldset>
					<legend><spring:message code="newPatient" /></legend>
				</fieldset>
				
				<spring:message code="patient.name" var="name" scope="page"/>
				<netcare:field containerId="nameContainer" name="name" label="${name}">
					<input type="text" name="name" />
				</netcare:field>
				
				<spring:message code="patient.cnr" var="cnr" scope="page" />
				<netcare:field containerId="cnrContainer" name="cnr" label="${cnr}">
					<input type="text" name="cnr" />
				</netcare:field>
				
				<spring:message code="patient.phoneNumber" var="phoneNumber" scope="page" />
				<netcare:field containerId="phoneNumberContainer" name="phoneNumber" label="${phoneNumber}">
					<input type="tel" name="phoneNumber" />
				</netcare:field>
				
				<div class="actions">
					<input type="submit" class="btn primary" value="<spring:message code="create" />" />
					<input type="reset" class="btn" value="<spring:message code="reset" />" />
				</div>
				
			</form>
			
			<section id="patients">
				<h3><spring:message code="patients" /></h3>
				<div class="alert-message info" style="display: none;">
					<p><spring:message code="noPatients" /></p>
				</div>
				<table id="patientsTable" class="bordered-table zebra-striped shadow-box">
					<thead>
						<th><spring:message code="patient.name" /></th>
						<th><spring:message code="patient.cnr" /></th>
						<th><spring:message code="patient.phoneNumber" />
						<th>&nbsp;</th>
					</thead>
					<tbody>
					
					</tbody>
				</table>
			</section>
		</netcare:content>
	</netcare:body>	
</netcare:page>