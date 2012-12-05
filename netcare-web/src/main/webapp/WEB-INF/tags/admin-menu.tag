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

<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<c:url value="/netcare/admin/home" var="adminHome" scope="page" />

<script type="text/javascript">
	$(function() {
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
				NC_MODULE.PATIENTS.findPatients(request.term, function(data) {
					NC.log("Found " + data.data.length + " patients.");
					response($.map(data.data, function(item) {
						console.log("Processing item: " + item.name);
						return { label : item.name + ' (' + NC.GLOBAL.formatCrn(item.civicRegistrationNumber) + ')', value : item.name, patientId : item.id };
					}));
				});
			},
			select : function(event, ui) {
				NC.log("Setting hidden field value to: " + ui.item.patientId);
				$('#pickPatientForm input[name="selectedPatient"]').prop('value', ui.item.patientId);
			}
		});
		
		var selectPatientSuccess = function(data) {
			var name = data.data.name;
			NC_MODULE.GLOBAL.updateCurrentPatient(name);
		};
		
		var selectPatient = function(event) {
			NC.log("Selecting patient...");
			event.preventDefault();
			NC_MODULE.GLOBAL.selectPatient($('#pickPatientForm input[name="selectedPatient"]').val(), selectPatientSuccess);
			
			NC.log("Hide modal.");
			$('#modal-from-dom').modal('hide');
			
			// Redirect to new health plan
			window.location = GLOB_CTX_PATH + '/netcare/admin/healthplans?showForm=true';
		};
		
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
		
		var currentPatient = '<c:out value="${sessionScope.currentPatient.name}" />';
		if (currentPatient.length == 0) {
			$('#workWith').hide();
		}
		
		var cnr = '<c:out value="${sessionScope.currentPatient.civicRegistrationNumber}" />';
		NC.log("Current patient cnr is: " + cnr);
		if (cnr.length != 0) {
			NC.log("Displaying patient cnr");
			$('#cnr').html('<strong>Personnummer:</strong> ' + NC.GLOBAL.formatCrn(cnr));
		}
		
		$('#quitPatientSession').click(function(e) {
			e.preventDefault();
			NC_MODULE.GLOBAL.unselect(function(d) {
				window.location = NC.getContextPath() + '/netcare/home';
			});
		});
	});
</script>

<form id="pickPatientForm" action="#" method="post">
	<netcare:modal id="modal-from-dom" confirmCode="admin.menu.patient.pickFromSearch" titleCode="admin.menu.patient.search">
		<spring:message code="admin.menu.patient.searchValue" var="pick" scope="page"/>
		<netcare:field name="pickPatient" label="${pick}">
			<input name="pickPatient" class="xlarge nc-autocomplete" size="30" type="text" />
			<input name="selectedPatient" type="hidden" />
		</netcare:field>
	</netcare:modal>
</form>

<div class="span3 menu">
	<c:if test="${not empty sessionScope.currentPatient}">
		<div id="workWith" style="padding-left: 5px; margin-right: 15px; margin-top: 10px;">
			<h4><c:out value="${sessionScope.currentPatient.name}" /></h4>
			<p>
				<span id="cnr"></span>
			</p>
			<ul>
				<li><a href="<spring:url value="/netcare/admin/healthplans" />"><spring:message code="admin.menu.patient.healthplans" /></a></li>
				<li><a id="quitPatientSession" href="#"><spring:message code="admin.menu.patient.quit" /></a></li>
			</ul>
		</div>
	</c:if>
	
	<ul class="menuList">
		<li><netcare:image name="list" size="16" /><a href="<spring:url value="/netcare/admin/patients" />"><spring:message code="admin.menu.patient.pick" /></a>
		<li><netcare:image name="gtk-find" size="16" /><a data-backdrop="true" data-toggle="modal" href="#modal-from-dom"><spring:message code="admin.menu.patient.search" /></a>
		<li><netcare:image name="new-patient" size="16" /><a href="<spring:url value="/netcare/admin/patients?showForm=true" />"><spring:message code="admin.menu.patient.new" /></a>
		<li><netcare:image name="new-activity" size="16" /><a href="<spring:url value="/netcare/admin/templates" />"><spring:message code="admin.menu.activityType" /></a></li>
	</ul>
</div>
</body>
