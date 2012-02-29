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
NC.ActivityCategories = function() {
	var _this = this;
	var _ajax = new NC.Ajax();
	
	var public = {
		load : function(callback) {
			_ajax.get('/activityCategory/load', callback, true);
		},
		
		loadAsOptions : function(selectElem) {
			var util = new NC.Util();
			
			public.load(function(data) {
				var arr = new Array();
				$.each(data.data, function(index, value) {
					arr.push({code : value.id, value : value.name});
				});
				
				util.createOptions(arr, selectElem);
			});
		},
		
		create : function(formData, callback) {
			_ajax.post('/activityCategory/create', formData, callback, true);
		}
	};
	
	return public;
};

NC.ActivityTypes = function() {
	var _ajax = new NC.Ajax();
	
	public = {
		load : function(callback) {
			_ajax.get('/activityType/load', callback, true);
		},
		
		search : function(searchString, callback) {
			_ajax.getWithParams('/activityType/search', {text : searchString}, callback);
		},
		
		create : function(formData, callback) {
			_ajax.post('/activityType/create', formData, callback, true);
		}
	};
	
	return public;
};