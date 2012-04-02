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
	var _support = new NC.Support();
	
	var _msgs;
	_support.loadMessages('result.targetValue, result.targetMinValue, result.targetMaxValue,activity.suspended, activity.suspend, activity.update', function(m) {
		_msgs = m;
	});
	
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
	
	var _initModalForGoalUpdates = function(data, callback) {
		
		/*
		 * Update modal header
		 */
		var title = $('#update-goal-values h3').html();
		$('#update-goal-values h3').html(title + ' : ' + data.type.name);
		
		/*
		 * Build modal form based on goal values 
		 */
		$.each(data.goalValues, function(i, v) {
			var row = $('<div>').prop('id', 'goal-values').addClass('row');
			var inner = $('<div>').addClass('span9');
			
			inner.append(
				$('<div>').addClass('span1').append($('<label>').html('&nbsp;')).append(
					$('<span>').html('<strong>' + v.measurementType.name + '</strong>')
				)
			);
			
			if (v.measurementType.valueType.code == "SINGLE_VALUE") {
				
				var col = $('<div>').addClass('span3');
				
				var input = $('<input>').prop('type', 'text').prop('id', 'goal-value-' + v.id + '-1').prop('value', v.target).addClass('span2');
				_util.validateNumericField(input, 6);
				var div = _util.createInputField(_msgs['result.targetValue'], '(' + v.measurementType.unit.value + ')', input);
				
				col.append(div);
				inner.append(col);
				
			} else if (v.measurementType.valueType.code == "INTERVAL") {
				
				var col = $('<div>').addClass('span3');
				
				var inputMin = $('<input>').prop('type', 'text').prop('id', 'goal-value-' + v.id + '-1').prop('value', v.minTarget).addClass('span2');
				_util.validateNumericField(inputMin, 6);
				var divMin = _util.createInputField(_msgs['result.targetMinValue'], '(' + v.measurementType.unit.value + ')', inputMin);
				
				col.append(divMin);
				
				var col2 = $('<div>').addClass('span3');
				
				var inputMax = $('<input>').prop('type', 'text').prop('id', 'goal-value-' + v.id + '-2').prop('value', v.maxTarget).addClass('span2');
				_util.validateNumericField(inputMax, 6);
				var divMax = _util.createInputField(_msgs['result.targetMaxValue'], '(' + v.measurementType.unit.value + ')', inputMax);
				
				col2.append(divMax);
				
				inner.append(col).append(col2);
				
			} else {
				throw new Error("Value type is not single value nor interval. Undefined value type found.");
			}
			
			$('#update-goal-values div.modal-body').append(row.append(inner));
		});
		
		$('#update-goal-values').find('.btn-primary').click(function(e) {
			e.preventDefault();
			
			var measureValues = new Array();
			var processed = 0;
			$.each($('#goal-values input'), function(i, v) {
				var id = $(v).prop('id').split('-')[2];
				if (id != processed) {
					var measure = new Object();
					measure.measurementType = new Object();
					measure.measurementType.id = id;
					
					var val1 = $(v).val();
					var val2 = $('#goal-value-' + id + '-2').val();
					if (typeof val2 === 'undefined') {
						measure.target = val1;
					} else {
						measure.minTarget = val1;
						measure.maxTarget = val2;
					}
					
					measureValues.push(measure);
					processed = id;
				}
			});
			
			var activity = new Object();
			activity.id = data.id;
			activity.goalValues = measureValues;
			
			_ajax.post('/healthplan/' + data.healthPlanId + '/activity/' + data.id + '/updateGoalValues', activity, function(data) {
				$('#update-goal-values').modal('hide');
				
				/*
				 * Reload activities
				 */
				callback();
				
			}, true);
			
		});
		
		$('#update-goal-values').modal('show');
	};

	var public = {
			
		load : function(healthPlanId, callback) {
			_ajax.get('/healthplan/' + healthPlanId + '/load', callback, true);
		},
		
		list : function(currentPatient, callback, display) {
			if (display === undefined || display == null || display == true) {
				_ajax.get('/healthplan/' + currentPatient + '/list', callback, true);
			} else {
				_ajax.get('/healthplan/' + currentPatient + '/list', callback, display);
			}
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
		 * Stops auto renewal.
		 */
		stopAutoRenewal : function(healthPlanId, callback) {
			_ajax.post('/healthplan/' + healthPlanId + '/stopAutoRenewal', null, callback, true);			
		},
		
		/**
		 * Performs manual renewal.
		 */
		performExplicitRenewal : function(healthPlanId, callback) {
			_ajax.post('/healthplan/' + healthPlanId + '/renew', null, callback, true);			
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
					if ((!value.publicDefinition && isPatient) || value.publicDefinition) {
						var deleteIcon = _util.createIcon('trash', 24, function() {
							public.deleteActivity(tableId, healthPlanId, value.id);
						}, _msgs['activity.suspend'], true).css('padding-left', '10px');
						
						var updateIcon = _util.createIcon('update-activity', 24, function() {
							
							_initModalForGoalUpdates(value, function() {
								public.listActivities(healthPlanId, tableId, isPatient);
							});
							
						}, _msgs['activity.update'], true);
						
						var actionCol = $('<td>').css('text-align', 'right');
						var showSuspend = (isPatient === undefined || !isPatient) && value.active;
						if (showSuspend) {
							updateIcon.appendTo(actionCol);
							deleteIcon.appendTo(actionCol);
						}
						
						var tr = $('<tr>');
						tr.append(
							$('<td>').html(value.type.name)
						).append(
							$('<td>').html(value.type.category.name)
						).append(
							$('<td>').html(value.startDate)
						);
						
						if (value.active) {
							tr.append(
								$('<td>').html(_util.formatFrequency(value))
							);
						} else {
							tr.append(
								$('<td>').css('font-style', 'italic').html(_msgs['activity.suspended'])
							);
						}
						
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
		
		loadLatestComments : function(patientId, callback) {
			_ajax.get('/healthplan/activity/reported/' + patientId + '/comments', callback, true);
		},
		
		loadNewReplies : function(callback) {
			_ajax.get('/healthplan/activity/reported/comments/newreplies', callback, true);
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