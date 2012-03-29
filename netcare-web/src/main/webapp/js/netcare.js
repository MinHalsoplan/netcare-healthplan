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
		
		getContextPath : function() {
			return GLOB_CTX_PATH;
		},

		focusGained : function(inputField) {
			$(inputField).css('background', '#D9EDF7');
			$(inputField).select();		
		},

		focusLost : function(inputField) {
			$(inputField).css('background', 'transparent');
		}

};

$(window).load(function() {
	$('#pageLoading').fadeOut('fast', function(e) {
		$(this).hide();
		$('#pageLoadingBox').hide();
	});
});

$(document).ready(function() {
	
	if (typeof console === "undefined") {
		console = {};
	}
	if (typeof console.log === "undefined") {
		console.log = function() {};
	}
	
	var _util = new NC.Util();
	var _support = new NC.Support();
	
	$('.page-header h1').css('background', 'url(' + NC.getContextPath() + '/img/icons/32/heart-logo.png) no-repeat left');
	
	$('#pageLoading').css('height', $(window).height()).show();
	$('#pageLoadingBox').show();
	
	$('#ajaxInProgress').ajaxStart(function() {
		$(this).show();
	});
	
	$('#ajaxInProgress').ajaxStop(function() {
		$(this).hide();
	});

	/*
	 * Bind all autocomplete boxes
	 */
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
	
	var handleErrorCode = function(code) {
		window.location.href = NC.getContextPath() + '/netcare/error/' + code;
		return false;
	};
	
		
	/*
	 * Select content when a text or number input field gains focus.
	 */
	$('input:text').focus( function (event) { 
		NC.focusGained($(this));
	});

	$('input:text').focusout( function () { 
		NC.focusLost($(this));
	});

	$('input:password').focus( function (event) { 
		NC.focusGained($(this));
	});

	$('input:password').focusout( function () { 
		NC.focusLost($(this));
	});
	
	$('input[type="tel"]').focus( function (event) { 
		NC.focusGained($(this));
	});

	$('input[type="tel"]').focusout( function () { 
		NC.focusLost($(this));
	});

	$('input[type="email"]').focus( function (event) { 
		NC.focusGained($(this));
	});

	$('input[type="email"]').focusout( function () { 
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
	
	$('.addButton').css('background', 'url(' + NC.getContextPath() + '/img/icons/16/add.png) no-repeat 3px').css('padding-left', '24px');;
	$('.spinner').css('background', 'url(' + NC.getContextPath() + '/img/ajax-loader-small.gif) no-repeat right');
	
	/*
	 * Process all date fields that exist on the
	 * current page.
	 */
	$('.dateInput').each(function(i, v) {
		
		$(v).datepicker({
			dateFormat : 'yy-mm-dd',
			firstDay : 1,
			minDate : +0
		});
		
		_support.loadMonths(function(data) { $(v).datepicker('option', 'monthNames', data); });
		_support.loadWeekdays(function(data) { $(v).datepicker('option', 'dayNamesMin', data); });
		
		$(v).siblings('span').css('cursor', 'pointer').click(function(e) {
			$(v).datepicker('show');
		});
		
	});
	
	/*
	 * Bind all time fields on the page
	 */
	$('.timeInput').each(function(i, v) {
		_util.validateTimeField($(v));
	});
});








