/*
 * Copyright (C) 2011,2012 Callista Enterprise AB <info@callistaenterprise.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
NC.Reports = function(statistics, captions) {
	
	var _statistics = statistics;
	var _captions = captions;
	
	var public = {
		/**
		 * Get a pie chart diagram showing the health plan's activity types
		 */
		getHealthPlanOverview : function(elementId, width, height) {
			var arr = new Array();
			
			$.each(_statistics.data.activities, function(index, value) {
				var item = new Array();
				item[0] = value.name;
				item[1] = value.count;
				
				arr.push(item);
			});
			
			var dataOverview = new google.visualization.DataTable();
			dataOverview.addColumn('string',  _captions.activityType);
			dataOverview.addColumn('number', _captions.numberOfActivities);
			
			dataOverview.addRows(arr);
			
			var options = {'width' : width, 'height' : height};
		
			var diagram = new google.visualization.PieChart(document.getElementById(elementId));
			diagram.draw(dataOverview, options);
		},
		
		/**
		 * Get results for a specific activity type
		 */
		getResultsForActivityType : function(type, elementId, width, height) {
			var measureValueType = new Array();
			$.each(_statistics.data.measuredValues, function(i, v) {
				if (v.name == type) {
					NC.log("Adding " + v.name + " to array");
					measureValueType.push(v);
				}
			});
			
			NC.log("Array consist of " + measureValueType.length + " measure types");
			
			/*
			 * Process each measure type. One diagram for each type
			 */
			$.each(measureValueType, function(i, v) {
				
				NC.log("Processing " + v.valueType.code);
				
				var entries = new Array();
				
				var chart = new google.visualization.DataTable();
				chart.addColumn('string', captions.date);
				if (v.interval) {
					
					chart.addColumn('number', 'Målvärde min');
					chart.addColumn('number', 'Målvärde max');
					chart.addColumn('number', captions.reportedValue);
					
					$.each(v.reportedValues, function(idx, val) {
						var entry = [val.reportedAt, val.minTargetValue, val.maxTargetValue, val.reportedValue];
						
						NC.log("Adding: " + entry);
						entries.push(entry);
					});
				} else {
					
					chart.addColumn('number', captions.targetValue);
					chart.addColumn('number', captions.reportedValue);
					
					$.each(v.reportedValues, function(idx, val) {
						var entry = [val.reportedAt, val.targetValue, val.reportedValue];
						
						NC.log("Adding: " + entry);
						entries.push(entry);
					});
				}
				
				chart.addRows(entries);
				
				/*
				 * Append new div for the diagram
				 */
				var id = elementId + '-' + i;
				$('#' + elementId).append(
					$('<div>').attr('id', id).addClass('shadow-box')
				);
				
				var diagram = new google.visualization.LineChart(document.getElementById(id));
				diagram.draw(chart, { width: width, height : height, title : v.valueType.code});
			});
		}
	};
	
	return public;
}