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

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>NetCare</title>
	
	<!-- Include JQuery UI Css -->
	<c:url value="/css/ui-lightness/jquery-ui-1.8.16.custom.css" var="jqueryUiCss" scope="page" />
	<link href="${jqueryUiCss}" type="text/css" rel="stylesheet" />
	
	<!-- Include our CSS -->
	<c:url value="/css/netcare.css" var="netcareCss" scope="page" />
	<link href="${netcareCss}" type="text/css" rel="stylesheet" />
	
	<!-- Include JQuery -->
	<c:url value="/js/jquery-1.6.2.min.js" var="jqueryJs" scope="page" />
	<script type="text/javascript" src="${jqueryJs}"></script>
	
	<!-- Include JQuery UI Widgets -->
	<c:url value="/js/jquery-ui-1.8.16.custom.min.js" var="jqueryUiJs" scope="page" />
	<script type="text/javascript" src="${jqueryUiJs}"></script>
	
	
	<!-- Include  -->
	<c:url value="/js/netcare.js" var="netcareJs" scope="page" />
	<script type="text/javascript" src="${netcareJs}"></script>
	
	<jsp:doBody />
</head>
