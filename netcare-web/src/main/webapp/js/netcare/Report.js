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
	
	var _getDefaultOptions = function() {
		return {
			'width' : 600,
			'height' : 300
		}
	};
	
	var public = {
		/**
		 * Get a pie chart diagram showing the health plan's activity types
		 */
		getHealthPlanOverview : function(elementId) {
			var arr = new Array();
			
			$.each(_statistics.data.activities, function(index, value) {
				arr.push([value.name, value.count]);
			});
			
			var dataOverview = new google.visualization.DataTable();
			dataOverview.addColumn('string',  _captions.activityType);
			dataOverview.addColumn('number', _captions.numberOfActivities);
			
			dataOverview.addRows(arr);
			
			var diagram = new google.visualization.PieChart(document.getElementById(elementId));
			diagram.draw(dataOverview, _getDefaultOptions());
		},
		
		/**
		 * Get results for a specific activity type
		 */
		getResultsForActivityType : function(type, elementId) {
			var measureValueType = new Array();
			$.each(_statistics.data.measuredValues, function(i, v) {
				if (v.name == type) {
					measureValueType.push(v);
				}
			});
			
			/*
			 * Process each measure type. One diagram for each type
			 */
			$.each(measureValueType, function(i, v) {
				
				var entries = new Array();
				
				var chart = new google.visualization.DataTable();
				chart.addColumn('string', captions.date);
				if (v.interval) {
					
					chart.addColumn('number', _captions.targetMinValue);
					chart.addColumn('number', _captions.targetMaxValue);
					chart.addColumn('number', _captions.reportedValue);
					
					$.each(v.reportedValues, function(idx, val) {
						entries.push([val.reportedAt, val.minTargetValue, val.maxTargetValue, val.reportedValue]);
					});
				} else {
					
					chart.addColumn('number', captions.targetValue);
					chart.addColumn('number', captions.reportedValue);
					
					$.each(v.reportedValues, function(idx, val) {
						entries.push([val.reportedAt, val.targetValue, val.reportedValue]);
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
				
				var opts = _getDefaultOptions();
				opts.title = v.valueType.code;
				//opts.curveType = 'function';
				
				var diagram = new google.visualization.LineChart(document.getElementById(id));
				diagram.draw(chart, opts);
			});
		}
	};
	
	return public;
}