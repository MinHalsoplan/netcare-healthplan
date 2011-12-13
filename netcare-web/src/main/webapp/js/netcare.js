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
//var NC;
//if (!NC) {
	NC = {};
//}

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
}

NC.Patient = function() {
	var _baseUrl = "/netcare-web/api/user";
	
	var public = {
		/**
		 * Called when the care giver wants to find a patient
		 */
		findPatients : function(searchValue, successFunction) {
			console.log("Finding patients. Searching for: " + searchValue);
			
			if (searchValue.length < 3) {
				return false;
			}
			
			$.ajax({
				url : _baseUrl + '/find',
				dataType : 'json',
				data : { search : searchValue },
				success : successFunction
			});
		},
		
		/**
		 * Called when care giver selects a patient to
		 * work with. This method adds the selected patient
		 * to the session scope and the menu should always
		 * display the selected patient
		 */
		selectPatient : function(patientId, successFunction) {
			console.log("Selecting patient: " + patientId);
			$.ajax({
				url : _baseUrl + '/' + patientId + '/select',
				dataType : 'json',
				type : 'post',
				success : successFunction
			});
		}
	};
	
	return public;
};

NC.Support = function() {
	
	var _baseUrl = "/netcare-web/api/support";
	
	/**
	 * Method that will make call to the server, if the call was
	 * successful, the onDataLoaded()-function will be executed
	 * with the array of values
	 */
	var _loadOptions = function(url, onDataLoaded) {
		console.log("Loading support data from url: " + url);
		$.ajax({
			url : url,
			dataType : 'json',
			error : function(jqXHR, textStatus, errorThrown) {
				console.log("Error: " + errorThrown);
			},
			success : function(data, textStatus, jqXHR) {
				var arr = new Array();
				$.each(data.data, function(index, value) {
					arr[index] = value;
				});
				
				onDataLoaded(arr);
			}
		});
	};
	
	var _createOptions = function(options, selectElem) {
		if (selectElem === undefined) {
			return false;
		}
		
		$.each(options, function(index, value) {
			var opt = $('<option>', { value : value.code });
			opt.html(value.value);
			opt.appendTo(selectElem);
		});
	};
	
	var public = {
		/**
		 * Load all unit options that exist in the
		 * application.
		 */
		loadOptions : function(selectElem) {
			var url = _baseUrl + '/units/load';
			
			_loadOptions(url, function(data) {
				_createOptions(data, selectElem);
			});
			
		},
	
		/**
		 * Load all durations as options that exist in the
		 * application
		 */
		loadDurations : function(selectElem) {
			var url = _baseUrl + '/durations/load';
			
			_loadOptions(url, function(data) {
				_createOptions(data, selectElem);
			});
		},
		
		/**
		 * Load month names
		 */
		loadMonths : function(callback) {
			var url = _baseUrl + '/months/load';
			_loadOptions(url, function(data) {
				console.log("Got result: " + data);
				callback(data);
			});
		},
		
		/**
		 * Load weekday names
		 */
		loadWeekdays : function(callback) {
			var url = _baseUrl + '/weekdays/load';
			_loadOptions(url, function(data) {
				console.log("Got result: " + data);
				callback(data);
			});
		}
	};
	
	return public;
}

NC.Ordinations = function(descriptionId, tableId) {
	
	var _baseUrl = "/netcare-web/api/ordination";
	var _ordinationCount = 0;
	
	var _descriptionId = descriptionId;
	var _tableId = tableId;
	
	var _updateDescription = function() {
		console.log("Updating ordination table description");
		if (_ordinationCount == 0) {
			$('#' + _descriptionId).html('Inga aktuella ordinationer').show();
			$('#' + _tableId).hide();
		} else {
			$('#' + _descriptionId).hide();
			$('#' + _tableId).show();
		}
	}
	
	var public = {
		
		init : function() {
			_updateDescription();
		},
		
		list : function(currentPatient) {
			console.log("Load active ordinations for the current patient");
			$.ajax({
				url : _baseUrl + '/' + currentPatient + '/list',
				dataType : 'json',
				success : function(data) {
					console.log("Success. Processing results...");
					
					/* Empty the result list */
					$('#' + tableId + ' tbody > tr').empty();
					
					$.each(data.data, function(index, value) {
						console.log("Processing index " + index + " value: " + value.name);
						
						var util = NC.Util();
						var editIcon = util.createIcon('bullet_info', function() {
							console.log("Edit icon clicked");
						});
						
						var deleteIcon = util.createIcon('bullet_delete', function() {
							public.delete(value.id, currentPatient);
						});
						
						var actionCol = $('<td>');
						actionCol.css('text-align', 'right');
						
						editIcon.appendTo(actionCol);
						deleteIcon.appendTo(actionCol);
						
						$('#' + tableId + ' tbody').append(
								$('<tr>').append(
									$('<td>').html(value.name)).append(
										$('<td>').html(value.duration)).append(
												$('<td>').html(value.startDate)).append(
														$('<td>').html(value.issuedBy.name)).append(
																actionCol));
						
						
					});
					
					console.log("Updating ordination count to: " + data.data.length);
					_ordinationCount = data.data.length;
					
					console.log("Updating description");
					_updateDescription();
				}
			});
		},
	
		/**
		 * Create a new ordination for the current patient.
		 * The method accepts the form data as a json-object which is
		 * sent to the server without any validations.
		 */
		create : function(formData, currentPatient, callback) {
			var url = _baseUrl + '/' + currentPatient + '/create';
			console.log("Creating new ordination. Url: " + url);
			
			$.ajax({
				url : url,
				dataType : 'json',
				type : 'post',
				data : formData,
				contentType : 'application/json',
				success :  function(data) {
					console.log('Ordination successfully created');
					new NC.Util().processServiceResult(data);
					
					/* List messages */
					public.list(currentPatient);
					
					/* Execute callback */
					callback(data);
				}
			});
		},
		
		/**
		 * Delete an ordination
		 */
		delete : function(ordinationId, currentPatient) {
			var url = _baseUrl + '/' + currentPatient + '/' + ordinationId + '/delete';
			console.log("Removing ordination " + ordinationId + " using url: " + url);
			
			$.ajax({
				url : url,
				type : 'post',
				success : function(data) {
					console.log('Deletion of ordination succeeded.');
					new NC.Util().processServiceResult(data);
					
					public.list(currentPatient);
				}
			});
		}
	};
	
	return public;
};