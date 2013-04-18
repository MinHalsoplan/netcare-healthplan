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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<hp:view>
	<hp:viewHeader>
		<script type="text/javascript">
			$(function() {
				var params = {
					patientId : <sec:authentication property="principal.id" />
				};
				
				NC_MODULE.PROFILE.init(params);
			});
		</script>
	</hp:viewHeader>
	
	<sec:authentication property="principal.name" var="currentPrincipal" scope="page" />
	<spring:message code="profile.title" arguments="${currentPrincipal}" var="title"/>
	<hp:viewBody title="${title}" plain="true">
		<div id="profile">
			
			<spring:message code="profile.update" var="update" scope="page" />
			
			<mvk:sheet>
			<div id="userprofile">
				<div class="pageMessages"></div>
			
				<form id="profile-form">
					<netcare:row>
						<netcare:col span="12">
							<spring:message code="profile.crn" var="crn" scope="page" />
							<netcare:field name="crn" label="${crn}">
								<input type="text" name="crn" disabled="disabled"/>
							</netcare:field>
						</netcare:col>
					</netcare:row>

					<netcare:row>
						<netcare:col span="6">
							<spring:message code="profile.firstName" var="firstName" scope="page" />
							<netcare:field containerId="firstNameContainer" name="firstName" label="${firstName}">
								<input type="text" id="firstName" name="firstName" class="medium required"/>
							</netcare:field>
						</netcare:col>
						<netcare:col span="6">
							<spring:message code="profile.surName" var="surName" scope="page" />
							<netcare:field containerId="surNameContainer" name="surName" label="${surName}">
								<input type="text" id="surName" name="surName" class="medium required"/>
							</netcare:field>
						</netcare:col>
					</netcare:row>

					<netcare:row>
						<netcare:col span="6">
							<spring:message code="profile.email" var="email" scope="page" />
							<netcare:field name="email" label="${email}">
								<input type="text" name="email" />
							</netcare:field>
						</netcare:col>
						<netcare:col span="6">
							<spring:message code="profile.phone" var="phone" scope="page" />
							<netcare:field name="phone" label="${phone}">
								<input type="text" name="phone" />
							</netcare:field>
						</netcare:col>
					</netcare:row>

					<div class="form-actions">
						<button type="submit" class="btn btn-info">
							<spring:message code="profile.update" />
						</button>
					</div>

				</form>
			</div>
			</mvk:sheet>
		</div>
	</hp:viewBody>
</hp:view>
