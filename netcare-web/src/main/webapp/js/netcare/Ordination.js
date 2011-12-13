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
NC.Ordination = function(descriptionId, tableId) {
	
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
							public.view(value.id, currentPatient);
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
		},
		
		view : function(ordinationId, currentPatient) {
			console.log("GET to view ordination with id: " + ordinationId);
			window.location = '/netcare-web/netcare/admin/ordination/' + ordinationId + '/view';
		}
	};
	
	return public;
};