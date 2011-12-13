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
NC.Util = function() {
	
	var _displayMessages = function(type, messages) {
		console.log('Displaying ' + type + ' messages...');
		if (messages == null) {
			return false;
		}
		
		if (messages.length > 0) {
			$.each(messages, function(index, value) {
				var msg = $('<div>');
				msg.addClass('alert-message ' + type);
				
				var closeLink = $('<a>', { href : '#' }).addClass('close').html('×').click(function(event){
					event.preventDefault();
					msg.fadeOut('slow').hide();
				});
				
				msg.append(closeLink);
				msg.append('<p><strong>' + value.message + '</strong></p>');
				
				msg.appendTo('#pageMessages');
			});
		}
	};
	
	public = {
		/**
		 * Update the current patient shown in the menu
		 * of the applica
		 */
		updateCurrentPatient : function(name) {
			console.log("Updating current patient. Display: " + name);
			$('#currentpatient a').html(name);
			$('#nopatient').hide();
			$('#currentpatient').show();
		},
	
		displayMessages : function(infoMessages, warningMessages, errorMessages) {
			console.log("Display page messages. Infos: " + infoMessages.length + " Warnings: " + warningMessages.length + " Errors: " + errorMessages.length);
			
			_displayMessages('success', infoMessages);
			_displayMessages('warning', warningMessages);
			_displayMessages('error', errorMessages);
		},
		
		processServiceResult : function(serviceResult) {
			console.log("Processing service results...");
			public.displayMessages(serviceResult.infoMessages, serviceResult.warningMessages, serviceResult.errorMessages);
		},
		
		createIcon : function(name, onClickFunction) {
			var icon = $('<img>', {
				src : '/netcare-web/img/icons/32/' + name + '.png'
			}).css('width', '16px').css('height', '16px').css('padding-left', '10px').css('cursor', 'pointer');
			
			icon.click(onClickFunction);
			
			return icon;
		}
	};
	
	return public;
};