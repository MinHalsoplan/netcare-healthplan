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
NC.ActivityTypes = function() {
	
	var _baseUrl = '/netcare-web/api/activityType';
	
	public = {
		load : function(callback) {
			var url = _baseUrl + '/load';
			console.log("Loading activity types from url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					if (data.success) {
						console.log("Activity types successfully fetched from server");
						callback(data.data);
					}
				}
			});
		}
	};
	
	return public;
};