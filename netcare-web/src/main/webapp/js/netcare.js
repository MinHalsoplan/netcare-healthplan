/*
 * Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>
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

NC = {};

NC.Util = function() {
	return {
		/**
		 * Update the current patient shown in the menu
		 * of the applica
		 */
		updateCurrentPatient : function(name) {
			console.log("Updating current patient. Display: " + name);
			$('#currentpatient a').html(name);
			$('#nopatient').hide();
			$('#currentpatient').show();
		}
	}
}

NC.Patient = function() {
	var _baseUrl = "/netcare-web/api/user";
	
	return {
		/**
		 * Called when the care giver wants to find a patient
		 */
		findPatients : function(searchValue, successFunction) {
			console.log("Finding patients. Searching for: " + searchValue)
			$.ajax({
				url : _baseUrl + '/find',
				dataType : 'json',
				data : { search : searchValue },
				success : successFunction
			});
		},
		
		/**
		 * Called when care giver selects a patient to
		 * work with. This method adds the selected patient
		 * to the session scope and the menu should always
		 * display the selected patient
		 */
		selectPatient : function(patientId, successFunction) {
			console.log("Selecting patient: " + patientId);
			$.ajax({
				url : _baseUrl + '/' + patientId + '/select',
				dataType : 'json',
				type : 'post',
				success : successFunction
			});
		}
	}
};

NC.Support = function() {
	
	var _baseUrl = "/netcare-web/api/support";
	
	/**
	 * Method that will make call to the server, if the call was
	 * successful, the onDataLoaded()-function will be executed
	 * with the array of values
	 */
	var _loadOptions = function(url, onDataLoaded) {
		console.log("Loading support data from url: " + url);
		$.ajax({
			url : url,
			dataType : 'json',
			error : function(jqXHR, textStatus, errorThrown) {
				console.log("Error: " + errorThrown);
			},
			success : function(data, textStatus, jqXHR) {
				var arr = new Array();
				$.each(data.data, function(index, value) {
					arr[index] = value;
				});
				
				onDataLoaded(arr);
			}
		});
	};
	
	var _createOptions = function(options, selectElem) {
		console.log("Creating options... options array is: " + options);
		if (selectElem === undefined) {
			return false;
		}
		
		$.each(options, function(index, value) {
			$('<option>').html(value).appendTo(selectElem);
		});
	};
	
	return {
		/**
		 * Load all unit options that exist in the
		 * application.
		 */
		loadOptions : function(selectElem) {
			var url = _baseUrl + '/units/load';
			
			_loadOptions(url, function(data) {
				_createOptions(data, selectElem);
			});
			
		},
	
		/**
		 * Load all durations as options that exist in the
		 * application
		 */
		loadDurations : function(selectElem) {
			var url = _baseUrl + '/durations/load';
			
			_loadOptions(url, function(data) {
				_createOptions(data, selectElem);
			});
		},
		
		/**
		 * Load month names
		 */
		loadMonths : function(callback) {
			var url = _baseUrl + '/months/load';
			_loadOptions(url, function(data) {
				console.log("Got result: " + data);
				callback(data);
			});
		},
		
		/**
		 * Load weekday names
		 */
		loadWeekdays : function(callback) {
			var url = _baseUrl + '/weekdays/load';
			_loadOptions(url, function(data) {
				console.log("Got result: " + data);
				callback(data);
			});
		}
	}
}

NC.Ordinations = function(descriptionElem, tableElem) {
	var _baseUrl = "/netcare-web/api/ordination";
	var _ordinationCount = 0;
	
	var _descriptionElem = descriptionElem;
	var _tableElem = tableElem;
	
	var _updateDescription = function() {
		console.log("Updating ordination table description");
		if (_ordinationCount == 0) {
			_descriptionElem.html('Inga aktuella ordinationer').show();
			_tableElem.hide();
		}
	}
	
	return {
		
		init : function() {
			_updateDescription();
		},
		
		list : function(tableElem, currentPatient) {
			console.log("Load active ordinations for the current patient");
			$.ajax({
				url : _baseUrl + '/' + currentPatient + '/list',
				dataType : 'json',
				success : function(data) {
					console.log("Success. Processing results...");
					$.each(data.data, function(index, value) {
						console.log("Processing: " + value);
					});
				}
			});
		},
	
		create : function(formId, currentPatient) {
			var url = _baseUrl + '/' + currentPatient + '/create';
			console.log("Creating new ordination. Url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				type : 'post',
				data : {
					name : $('#' + formId + ' input[name="name"]').val(),
					startDate : $('#' + formId + ' input[name="startDate"]').val(),
					duration : $('#' + formId + ' input[name="duration"]').val(),
					durationUnit : $('#' + formId + ' input[name="type"]').val()
				},
				success :  function(data) {
					if (data.success == 'true') {
						console.log('Ordination successfully created');
					}
				}
			});
		}
	}
};