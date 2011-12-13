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
NC.Patient = function() {
	var _baseUrl = "/netcare-web/api/user";
	
	var public = {
		/**
		 * Called when the care giver wants to find a patient
		 */
		findPatients : function(searchValue, successFunction) {
			console.log("Finding patients. Searching for: " + searchValue);
			
			if (searchValue.length < 3) {
				return false;
			}
			
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
	};
	
	return public;
};