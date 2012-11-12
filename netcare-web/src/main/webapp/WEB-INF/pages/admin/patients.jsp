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

<hp:view>
	<hp:viewHeader>
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				
				var currentPatientId = "<c:out value='${sessionScope.currentPatient.id}' />";
				
				var params = {	
				};
				
				NC_MODULE.PATIENTS.init(params);
				
				var util = new NC.Util();
				var support = new NC.Support();
				
				util.bindNotEmptyField($('#nameContainer'), $('input[name="firstName"]'));
				util.bindNotEmptyField($('#nameContainer'), $('input[name="surName"]'));
				util.bindNotEmptyField($('#cnrContainer'), $('input[name="cnr"]'));
				util.bindLengthField($('#cnrContainer'), $('input[name="cnr"]'), 12);
				
				var patients = new NC.Patient();
							
				var validateMandatory = function(field) {
					field.focusout(function() {
						var first = $('input[name="firstName"]');
						var sur = $('input[name="surName"]');
						var crn = $('input[name="crn"]');
						if (first.val() != '' && sur.val() != '' && crn.val().length == 12) {
							$('#patientForm :submit').removeAttr('disabled');						
						} else {
							$('#patientForm :submit').attr('disabled', 'disbaled');						
						}
					});
				}
				
				/*var updatePatientTable = function(data) {
					
					if (data != null) {
						if (data.success) {
							 // Select the patient and navigate to home
							support.selectPatient(data.data.id, function(data) {
								NC.log("Created patient selected. Go to home...");
								window.location = NC.getContextPath() + '/netcare/admin/healthplans?showForm=true';
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
								
								var loginAsIcon = $('<button>').addClass('btn btn-info').html('Välj patient').click(function(e) {
									e.preventDefault();
									support.selectPatient(value.id, function(data) {
										util.updateCurrentPatient(data.data.name);
										window.location = NC.getContextPath() + '/netcare/admin/healthplans?showForm=true';
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
				};*/
				
				/*
				 * Load patients
				 */
				//updatePatientTable(null);
				
				/**
				 * Validate crn, only allow [0-9], maxlength of 6
				 */
				$('.numericInput').each(function(i, v) {
					util.validateNumericField($(v), 12);
				});

				/**
				 * Disable.
				 */
				$('#patientForm :submit').attr('disabled', 'disabled');
				validateMandatory($('input[name="crn"]'));
				validateMandatory($('input[name="firstName"]'));
				validateMandatory($('input[name="surName"]'));
				
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
				
				$('#showCreatePatient').click(function(e) {
					$('#patientForm').toggle();
				});
				
				var showForm = '<c:out value="${param.showForm}" />';
				if (showForm != '') {
					$('#patientForm').toggle();
				}
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Patienter">
		<section id="patients">
			<h2><spring:message code="admin.patients.list" /></h2>
			
<%-- 			<netcare:block-message type="info" style="display:none;"> --%>
<%-- 				<spring:message code="admin.patients.none" /> --%>
<%-- 			</netcare:block-message> --%>
<%-- 			<netcare:table id="patientsTable"> --%>
<!-- 				<thead> -->
<!-- 					<tr> -->
<%-- 						<th><spring:message code="patient.surName" /></th> --%>
<%-- 						<th><spring:message code="patient.firstName" /></th> --%>
<%-- 						<th><spring:message code="patient.crn" /></th> --%>
<%-- 						<th><spring:message code="patient.phoneNumber" /> --%>
<!-- 						<th>&nbsp;</th> -->
<!-- 					</tr> -->
<!-- 				</thead> -->
<!-- 				<tbody></tbody> -->
<%-- 			</netcare:table> --%>
			<mvk:touch-list id="patientList">
			
			</mvk:touch-list>

		</section>
	
		<button id="showCreatePatient" class="btn btn-block btn-large btn-info" style="margin-top: 20px; margin-bottom: 20px;">
			<spring:message code="admin.patients.new" />
		</button>
		
		<form id="patientForm" style="display: none;">
			<netcare:row>
				<netcare:col span="4">
					<spring:message code="patient.firstName" var="name" scope="page"/>
					<netcare:field containerId="nameContainer" name="firstName" label="${name}">
						<input type="text" name="firstName" />
					</netcare:field>
				</netcare:col>
				<netcare:col span="4">
					<spring:message code="patient.surName" var="surName" scope="page"/>
					<netcare:field containerId="nameContainer" name="surName" label="${surName}">
						<input type="text" name="surName" />
					</netcare:field>
				</netcare:col>
			</netcare:row>
			
			<netcare:row>
				<netcare:col span="4">
					<spring:message code="patient.crn" var="cnr" scope="page" />
					<netcare:field containerId="cnrContainer" name="crn" label="${cnr}">
						<input type="text" name="crn" placeholder="<spring:message code="pattern.crn" />" class="numericInput"/>
						<br />
						<span class="help-block"><small>Exempel: 191212121212</small></span>
					</netcare:field>
				</netcare:col>
				<netcare:col span="4">
					<spring:message code="patient.phoneNumber" var="phoneNumber" scope="page" />
					<netcare:field containerId="phoneNumberContainer" name="phoneNumber" label="${phoneNumber}">
						<input type="tel" name="phoneNumber" />
					</netcare:field>
				</netcare:col>
			</netcare:row>
			
			<div class="form-actions">
				<input type="submit" class="btn btn-info" value="<spring:message code="create" />" />
				<input type="reset" class="btn" value="<spring:message code="reset" />" />
			</div>
			
		</form>
	</hp:viewBody>
</hp:view>
