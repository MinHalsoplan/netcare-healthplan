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
NC.PageMessages = function() {
	
	var _addMessage = function(type, msg) {
		if (msg == undefined || msg == null || msg.length < 1) {
			return;
		}
		
		// don't annoy user with info messages
//		if (type == 'info' || type == 'success') {
//			$.each(msg, function(i, v) {
//				NC.log(v.type + ": " + v.message);				
//			});
//			return;
//		}
		
		var exist = $('#pageMessages div[class*="' + type + '"]').size() == 1;
		if (!exist) {
			$('#pageMessages').append(
				$('<div>').addClass('alert').addClass('alert-' + type).append(
					$('<a>').addClass('close').attr('href', '#').html('×').click(function(e) {
						e.preventDefault();
						$('#pageMessages div[class*="' + type + '"]').slideUp('slow');
					})
				).append(
					$('<ul>')
				)
			);
		}
		
		$.each(msg, function(i, v) {
			var li = $('<li>').html(v.message);
			$('#pageMessages div[class*="' + type + '"] ul').append(li);
			
			setTimeout(function() {
				li.slideUp('slow', function() {
					li.remove();
					
					if (($('#pageMessages div[class*="' + type + '"] ul li').size() - 1) <= 0) {
						$('#pageMessages').slideUp('slow', function() {
							$('#pageMessages div[class*="' + type + '"]').remove();
						});
					}
				});
			}, 5000);
		});
		
		$('#pageMessages').show();
	};
	
	var public = {
		addMessage : function(type, name) {
			_addMessage(type, name);
		},
	
		processServiceResult : function(serviceResult) {
			_addMessage('success', serviceResult.successMessages);
			_addMessage('info', serviceResult.infoMessages);
			_addMessage('warning', serviceResult.warningMessages);
			_addMessage('error', serviceResult.errorMessages);
		}
	};
	
	return public;
	
};