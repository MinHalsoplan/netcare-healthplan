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

<c:url value="/netcare/user/home" var="userHome" scope="page" />

<script type="text/javascript">
	$(function() {
		var patientId = "<sec:authentication property='principal.id' />";
		var crn = "<sec:authentication property="principal.civicRegistrationNumber" />";
		
		var format = new NC.Util().formatCnr(crn);
		
		$('#crn').html(format);
		
		var hps = new NC.HealthPlan();
		hps.list(patientId, function(data) {
			var util = new NC.Util();
			$.each(data.data, function(i, v) {
				
				var li = $('<li>');
				li.append(
					util.createIcon('add', 16)
				);
				
				var link = $('<a>').html(v.name);
				link.click(function(e) {
					window.location = NC.getContextPath() + '/netcare/user/healthplan/' + v.id + '/view';
				});
				
				li.append(link);
				
				$('#menuHealthplans').append(li);
			});
			
		}, false);
	});
</script>

<div class="span4">
	<h3 class="menu"><netcare:image name="auth" size="16"/><spring:message code="loggedInAs" /></h3>
	<p>
		<a href="#"><sec:authentication property="principal.name" /></a> | <a href="<spring:url value="/netcare/security/logout" htmlEscape="true"/>"><spring:message code="logout" /></a>
	</p>
	<p>
		<strong><spring:message code="cnr" />:</strong> <span id="crn"></span>
	</p>
	
	<ul class="menu">
		<li><netcare:image name="user" size="16" /><a href="<spring:url value="/netcare/user/profile" />"><spring:message code="phome.profile" /></a>
	</ul>
	
	<h3 class="menu"><spring:message code="patient.menu.healthplans" /></h3>
	<ul class="menu">
		<li><netcare:image name="edit" size="16" /><a id="reportLink" href="<spring:url value="/netcare/user/report" />"><spring:message code="patient.menu.report" /></a></li>
		<li><netcare:image name="results" size="16" /><a id="resultLink" href="<spring:url value="/netcare/user/results" />"><spring:message code="patient.menu.results" /></a></li>
	</ul>
	
	<h3 class="menu"><spring:message code="patient.menu.addActivities" /></h3>
	<ul id="menuHealthplans" class="menu"></ul>
</div>
	
</body>
