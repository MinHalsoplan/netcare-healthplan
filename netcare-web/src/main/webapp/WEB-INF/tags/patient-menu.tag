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
		var cnr = "<sec:authentication property="principal.civicRegistrationNumber" />";
		var format = new NC.Util().formatCnr(cnr);
		
		$('#cnr').html(format);
	});
</script>

<div class="span4">
	<h3><netcare:image name="auth" size="16"/><spring:message code="loggedInAs" /></h3>
	<p>
		<a href="#"><sec:authentication property="principal.name" /></a> | <a href="<spring:url value="/netcare/security/logout" htmlEscape="true"/>"><spring:message code="logout" /></a>
	</p>
	<p>
		<strong><spring:message code="cnr" />:</strong> <span id="cnr"></span>
	</p>
	
	<h3><spring:message code="workWith" /></h3>
	<ul>
		<li><a id="homeLink" href="<spring:url value="/netcare/user/home" />"><spring:message code="phome.header" /></a></li>
		<li><a id="reportLink" href="<spring:url value="/netcare/user/report" />"><spring:message code="report.header" /></a></li>
		<li><a id="resultLink" href="<spring:url value="/netcare/user/results" />"><spring:message code="result.title" /></a></li>
	</ul>
	
	<ul>
		<li><a href="<spring:url value="/netcare/user/profile" />"><spring:message code="phome.profile" /></a></li>
	</ul>
</div>
	
</body>
