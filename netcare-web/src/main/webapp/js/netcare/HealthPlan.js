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
	
	var _baseUrl = "/netcare-web/api/healthplan";
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
		var ms = '';
		$.each(data, function(index, value) {
			var target;
			if (value.measurementDefinition.measurementType.valueType.code == 'INTERVAL') {
				target = value.minTarget + '-' + value.maxTarget;
			} else {
				target = value.target;
			}
			if (index > 0) {
				ms += '<br/>'
			}
			ms += value.measurementDefinition.measurementType.name + ':&nbsp;' + value.reportedValue + '&nbsp;' 
				+ _util.formatUnit(value.measurementDefinition.measurementType.unit) + '&nbsp;(' + target + ')';
		});
		NC.log(ms);
		return ms;
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
			var url = _baseUrl + '/' + healthPlanId + '/delete';
			NC.log("Removing health plan " + healthPlanId + " using url: " + url);
			
			$.ajax({
				url : url,
				type : 'post',
				success : function(data) {
					NC.log('Deletion of health plan succeeded.');
					_util.processServiceResult(data);
					callback(data);					
				}
			});
		},
		
		/**
		 * View a single ordination
		 */
		view : function(healthPlanId) {
			NC.log("GET to view ordination with id: " + healthPlanId);
			window.location = '/netcare-web/netcare/user/healthplan/' + healthPlanId + '/view';
		},
		
		results : function(healthPlanId) {
				NC.log("GET to view health plan results. Health plan id: " + healthPlanId);
				window.location = '/netcare-web/netcare/user/results?healthPlan=' + healthPlanId;
		},
		
		/**
		 * List all activities that exist on a health plan
		 */
		listActivities : function(healthPlanId, tableId) {
			_updateActivityTable(tableId);
			_ajax.get('/healthplan/' + healthPlanId + '/activity/list', function(data) {
				NC.log("Emptying the activity table");
				$('#' + tableId + ' tbody > tr').empty();
				
				$.each(data.data, function(index, value) {
					
					NC.log("Processing id: " + value.id);
					
					var deleteIcon = _util.createIcon('trash', 24, function() {
						NC.log("Delete icon clicked");
						public.deleteActivity(tableId, healthPlanId, value.id);
					});
					
					var actionCol = $('<td>');
					actionCol.css('text-align', 'right');
					
					deleteIcon.appendTo(actionCol);
					
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
			var url = _baseUrl + '/' + healthPlanId + '/activity/new';
			NC.log("Adding new activity using url: " + url);
			$.ajax({
				url : url,
				type : 'post',
				data : formData,
				contentType : 'application/json',
				success : function(data) {
					NC.log("Call was successful!");
					_util.processServiceResult(data);
					
					callback(data);
				}
			});
		},
		
		/**
		 * Delete an activity that is attached to a health
		 * plan.
		 */
		deleteActivity : function(tableId, healthPlanId, activityId) {
			var url = _baseUrl + '/' + healthPlanId + '/activity/' + activityId + '/delete';
			NC.log("Deleting activity using url: " + url);
			
			$.ajax({
				url : url,
				type : 'post',
				contentType : 'application/json',
				success : function(data) {
					NC.log("Delete activity service call successfully executed.");
					_util.processServiceResult(data);
					public.listActivities(healthPlanId, tableId);
				}
			});
		},
		
		loadScheduledActivity : function(activityId, callback) {
			_ajax.get('/healthplan/activity/' + activityId + '/load', callback);
		},
		
		/**
		 * Load the latest reported activities
		 */
		loadLatestReportedActivities : function(containerId) {
			var url = _baseUrl + '/activity/reported/latest';
			NC.log("Loading latest reported activities from url: " + url);
			
			_ajax.get('/healthplan/activity/reported/latest', function(data) {
				$.each(data.data, function(index, value) {
					NC.log("Processing value: " + value);
					
					var patient = value.patient.name + '<br/>' + _util.formatCnr(value.patient.civicRegistrationNumber);
					var typeName = value.definition.type.name;
					var reportedAt = value.reported;
					
					var tr = $('<tr>');
					var name = $('<td>' + patient + '</td>');
					var type = $('<td>' + typeName + '</td>');
					var reported = $('<td style="font-size: 11px">' + _formatMeasurements(value.measurements) + '</td>');
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
			var commentUrl = _baseUrl + '/activity/' + activityId + '/comment';
			NC.log("Posting comment using url: " + commentUrl);
			
			$.ajax({
				url : commentUrl,
				dataType : 'json',
				type : 'post',
				data : { comment : comment },
				success : function(data) {
					NC.log("Successfully commented activity...");
					_util.processServiceResult(data);
					
					if (data.success) {
						callback(data);
					}
				}
			});
		},
		
		sendCommentReply : function(commentId, reply, callback) {
			var curl = _baseUrl + '/activity/reported/comment/' + commentId + '/reply';
			NC.log("Reply to comment " + commentId + " using url: " + curl);
			
			$.ajax({
				url : curl,
				dataType : 'json',
				type : 'post',
				data : { reply : reply },
				success : function(data) {
					_util.processServiceResult(data);
					if (data.success) {
						callback(data);
					}
				}
			});
		},
		
		deleteComment : function(commentId, callback) {
			var url = _baseUrl + '/activity/reported/comments/' + commentId + '/delete';
			NC.log("Deleting comment " + commentId + " using url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				type : 'post',
				success : function(data) {
					_util.processServiceResult(data);
					if (data.success) {
						callback(data);
					}
				}
			});
		}
	};
	
	return public;
};