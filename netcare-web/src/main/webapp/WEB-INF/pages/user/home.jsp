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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			google.load('visualization', '1', {
				packages : [ 'gauge' ]
			});
		</script>
		
		<script type="text/javascript">
			 function createGauge(pd) {
			    // Create and populate the data table.
		        var data = new google.visualization.DataTable();
		        data.addColumn('string', 'Label');
		        data.addColumn('number', 'Value');
		        data.addRows(1);
		        data.setValue(0, 0, pd.unit);
		        data.setValue(0, 1, pd.sumDone);
		        var options = new Object();
		        options.max = Math.max(pd.sumTotal, pd.sumDone);
		        options.min = 0;
		        options.redFrom = 0;
		        options.redTo = pd.sumTarget * 0.6;
		        options.yellowFrom = options.redTo;
		        options.yellowTo =  pd.sumTarget * 0.9;
		        options.greenFrom = options.yellowTo;
		        options.greenTo = options.max;
		        
		        // Create and draw the visualization.
		        new google.visualization.Gauge($('#' + pd.id).get(0)).draw(data, options);
			}
			
			google.setOnLoadCallback(function() {
				$(function() {
					var home = NC.PatientHome('planDescription', 'planTable',
							'eventBody');
					home.status();
					home.list(function() {
						$.each(home.perfData(), function(index, pd) {
							createGauge(pd);
						});
					});
				});
			});

		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h1><spring:message code="phome.header" /></h1>
			<div id="eventBody" style="border-radius: 10px" class="alert-message"></div>
			<div id="planDescription" style="margin: 10px"></div>
			<table id="planTable"
				style="width: 99%; border-radius: 10px; box-shadow: 2px 2px 5px #333;"
				class="condensed zebra-striped">
				<thead>
					<th>&nbsp;</th>
					<th><spring:message code="phome.activity" /></th>
					<th><spring:message code="phome.until" /></th>
					<th><spring:message code="phome.frequency" /></th>
					<th colspan='2'><spring:message code="phome.done" /></th>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div style="text-align: right">
				<a href="results"><spring:message code="phome.resultLink" /></a>&nbsp;|&nbsp; <a
					href="/netcare-web/api/patient/schema/min-halso-plan"><spring:message code="phome.icalLink" /></a>
			</div>
		</netcare:content>
	</netcare:body>
</netcare:page>