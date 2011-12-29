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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>

<netcare:page>
	<netcare:header>
			<script type="text/javascript">
			$(function() {
				var schema = NC.PatientSchema('schemaDescription', 'schemaTable');
				schema.list();
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h1>Mitt Schema</h1>
			<p id="schemaDescription"></p>
			<table id="schemaTable" class="bordered-table zebra-striped">
				<thead>
					<tr>
						<th colspan='2'><spring:message code="time" />
						</th>
						<th><spring:message code="ActivityDefinitionEntity" />
						</th>
						<th><spring:message code="reportValue" />
						</th>						
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>

		</netcare:content>
		<netcare:patient-menu>
		</netcare:patient-menu>
	</netcare:body>
</netcare:page>