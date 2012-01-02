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
NC.PatientHome = function(descriptionId, tableId, eventHeadId, eventBodyId) {
	
	var _baseUrl = "/netcare-web/api/patient/";
	var _schemaCount = 0;
	var _eventCount = 0;
	
	var _descriptionId = descriptionId;
	var _eventHeadId = eventHeadId;
	var _eventBodyId = eventBodyId;
	var _tableId = tableId;
	
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
		
		list : function() {
			var curDay = '';
			var curActivity = '';
			var util = NC.Util();
			var _url = _baseUrl + 'activities';
			console.log("Load activitues for the patient: " + _url);
			$.ajax({
				url : _url,
				dataType : 'json',
				success : function(data) {
					console.log('Success.');
					
					/* Empty the result list */
					$('#' + tableId + ' tbody > tr').empty();
					
					$.each(data.data, function(index, value) {
						console.log("Processing index " + index + " value: " + value.reported + ", " + value.actual);
							
						var period;
						if (value.activityRepeat == 0) {
							period = value.startDate;
						} else {
							period = value.endDate;
						}
						
						var pctSum = ((value.sumDone / value.sumTotal)*100).toFixed(0) + '%';
						var pctNum = ((value.numDone / value.numTotal)*100).toFixed(0) + '%';
						var sumText = value.sumDone + '&nbsp;av&nbsp;' + value.sumTotal + '&nbsp;' + util.formatUnit(value.type.unit);
						var numText = value.numDone + '&nbsp;av&nbsp;' + value.numTotal + '&nbsp;ggr';
						var result = (value.sumTarget > 0) ? (value.sumDone / value.sumTarget) * 100 : -1;
						var icon;
						if (result == -1) {
							icon = util.createIcon("face-smile", 32, null);
						} else if (result > 120) {
							icon = util.createIcon('face-grin', 32, null);
						} else if (result > 90) {
							icon = util.createIcon("face-smile", 32, null);
						} else if (result > 70) {
							icon = util.createIcon("face-plain", 32, null);
						} else if (result > 50) {
							icon = util.createIcon("face-sad", 32, null);
						} else {
							icon = util.createIcon("face-crying", 32, null);	
						}
						
						$('#' + tableId + ' tbody').append(
								$('<tr>').append(
										$('<td>').html(icon)).append(
												$('<td>').html(value.type.name + '<br/>' + value.goal + '&nbsp' + util.formatUnit(value.type.unit))).append(
														$('<td>').html(period)).append(
																$('<td>').css('text-align', 'right').html(pctNum+ '<br/>' + pctSum)).append(
																		$('<td>').html(numText + '<br/>' + sumText)).append(
																				$('<td>').html(util.formatFrequency(value))));
					});

					_schemaCount = data.data.length;
					
					_updateDescription();
				}
			});
		},
		
		status : function() {
			var _url = _baseUrl + 'event';
			console.log("Load actual events for patient: " + _url);
			$.ajax({
				url : _url,
				dataType : 'json',
				success : function(data) {
					console.log('Success.');
					var event = data.data;
					var _eventCount = event.numReports + event.dueReports;
					var msg = '';
					if (_eventCount > 0) {
						msg = '<a href="report">[' + _eventCount + ']</a>';
					} else {
						msg = '[' + _eventCount + ']';
					}
					$('#' + _eventHeadId).html('Nya Händelser ' + msg);
					msg = '';
					if (_eventCount == 0) {
						msg = '<br/>Inga nya händelser';
					}
					if (event.numReports > 0) {
						msg += '<br/>Aktuella händelser ' + event.numReports; 
					}
					if (event.dueReports > 0) {
						msg += '<br/>Gamla händelser ' + event.dueReports;
					}					
					$('#' + _eventBodyId).html(msg);
				}
			});
		},
		
		eventCount : function() {
			return _eventCount;
		},
		
		
	};
	
	return public;
};