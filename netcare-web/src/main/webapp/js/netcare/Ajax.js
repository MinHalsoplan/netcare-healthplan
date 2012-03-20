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
	
	var _contextPath = NC.getContextPath();
	var _basePath = '/api';
	
	var _dataType = 'json';
	var _contentType = 'application/json'
	
	var _util = new NC.Util();
	var _pm = new NC.PageMessages();
		
	var _defaultSuccess = function(data, show, callback) {
		/*
		 * Show results
		 */
		if (show) {
			_pm.processServiceResult(data);
		}
		
		/*
		 * Execute callback 
		 */
		if (data.success && callback !== undefined) {
			callback(data);
		} else {
			NC.log("Call was not successful. Data success: " + data.success + " Callback: " + callback);
		}
	};
	
	var _showMessages = function(show) {
		var showMessages;
		if (show === undefined || show == null || show == false) {
			showMessages = false;
		} else {
			showMessages = true;
		}
		
		return showMessages;
	};
	
	var _getDefaultOpts = function(url, callback, displayMessages) {
		NC.log("==== AJAX GET " + url + " ====");
		return {
			url : url,
			cache : false,
			success : function(data) {
				_defaultSuccess(data, _showMessages(displayMessages), callback);
			}
		}
	};
	
	var _getDefaultPostOpts = function(url, callback, displayMessages) {
		NC.log("==== AJAX POST " + url + " ====");
		
		return {
			url : url,
			type : 'post',
			success : function(data) {
				_defaultSuccess(data, _showMessages(displayMessages), callback);
			}
		}
	}
	
	public = {
			/**
			 * Execute a GET request
			 */
			get : function(url, callback, displayMessages) {
				var call = _contextPath + _basePath + url;
				$.ajax(_getDefaultOpts(call, callback, displayMessages));
			},
			
			getWithParams : function(url, data, callback, displayMessages) {
				var opts = _getDefaultOpts(_contextPath + _basePath + url, callback, displayMessages);
				opts.data = data;
				
				$.ajax(opts);
			},
			
			getWithParamsSynchronous : function(url, data, callback, displayMessages) {
				var opts = _getDefaultOpts(_contextPath + _basePath + url, callback, displayMessages);
				opts.data = data;
				opts.async = false;
				
				$.ajax(opts);
			},
			
			post : function(url, data, callback, displayMessages) {
				var call = _contextPath + _basePath + url;
				var opts = _getDefaultPostOpts(call, callback, displayMessages);
				opts.contentType = _contentType;
				opts.dataType = _dataType;
				if (data != null) {
					opts.data = JSON.stringify(data);
				}
				
				$.ajax(opts);
			},
			
			postWithParams : function(url, data, callback, displayMessage) {
				var call = _contextPath + _basePath + url;
				var opts = _getDefaultPostOpts(call, callback, displayMessage);
				opts.dataType = _dataType;
				if (data != null) {
					opts.data = data;
				}
				
				$.ajax(opts);
			},
			
			postSynchronous : function(url, data, callback, displayMessages) {
				var call = _contextPath + _basePath + url;
				var opts = _getDefaultPostOpts(call, callback, displayMessages);
				opts.async = false;
				opts.contentType = _contentType;
				opts.dataType = _dataType;
				if (data != null) {
					opts.data = JSON.stringify(data);
				}
				
				$.ajax(opts);
			}
	};
	
	return public;
}
