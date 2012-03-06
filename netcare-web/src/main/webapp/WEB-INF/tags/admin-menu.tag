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
<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>

<c:url value="/netcare/admin/home" var="adminHome" scope="page" />

<script type="text/javascript">
	$(function() {
		var util = new NC.Util();
		var patient = new NC.Patient();
		var patientSearchInput = $('#pickPatientForm input[name="pickPatient"]'); 
		
		/*
		 *
		 */
		$('#modal-from-dom').bind('shown', function() {
			patientSearchInput.focus();
		});
		
		patientSearchInput.autocomplete({
			source : function(request, response) {
				
				/*
				 * Call find patients. Pass in the search value as well as
				 * a function that should be executed upon success.
				 */
				patient.findPatients(request.term, function(data) {
					NC.log("Found " + data.data.length + " patients.");
					response($.map(data.data, function(item) {
						console.log("Processing item: " + item.name);
						return { label : item.name + ' (' + util.formatCnr(item.civicRegistrationNumber) + ')', value : item.name, patientId : item.id };
					}));
				});
			},
			select : function(event, ui) {
				NC.log("Setting hidden field value to: " + ui.item.patientId);
				$('#pickPatientForm input[name="selectedPatient"]').attr('value', ui.item.patientId);
			}
		});
		
		var selectPatientSuccess = function(data) {
			var name = data.data.name;
			util.updateCurrentPatient(name);
		};
		
		var selectPatient = function(event) {
			NC.log("Selecting patient...");
			event.preventDefault();
			patient.selectPatient($('#pickPatientForm input[name="selectedPatient"]').val(), selectPatientSuccess);
			
			NC.log("Hide modal.");
			$('#modal-from-dom').modal('hide');
			
			/* Redirect to home in order to prevent weird stuff
			 * to happen
			 */
			window.location = NC.getContextPath() + '/netcare/admin/healthplan/new';
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
		NC.log("Current patient cnr is: " + cnr);
		if (cnr.length != 0) {
			NC.log("Displaying patient cnr");
			$('#cnr').html('<strong>Personnummer:</strong> ' + util.formatCnr(cnr));
		}
	});
</script>

<div class="span3 menu">
	<h3 class="menuHeader"><netcare:image name="auth" size="16"/><spring:message code="loggedInAs" /></h3>
	<p>
		<a href="#"><sec:authentication property="principal.name" /></a> | <a href="<spring:url value="/netcare/security/logout" htmlEscape="true"/>"><spring:message code="logout" /></a>
	</p>
	<p>
		<strong><spring:message code="careUnit" />:</strong><br />
		<sec:authentication property="principal.careUnit.name" /> <br /><small>(<sec:authentication property="principal.careUnit.hsaId" />)</small>
	</p>
		
	<h3 id="patientName" class="menuHeader"><netcare:image name="user" size="16"/><spring:message code="admin.menu.patient" /></h3>
	<c:choose>
		<c:when test="${not empty sessionScope.currentPatient}">
			<div id="workWith" class="shadow-box" style="padding-left: 5px;">
			<h4><c:out value="${sessionScope.currentPatient.name}" /></h4>
			<p>
				<span id="cnr"></span>
			</p>
			<ul>
				<li><a href="<spring:url value="/netcare/admin/healthplan/new" />"><spring:message code="admin.menu.patient.healthplans" /></a></li>
			</ul>
		</div>
		</c:when>
		<c:otherwise>
			<ul class="menuList">
				<li><netcare:image name="list" size="16" /><a href="<spring:url value="/netcare/admin/patients" />"><spring:message code="admin.menu.patient.pick" /></a>
				<li><netcare:image name="gtk-find" size="16" /><a data-backdrop="true" data-toggle="modal" href="#modal-from-dom"><spring:message code="admin.menu.patient.search" /></a>
				<li><netcare:image name="add" size="16" /><a href="<spring:url value="/netcare/admin/patients" />"><spring:message code="admin.menu.patient.new" /></a>
			</ul>
		</c:otherwise>
	</c:choose>
	
	<div id="modal-from-dom" class="modal hide fade" style="display: none;">
			<div class="modal-header">
				<a href="#" class="close" data-dismiss="modal">x</a>
				<h3><spring:message code="admin.menu.patient.search" /></h3>
			</div>
			<div class="modal-body">
				<form id="pickPatientForm">
				<div class="clearfix">
					<label for="pickPatient"><spring:message code="admin.menu.patient.searchValue" /></label>
					<div class="input">
						<input name="pickPatient" class="xlarge nc-autocomplete" size="30" type="text" />
						<input name="selectedPatient" type="hidden" />
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<input name="pickSubmit" type="submit" value="<spring:message code="admin.menu.patient.pickFromSearch" />" class="btn btn-primary"/>
				</form>
			</div>
	</div>
	
	<div id="system">
		<h3 class="menuHeader"><spring:message code="admin.menu.create" /></h3>
		<ul class="menuList">
			<li><netcare:image name="gtk-add" size="16" /><a href="<spring:url value="/netcare/admin/activitytypes" />"><spring:message code="admin.menu.activityType" /></a></li>
			<li><netcare:image name="gtk-add" size="16" /><a href="<spring:url value="/netcare/admin/categories" />"><spring:message code="admin.menu.activityCategory" /></a>
		</ul>
	</div>
</div>
</body>
