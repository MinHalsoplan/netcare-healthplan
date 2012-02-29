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
NC.HealthPlan = function(descriptionId, tableId) {
	
	var _activityCount = 0;
	
	var _ajax = new NC.Ajax();
	var _util = new NC.Util();
	
	var _updateActivityTable = function(tableId) {
		NC.log("Updating activity table. Activity count = " + _activityCount);
		if (_activityCount != 0) {
			NC.log("Show activity table");
			$('#activityContainer div').hide();
			$('#' + tableId).show();
		} else {
			NC.log("Hide activity table");
			$('#activityContainer div').show();
			$('#' + tableId).hide();
		}
	};
	
	var _formatMeasurements = function(data) {
		NC.log('formatMeasurements()')
		var rc = new Object();
		rc.alarm = false;
		rc.html = '';
		
		$.each(data, function(index, value) {
			var target;
			var alarm;
			if (value.measurementDefinition.measurementType.valueType.code == 'INTERVAL') {
				target = value.minTarget + '-' + value.maxTarget;
				alarm = (value.reportedValue < value.minTarget || value.reportedValue> value.maxTarget);
				if (alarm) {
					rc.alarm = true;
				}
			} else {
				target = value.target;
			}
			if (index > 0) {
				rc.html += '<br/>'
			}
			
			var report = alarm ? '<i style="font-weight: bold">' + value.reportedValue + '</i>' : value.reportedValue ;
			
			rc.html += value.measurementDefinition.measurementType.name + ':&nbsp;' + report + '&nbsp;' 
				+ _util.formatUnit(value.measurementDefinition.measurementType.unit) + '&nbsp;(' + target + ')';
			
		});		
		return rc;
	};

	var public = {
			
		load : function(healthPlanId, callback) {
			_ajax.get('/healthplan/' + healthPlanId + '/load', callback, true);
		},
		
		list : function(currentPatient, callback) {
			_ajax.get('/healthplan/' + currentPatient + '/list', callback, true);
		},
	
		/**
		 * Create a new health plan for the current patient.
		 * The method accepts the form data as a json-object which is
		 * sent to the server without any validations.
		 */
		create : function(formData, currentPatient, callback) {
			_ajax.post('/healthplan/' + currentPatient + '/create', formData, callback, true);
		},
		
		/**
		 * Delete an ordination
		 */
		remove : function(healthPlanId, callback) {
			_ajax.post('/healthplan/' + healthPlanId + '/delete', null, callback, true);
		},
		
		/**
		 * View a single ordination
		 */
		view : function(healthPlanId) {
			NC.log("GET to view ordination with id: " + healthPlanId);
			window.location = NC.getContextPath() + '/netcare/user/healthplan/' + healthPlanId + '/view';
		},
		
		results : function(healthPlanId) {
				NC.log("GET to view health plan results. Health plan id: " + healthPlanId);
				window.location = NC.getContextPath() + '/netcare/user/results?healthPlan=' + healthPlanId;
		},
		
		/**
		 * List all activities that exist on a health plan
		 */
		listActivities : function(healthPlanId, tableId, isPatient) {
			_updateActivityTable(tableId);
			_ajax.get('/healthplan/' + healthPlanId + '/activity/list', function(data) {
				NC.log("Emptying the activity table");
				$('#' + tableId + ' tbody > tr').empty();
				
				$.each(data.data, function(index, value) {
					
					NC.log("Processing id: " + value.id);
					
					NC.log("Public: " + value.publicDefinition);
					NC.log("Patient: " + isPatient);
					
					if ((!value.publicDefinition && isPatient) || value.publicDefinition) {
						var deleteIcon = _util.createIcon('trash', 24, function() {
							NC.log("Delete icon clicked");
							public.deleteActivity(tableId, healthPlanId, value.id);
						});
						
						var actionCol = $('<td>');
						actionCol.css('text-align', 'right');
						
						if (isPatient === undefined || !isPatient) {
							deleteIcon.appendTo(actionCol);
						}
						
						var tr = $('<tr>');
						tr.append(
							$('<td>').html(value.type.name)
						).append(
							$('<td>').html(value.type.category.name)
						).append(
							$('<td>').html(value.startDate)
						).append(
							$('<td>').html(_util.formatFrequency(value))
						);
						
						tr.append(actionCol);
						$('#' + tableId + ' tbody').append(tr);
					}
				});
				
				_activityCount = data.data.length;
				_updateActivityTable(tableId);
			}, true);
		},
		
		loadStatistics : function(healthPlanId, callback) {
			_ajax.get('/healthplan/' + healthPlanId + '/statistics', callback, true);
		},
		
		/**
		 * Add an activity to a health plan
		 */
		addActivity : function(healthPlanId, formData, callback, activityTableId) {
			_ajax.post('/healthplan/' + healthPlanId + '/activity/new', formData, callback, true);
		},
		
		/**
		 * Delete an activity that is attached to a health
		 * plan.
		 */
		deleteActivity : function(tableId, healthPlanId, activityId) {
			_ajax.post('/healthplan/' + healthPlanId + '/activity/' + activityId + '/delete', null, function(data){
				public.listActivities(healthPlanId, tableId);
			}, true);
		},
		
		loadScheduledActivity : function(activityId, callback) {
			_ajax.get('/healthplan/activity/' + activityId + '/load', callback);
		},
		
		/**
		 * Load the latest reported activities
		 */
		loadLatestReportedActivities : function(containerId) {
			_ajax.get('/healthplan/activity/reported/latest', function(data) {
				$.each(data.data, function(index, value) {
					NC.log("Processing value: " + value);
					
					var patient = value.patient.name + '<br/>' + _util.formatCnr(value.patient.civicRegistrationNumber);
					var typeName = value.definition.type.name;
					var reportedAt = value.reported;
					
					var tr = $('<tr>');
					var name = $('<td>' + patient + '</td>');
					var type = $('<td>' + typeName + '</td>');
					var ms = _formatMeasurements(value.measurements);
					var reported = $('<td>').css('font-size', '11px').html(ms.html);
					reported.css('background-color', ms.alarm ? '#F2DEDE' : '#DFF0D8');
					var at = $('<td>' + reportedAt + '</td>');
					
					var likeIcon = _util.createIcon('like', 24, function() {
						
						NC.log("Clicked on " + value.id);
						
						$('#commentActivity button').click(function(event) {
							event.preventDefault();
							
							NC.log("Executing... " + value.id);
							
							var comment = $('#commentActivity input[name="comment"]').val();
							public.sendComment(value.id, comment, function(data) {
								NC.log("Successfully posted " + comment);
								
								$('#commentActivity input[name="comment"]').val('');
								$('#commentActivity').modal('hide');
								
								$('#commentActivity button').unbind('click');
							});
						});
						
						$('#commentActivity').modal('show');
						$('#commentActivity input[name="comment"]').focus();
					});
					
					var actionCol = $('<td>');
					actionCol.append(likeIcon);
					
					tr.append(name);
					tr.append(type);
					tr.append(reported);
					tr.append(at);
					tr.append(actionCol);
					
					$('#' + containerId + ' table tbody').append(tr);
					NC.log("Appended to table body");
				});
				
				var count = $('#' + containerId + ' table tbody tr').size();
				if(count > 0) {
					$('#' + containerId + ' table').show();
					$('#' + containerId + ' div').hide();
				} else {
					$('#' + containerId + ' table').hide();
					$('#' + containerId + ' div').show();
				}
			}, true);
		},
		
		loadLatestComments : function(patientId, callback) {
			_ajax.get('/healthplan/activity/reported/' + patientId + '/comments', callback, true);
		},
		
		loadNewReplies : function(callback) {
			_ajax.get('/healthplan/activity/reported/comments/newreplies', callback, true);
		},
		
		sendComment : function(activityId, comment, callback) {
			_ajax.postWithParams('/healthplan/activity/' + activityId + '/comment', { comment : comment }, callback, true);
		},
		
		sendCommentReply : function(commentId, reply, callback) {
			_ajax.postWithParams('/healthplan/activity/reported/comment/' + commentId + '/reply', { reply : reply }, callback, true);
		},
		
		deleteComment : function(commentId, callback) {
			_ajax.post('/healthplan/activity/reported/comments/' + commentId + '/delete', null, callback, true);
		}
	};
	
	return public;
};