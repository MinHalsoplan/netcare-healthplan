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
	
	var _captions;
	var _today = $.datepicker.formatDate( 'yy-mm-dd', new Date(), null );
	var support = new NC.Support();

	support.loadCaptions('report', ['report', 'change', 'reject'], function(data) {
		_captions = data;
	});

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
	
	var createButton = function(type, value, cls) {
		var btn = $('<input>').attr('type', type).attr('value', value).attr('class', cls);
		return btn;
	}
	
	var createButtons = function(act) {
		var div = $('<div>');
		var rbtn;
		if (act.reported == null) {
			rbtn = createButton('submit', _captions.report, 'btn small success');			
		} else {
			rbtn = createButton('submit', _captions.change, 'btn small primary');
		}
		rbtn.css('margin', '5px');
		div.append(rbtn);
		div.append($('<br>'));
		var cbtn = createButton('submit', _captions.reject, 'btn small danger');
		cbtn.attr('cbtn-' + act.id);
		cbtn.css('margin', '5px');
		div.append(cbtn);
		cbtn.attr('disabled', act.rejected);
		
		rbtn.click(function(event) {
			var value = (act.actualValue == 0) ? act.definition.goal : act.actualValue;
			console.log("value = " + value);

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
		
		cbtn.click(function(event) {
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

			console.log("JSON: " + jsonObj.toString());

			public.performReport(id, jsonObj, function(data) {
				cbtn.attr('disabled', data.rejected);
			});
		});
		
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
					console.log('Report successfully done');
					new NC.Util().processServiceResult(data);
					$('#act-' + activityId).css('color', _lineColor(data.data));
					console.log('#rep-' + activityId + ', ' + _reportText(data.data));
					$('#rep-' + activityId).html(_reportText(data.data));
					callback(data.data);	
				}
			});		
		},
				
		list : function() {
			console.log("Load activitues for the patient");
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
						console.log("Processing index " + index + " value: " + value.id);	
						
						if (curDay != value.day.value) {
							curDay = value.day.value;
							dayField = curDay + '<br/>' + value.date;
						} else {
							dayField = '-&nbsp;"&nbsp;-';
						}
						
						var reportField;
						if (value.due || _today == value.date) {
							reportField = createButtons(value);
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
					
					console.log("Updating ordination count to: " + data.data.length);
					_schemaCount = data.data.length;
					
					console.log("Updating description");
					_updateDescription();
				}
			});
		},
		
		getCaptions : function() {
			return _captions;
		}
			
	};
	
	return public;
};