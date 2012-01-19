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
	
	var _updateActivityTable = function(tableId) {
		console.log("Updating activity table. Activity count = " + _activityCount);
		if (_activityCount != 0) {
			console.log("Show activity table");
			$('#activityContainer div').hide();
			$('#' + tableId).show();
		} else {
			console.log("Hide activity table");
			$('#activityContainer div').show();
			$('#' + tableId).hide();
		}
	};
	
	var public = {
			
		load : function(healthPlanId, callback) {
			var url = _baseUrl + '/' + healthPlanId + '/load';
			console.log("Loading health plan from url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					new NC.Util().processServiceResult();
					callback(data);
				}
			});
		},
		
		list : function(currentPatient, callback) {
			console.log("Load active ordinations for the current patient");
			$.ajax({
				url : _baseUrl + '/' + currentPatient + '/list',
				dataType : 'json',
				success : function(data) {
					new NC.Util().processServiceResult(data);
					callback(data);
				}
			});
		},
	
		/**
		 * Create a new ordination for the current patient.
		 * The method accepts the form data as a json-object which is
		 * sent to the server without any validations.
		 */
		create : function(formData, currentPatient, callback) {
			var url = _baseUrl + '/' + currentPatient + '/create';
			console.log("Creating new ordination. Url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				type : 'post',
				data : formData,
				contentType : 'application/json',
				success :  function(data) {
					console.log('Health plan successfully created');
					new NC.Util().processServiceResult(data);
					
					/* Execute callback */
					callback(data);
				}
			});
		},
		
		/**
		 * Delete an ordination
		 */
		delete : function(healthPlanId, callback) {
			var url = _baseUrl + '/' + healthPlanId + '/delete';
			console.log("Removing health plan " + healthPlanId + " using url: " + url);
			
			$.ajax({
				url : url,
				type : 'post',
				success : function(data) {
					console.log('Deletion of health plan succeeded.');
					new NC.Util().processServiceResult(data);
					callback(data);					
				}
			});
		},
		
		/**
		 * View a single ordination
		 */
		view : function(healthPlanId) {
			console.log("GET to view ordination with id: " + healthPlanId);
			window.location = '/netcare-web/netcare/user/healthplan/' + healthPlanId + '/view';
		},
		
		results : function(healthPlanId) {
				console.log("GET to view health plan results. Health plan id: " + healthPlanId);
				window.location = '/netcare-web/netcare/user/results?healthPlan=' + healthPlanId;
		},
		
		/**
		 * List all activities that exist on a health plan
		 */
		listActivities : function(healthPlanId, tableId) {
			var url = _baseUrl + '/' + healthPlanId + '/activity/list';
			console.log("Loading activities for health plan " + healthPlanId + " from url: " + url);
			
			_updateActivityTable(tableId);
			
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					console.log("Successfully call to rest service");
					var util = new NC.Util();
					if (data.success) {
						console.log("Emptying the activity table");
						$('#' + tableId + ' tbody > tr').empty();
						
						$.each(data.data, function(index, value) {
							
							console.log("Processing id: " + value.id);
							
							var deleteIcon = util.createIcon('trash', 24, function() {
								console.log("Delete icon clicked");
								public.deleteActivity(tableId, healthPlanId, value.id);
							});
							
							var actionCol = $('<td>');
							actionCol.css('text-align', 'right');
							
							deleteIcon.appendTo(actionCol);
							
							var tr = $('<tr>');
							var type = $('<td>' + value.type.name + '</td>');
							var goal = $('<td>' + value.goal + ' ' + value.type.unit.value + '</td>');
							
							tr.append(type);
							tr.append(goal);
							tr.append(actionCol);
							
							$('#' + tableId + ' tbody').append(tr);
						});
						
						_activityCount = data.data.length;
						_updateActivityTable(tableId);
					} else {
						util.processServiceResult(data);
					}
				}
			});
			
		},
		
		loadStatistics : function(healthPlanId, callback) {
			var url = _baseUrl + '/' + healthPlanId + '/statistics';
			console.log("Loading scheduled activities from url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					new NC.Util().processServiceResult(data);
					if (data.success) {
						callback(data);
					}
				}
			});
		},
		
		/**
		 * Add an activity to a health plan
		 */
		addActivity : function(healthPlanId, formData, callback, activityTableId) {
			var url = _baseUrl + '/' + healthPlanId + '/activity/new';
			console.log("Adding new activity using url: " + url);
			$.ajax({
				url : url,
				type : 'post',
				data : formData,
				contentType : 'application/json',
				success : function(data) {
					console.log("Call was successful!");
					new NC.Util().processServiceResult(data);
					
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
			console.log("Deleting activity using url: " + url);
			
			$.ajax({
				url : url,
				type : 'post',
				contentType : 'application/json',
				success : function(data) {
					console.log("Delete activity service call successfully executed.");
					new NC.Util().processServiceResult(data);
					public.listActivities(healthPlanId, tableId);
				}
			});
		},
		
		/**
		 * Load the latest reported activities
		 */
		loadLatestReportedActivities : function(containerId) {
			var url = _baseUrl + '/activity/reported/latest';
			console.log("Loading latest reported activities from url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					var util = new NC.Util();
					
					util.processServiceResult(data);
					
					if (data.success) {
						
						$.each(data.data, function(index, value) {
							console.log("Processing value: " + value);
							
							var patient = value.patient.name + ' (' + util.formatCnr(value.patient.civicRegistrationNumber) + ')';
							var unit = value.definition.type.unit.value;
							var typeName = value.definition.type.name;
							var goalString = value.definition.goal + ' ' + unit;
							var reportedString = value.actualValue + ' ' + unit;
							var reportedAt = value.reported;
							
							var tr = $('<tr>');
							
							var name = $('<td>' + patient + '</td>');
							var type = $('<td>' + typeName + '</td>');
							var goal = $('<td>' + goalString + '</td>');
							var reported = $('<td>' + reportedString + '</td>');
							var at = $('<td>' + reportedAt + '</td>');
							
							console.log("Actual: " + value.actual + ", Goal: " + value.definition.goal);
							if (value.actualValue !== value.definition.goal) {
								console.log("Value diff. Mark yellow");
								reported.css('background-color', 'lightyellow');
							} else if (value.actualValue == value.definition.goal) {
								reported.css('background-color', 'lightgreen');
							}
							
							var aElem = $('<a data-controls-modal="commentActivity" data-backdrop="true">');
							var likeIcon = util.createIcon('like', 24, function() {
								
								console.log("Clicked on " + value.id);
								
								$('#commentActivity button').click(function(event) {
									event.preventDefault();
									
									console.log("Executing... " + value.id);
									
									var comment = $('#commentActivity input[name="comment"]').val();
									public.sendComment(value.id, comment, function(data) {
										console.log("Successfully posted " + comment);
										
										$('#commentActivity input[name="comment"]').val('');
										$('#commentActivity').modal('hide');
										
										$('#commentActivity button').unbind('click');
									});
								});
							});
							
							var actionCol = $('<td>');
							aElem.append(likeIcon);
							actionCol.append(aElem);
							
							tr.append(name);
							tr.append(type);
							tr.append(goal);
							tr.append(reported);
							tr.append(at);
							tr.append(actionCol);
							
							$('#' + containerId + ' table tbody').append(tr);
							console.log("Appended to table body");
						});
						
						var count = $('#' + containerId + ' table tbody tr').size();
						if(count > 0) {
							$('#' + containerId + ' table').show();
							$('#' + containerId + ' div').hide();
						} else {
							$('#' + containerId + ' table').hide();
							$('#' + containerId + ' div').show();
						}
						
					} else {
						util.processServiceResult(data);
					}
				}
			});
		},
		
		loadLatestComments : function(patientId, callback) {
			var url = _baseUrl + '/activity/reported/' + patientId + '/comments';
			console.log("Loading latest comments using url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					new NC.Util().processServiceResult(data);
					
					if (data.success) {
						callback(data);
					}
				}
			});
		},
		
		loadNewReplies : function(callback) {
			var url = _baseUrl + '/activity/reported/comments/newreplies';
			console.log("Loading new replies using url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					new NC.Util().processServiceResult(data);
					if (data.success && callback !== undefined) {
						callback(data);
					}
				}
			});
		},
		
		sendComment : function(activityId, comment, callback) {
			var commentUrl = _baseUrl + '/activity/' + activityId + '/comment';
			console.log("Posting comment using url: " + commentUrl);
			
			$.ajax({
				url : commentUrl,
				dataType : 'json',
				type : 'post',
				data : { comment : comment },
				success : function(data) {
					console.log("Successfully commented activity...");
					new NC.Util().processServiceResult(data);
					
					if (data.success && callback !== undefined) {
						callback(data);
					}
				}
			});
		},
		
		sendCommentReply : function(commentId, reply, callback) {
			var url = _baseUrl + '/activity/reported/comment/' + commentId + '/reply';
			console.log("Reply to comment " + commentId + " using url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				type : 'post',
				data : { reply : reply },
				success : function(data) {
					new NC.Util().processServiceResult(data);
					if (data.success && callback !== undefined) {
						callback(data);
					}
				}
			});
		},
		
		deleteComment : function(commentId, callback) {
			var url = _baseUrl + '/activity/reported/comments/' + commentId + '/delete';
			console.log("Deleting comment " + commentId + " using url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				type : 'post',
				success : function(data) {
					new NC.Util().processServiceResult(data);
					if (data.success && callback !== undefined) {
						callback(data);
					}
				}
			});
		}
	};
	
	return public;
};