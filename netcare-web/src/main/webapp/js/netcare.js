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
	
	var _loadOptions = function(url, selectElem) {
		if (selectElem === undefined) {
			return false;
		}
		
		$.ajax({
			url : url,
			dataType : 'json',
			error : function(jqXHR, textStatus, errorThrown) {
				console.log("Error: " + errorThrown);
			},
			success : function(data, textStatus, jqXHR) {
				console.log("Success: " + data.data);
				
				$.each(data.data, function(index, value) {
					console.log("Processing index: " + index + ", data: " + value);
					$('<option>' + value + '</option>').appendTo(selectElem);
				});
			}
		});
	};
	
	return {
		/**
		 * Load all unit options that exist in the
		 * application.
		 */
		loadOptions : function(selectElem) {
			var url = _baseUrl + '/units/load';
			console.log("Loading unit options from: " + url);
			
			_loadOptions(url, selectElem);
		},
	
		loadDurations : function(selectElem) {
			var url = _baseUrl + '/durations/load';
			console.log("Loading durations from: " + url);
			
			_loadOptions(url, selectElem);
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
		
		list : function(tableElem) {
		
		},
	
		create : function() {
		
		}
	}
};