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

NC.ActivityCategories = function() {
	var _baseUrl = '/netcare-web/api/activityCategory';
	var _this = this;
	
	public = {
		load : function(callback) {
			var url = _baseUrl + '/load';
			console.log("Loading activity categories from url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					if (data.success) {
						console.log("Activity categories successfully fetched from server");
						callback(data);
					}
				}
			});
		},
		
		loadAsOptions : function(selectElem) {
			console.log("Loading activity categories as options...");
			var util = new NC.Util();
			
			new NC.ActivityCategories().load(function(data) {
				var arr = new Array();
				
				$.each(data.data, function(index, value) {
					var item = new Object();
					item.code = value.id;
					item.value = value.name;
					
					console.log("Item created: " + item.code + " " + item.value);
					
					arr.push(item);
				});
				
				util.createOptions(arr, selectElem);
			});
		},
		
		create : function(formData, callback) {
			var url = _baseUrl + '/create';
			console.log("Creating new activity category using url " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				type : 'post',
				data : formData,
				contentType : 'application/json',
				success : function(data) {
					new NC.Util().processServiceResult(data);
					
					if (data.success) {
						callback(data);
					}
				}
			})
		}
	};
	
	return public;
};

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
		},
		
		search : function(searchString, callback) {
			var url = _baseUrl + '/search';
			console.log("Searching for activity types with name like " + searchString + " from url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				data : { text : searchString },
				success : function(data) {
					if (data.success) {
						console.log("Successfully searched for activity types...");
						callback(data);
					} else {
						console.log("Error searching for activity types...");
					}
				}
			});
		},
		
		create : function(formData, callback) {
			var url = _baseUrl + '/create';
			console.log("Creating new activity type using url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				type : 'post',
				data : formData,
				contentType : 'application/json',
				success : function(data) {
					new NC.Util().processServiceResult(data);
					if (data.success) {
						callback(data);
					}
				}
			});
		}
	};
	
	return public;
};