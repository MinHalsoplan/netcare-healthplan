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
								
				
				// reporting stuff				
				/*var report = new NC.PatientReport('schemaTable', true);
				report.init();
				report.reportCallback(function(id, done, last) {
					if (last) {
						$('#eventBody').hide();
					}
					var gid = 'gauge-' + id;
					var arr = home.perfData();
					for (var i = 0; i < arr.length; i++) {
						if (arr[i].id == gid) {
							var pd = arr[i];
							pd.numDone += done;
							var pctDone = Math.ceil((pd.numDone / pd.numTarget)*100);						
					        pd.options.max = Math.max(120, pctDone);
							pd.data.setValue(0, 1, pctDone);
				        	pd.gauge.draw(pd.data, pd.options);
				        	break;
						}
					}
				});*/

			});

		</script>
	</hp:viewHeader>
	<hp:viewBody title="Startsida">
		
		<section id="report">
			<mvk:touch-list id="reportList">
			
			</mvk:touch-list>
		</section>
		
		<section id="comments" style="display: none;">
			<div class="row-fluid bubble">
				<div class="span12">
					Hello
				</div>
			</div>
		</section>
		
		<br />

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
