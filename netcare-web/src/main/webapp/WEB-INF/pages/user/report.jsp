
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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>

<mvk:page>
	<mvk:header title="Netcare 2.0" resourcePath="/netcare/resources"
		contextPath="${pageContext.request.contextPath}">
		<netcare:js />
		<link href="<c:url value="/css/netcare.css" />" type="text/css" rel="stylesheet" />
		<script type="text/javascript">
			$(function() {
				var report = new NC.PatientReport('schemaTable', false);
				report.init();
			});
		</script>
	</mvk:header>
	<mvk:body>
		<mvk:pageHeader title="Min hälsoplan - Profil" loggedInUser="Testar Test" loggedInAsText="Inloggad som : "
			logoutUrl="/netcare/security/logout" logoutText="Logga ut" />

		<mvk:pageContent>
			<mvk:leftMenu>
				<mvk:menuItem active="true" label="Test" />
				<mvk:menuItem label="Another test" />
			</mvk:leftMenu>
			<mvk:content title="Rapportera resultat">
				<netcare:content>
					<h2>
						<spring:message code="report.header" />
					</h2>
					<p>
						<span class="label label-info"><spring:message code="information" /></span>
						<spring:message code="report.desc" />
					</p>
					<netcare:report />
				</netcare:content>
			</mvk:content>
		</mvk:pageContent>
	</mvk:body>
</mvk:page>