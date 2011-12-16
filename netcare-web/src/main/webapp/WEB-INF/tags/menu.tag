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
<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="/netcare/admin/home" var="adminHome" scope="page" />

<div class="span4">
	<h3><spring:message code="loggedInAs" /></h3>
	<p><a href="#"><sec:authentication property="principal.username" /></a> | <a href="<spring:url value="/j_spring_security_logout" htmlEscape="true"/>"><spring:message code="logout" /></a>
	<p><strong><spring:message code="careUnit" />:</strong><br />
	<sec:authentication property="principal.careUnit.name" /> <br /><small>(<sec:authentication property="principal.careUnit.hsaId" />)</small>
	<h3><spring:message code="patient" /></h3>
	<c:choose>
		<c:when test="${not empty sessionScope.currentPatient}">
			<p id="nopatient" style="display: none;">
				<spring:message code="noCurrentPatient" /> <a href="${adminHome}"><spring:message code="clickHere" /></a> <spring:message code="toPickPatient" />
			</p>
			<p id="currentpatient" style="display: block;">
				<spring:message code="currentPatient" /> <a href="#"><c:out value="${sessionScope.currentPatient.name}" /></a>
			</p>
		</c:when>
		<c:otherwise>
			<p id="nopatient" style="display: block;">
				<spring:message code="noCurrentPatient" /> <a href="${adminHome}"><spring:message code="clickHere" /></a> <spring:message code="toPickPatient" />
			</p>
			<p id="currentpatient" style="display: none;">
				<spring:message code="currentPatient" /> <a href="#"><c:out value="${sessionScope.currentPatient.name}" /></a>
			</p>
		</c:otherwise>
	</c:choose>
	
	<h3><spring:message code="workWith" /></h3>
	<ul>
		<li><a href="<spring:url value="/netcare/admin/home" />"><spring:message code="switchPatient" /></a>
		<li><a href="<spring:url value="/netcare/admin/healthplan/new" />"><spring:message code="create" /> <spring:message code="healthPlan" /></a>
	</ul>
</div>
	
</body>
