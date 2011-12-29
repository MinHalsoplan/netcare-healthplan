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
NC.PatientSchema = function(descriptionId, tableId) {
	
	var _baseUrl = "/netcare-web/api/patient/";
	var _schemaCount = 0;
	
	var _descriptionId = descriptionId;
	var _tableId = tableId;
	var _lastUpdatedId = -1;
	
	var _updateDescription = function() {
		console.log("Updating schema table description");
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
		
		focus : function() {
			console.log('set focus : ' + _lastUpdatedId);
			if (_lastUpdatedId != -1) {
				$('#rep-' + _lastUpdatedId).focus();
			}
		},
		
		list : function() {
			console.log("Load activitues for the patient");
			var today = $.datepicker.formatDate( 'yy-m-d', new Date(), null );
			var curDay = '';
			var curActivity = '';
			var util = NC.Util();
			$.ajax({
				url : _baseUrl + 'schema',
				dataType : 'json',
				success : function(data) {
					console.log("Success. Processing results...");
					
					/* Empty the result list */
					$('#' + tableId + ' tbody > tr').empty();
					
					$.each(data.data, function(index, value) {
						console.log("Processing index " + index + " value: " + value.reported + ", " + value.actual);
												
						var reportField = $('<div>');
						var inputValue;
						
						if (value.definition.type.code != 'NONE') {
							inputValue = $('<input>');
							inputValue.val((value.reported != null) ? value.actual : '');
							inputValue.attr('size', 4);
							inputValue.attr('id', 'rep-' + value.id);

							if (today == value.date) {
								inputValue.css('background', 'lightgreen');
							} else {
								inputValue.css('background', (!value.due) ? 'lightpink' : 'lightyellow');
//								XXX: Disable up-coming days.	
								if (value.due) {
									inputValue.attr('disabled', true);
								}
							}
							
							inputValue.change(function() {
								public.accept(value.id, inputValue.val());
							});
							inputValue.appendTo(reportField);

							if (!value.due) {
								var editIcon = util.createIcon('bullet_accept', 32, null);
								editIcon.css('align', 'right');
								reportField.append(editIcon);
							}
						} else {
							inputValue = '&nbsp;';
							inputValue.appendTo(reportField);
						}						
						
						if (curDay != value.day.value) {
							curDay = value.day.value;
							if (today == value.date) {
								dayField = '<b>' + curDay + '<br/>' + value.date + '</b>';								
							} else {
								dayField = curDay + '<br/>' + value.date;
							}
						} else {
							dayField = '-&nbsp;"&nbsp;-';
						}
						
						var activity = value.definition.type.name + "<br/>" + value.definition.goal + '&nbsp;' + value.definition.type.unit.value;
						if (curActivity != activity) {
							curActivity = activity;
							activityField = curActivity;
						} else {
							activityField = '-&nbsp;"&nbsp;-';
						}
						
						$('#' + tableId + ' tbody').append(
								$('<tr>').append(
										$('<td>').html(dayField)).append(
												$('<td>').html(value.time)).append(
														$('<td>').css('text-align', 'center').html(activityField)).append(
																$('<td>').html(reportField)));
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
			console.log("POST accept activity with id: " + id + ', value: ' + value);
			var url = _baseUrl + "schema/" + id + '/accept/' + value;
			_lastUpdatedId = id;
			$.ajax({
				url : url,
				type : 'post',
				success : function(data) {
					console.log('Accept of activity succeeded.');
					new NC.Util().processServiceResult(data);
					public.list();
					public.focus();
				}
			});
		},
		
	};
	
	return public;
};