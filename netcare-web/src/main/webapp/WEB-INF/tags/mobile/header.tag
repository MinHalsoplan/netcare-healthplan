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

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Planerade Hälsoaktiviteter</title>
	
	<link rel="stylesheet" href="<spring:url value="/css/jquery.mobile-1.0.min.css" />" />
		
	<script type="text/javascript" src="<spring:url value="/js/jquery-1.6.2.min.js" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/jquery.mobile-1.0.min.js" />"></script>
	
	<script type="text/javascript">
	
		var GLOB_CTX_PATH = '<c:out value="${pageContext.request.contextPath}" />';
	
		NC = {
			log : function(msg) {
				if (typeof console === "undefined" || typeof console.log === "undefined") {
					console = {};
					console.log = function() {};
				}
				
				console.log(msg);
			},
			
			getContextPath : function() {
				return GLOB_CTX_PATH;
			}
		};
	</script>
	
	<!-- Include NETCARE javascripts  -->
	<script type="text/javascript" src="<c:url value='/js/netcare/PageMessages.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/Mobile.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/Util.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/Ajax.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/Patient.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/HealthPlan.js' />"></script>
	<jsp:doBody />
</head>
