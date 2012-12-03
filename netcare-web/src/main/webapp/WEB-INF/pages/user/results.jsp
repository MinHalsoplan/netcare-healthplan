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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags" %>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags" %>

<hp:view>
	<hp:viewHeader>
		<script src="<c:url value="/js/highstock-1.2.4/highstock.js" />" type="text/javascript"></script>
		
		<hp:templates />
		<script type="text/javascript">
			$(function() {

				var params = {
					activityId : <c:out value="${param.activity}" />
				};
				
				NC_MODULE.RESULTS.init(params);
				
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Resultat">
		<h2><spring:message code="result.title" /></h2>
		<p>
			<span class="label label-info"><spring:message code="information" /></span>
			<spring:message code="result.desc" />
		</p>
		<div id="pieChart" style="display: none;" class="shadow-box"></div><br />
		
		<section id="filter" style="display:none;">
			<h2><spring:message code="result.filter" /></h2>
			<p>
				<span class="label label-info"><spring:message code="information" /></span>
				<spring:message code="result.filter.desc" />
			</p>
			<form>
				<netcare:row id="filter-row"></netcare:row>
			</form>
		</section>
		<div id="activities"></div>
		<div id="chartcontainer" style="width: 100%; height: 400px"></div>
	</hp:viewBody>
</hp:view>