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
		if (selectElem === undefined) {
			return false;
		}
		
		$.each(options, function(index, value) {
			var opt = $('<option>', { value : value.code });
			opt.html(value.value);
			opt.appendTo(selectElem);
		});
	};
	
	var public = {
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
	};
	
	return public;
};