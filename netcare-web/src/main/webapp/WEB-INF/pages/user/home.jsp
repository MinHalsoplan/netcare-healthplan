<%--

    Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>

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
				
				var freq = new Array();
				freq[0] = '<spring:message code="util.freq0" />';
				freq[1] = '<spring:message code="util.freq1" />';
				freq[2] = '<spring:message code="util.freq2" />';
				freq[3] = '<spring:message code="util.freq3" />';
				freq[4] = '<spring:message code="util.freq4" />';
				freq[5] = '<spring:message code="util.freq5" />';
				
				var schemaLang = {
					freqs : freq,
					week : '<spring:message code="util.week" />',
					every : '<spring:message code="util.every" />',
					repeat : '<spring:message code="phome.repeat" />'
				};
				
				var params = {
					patientId : '<sec:authentication property="principal.id" />',
					due : true,
					reported : false,
					start : new Date().getTime() - 1209600000,
					end : new Date().getTime(),
					lang : schemaLang
				};
				
				NC_MODULE.COMMENTS.init(params);
				NC_MODULE.SCHEDULE.init(params);
				NC_MODULE.PATIENT_ACTIVITIES.init(params);
			});

		</script>
	</hp:viewHeader>
	<hp:viewBody title="Min hälsoplan" plain="true">
		
		<div id="comments" style="display: none;"></div>
		
		<div id="report">
			<div class="sectionLoader" style="display: none;">
				<img src="<c:url value="/netcare/resources/images/loaders/ajax-loader-medium.gif" />" />
				<span class="loaderMessage"></span>
			</div>
			<div id="reportContainer" style="display: none;">
				<h3 class="title">Du har aktiviteter att rapportera</h3>
				<mvk:touch-list id="reportList"></mvk:touch-list>
			</div>
		</div>
		
		<div id="my-schedule">
			<div class="sectionLoader" style="display: none;">
				<img src="<c:url value="/netcare/resources/images/loaders/ajax-loader-medium.gif" />" />
				<span class="loaderMessage"></span>
			</div>
			<div id="schemaContainer">
				<h3 class="title">Mina aktiviteter</h3>
				<mvk:touch-list id="activity-list"></mvk:touch-list>
			</div>
		</div>
	</hp:viewBody>
</hp:view>
