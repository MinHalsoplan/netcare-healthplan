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

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<hp:view>
	<hp:viewHeader>
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				NC_MODULE.UNITS.init();
			});
		</script>
	</hp:viewHeader>
	
	<spring:message code="admin.menu.units" var="units" />
	<hp:viewBody title="${units}">
		
		<form id="unitForm">
			<input id="unitId" name="unitId" type="hidden" />
		
			<spring:message code="units.dn" var="dn" />
			<netcare:field name="dn" label="${dn}">
				<input id="dn" type="text" name="dn" />
				<span class="help-block"><small>Exempel: <spring:message code="units.dn.ex" /></small></span>
			</netcare:field>
			
			<spring:message code="units.abbr" var="abbr" />
			<netcare:field name="name" label="${abbr}">
				<input id="name" type="text" name="name" />
				<span class="help-block"><small>Exempel: <spring:message code="units.abbr.ex" /></small></span>
			</netcare:field>
			
			<div class="form-actions">
				<button type="submit" class="btn btn-info btn-primary">Lägg till</button>
		</div>
		</form>
		
		<netcare:table id="measureUnitsTable" style="display: none;">
			<thead>
				<tr>
					<th><spring:message code="units.dn" /></th>
					<th><spring:message code="units.abbr" /></th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody></tbody>
		</netcare:table>
		
	</hp:viewBody>
</hp:view>