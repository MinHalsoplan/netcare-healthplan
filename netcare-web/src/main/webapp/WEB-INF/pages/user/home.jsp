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
<%@ taglib prefix="c_rt" uri="http://java.sun.com/jstl/core_rt"%>
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
					patientId : '<sec:authentication property="principal.id" />'
				};
				
				NC_MODULE.COMMENTS.init(params);
				NC_MODULE.SCHEDULE.init(params);
			});

		</script>
	</hp:viewHeader>
	<hp:viewBody title="Startsida">
		
		<section id="comments" style="display: none;">
			
		</section>
		
		<section id="report">
			<div class="sectionLoader" style="display: none;">
				<img src="<c:url value="/netcare/resources/img/loaders/ajax-loader-medium.gif" />" />
				<span class="loaderMessage"></span>
			</div>
			<div id="reportContainer" style="display: none;">
				<h2>Du har aktiviteter att rapportera</h2>
				<mvk:touch-list id="reportList"></mvk:touch-list>
			</div>
		</section>
		
		<section id="my-schedule">
			<h2>Mitt schema</h2>
			
			
			
		</section>

<%-- 		<section id="healthPlan"> --%>
<%-- 			<h2><spring:message code="phome.header" /></h2> --%>
<!-- 			<p> -->
<%-- 				<span class="label label-info"><spring:message code="information" /></span> --%>
<%-- 				<spring:message code="phome.headerDesc" /> --%>
<!-- 			</p> -->
<!-- 			<div id="planDescription" style="margin: 10px"></div> -->
<%-- 			<netcare:table id="planTable"> --%>
<!-- 				<thead> -->
<!-- 					<tr> -->
<!-- 						work-around (twitter bootstrap problem): hard coded width to avoid compression of icon -->
<!-- 						<th width="40px">&nbsp;</th> -->
<%-- 						<th><spring:message code="phome.plan" /></th> --%>
<%-- 						<th><spring:message code="phome.activity" /></th> --%>
<%-- 						<th><spring:message code="phome.until" /></th> --%>
<%-- 						<th><spring:message code="phome.frequency" /></th> --%>
<%-- 						<th><spring:message code="phome.done" /><br /> <input id="totalBoxId" type="checkbox" /> <spring:message code="phome.showTot" /></th> --%>
<!-- 					</tr> -->
<!-- 				</thead> -->
<!-- 				<tbody></tbody> -->
<%-- 			</netcare:table> --%>
<!-- 			<br /> -->
<!-- 			<div style="text-align: right"> -->
<%-- 				<a href="<c:out value="${GLOB_CTX_PATH}" />/api/patient/schema/min-halso-plan"><spring:message code="phome.icalLink" /></a> --%>
<!-- 			</div> -->
<%-- 		</section> --%>
	</hp:viewBody>
</hp:view>
