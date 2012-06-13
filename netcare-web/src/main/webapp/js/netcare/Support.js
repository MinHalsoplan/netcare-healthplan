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
NC.Support = function() {
	var _ajax = new NC.Ajax();
	
	/**
	 * Method that will make call to the server, if the call was
	 * successful, the onDataLoaded()-function will be executed
	 * with the array of values
	 */
	var _loadOptions = function(url, onDataLoaded) {
		_ajax.get(url, function(data) {
			var arr = new Array();
			$.each(data.data, function(index, value) {				
				arr[index] = value;
			});
			
			onDataLoaded(arr);
		});
	};
	
	/**
	 * Loads captions from an array of message properties.
	 * 
	 * Callback is called with an object containing the actual fields (property name) and values (property value).
	 */
	var _loadCaptions = function(url, record, fields, onLoaded) {
		NC.log("Loading support captions from url: " + url);
		var msg = new Object();
		msg.record = record;
		msg.fields = fields;
				
		_ajax.postSynchronous(url, msg, function(data) { onLoaded(data.data); });
	};
	
	var _createOptions = function(options, selectElem) {
		NC.log("Creating options...");
		if (selectElem === undefined) {
			NC.log("Select element is undefined.");
			return false;
		}
		
		$.each(options, function(index, value) {
			NC.log("Creating option: " + value.code);
			var opt = $('<option>', { value : value.code });
			opt.html(value.value);
			opt.appendTo(selectElem);
		});
	};
	
	var public = {			
			
		getUnits : function(callback) {
			_ajax.get('/support/units/load', callback);
		},
		
		getMeasureValueTypes : function(callback) {
			_ajax.get('/support/measureValueTypes', callback);
		},
			
		/**
		 * Load all unit options that exist in the
		 * application.
		 */
		loadUnits : function(selectElem) {
			var url = '/support/units/load';
			
			_loadOptions(url, function(data) {
				_createOptions(data, selectElem);
			});
			
		},
		
		//
		setSelectOptions : function(selectElement, options) {
			_createOptions(options, selectElement);
		},
	
		/**
		 * Load all durations as options that exist in the
		 * application
		 */
		loadDurations : function(selectElem) {
			var url = '/support/durations/load';
			
			_loadOptions(url, function(data) {
				_createOptions(data, selectElem);
			});
		},
		
		loadCaptions : function(record, fields, callback) {
			var url = '/support/caption';
			_loadCaptions(url, record, fields, function(data) {
				callback(data);
			}); 
		},
		
		/**
		 * Load month names
		 */
		loadMonths : function(callback) {
			var url = '/support/months/load';
			_loadOptions(url, function(data) {
				NC.log("Got result: " + data);
				callback(data);
			});
		},
		
		/**
		 * Load weekday names
		 */
		loadWeekdays : function(callback) {
			var url = '/support/weekdays/load';
			_loadOptions(url, function(data) {
				NC.log("Got result: " + data);
				callback(data);
			});
		},
		
		loadMessages : function(c, callback) {
			var messages = new Array();
			_ajax.getWithParamsSynchronous('/support/message', { codes : c }, function(data) {
				$.each(data.data, function(i, v) {
					messages[v.code] = v.value;
				});
				
				callback(messages);
			}, false);
		},
		
		/**
		 * Called when care giver selects a patient to
		 * work with. This method adds the selected patient
		 * to the session scope and the menu should always
		 * display the selected patient
		 */
		selectPatient : function(patientId, successFunction) {
			if (patientId === undefined || patientId == '') {
				throw new Error('Cannot select patient since there is no patient to select');
			}
			_ajax.postSynchronous('/user/' + patientId + '/select', null, successFunction);
		},
		
		unselect : function(callback) {
			_ajax.postSynchronous('/user/unselect', null, callback);
		}
	};
	
	return public;
};