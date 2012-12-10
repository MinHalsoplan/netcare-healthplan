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
	<script type="text/javascript">
	$.validator.addMethod("personnummer", function (value, element) {

		// Check valid length & form
		if (!value)
			return false;

		if (value.indexOf('-') == -1) {
			if (value.length === 10) {
				value = value.slice(0, 6) + "-"
						+ value.slice(6);
			} else {
				value = value.slice(0, 8) + "-"
						+ value.slice(8);
			}
		}
		if (!value
				.match(/^(\d{2})(\d{2})(\d{2})\-(\d{4})|(\d{4})(\d{2})(\d{2})\-(\d{4})$/))
			return false;

		// Clean value
		value = value.replace('-', '');
		if (value.length == 12) {
			value = value.substring(2);
		}

		// Declare variables
		var d = new Date(
				((!!RegExp.$1) ? RegExp.$1 : RegExp.$5),
				(((!!RegExp.$2) ? RegExp.$2 : RegExp.$6) - 1),
				((!!RegExp.$3) ? RegExp.$3 : RegExp.$7)), sum = 0, numdigits = value.length, parity = numdigits % 2, i, digit;

		// Check valid date
		if (Object.prototype.toString.call(d) !== "[object Date]"
				|| isNaN(d.getTime()))
			return false;

		// Check luhn algorithm
		for (i = 0; i < numdigits; i = i + 1) {
			digit = parseInt(value.charAt(i))
			if (i % 2 == parity)
				digit *= 2;
			if (digit > 9)
				digit -= 9;
			sum += digit;
		}
		return (sum % 10) == 0;
		
	});
	</script>
	<jsp:doBody />
	
</mvk:header>
