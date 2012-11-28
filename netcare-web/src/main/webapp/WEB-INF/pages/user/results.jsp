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
		<script src="/js/highcharts-2.3.3/highcharts.js" type="text/javascript"></script>
		<script src="/js/highstock-1.2.4/highstock.js" type="text/javascript"></script>
		
		<script type="text/javascript">

			var over = [
			            [Date.UTC(2012,11,01), 100],
			            [Date.UTC(2012,11,02), 110],
			            [Date.UTC(2012,11,03), 110],
			            [Date.UTC(2012,11,04), 110],
			            [Date.UTC(2012,11,05), 130],
			            [Date.UTC(2012,11,06), 130],
			            [Date.UTC(2012,11,07), 120],
			            [Date.UTC(2012,11,08), 120],
			            [Date.UTC(2012,11,09), 130],
			            [Date.UTC(2012,11,10), 110],
			            [Date.UTC(2012,11,11), 110],
			            [Date.UTC(2012,11,12), 120],
			            [Date.UTC(2012,11,13), 110],
			            [Date.UTC(2012,11,14), 120],
			            [Date.UTC(2012,11,15), 110],
			            [Date.UTC(2012,11,16), 115],
			            [Date.UTC(2012,11,17), 115],
			            [Date.UTC(2012,11,18), 110],
			            [Date.UTC(2012,11,19), 110],
			         ];
			var under = [
			            [Date.UTC(2012,11,01), 60],
			            [Date.UTC(2012,11,02), 60],
			            [Date.UTC(2012,11,03), 65],
			            [Date.UTC(2012,11,04), 60],
			            [Date.UTC(2012,11,05), 75],
			            [Date.UTC(2012,11,06), 75],
			            [Date.UTC(2012,11,07), 70],
			            [Date.UTC(2012,11,08), 80],
			            [Date.UTC(2012,11,09), 80],
			            [Date.UTC(2012,11,10), 70],
			            [Date.UTC(2012,11,11), 60],
			            [Date.UTC(2012,11,12), 60],
			            [Date.UTC(2012,11,13), 70],
			            [Date.UTC(2012,11,14), 70],
			            [Date.UTC(2012,11,15), 65],
			            [Date.UTC(2012,11,16), 65],
			            [Date.UTC(2012,11,17), 65],
			            [Date.UTC(2012,11,18), 60],
			            [Date.UTC(2012,11,19), 60],
			         ];
			
			$(function() {
				
				var chart1; // globally available
				$(document).ready(function() {
				      chart1 = new Highcharts.StockChart({
				         chart: {
				            renderTo: 'chartcontainer',
				            type: 'line'
				         },
				         rangeSelector: {
					            enabled: false
				         },
				         title: {
				            text: 'Blodtryck'
				         },
				         xAxis: {
				            dateTimeLabelFormats: {
				                second: '%Y-%m-%d<br/>%H:%M:%S',
				                minute: '%Y-%m-%d<br/>%H:%M',
				                hour: '%Y-%m-%d<br/>%H:%M',
				                day: '%Y<br/>%m-%d',
				                week: '%Y<br/>%m-%d',
				                month: '%Y-%m',
				                year: '%Y'
				            }
				         },
				         yAxis: {
				            title: {
				               text: 'mmHg'
				            },
				         },
				         series: [{
				            name: 'Övertryck',
				            data: over
				         }, {
				            name: 'Undertryck',
				            data: under
				         }]
				      });
				   });
				
				
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