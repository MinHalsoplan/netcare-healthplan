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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			$(function() {
				var support = NC.Support();
				support.loadDurations($('#createordinationform select'));
				
				var ordinations = NC.Ordinations($('#ordinationDescription'), $('#ordinationTable'));
				ordinations.init();
				
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2><spring:message code="ordinations" /></h2>
			<p>
				Den här sidan låter dig skapa en ordination för en patient. Ordinationen kan sedan schemaläggas som sedan patient
				kan rapportera på. Beskrivande text... bla bla.
			</p>
			
			<spring:message code="create" var="title" scope="page" />
			<spring:message code="clear" var="clear" scope="page" />
			<spring:message code="name" var="name" scope="page" />
			<spring:message code="type" var="type" scope="page" />
			
			<netcare:form title="${title}" id="createordinationform" classes="form-stacked">
				<netcare:field name="name" label="${name}">
					<input type="text" name="name" class="xlarge" />
				</netcare:field>
				
				<netcare:field name="type" label="${type}">
					<select name="type"></select>
				</netcare:field>
				
				<div class="actions">
					<input type="submit" class="btn primary" value="${title}" />
					<input type="reset" class="btn" value="${clear}" />
				</div>
				
			</netcare:form>
			
			<h3>Aktuella ordinationer</h3>
			<p id="ordinationDescription"></p>
			<table id="ordinationTable" class="bordered-table zebra-striped">
				<thead>
					<tr>
						<th><spring:message code="name" /></th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			
		</netcare:content>
		<netcare:menu />
	</netcare:body>
</netcare:page>