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

NC.Patient = function() {
	var _baseUrl = "/netcare-web/api/user";
	
	return {
		findPatients : function() {
			console.log("Finding patients. Searching for: ")
			$.ajax({
				url : _baseUrl + '/find',
				dataType : 'json',
				data : { search : search },
				success : function(data) {
					
				}
			})
		}
	}
};

NC.Units = function() {
	
	var _baseUrl = "/netcare-web/api/support/units";
	
	return {
		loadOptions : function(selectElem) {
			
			if (selectElem === undefined) {
				return false;
			}
			
			var url = _baseUrl + '/load';
			console.log("Loading unit options from: " + url);
			
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
		}
	}
}