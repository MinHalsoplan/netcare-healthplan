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

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
		
			console.log("Load google visualization package...");
			google.load('visualization', '1.0', {'packages' : ['corechart']});
			console.log("done.");
		
			$(function() {
				
				var drawOverview = function() {
					
					var hp = new NC.HealthPlan();
					
					var statisticsCallback = function(data) {
						var arr = new Array();
						
						$.each(data.data.activities, function(index, value) {
							var item = new Array();
							item[0] = value.name;
							item[1] = value.count;
							
							arr.push(item);
						});
						
						var dataOverview = new google.visualization.DataTable();
						dataOverview.addColumn('string',  'Aktivitetstyp');
						dataOverview.addColumn('number', 'Antal aktiviteter');
						
						console.log("Add rows: " + arr);
						dataOverview.addRows(arr);
						
						var options = {'width' : 700, 'height' : 300};
					
						var chart = new google.visualization.PieChart(document.getElementById('pieChart'));
						chart.draw(dataOverview, options);
						
						$('#pieChart').show();
						
						$.each(data.data.reportedActivities, function(index, value) {
							console.log("Processing " + value.name + " ...");
							var chartData = new google.visualization.DataTable();
							chartData.addColumn('string', 'Datum');
							chartData.addColumn('number', 'Rapporterat värde');
							chartData.addColumn('number', 'Uppsatt  målvärde');
							
							var items = new Array();
							$.each(value.reportedValues, function(index, val) {
								var arr = new Array();
								arr[0] = val.first;
								arr[1] = val.second;
								arr[2] = value.goal;
								
								items.push(arr);
							});
							
							console.log("Adding rows: " + items);
							chartData.addRows(items);
							
							var chartDiv = $('<div>', { id: 'activity-' + index});
							$('#activityCharts').append(chartDiv);
							
							var chart = new google.visualization.LineChart(document.getElementById('activity-' + index));
							chart.draw(chartData, { width: 700, height: 300, title: value.name});
							
							$('#pieChart').show();
							$('#activityCharts').show();
						});
					};
					
					hp.list(<sec:authentication property="principal.id" />, function(data) {
						$.each(data.data, function(index, value) {
							healthPlanId = value.id;
							hp.listScheduledActivities(healthPlanId, statisticsCallback);
						});
					});
				};
								
				google.setOnLoadCallback(drawOverview);
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2><spring:message code="myStatistics" /></h2>
			<p>
				<span class="label notice">Information</span>
				Nedan visas hur din hälsoplan är fördelad. Din hälsoplan innehåller aktiviteter och diagrammet visar hur stor del
				dessa aktiviteter utgör av hälsoplanen.
			</p>
			<div id="pieChart" style="display: none;"></div>
			
			<h2>Rapporterade Resultat</h2>
			<p>
				<span class="label notice">Information</span>
				Diagrammen nedan visar dina rapporterade resultat i förhållande till ditt uppsatta målvärde per aktivitet.
			</p>
			<div id="activityCharts" style="display: none;"></div>
		</netcare:content>
		<netcare:patient-menu />
		
	</netcare:body>	
</netcare:page>