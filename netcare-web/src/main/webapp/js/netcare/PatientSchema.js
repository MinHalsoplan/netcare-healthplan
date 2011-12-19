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
NC.PatientSchema = function(descriptionId, tableId) {
	
	var _baseUrl = "/netcare-web/api/patient/";
	var _schemaCount = 0;
	
	var _descriptionId = descriptionId;
	var _tableId = tableId;
	
	var _updateDescription = function() {
		console.log("Updating ordination table description");
		if (_schemaCount == 0) {
			$('#' + _descriptionId).html('Inga aktuella aktiviteter').show();
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
		
		list : function() {
			console.log("Load activitues for the patient");
			$.ajax({
				url : _baseUrl + 'schema',
				dataType : 'json',
				success : function(data) {
					console.log("Success. Processing results...");
					
					/* Empty the result list */
					$('#' + tableId + ' tbody > tr').empty();
					
					$.each(data.data, function(index, value) {
						console.log("Processing index " + index + " value: " + value.name);
						
						var util = NC.Util();
						var inputCol = $('<td>');
						var inputValue;
						if (value.targetValue > 0) {
							inputValue = $('<input>');
							inputValue.val(value.targetValue);
						} else {
							inputValue = '&nbsp;'
						}
						
						
						var editIcon = util.createIcon('bullet_accept', function() {
							public.accept(value.id, inputValue.val());
						});						
						var denyIcon = util.createIcon("bullet_deny", null);
						
						var actionCol = $('<td>');
						actionCol.css('text-align', 'right');
						
						if (value.due) {
							editIcon.appendTo(actionCol);
						} else {
							denyIcon.appendTo(actionCol);
						}						
						inputCol.html(inputValue);

						$('#' + tableId + ' tbody').append(
								$('<tr>').append(
										$('<td>').html(value.scheduledTime)).append(
												$('<td>').html(value.name)).append(
														inputCol).append(
																actionCol));
					});
					
					console.log("Updating ordination count to: " + data.data.length);
					_schemaCount = data.data.length;
					
					console.log("Updating description");
					_updateDescription();
				}
			});
		},
	
		
		/**
		 * View a single ordination
		 */
		accept : function(id, value) {
			console.log("GET accept activity with id: " + id + ', value: ' + value);
			var url = _baseUrl + id + '/accept/' + value;
			
			$.ajax({
				url : url,
				type : 'post',
				success : function(data) {
					console.log('Accept of activity succeeded.');
					new NC.Util().processServiceResult(data);
					public.list();
				}
			});
		},
		
	};
	
	return public;
};