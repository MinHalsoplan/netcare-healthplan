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
				
				var units = new NC.Units();
				units.loadOptions($('#activityTypeForm select[name="unit"]'));
				
			});
		</script>
	</netcare:header>
	<netcare:body>
		<h1><spring:message code="ordinations" /></h1>
		<p>
			Den här sidan låter dig skapa en ordination för en patient. Ordinationen kan sedan schemaläggas som sedan patient
			kan rapportera på. Beskrivande text... bla bla.
		</p>
		
		<form>
			<fieldset>
				<legend><spring:message code="create" /></legend>
			</fieldset>
		</form>
	</netcare:body>
</netcare:page>