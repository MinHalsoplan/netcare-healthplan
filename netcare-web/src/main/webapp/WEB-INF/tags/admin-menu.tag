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

<c:if test="${not empty sessionScope.currentPatient}">
	<div id="workWith" style="padding-left: 5px; margin-right: 15px; margin-top: 10px;">
		<h4><c:out value="${sessionScope.currentPatient.name}" /></h4>
		<p>
			<span id="cnr"></span>
		</p>
		<ul>
			<li><a href="<spring:url value="/netcare/admin/healthplans" />"><spring:message code="admin.menu.patient.healthplans" /></a></li>
			<li><a href="<spring:url value="/netcare/shared/select-results" />"><spring:message code="admin.menu.patient.results" /></a></li>
			<li><a id="quitPatientSession" href="#"><spring:message code="admin.menu.patient.quit" /></a></li>
		</ul>
	</div>
</c:if>

<ul id="permanent">
	<li>
		<a href="<spring:url value="/netcare/home" />">
			<span class="icon start"></span>
			<span class="iconLabel">Startsida</span>
		</a>
	</li>
	<li>
		<a href="<spring:url value="/netcare/admin/patients" />">
			<span class="icon patients"></span>
			<span class="iconLabel">Patienter</span>
		</a>
	</li>
	<li>
		<a href="<spring:url value="/netcare/admin/templates" />">
			<span class="icon templates"></span>
			<span class="iconLabel"><spring:message code="admin.menu.activityType" /></span>
		</a>
	</li>
</ul>
