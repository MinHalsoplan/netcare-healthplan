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
NC.Ajax = function() {
	
	var _contextPath = '/netcare-web';
	var _basePath = '/api';
	
	var _dataType = 'json';
	var _contentType = 'application/json'
	
	var _util = new NC.Util();
		
	var _defaultSuccess = function(data, show, callback) {
		/*
		 * Show results
		 */
		if (show) {
			_util.processServiceResult(data);
		}
		
		/*
		 * Execute callback 
		 */
		if (data.success && callback !== undefined) {
			callback(data);
		}
	};
	
	var _showMessages = function(show) {
		var showMessages;
		if (show === undefined || show == null) {
			showMessages = false;
		} else {
			showMessages = true;
		}
		
		return showMessages;
	};
	
	public = {
			/**
			 * Execute a GET request
			 */
			get : function(url, callback, displayMessages) {
				var call = _contextPath + _basePath + url;
				console.log("==== AJAX GET " + call + " ====");
				
				$.ajax({
					url : call,
					dataType : _dataType,
					success : function(data) {
						_defaultSuccess(data, _showMessages(displayMessages), callback);
					}
				});
				
			},
			
			post : function(url, data, callback, displayMessages) {
				var call = _contextPath + _basePath + url;
				console.log("==== AJAX POST " + call + " ====");
				
				$.ajax({
					url : call,
					data : data,
					type : 'post',
					contentType : 'application/json',
					success : function(data) {
						_defaultSuccess(data, _showMessages(displayMessages), callback);
					}
				});
			}
	};
	
	return public;
}