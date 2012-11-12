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
NC.ReportedActivities = function(messages) {
	
	var _ajax = new NC.Ajax();
	var _util = new NC.Util();
	var _support = new NC.Support();
	var _hp = new NC.HealthPlan();
	
	var _msgs = messages;
	
	var public = {
			
			loadData : function(latest, onComplete, onNoData) {
				var load = '';
				if (latest) {
					load = 'latest';
				} else {
					load = 'all';
				}
				
				_ajax.get('/healthplans/activity/reported/' + load, function(data) {
					
					if (data.data.length == 0) {
						onNoData();
						return false;
					}
					
					var arr = new Array();
					
					$.each(data.data, function(i, v) {
						var report = new Object();
						report.id = v.id;
						report.patient = new Object();
						report.patient.id = v.patient.id;
						report.patient.firstName = v.patient.firstName;
						report.patient.surName = v.patient.surName;
						report.patient.crn = v.patient.civicRegistrationNumber;
						report.activity = new Object();
						report.activity.name = v.definition.type.name;
						report.rejected = v.rejected;
						report.reportedAt = v.reported;
						report.healthPlan = new Object();
						report.healthPlan.id = v.definition.healthPlanId;
						report.healthPlan.name = v.definition.healthPlanName;
						report.measuringSense = v.definition.type.measuringSense;
						if (report.measuringSense) {
							report.sense = v.sense;
							report.senseLow = v.definition.type.minScaleText;
							report.senseHigh = v.definition.type.maxScaleText;
						}
						report.note = v.note;
						report.measurements = v.activityItemValues;
						
						arr.push(report);
					});
					
					onComplete(arr);
				}, true);	
			},
			
			loadUI : function(objectArray, onRowCreated, onCommentClick) {
				
				$.each(objectArray, function(i, v) {
					
					NC.log("Processing: " + v.activity.name);
					
					var row = $('<tr>');
					
					row.append(
						$('<td>').html(v.patient.surName + ', ' + v.patient.firstName + '<br />' + _util.formatCnr(v.patient.crn))
					);
					
					row.append(
						$('<td>').html(v.activity.name)		
					);
					
					var resultIcon = _util.createIcon('results', 24, function() {
						
						_support.selectPatient(v.patient.id, function() {
							_hp.results(v.healthPlan.id);
						});
						
						
					}, _msgs['healthplan.icons.result'], true);
					
					var editIcon = _util.createIcon('edit', 24, function() {
						
						_support.selectPatient(v.patient.id, function() {
							_hp.view(v.healthPlan.id);
						});
						
					}, _msgs['healthplan.icons.edit'], true);
					
					row.append(
						$('<td>').append(resultIcon).append(editIcon)
					);
					var note = "Anm: "+ v.note;
					if (v.measuringSense) {
						note += "\nKänsla: " + v.senseLow + "-" + v.senseHigh + " [1-10]: " + v.sense;
					}
					var measures = _util.formatMeasurements(_msgs['report.reject'], v.rejected, v.measurements);
					row.append(
						$('<td>').css('font-size', '11px').css('background-color', measures.alarm ? '#F2DEDE' : '#DFF0D8').attr('title', note).html(measures.html)
					);
					
					row.append(
						$('<td>').html(v.reportedAt)
					);
					
					var commentIcon = _util.createIcon('send-message', 24, function() {
						onCommentClick(v);
					}, _msgs['comments.sendComment'], true);
					
					row.append(
						$('<td>').css('text-align', 'right').append(commentIcon)
					);
					
					onRowCreated(row);
				});
			},
			
			sendComment : function(activityId, message, onComplete) {
				_ajax.postWithParams('/healthplan/activity/' + activityId + '/comment', { comment : message }, onComplete, true);
			}
	};
	
	return public;
};
