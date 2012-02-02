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

NC = {		
		log : function(msg) {
			console.log(msg);
		},

		focusGained : function(inputField) {
			$(inputField).css('background', 'lightcyan');
			$(inputField).select();		
		},

		focusLost : function(inputField) {
			$(inputField).css('background', 'transparent');
		}

};

$(function() {
	
	if (typeof console === "undefined") {
		console = {};
	}
	if (typeof console.log === "undefined") {
		console.log = function() {};
	}

	/*
	 * Bind all autocomplete boxes
	 */
	NC.log("Bind autocomplete fields...");
	$('.nc-autocomplete').autocomplete({
		search : function(event, ui) {
			$(this).addClass('spinner');
		},
		open : function(event, ui) {
			$(this).removeClass('spinner');
		}
	});
	
	$('.nc-autocomplete').blur(function() {
		$(this).removeClass('spinner');
	});
	NC.log("done.");
	
	var handleErrorCode = function(code) {
		window.location.href = '/netcare-web/netcare/error/' + code;
		return false;
	};
	
		
	/*
	 * Select content when a text or number input field gains focus.
	 */
	$('input:text').focus( function (event) { 
		NC.focusGained($(this));
	});
	
	$('input[type=number]').focus( function (event) { 
		NC.focusGained($(this));
	});

	$('input:text').focusout( function () { 
		NC.focusLost($(this));
	});
	
	$('input[type=number]').focusout( function () { 
		NC.focusLost($(this));
	});
	
	/*
	 * Fix for IE8, make sure enter submits a form.
	 */
	$('form').keypress(function(event) {
		// enter
		if (event.which == 13) {
			event.stopPropagation();
		}
	});
	
	/*
	 * Setup ajax status mappings
	 */
	NC.log("Setting upp ajax...");
	$.ajaxSetup({
		dataType : 'json',
		statusCode : {
			404 : function() {
				return handleErrorCode(404);
			},
			403 : function() {
				return handleErrorCode(403);
			},
			500 : function() {
				return handleErrorCode(500);
			}
		}
	});
	
	NC.log("done.");
});








