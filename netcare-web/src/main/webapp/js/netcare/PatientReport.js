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
NC.PatientReport = function(tableId, shortVersion) {
	
	var _schemaCount = 0;
	
	var _tableId = tableId;
	var _lastUpdatedId = -1;
	var _dueActivities;
	var _shortVersion = shortVersion;
	var _captions;
	var _reportCallback = null;
		
	var _today = $.datepicker.formatDate( 'yy-mm-dd', new Date(), null );
	var _util = new NC.Util();
	var _ajax = new NC.Ajax();
	var _support = new NC.Support();
	
	_support.loadCaptions('report', ['report', 'change', 'reject', 'time'], function(data) {
		_captions = data;
	});		

	var _updateDescription = function() {
		NC.log("Updating schema table description: " + _schemaCount);
		if (_schemaCount == 0) {
			$('#' + _tableId).hide();
		} else {
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
			return (value.rejected) ? _captions.reject : value.reported;
		} else {
			return '&nbsp;';
		}
	}
	
	var _render = function(data) {
		var curDay = '';
		var curActivity = '';
		_dueActivities = new Array();
		
		$.each(data, function(index, value) {
			NC.log("Processing index " + index + " value: " + value.id);	

			var dayField = value.day.value + '<br/>' + value.date;
			var reportField;
			if (value.due || _today == value.date) {
				reportField = createButtons(value);
				if (value.due) {
					_dueActivities.push(value);
				}
			} else {
				reportField = '&nbsp;';
			}

			var activity = value.definition.type.name;

			var lineColor = _lineColor(value);
			var reported = _reportText(value);

			if (!_shortVersion || ((value.due || _today == value.date) && value.reported == null)) {
				$('#' + tableId + ' tbody').append(
						$('<tr>').attr('id', 'act-' + value.id).css('color', lineColor).append(
								$('<td>').html(dayField)).append(
										$('<td>').html(value.time)).append(
												$('<td>').css('text-align', 'left').html(activity)).append(
														$('<td>').css('text-align', 'center').html(reportField)).append(
																$('<td>').attr('id', 'rep-' + value.id).css('text-align', 'left').html(reported)));
				_schemaCount++;
			}
		});
		
		return true;
	}
	
	var createButtons = function(act) {
		var div = $('<div>');
		var rbtn = _util.createIcon('edit', 24, function() {

			$('#reportFormDiv input[name="activityId"]').attr('value', act.id);
			$('#reportFormDiv input[name="note"]').attr('value', (act.note == null) ? '' : act.note);
			$('#reportFormDiv input[name="numValueId"]').attr('value', act.measurements.length);

			if (act.definition.type.measuringSense) {
				if (act.sense == 0) {
					act.sense = 3;
				}
				$('input:radio[name=gsense]').filter('[value=' + act.sense + ']').attr('checked', true);
				$('#senseLowId').html(act.definition.type.minScaleText);
				$('#senseHighId').html(act.definition.type.maxScaleText);
				$('#senseSectionId').show();
			} else {
				$('#senseSectionId').hide();
			}

			var planned = act.definition.type.name + ',&nbsp;' + act.day.value + '&nbsp;' + act.date + '&nbsp;' + act.time;

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

			// keep template in form, instead of removing it...
			$('#reportFormId').append($('#dateTimeInputId'));
			$('#measurementTableId tbody > tr').empty();
			
			
			$('#dateTimeInputId input[name="date"]').datepicker( "option", "defaultDate", date);
			$('#dateTimeInputId input[name="date"]').attr('value', date);
			$('#dateTimeInputId input[name="time"]').attr('value', time);
			var label = $('<label>');
			label.attr('for', 'date');
			label.html(_captions.time);
			$('#measurementTableId tbody').append($('<tr>').append($('<td>').append(label)).append($('<td>').append($('#dateTimeInputId'))));
			$('#dateTimeInputId').show();
			
			$.each(act.measurements, function(index, m) {
				var type = m.measurementDefinition.measurementType;
				var unit = _util.formatUnit(type.unit);

				var tr = $('<tr>');
				var label = $('<label>');
				label.attr('for', 'mval-' + type.seqno);
				label.html(type.name + ' (' + unit + ')');
				tr.append($('<td>').append(label));
				
				var input = $('<input>');
				input.attr('name', 'mval-' + type.seqno);
				input.attr('type', 'number');
				input.attr('step', '1');
				input.attr('class', 'small');
				
				input.focusin(function() {
					NC.focusGained(input);
				});	
				
				input.focusout( function () { 
					NC.focusLost(input);
				});
				if (type.valueType.code == 'INTERVAL') {
					input.attr('value', act.reported != null ? m.reportedValue : (m.maxTarget + m.minTarget) / 2);
				} else {
					input.attr('value', act.reported != null ? m.reportedValue : m.target);
				}
				tr.append($('<td>').append(input));
				
				$('#measurementTableId tbody').append(tr);
			});

			
			$('#reportFormDiv').modal('show');
			$('#reportFormDiv input[name="mval-1"]').focus();		

		});
		rbtn.attr('title', act.reported != null ? _captions.change : _captions.report);
		div.append(rbtn);
		
		var cbtn = _util.createIcon('exit', 24, function(xevent) {
			xevent.preventDefault();
			var id = act.id;
			var rep = new Object();
			rep.actualDate = null;
			rep.actualTime = null;
			rep.sense = 0;
			rep.note = null;
			rep.rejected = true;
			rep.values = new Array();

			var jsonObj = JSON.stringify(rep);

			NC.log("JSON: " + jsonObj.toString());

			public.performReport(id, jsonObj, function(data, last) {
				cbtn.attr('disabled', data.rejected);
				if (_reportCallback != null) {
					_reportCallback(data.definition.id, 0, last);
				}
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

			if (_shortVersion) {
				$('#historyOptions').hide();
			}
			
			$('#dateTimeInputId input[name="date"]').datepicker({
				dateFormat : 'yy-mm-dd',
				firstDay : 1,
				minDate : -14
			});

			_support.loadMonths(function(data) {
				$('#dateTimeInputId input[name="date"]').datepicker('option',
						'monthNames', data);
			});

			_support.loadWeekdays(function(data) {
				$('#dateTimeInputId input[name="date"]').datepicker('option',
						'dayNamesMin', data);
			});

			_util.validateTimeField($('#dateTimeInputId input[name="time"]'));

			$('#reportFormId :submit').click(function(event) {
				event.preventDefault();
				var id = $('#reportFormDiv input[name="activityId"]').val();
				var rep = new Object();
				rep.actualDate = $('#reportFormDiv input[name="date"]').val();
				rep.actualTime = $('#reportFormDiv input[name="time"]').val();				
				rep.sense = $('#reportFormDiv input[name="gsense"]:checked').val();
				rep.note = $('#reportFormDiv input[name="note"]').val();
				rep.rejected = false;
				rep.values = new Array();
				var nValues = parseInt($('#reportFormDiv input[name="numValueId"]').val());
				NC.log('Report contains ' + nValues + ' values.');
				for (var i = 0; i < nValues; i++) {
					var value = new Object();
					value.seqno = i+1;
					value.value = parseInt($('#reportFormDiv input[name="mval-' + value.seqno + '"]').val());
					rep.values.push(value);
				}
				var jsonObj = JSON.stringify(rep);
				public.performReport(id, jsonObj, function(data, last) {
					$('#reportFormDiv').modal('hide');
					if (_reportCallback != null) {
						_reportCallback(data.definition.id, 1, last);
					}
				});
			});

			$('#historyBoxId').click(function() {
				public.showHistory($(this).is(':checked'));
			});
			public.list();
		},
		
		performReport : function(activityId, formData, callback) {
			_ajax.post('/patient/schema/' + activityId + '/accept', formData, function(data) {
				$('#act-' + activityId).css('color', _lineColor(data.data));
				NC.log('#rep-' + activityId + ', ' + _reportText(data.data));
				$('#rep-' + activityId).html(_reportText(data.data));
				_dueActivities.push(data.data);
				if (_shortVersion) {
					$('#act-' + activityId).hide();
					_schemaCount--;
					if (_schemaCount == 0) {
						_updateDescription();
					}
				}
				callback(data.data, _schemaCount <= 0);
				
			}, true);	
		},
				
		list : function() {
			_ajax.get('/patient/schema', function(data) {
				/* Empty the result list */
				$('#' + tableId + ' tbody > tr').empty();
				_render(data.data);
				public.showHistory(_shortVersion);
				_updateDescription();
			}, false);
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
		},
		
		reportCallback : function(callback) {
			_reportCallback = callback;
		}
			
	};
	
	return public;
};