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
				
				var currentPatientId = "<c:out value='${sessionScope.currentPatient.id}' />";
				
				var util = new NC.Util();
				var support = new NC.Support();
				
				util.bindNotEmptyField($('#nameContainer'), $('input[name="firstName"]'));
				util.bindNotEmptyField($('#nameContainer'), $('input[name="surName"]'));
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
								window.location = NC.getContextPath() + '/netcare/home';
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
								
								var name = $('<td>' + value.surName + '</td>');
								var firstName = $('<td>' + value.firstName + '</td>');
								var cnr = $('<td>' + new NC.Util().formatCnr(value.civicRegistrationNumber) + '</td>');
								var phone = $('<td>' + value.phoneNumber + '</td>');
								
								var loginAsIcon = $('<button>').addClass('btn primary').html('Välj patient').click(function(e) {
									e.preventDefault();
									patients.selectPatient(value.id, function(data) {
										util.updateCurrentPatient(data.data.name);
										window.location = NC.getContextPath() + '/netcare/admin/healthplan/new';
									});
								});
								
								var actionCol = $('<td>').css('text-align', 'right');
								actionCol.append(loginAsIcon);
								
								tr.append(name).append(firstName).append(cnr).append(phone).append(actionCol);
								
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
					formData.firstName = $('input[name="firstName"]').val();
					formData.surName = $('input[name="surName"]').val();
					formData.civicRegistrationNumber = $('input[name="crn"]').val();
					formData.phoneNumber = $('input[name="phoneNumber"]').val();
					
					patients.create(formData, updatePatientTable);
					
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
			<h2><spring:message code="admin.patients.new" /></h2>
			<p>
				<span class="label label-info"><spring:message code="information" /></span>
				<spring:message code="admin.patients.desc" />
			</p>
			
			<p style="text-align: right; padding-right: 20px;">
				<a id="showCreateForm" class="btn addButton">
					<spring:message code="admin.patients.new" />
				</a>
			</p>
			
			<form id="patientForm">
				<fieldset>
					<legend><spring:message code="admin.patients.new" /></legend>
				</fieldset>
				
				<netcare:row>
					<netcare:col span="5">
						<spring:message code="patient.firstName" var="name" scope="page"/>
						<netcare:field containerId="nameContainer" name="firstName" label="${name}">
							<input type="text" name="firstName" />
						</netcare:field>
					</netcare:col>
					<netcare:col span="5">
						<spring:message code="patient.surName" var="surName" scope="page"/>
						<netcare:field containerId="nameContainer" name="surName" label="${surName}">
							<input type="text" name="surName" />
						</netcare:field>
					</netcare:col>
				</netcare:row>
				
				<netcare:row>
					<netcare:col span="5">
						<spring:message code="patient.crn" var="cnr" scope="page" />
						<netcare:field containerId="cnrContainer" name="crn" label="${cnr}">
							<input type="text" name="crn" />
						</netcare:field>
					</netcare:col>
					<netcare:col span="5">
						<spring:message code="patient.phoneNumber" var="phoneNumber" scope="page" />
						<netcare:field containerId="phoneNumberContainer" name="phoneNumber" label="${phoneNumber}">
							<input type="tel" name="phoneNumber" />
						</netcare:field>
					</netcare:col>
				</netcare:row>
				
				<div class="form-actions">
					<input type="submit" class="btn btn-primary" value="<spring:message code="create" />" />
					<input type="reset" class="btn" value="<spring:message code="reset" />" />
				</div>
				
			</form>
			
			<section id="patients">
				<h3><spring:message code="admin.patients.list" /></h3>
				<p>
					<span class="label label-info"><spring:message code="information" /></span>
					<spring:message code="admin.patients.list.desc" />
				</p>
				<div class="alert alert-info" style="display: none;">
					<p><spring:message code="admin.patients.none" /></p>
				</div>
				<netcare:table id="patientsTable">
					<thead>
						<tr>
							<th><spring:message code="patient.surName" /></th>
							<th><spring:message code="patient.firstName" /></th>
							<th><spring:message code="patient.crn" /></th>
							<th><spring:message code="patient.phoneNumber" />
							<th>&nbsp;</th>
						</tr>
					</thead>
					<tbody>
					
					</tbody>
				</netcare:table>
			</section>
		</netcare:content>
	</netcare:body>	
</netcare:page>