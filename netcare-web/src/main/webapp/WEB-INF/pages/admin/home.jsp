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
		<h1>Välkommen till Admin!</h1>
		
		<form id="activityTypeForm" method="post" action="#">
			<fieldset>
				<legend><spring:message code="addActivityType" /></legend>
				<div class="clearfix">
					<label for="activityName"><spring:message code="name" /></label>
					<div class="input">
						<input name="activityName" class="xlarge" size="30" type="text" />
					</div>
				</div>
				
				<div class="clearfix">
					<label for="activityUnit"><spring:message code="unit" /></label>
					<div class="input">
						<select name="unit" class="medium">
						</select>
					</div>
				</div>
				
			</fieldset>
		</form>
	</netcare:body>	
</netcare:page>