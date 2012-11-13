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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>
<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="resourcePath" value="/netcare/resources" />
<mvk:header title="Min hälsoplan" resourcePath="${resourcePath}" contextPath="${contextPath}">
	<netcare:css resourcePath="${resourcePath}" />
	<link rel="stylesheet" href="<c:url value='/css/netcare-healthplan.css' />" type="text/css" />
	
	<netcare:js resourcePath="${resourcePath}"/>
	<hp:healthplan-js />
	
	<%-- Custom javascripts go here --%>
	<jsp:doBody />
	
</mvk:header>
