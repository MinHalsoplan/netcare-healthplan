
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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<hp:view>
	<hp:viewHeader>
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				var params = {
					patientId : '<sec:authentication property="principal.id" />',
					showAll : true,
					due : true,
					reported : true,
					start : new Date().getTime(),
					end : new Date().getTime()
				};
				
				NC_MODULE.PATIENT_EXTRA_REPORT.init(params);
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Mitt schema">
		<section id="extraReport">
			<form>
				<fieldset>
					<legend>1. Välj aktivitet</legend>
					<select id="activityDefinitions"></select>
				</fieldset>
				<fieldset>
					<legend>2. Mata in värden</legend>
					<mvk:touch-list id="reportList"></mvk:touch-list>
				</fieldset>
			</form>
		</section>
	</hp:viewBody>
</hp:view>