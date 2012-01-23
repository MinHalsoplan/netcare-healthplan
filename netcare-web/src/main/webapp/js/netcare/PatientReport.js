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
NC.PatientReport = function(descriptionId, tableId) {
	
	var _baseUrl = "/netcare-web/api/patient/";
	var _schemaCount = 0;
	
	var _descriptionId = descriptionId;
	var _tableId = tableId;
	var _lastUpdatedId = -1;
	var _dueActivities = new Array();

	var _captions;
	new NC.Support().loadCaptions('report', ['report', 'change', 'reject'], function(data) {
		_captions = data;
	});		
	
	var _today = $.datepicker.formatDate( 'yy-mm-dd', new Date(), null );
	var _util = new NC.Util();
	
		
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
	
	
	var _lineColor = function(value) {
		if ((value.due && value.reported == null)) {
			return 'red';
		} else if (_today == value.date) {
			return 'blue';
		} else {
			return 'gray';
		}		
	}
	
	var _reportText = function(value) {
		if (value.reported != null) {
			return ((value.rejected) ? _captions.reject : (value.actualValue + '&nbsp;' + value.definition.type.unit.value))
			+ '<br/>' + value.reported;
		} else {
			return '&nbsp;';
		}
	}
		
	var createButtons = function(act) {
		var div = $('<div>');
		var rbtn = _util.createIcon('edit', 24, function() {
			var value = (act.actualValue == 0) ? act.definition.goal : act.actualValue;
			NC.log("value = " + value);

			$('#reportFormDiv input[name="activityId"]').attr('value', act.id);
			$('#reportFormDiv input[name="value"]').attr('value', value);
			$('#reportFormDiv input[name="note"]').attr('value', act.note);
			$('#unitId').html(act.definition.type.unit.value);

			if (act.definition.type.measuringSense) {
				$('input:radio[name=gsense]').filter('[value=' + act.sense + ']').attr('checked', true);
				$('#senseSectionId').show();
				if (act.definition.type.scaleText !== undefined) {
					$('#senseTextId').text(act.definition.type.scaleText);
				}
			} else {
				$('#senseSectionId').hide();
			}

			var planned = act.definition.type.name + '&nbsp;' + act.definition.goal 
			+ '&nbsp;' 
			+ act.definition.type.unit.value
			+ ',&nbsp;' + act.day.value + '&nbsp;' + act.date + '&nbsp;' + act.time;

			$('#plannedId').html(planned);

			var date;
			var time;
			if (act.actualTime == null) {
				date = act.date;
				time = act.time;
			} else {
				var str = act.actualTime.split(' ');
				date = str[0];
				time = str[1];
			}

			$('#reportFormDiv input[name="date"]').datepicker( "option", "defaultDate", date);
			$('#reportFormDiv input[name="date"]').attr('value', date);
			$('#reportFormDiv input[name="time"]').attr('value', time);
			$('#reportFormDiv').modal('show');
			$('#reportFormDiv input[name="value"]').focus();		

		});
		rbtn.attr('title', act.reported != null ? _captions.change : _captions.report);
		div.append(rbtn);

		
		var cbtn = _util.createIcon('remove', 24, function() {
			event.preventDefault();
			var id = act.id;
			var rep = new Object();
			rep.actualValue = 0;
			rep.actualDate = null;
			rep.actualTime = null;
			rep.sense = 0;
			rep.note = null;
			rep.rejected = true;

			var jsonObj = JSON.stringify(rep);

			NC.log("JSON: " + jsonObj.toString());

			public.performReport(id, jsonObj, function(data) {
				cbtn.attr('disabled', data.rejected);
			});
		});
		cbtn.attr('cbtn-' + act.id);
		cbtn.attr('title', _captions.reject);
		cbtn.attr('disabled', act.rejected);
		div.append(cbtn);
		
		return div;
	}
	

	
	var public = {
		
		init : function() {
			_updateDescription();
		},
		
		performReport : function(activityId, formData, callback) {
			var url = _baseUrl + 'schema/' + activityId + '/accept';
			$.ajax({
				url : url,
				dataType : 'json',
				type : 'post',
				data : formData,
				contentType : 'application/json',
				success :  function(data) {
					NC.log('Report successfully done');
					_util.processServiceResult(data);
					$('#act-' + activityId).css('color', _lineColor(data.data));
					NC.log('#rep-' + activityId + ', ' + _reportText(data.data));
					$('#rep-' + activityId).html(_reportText(data.data));
					_dueActivities.push(data.data);
					callback(data.data);
				}
			});		
		},
				
		list : function() {
			NC.log("Load activitues for the patient");
			var curDay = '';
			var curActivity = '';
			$.ajax({
				url : _baseUrl + 'schema',
				dataType : 'json',
				success : function(data) {
					NC.log("Success. Processing results...");
					
					/* Empty the result list */
					$('#' + tableId + ' tbody > tr').empty();
					
					$.each(data.data, function(index, value) {
						NC.log("Processing index " + index + " value: " + value.id);	
						
						if (curDay != value.day.value) {
							curDay = value.day.value;
							dayField = curDay + '<br/>' + value.date;
						} else {
							dayField = '-&nbsp;"&nbsp;-';
						}
						
						var reportField;
						if (value.due || _today == value.date) {
							reportField = createButtons(value);
							if (value.due) {
								_dueActivities.push(value);
							}
						} else {
							reportField = '&nbsp;';
						}
						
						var activity = value.definition.type.name + "<br/>" + value.definition.goal + '&nbsp;' + value.definition.type.unit.value;
						if (curActivity != activity) {
							curActivity = activity;
							activityField = curActivity;
						} else {
							activityField = '-&nbsp;"&nbsp;-';
						}

						var lineColor = _lineColor(value);

						var reported = _reportText(value);
						
						$('#' + tableId + ' tbody').append(
								$('<tr>').attr('id', 'act-' + value.id).css('color', lineColor).append(
										$('<td>').html(dayField)).append(
												$('<td>').html(value.time)).append(
														$('<td>').css('text-align', 'left').html(activityField)).append(
																$('<td>').css('text-align', 'center').html(reportField)).append(
																		$('<td>').attr('id', 'rep-' + value.id).css('text-align', 'left').html(reported)));
					});
					
					public.showHistory(false);
					_schemaCount = data.data.length;
					_updateDescription();
				}
			});
		},

		showHistory : function(show) {
			$.each(_dueActivities, function(index, value) {
				if (show || (value.reported == null)) {
					$('#act-' + value.id).show();
				} else {
					$('#act-' + value.id).hide();
				}
			});
		},
		
		getCaptions : function() {
			return _captions;
		}
			
	};
	
	return public;
};