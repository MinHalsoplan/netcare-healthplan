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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>

<netcare:page>
	<head>
		<title>Test</title>
		
		<link rel="stylesheet" href="<spring:url value="/css/jquery.mobile-1.0.min.css" />" />
		
		<script type="text/javascript" src="<spring:url value="/js/jquery-1.6.2.min.js" />"></script>
		<script type="text/javascript" src="<spring:url value="/js/jquery.mobile-1.0.min.js" />"></script>
		
	</head>
	<body>
		<div data-role="page">
			<div data-role="header">
				<p>Planerade hälsotjänster</p>
			</div>
			<div data-role="content"><p>Content</p></div>
			<div data-role="footer"><p>A footer</p></div>
		</div>
	</body>
</netcare:page>