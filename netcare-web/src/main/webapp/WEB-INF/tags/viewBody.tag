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
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="title" required="false" %>
<%@ attribute name="backTitle" required="false" %>
<%@ attribute name="backUrl" required="false" %>
<%@ attribute name="backToWhat" required="false" %>
<%@ attribute name="plain" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>
<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<mvk:body>
<sec:authentication property="principal" var="p"/>

<sec:authorize access="hasRole('CARE_ACTOR')">
	<mvk:pageHeader title="${title}"
		loggedInUser="${p.name} (${p.careUnit.name})"
		loggedInAsText="Inloggad som : "
		logoutUrl="${pageContext.request.contextPath}/netcare/security/logout"
		logoutText="Stäng" />
</sec:authorize>
<sec:authorize access="hasRole('PATIENT')">
	<mvk:pageHeader title="${title}"
		loggedInUser="${p.name} (${p.civicRegistrationNumber})"
		loggedInAsText="Inloggad som : "
		logoutUrl="${pageContext.request.contextPath}/netcare/security/logout"
		logoutText="Logga ut" />
</sec:authorize>

<mvk:pageContent>
	
	<mvk:leftMenu>
		<hp:menu />
	</mvk:leftMenu>
	
	<mvk:content title="${title}" 
		backTitle="${backTitle}" 
		backUrl="${backUrl}" 
		backToWhat="${backToWhat}"
		plain="${plain}">
		<jsp:doBody/>
	</mvk:content>
</mvk:pageContent>

</mvk:body>
