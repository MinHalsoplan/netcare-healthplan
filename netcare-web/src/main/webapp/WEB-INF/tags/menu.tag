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
<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="/netcare/admin/home" var="adminHome" scope="page" />

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
						return { label : item.name + ' (' + util.formatCnr(item.civicRegistrationNumber) + ')', value : item.name, patientId : item.id };
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
			console.log("Selecting patient...");
			event.preventDefault();
			patient.selectPatient($('#pickPatientForm input[name="selectedPatient"]').val(), selectPatientSuccess);
			
			console.log("Hide modal.");
			$('#modal-from-dom').modal('hide');
			
			/* Redirect to home in order to prevent weird stuff
			 * to happen
			 */
			window.location = '/netcare-web/netcare/home';
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
		
		var currentPatient = "<c:out value="${sessionScope.currentPatient.name}" />"
		if (currentPatient.length == 0) {
			$('#workWith').hide();
		}
		
		var cnr = '<c:out value="${sessionScope.currentPatient.civicRegistrationNumber}" />';
		console.log("Current patient cnr is: " + cnr);
		if (cnr.length != 0) {
			console.log("Displaying patient cnr");
			$('#cnr').html('<strong>Personnummer:</strong> ' + util.formatCnr(cnr));
		}
	});
</script>

<div class="span4">
	<h3><spring:message code="loggedInAs" /></h3>
	<p>
		<a href="#"><sec:authentication property="principal.username" /></a> | <a href="<spring:url value="/j_spring_security_logout" htmlEscape="true"/>"><spring:message code="logout" /></a>
	</p>
	<p>
		<strong><spring:message code="careUnit" />:</strong><br />
		<sec:authentication property="principal.careUnit.name" /> <br /><small>(<sec:authentication property="principal.careUnit.hsaId" />)</small>
	</p>
		
	<h3><spring:message code="patient" /></h3>
	<c:if test="${not empty sessionScope.currentPatient}">
		<p id="currentpatient" style="display: block;">
			<spring:message code="currentPatient" /> <a href="#"><c:out value="${sessionScope.currentPatient.name}" /></a>
		</p>
		<p>
			<span id="cnr"></span>
		</p>
	</c:if>
	<p>
		<a data-backdrop="true" data-controls-modal="modal-from-dom"><spring:message code="clickHere" /></a> <spring:message code="toPickPatient" /><br />
	</p>
	
	<div id="modal-from-dom" class="modal hide fade" style="display: none;">
		<form id="pickPatientForm" class="form-stacked">
			<div class="modal-header">
				<a href="#" class="close">x</a>
				<h3><spring:message code="pickPatient" /></h3>
			</div>
			<div class="modal-body">
				<div class="clearfix">
					<label for="pickPatient"><spring:message code="search" /></label>
					<div class="input">
						<input name="pickPatient" class="xlarge" size="30" type="text" />
						<input name="selectedPatient" type="hidden" />
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<input name="pickSubmit" type="submit" value="<spring:message code="pick" />" class="btn primary"/>
			</div>
		</form>
	</div>
	
	<div id="workWith">
		<h3><spring:message code="workWith" /></h3>
		<ul>
			<li><a href="<spring:url value="/netcare/admin/healthplan/new" />"><spring:message code="healthPlans" /></a>
			<li><a href="<spring:url value="/netcare/admin/categories" />"><spring:message code="activityCategories" /></a>
		</ul>
	</div>
</div>
</body>
