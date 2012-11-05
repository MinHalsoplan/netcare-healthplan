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
	var _captions = null;
	var _units = null;
	
	/**
	 * Loads captions used by util.
	 */
	var _loadCaptions = function() {
		var _support = new NC.Support();
		
		_support.loadCaptions('util', ['week', 'freq0', 'freq1', 'freq2', 'freq3',
		                               'freq4', 'freq5', 'every', 'meter', 'minute', 
		                               'step', 'newEvents', 'kilogram', 'gram', 'pressure_mmhg',
		                               'flow_mlmin', 'milliliter', 'liter'],
		                               function(data) {
			_captions = data;
			NC.log('Localized captions for util loaded.');
		});
		_support.loadCaptions(null, ['METER', 'MINUTE', 'STEP', 'KILOGRAM', 'GRAM', 'PRESSURE_MMHG','FLOW_MLMIN', 'MILLILITER', 
		                               'LITER', 'MMOL_LITER', 'QUANTIY', 'POINTS', 'PERCENT'], 
		                               function(data) {
			_units = data;
			NC.log('Localized captions for util loaded.');
		});

	};
	
	var public = {
			
		createInputField : function(label, helpText, inputElement) {
			var container = $('<div>').addClass('control-group');
			container.append(
				$('<label>').prop('for', name).addClass('control-label').html(label)
			);
			
			container.append(
				$('<div>').addClass('controls').append(inputElement).append(
					$('<span>').addClass('help-inline').html(helpText)
				)
			);
			
			return container;
		},
		
		/**
		 * Check if current logged in user is a patient or not
		 */
		isPatient : function(currentPatient) {
			if (currentPatient != '') {
				isPatient = false;
			} else {
				var patientId = '<sec:authentication property="principal.id" />';
				if (patientId != "") {
					isPatient = true;
				} else {
					throw new Error("Could not determine whether user is patient or care giver");
				}
			}
			
			NC.log("Is user patient: " + isPatient);
			return isPatient;
		},
			
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
		
		createIcon : function(name, size, onClickFunction, messageCode, isResolved) {
			var icon = $('<img>', {
				src : NC.getContextPath() + '/img/icons/' + size + '/' + name + '.png'
			});
			
			if (onClickFunction != null) {
				icon.css('cursor', 'pointer');
				icon.click(onClickFunction);
			}
			
			if (isResolved === undefined || isResolved == null || isResolved == false) {			
				if (messageCode !== undefined || messageCode != null) {
					var support = new NC.Support();
					support.loadMessages(messageCode, function(messages) {
						icon.attr('title', messages[messageCode]);
					}, false);
				}
			} else {
				icon.prop('title', messageCode);
			}
			
			return icon;
		},
		
		/**
		 * Returns true if char is numeric, otherwise false
		 */
		isNumeric : function(char) {
			return (char >= 48 && char <= 57);
		},
		
		/*
		 * Check whether the char is numeric
		 * or not.
		 */
		isCharAllowed : function(char, allowedCharacters) {
			NC.log("Checking if " + char + " is allowed.");
			var numerics;
			if (allowedCharacters == undefined) {
				// 0-9
				numerics = [48,49,50,51,52,53,54,55,56,57];
			} else {
				numerics = allowedCharacters;
			}
			
			NC.log("Allowed characters are: " + numerics);
			
			for (var i = 0; i < numerics.length; i++) {
				if (char == numerics[i]) {
					NC.log("Character allowed!");
					return true;
				}
			}
			NC.log("Character not allowed!");
			return false;
		},
		
		/**
		 * Validates numeric input with max length, flash field background on invalid input.
		 */
		validateNumericField : function(numericField, maxLen) {
			numericField.focusout( function () { 
				NC.focusLost($(this));
			});

			numericField.focus( function (event) { 
				NC.focusGained($(this));
			});

			numericField.keypress(function(event) {
				var val = numericField.val();
				// FIXME: just terrible logic!
				if (val.length >= maxLen || !((event.which == 46 && numericField.val().indexOf('.') == -1) || public.isNumeric(event.which))) {
					event.preventDefault();
					numericField.css('background', '#F2DEDE');
					setTimeout(function() {
						numericField.css('background', '#D9EDF7');												
					}, 100);
				} else if (numericField.css('background') != '#D9EDF7') {
					numericField.css('background', '#D9EDF7');
				}
			});
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
		
		validateFieldNotEmpty : function(input) {
			if (input.val() == '' || input.val() == null || input.val() == undefined) {
				var id = input.attr('id');
				var existingText = $('label[for="' + id + '"]').html();
				$('label[for="' + id + '"]').parent().addClass('error');
				return false;
			}
			
			return true;
		},
		
		
		/**
		 * Formats a frequency.
		 */
		formatFrequency : function (activityDefinition) {
			if (_captions == null) {
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
			
			text += '<i style="font-size: 10px;">';			
			switch (activityDefinition.activityRepeat) {
				case 0: text += _captions.freq0; break;
				case 1: text += _captions.freq1; break;
				case 2: text += _captions.freq2; break;
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
			if (_units == null) {
				_loadCaptions();
			}
			if (unitOption.code == 'METER') {
				return _units.METER;
			}
			if (unitOption.code == 'MINUTE') {
				return _units.MINUTE;
			}
			if (unitOption.code == 'STEP') {
				return _units.STEP;
			}
			if (unitOption.code == 'KILOGRAM') {
				return _units.KILOGRAM;
			}
			if (unitOption.code == 'GRAM') {
				return _units.GRAM;
			}
			if (unitOption.code == 'PRESSURE_MMHG') {
				return _units.PRESSURE_MMHG;
			}
			if (unitOption.code == 'FLOW_MLMIN') {
				return _units.FLOW_MLMIN;
			}
			if (unitOption.code == 'MILLILITER') {
				return _units.MILLILITER;
			}
			if (unitOption.code == 'LITER') {
				return _units.LITER;
			}
			if (unitOption.code == 'MMOL_LITER') {
				return _units.MMOL_LITER;
			}
			if (unitOption.code == 'QUANTIY') {
				return _units.QUANTIY;
			}
			if (unitOption.code == 'POINTS') {
				return _units.POINTS;
			}
			if (unitOption.code == 'PERCENT') {
				return _units.PERCENT;
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
			});
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
			_ajax.get(url, function(data) {
				var arr = new Array();
				$.each(data.data, function(index, value) {
					NC.log("Processing " + value.value + "...");
					arr[index] = value;
				});
				
				onDataLoaded(arr);
			});
		},
		
		getCaptions : function() {
			if (_captions == null) {
				_loadCaptions();
			}
			return _captions;
		},
		
		createCheckbox : function(id, label) {
			var container = $('<div>').addClass('clearfix').attr('id', id + '-container');
			container.append(
				$('<label>').attr('for', id).html(label)
			).append(
				$('<div>').addClass('input').append(
					$('<input>').attr('type', 'checkbox').attr('id', id).attr('name', id)
				)
			);
			
			return container;
		},
		
		formatMeasurements : function(rejectedLabel, rejected, data) {
			
			var rc = new Object();
			rc.alarm = rejected;
			rc.html = '';
			
			if (rejected) {
				rc.html = rejectedLabel;
				return rc;
			}
			
			$.each(data, function(index, value) {
				var target;
				var alarm = false;
				if (value.measurementDefinition.measurementType.valueType.code == 'INTERVAL') {
					target = value.minTarget + '-' + value.maxTarget;
					alarm = (value.reportedValue < value.minTarget || value.reportedValue> value.maxTarget);
					if (alarm) {
						rc.alarm = true;
					}
				} else {
					target = value.target;
				}
				if (index > 0) {
					rc.html += '<br/>';
				}
				
				var report = alarm ? '<i style="font-weight: bold">' + value.reportedValue + '</i>' : value.reportedValue ;
				
				rc.html += value.measurementDefinition.measurementType.name + ':&nbsp;' + report + '&nbsp;' 
					+ public.formatUnit(value.measurementDefinition.measurementType.unit) + '&nbsp;(' + target + ')';
				
			});
			
			return rc;
		}
	};
	
	return public;
};