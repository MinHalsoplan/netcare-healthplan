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
	<title><spring:message code="system.name" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1"> 
	
	<link rel="stylesheet" href="<spring:url value="/css/jquery.mobile-1.2.0.min.css" />" />

	<style>
		.ui-bar-c {
			color: white;
			text-shadow: none;
			background-color: #2DA1AE;
			background-image: -webkit-gradient(linear,left top,left bottom,from( #33B4C3 ),to( #2DA1AE ));
		}

		.ui-btn-active {
	        background: #008391;
		}
		
		.ui-li-has-count {
	        background: #aaa;
		}
			
		.ui-btn-c {
			background: #8d0017;
		}		
	</style>
			
	<c:set var="contextPath" value="${pageContext.request.contextPath}" />
	<c:set var="resourcePath" value="/netcare/resources" />
	<script type="text/javascript" src="${contextPath}${resourcePath}/js/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="${contextPath}${resourcePath}/js/underscore-1.4.2-min.js"></script>
	<script type="text/javascript">
		_.templateSettings.variable = "us";
		_.templateSettings = {
			interpolate : /\{\{(.+?)\}\}/g // use mustache style delimiters for underscorejs template  
		};
	</script>
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
	<script type="text/javascript" src="<c:url value='${resourcePath}/js/netcare-ui/Util.js' />"></script>
	<script type="text/javascript" src="<c:url value='${resourcePath}/js/netcare-ui/PageMessages.js' />"></script>
	<script type="text/javascript" src="<c:url value='${resourcePath}/js/netcare-ui/Ajax.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare-mobile-healthplan.js' />"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.mobile-1.2.0.min.js" />"></script>	
	<jsp:doBody />
	
</head>
