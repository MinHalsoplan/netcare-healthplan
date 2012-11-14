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
	
	var _msgs = null;
	
	_support.loadMessages('result.targetValue, result.targetMinValue, result.targetMaxValue,activity.suspended, activity.suspend, activity.update', function(m) {
		_msgs = m;
	});
	
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
			_ajax.get('/healthplans/' + healthPlanId + '/load', callback, true);
		},
		
		list : function(currentPatient, callback, display) {
			if (display === undefined || display == null || display == true) {
				_ajax.get('/healthplans/' + currentPatient + '/list', callback, true);
			} else {
				_ajax.get('/healthplans/' + currentPatient + '/list', callback, display);
			}
		},
	
		/**
		 * Create a new health plan for the current patient.
		 * The method accepts the form data as a json-object which is
		 * sent to the server without any validations.
		 */
		create : function(formData, currentPatient, callback) {
			_ajax.post('/healthplans/' + currentPatient + '/create', formData, callback, true);
		},
		
		/**
		 * Delete an ordination
		 */
		remove : function(healthPlanId, callback) {
			_ajax.post('/healthplans/' + healthPlanId + '/delete', null, callback, true);
		},
		
		/**
		 * Stops auto renewal.
		 */
		stopAutoRenewal : function(healthPlanId, callback) {
			_ajax.post('/healthplans/' + healthPlanId + '/stopAutoRenewal', null, callback, true);			
		},
		
		/**
		 * Performs manual renewal.
		 */
		performExplicitRenewal : function(healthPlanId, callback) {
			_ajax.post('/healthplans/' + healthPlanId + '/renew', null, callback, true);			
		},
		
		/**
		 * View a single ordination
		 */
		view : function(healthPlanId) {
			NC.log("GET to view ordination with id: " + healthPlanId);
			window.location = NC.getContextPath() + '/netcare/user/healthplans/' + healthPlanId + '/view';
		},
		
		results : function(healthPlanId) {
				NC.log("GET to view health plan results. Health plan id: " + healthPlanId);
				window.location = NC.getContextPath() + '/netcare/user/results?healthPlan=' + healthPlanId;
		},
		
		loadStatistics : function(healthPlanId, callback) {
			_ajax.get('/healthplans/' + healthPlanId + '/statistics', callback, true);
		},
		
		loadScheduledActivity : function(activityId, callback) {
			_ajax.get('/healthplans/activity/' + activityId + '/load', callback);
		},
		
		loadNewReplies : function(callback) {
			_ajax.get('/healthplans/activity/reported/comments/newreplies', callback, true);
		},
	};
	
	return public;
};