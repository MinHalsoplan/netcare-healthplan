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
NC.Alarm = function() {
	
	var _baseUrl = "/netcare-web/api/alarm";
	
	public = {
		loadAlarms : function(successCallback) {
			var url = _baseUrl + '/list';
			console.log("Loading alarms from url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					
					new NC.Util().processServiceResult(data);
					
					if (data.success && successCallback !== undefined) {
						successCallback(data);
					}
				}
			});
		}	
	};
	
	return public;
};