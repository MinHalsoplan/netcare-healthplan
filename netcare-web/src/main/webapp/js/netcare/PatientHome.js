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
NC.PatientHome = function(descriptionId, tableId, eventBodyId) {
	
	var _baseUrl = "/netcare-web/api/patient/";
	var _schemaCount = 0;
	var _eventCount = 0;
	
	var _descriptionId = descriptionId;
	var _eventBodyId = eventBodyId;
	var _tableId = tableId;
	var _perfData;

	var util = new NC.Util();

	var _updateDescription = function() {
		NC.log("Updating schema table description");
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
		
		list : function(callback) {
			var curDay = '';
			var curActivity = '';
			var _url = _baseUrl + 'activities';
			NC.log("Load activitues for the patient: " + _url);
			$.ajax({
				url : _url,
				dataType : 'json',
				cache : false,
				success : function(data) {
					NC.log('Success.');
					
					/* Empty the result list */
					$('#' + tableId + ' tbody > tr').empty();
					
					_perfData = new Array();
					
					$.each(data.data, function(index, value) {							
						var period;
						if (value.activityRepeat == 0) {
							period = value.startDate;
						} else {
							period = value.endDate;
						}
						
						var pdata = new Object();
						pdata.id = 'gauge-' + value.id;
						pdata.pctSum = Math.ceil((value.sumDone / value.sumTarget)*100);
						pdata.sumDone = value.sumDone;
						pdata.sumTarget = value.sumTarget;
						// gauge & data
						pdata.gauge = null;
						pdata.options = null;
						pdata.data = null;
						_perfData.push(pdata);
												
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
						
						var actText = value.type.name + '<br/>' + value.goal + '&nbsp' + util.formatUnit(value.type.unit);
						$('#' + tableId + ' tbody').append(
								$('<tr>').append(
										$('<td>').html(icon)).append(
												$('<td>').html(value.healthPlanName + '<br/><i>' + value.issuedBy.careUnit.name + '<br/>' + value.issuedBy.name + '</i>')).append(
														$('<td>').html(actText)).append(
																$('<td>').html(period)).append(
																		$('<td>').html(util.formatFrequency(value))).append(
																				$('<td>').attr('id', pdata.id).css('height', '100px').css('width', '100px').html('&nbsp;')));
					});
					_schemaCount = data.data.length;
					
					_updateDescription();
					
					callback();
				}
			});
		},
		
		status : function() {
			var _url = _baseUrl + 'event';
			NC.log("Load actual events for patient: " + _url);
			$.ajax({
				url : _url,
				dataType : 'json',
				cache : false,
				success : function(data) {
					NC.log('Success.');
					var event = data.data;
					_eventCount = event.numReports + event.dueReports;
					NC.log('event count: ' + _eventCount);
					if (_eventCount == 0) {
						$('#' + _eventBodyId).hide();
					} else {
						var msg = $('<a>');
						msg.css('color', 'white');
						var caps = util.getCaptions();
						msg.text(caps.newEvents);
						msg.attr('href', 'report');
						$('#' + _eventBodyId).append(msg);
						$('#' + _eventBodyId).show();
					}
				}
			});
		},
		
		eventCount : function() {
			return _eventCount;
		},
		
		perfData : function() {
			return _perfData;
		},
		
		
	};
	
	return public;
};