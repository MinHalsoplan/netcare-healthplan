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
NC.Util = function() {
	/** loadCaptions must be used prior to use functions with captions required. */
	var _captions;

	/**
	 * Loads captions used by util.
	 */
	var _loadCaptions = function() {
		var _support = new NC.Support();
		
		_support.loadCaptions('util', ['week', 'freq0', 'freq1', 'freq2', 'freq3', 
		                             'freq4', 'freq5', 'every', 'meter', 'minute', 
		                             'step', 'events', 'today', 'youhave'], 
		                             function(data) {
			_captions = data;
			NC.log('Localized captions for util loaded.');
		});
	}

	var _displayMessages = function(type, messages) {
		NC.log('Displaying ' + type + ' messages...');
		if (messages == null) {
			return false;
		}
		
		if (messages.length > 0) {
			$.each(messages, function(index, value) {
				_displayMessage(type, value.message);
			});
		}
	};
	
	var _displayMessage = function(type, messageText) {
		NC.log("Displaying: " + messageText);
		var msg = $('<div>');
		msg.addClass('alert-message ' + type);
		
		var closeLink = $('<a>', { href : '#' }).addClass('close').html('×').click(function(event){
			event.preventDefault();
			msg.slideUp('slow');
		});
		
		msg.append(closeLink);
		msg.append('<p><strong>' + messageText + '</strong></p>');
		msg.appendTo('#pageMessages');
		
		setTimeout(function() {
			msg.slideUp('slow');
		}, 5000);
	};
	
	public = {
		/**
		 * Update the current patient shown in the menu
		 * of the applica
		 */
		updateCurrentPatient : function(name) {
			NC.log("Updating current patient. Display: " + name);
			$('#currentpatient a').html(name);
			$('#nopatient').hide();
			$('#currentpatient').show();
		},
	
		displayMessages : function(successMessages, infoMessages, warningMessages, errorMessages) {
			NC.log("Display page messages. Success: " + successMessages.length + " Infos: " + infoMessages.length + " Warnings: " + warningMessages.length + " Errors: " + errorMessages.length);
			
			_displayMessages('success', successMessages);
			_displayMessages('info', infoMessages);
			_displayMessages('warning', warningMessages);
			_displayMessages('error', errorMessages);
		},
		
		processServiceResult : function(serviceResult) {
			NC.log("Processing service results...");
			new NC.Util().displayMessages(serviceResult.successMessages, serviceResult.infoMessages, serviceResult.warningMessages, serviceResult.errorMessages);
		},
		
		createIcon : function(name, size, onClickFunction) {
			var icon = $('<img>', {
				src : '/netcare-web/img/icons/' + size + '/' + name + '.png'
			}).css('padding-left', '10px');
			
			if (onClickFunction != null) {
				icon.css('cursor', 'pointer');
				icon.click(onClickFunction);
			}
			
			return icon;
		},
		
		/*
		 * Check whether the char is numeric
		 * or not.
		 */
		isCharAllowed : function(char, allowedCharacters) {
			NC.log("Checking if " + char + " is allowed.");
			NC.log("Allowed characters: " + allowedCharacters);
			var numerics;
			if (allowedCharacters == undefined) {
				// 0-9 and :
				numerics = [48,49,50,51,52,53,54,55,56,57];
			} else {
				numerics = allowedCharacters;
			}
			
			NC.log("Allowed characters are: " + numerics);
			
			var result = false;
			$.each(numerics, function(index, value) {
				if (!result) {
					NC.log("Processing char: " + char + " against: " + value);
					if (char == value) {
						NC.log("Character allowed!");
						result = true;
					}
				}
			});
			
			return result;
		},
		
		/**
		 * Method for ensuring that the only valid input of the timeField
		 * is in format XX:XX
		 * @param timeField - The input field to bind
		 * @param callback - Executed when the user presses enter in the timeField.
		 * @returns
		 */
		validateTimeField : function(timeField, callback) {
			
			timeField.keypress(function(event) {
				var text = timeField.val();
				
				NC.log("Character count: " + text.length);
				if (text.length == 0 && !public.isCharAllowed(event.which, [48,49,50])) {
					event.preventDefault();
				}
				
				if (text.length == 1 && !public.isCharAllowed(event.which)) {
					event.preventDefault();
				}
				
				if (text.length == 2 && !public.isCharAllowed(event.which, [58])) {
					event.preventDefault();
				}
				
				if (text.length == 3 && !public.isCharAllowed(event.which, [48,49,50,51,52,53])) {
					event.preventDefault();
				}
				
				if (text.length == 4 && !public.isCharAllowed(event.which)) {
					event.preventDefault();
				}
				
				if (text.length == 5) {
					event.preventDefault();
				}
				
				if (event.keyCode == 13 && text.length == 5) {
					event.preventDefault();
					NC.log("Pressed enter in add time field. Add the time");
					if (callback !== undefined)
						callback(text);
				}
			});
		},
		
		/**
		 * Bind inputField to not accept empty input. The field and its
		 * container will be marked red when field is empty and loses focus.
		 */
		bindNotEmptyField : function(containerDiv, inputField) {
			NC.log("Binding " + inputField.attr('name') + " as a not empty field");
			inputField.blur(function(event) {
				NC.log("Input field" + inputField.attr('name') + " lost focus. Current value is: " + inputField.val());
				var val = inputField.val();
				if (val.length == 0) {
					containerDiv.addClass('error');
				} else {
					containerDiv.removeClass('error');
				}
			});
		},
		
		
		/**
		 * Formats a frequency.
		 */
		formatFrequency : function (activityDefinition) {
			if (_captions === undefined) {
				_loadCaptions();
			}
			var text = '';
			
			$.each(activityDefinition.dayTimes, function (index1, day) {
				text += day.dayCaption.value;
				$.each(day.times, function(index2, time) {
					if (index2 > 0) {
						text += ',';
					}
					text += '&nbsp;' + time;
				});
				text += '<br/>';				
			});
			
			text += '<i>';
			switch (activityDefinition.activityRepeat) {
				case 0: text += _captions.freq0; break;
				case 1: text += _captions.freq1; break;
				case 2: text +=_captions.freq2; break;
				case 3: text += _captions.freq3; break;
				case 4: text += _captions.freq4; break;
				case 5: text += _captions.freq5; break;
				default:
					text += _captions.every + activityDefinition.activityRepeat + ' ' + _captions.week;
				break;
			}

			text += '</i>';
			
			return text;
		}, 
		
		formatUnit : function (unitOption) {
			if (_captions === undefined) {
				_loadCaptions();
			}
			if (unitOption.code == 'METER') {
				return _captions.meter;
			}
			if (unitOption.code == 'MINUTE') {
				return _captions.minute;
			}
			if (unitOption.code == 'STEP') {
				return _captions.step;
			}
			return unitOption.code;
		},
		
		/**
		 * Mark error if the lenght is invalid
		 */
		bindLengthField : function(containerDiv, inputField, size) {
			NC.log("Binding " + inputField.attr('name') + " as a length specific field");
			inputField.blur(function(event) {
				var val = inputField.val();
				NC.log("Lenght of input is: " + val.length);
				if (val.length < size) {
					containerDiv.addClass('error');
				} else {
					containerDiv.removeClass('error');
				}
			})
		},
		
		/**
		 * Format a civic registration number
		 */
		formatCnr : function(cnr) {
			var first = cnr.substring(0, 8);
			var last = cnr.substring(8, 12);
			
			return first + '-' + last;
		},
		
		/**
		 * Create option elements from a set of options
		 */
		createOptions : function(options, selectElem) {
			NC.log("Creating options...");
			if (selectElem === undefined) {
				NC.log("Select element is undefined.");
				return false;
			}
			
			$.each(options, function(index, value) {
				NC.log("Creating option: " + value.code);
				var opt = $('<option>', { value : value.code });
				opt.html(value.value);
				opt.appendTo(selectElem);
			});
		},
		
		/**
		 * Method that will make call to the server, if the call was
		 * successful, the onDataLoaded()-function will be executed
		 * with the array of values
		 */
		loadOptions : function(url, onDataLoaded) {
			NC.log("Loading support data from url: " + url);
			$.ajax({
				url : url,
				dataType : 'json',
				error : function(jqXHR, textStatus, errorThrown) {
					NC.log("Error: " + errorThrown);
				},
				success : function(data, textStatus, jqXHR) {
					var arr = new Array();
					$.each(data.data, function(index, value) {
						NC.log("Processing " + value.value + "...");
						arr[index] = value;
					});
					
					onDataLoaded(arr);
				}
			});
		},
		
		getCaptions : function() {
			if (_captions === undefined) {
				_loadCaptions();
			}
			return _captions;
		}	
	};
	
	return public;
};