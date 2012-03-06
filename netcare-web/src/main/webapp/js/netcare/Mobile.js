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
NC.Mobile = function() {
	
	var public = {
			
		createReportField : function(id, parent, label, value) {
			var div = $('<div>').attr('id', id).attr('data-role', 'fieldcontain').append(
				$('<label>').attr('for', id).html(label).addClass('ui-input-text')
			).append(
				$('<input>').attr('type', 'number').attr('id', id).attr('name', name).attr('value', value).addClass('ui-input-text').addClass('ui-body-d').addClass('ui-corner-all').addClass('ui-shadow-inset')
			);
			
			div.addClass('ui-field-contain').addClass('ui-body').addClass('ui-br');
			
			NC.log("Prepending input div");
			parent.prepend(div);
		},
			
		createListHeader : function(parent, title) {
			if (parent === undefined) {
				throw new Error("Parent element is not defined");
			}
			
			var header = $('<li>').attr('data-role', 'list-divider')
			.attr('role', 'heading')
			.addClass('ui-li')
			.addClass('ui-li-divider')
			.addClass('ui-btn')
			.addClass('ui-bar-b')
			.addClass('ui-li-has-count')
			.addClass('ui-btn-up-undefined') 
			.html(title);
			
			parent.append(header);
		},
		
		createListRow : function(parent, href, value, clickCallback) {
			
			if (!value.active) {
				return false;
			}
			
			var activityContainer = $('<li>').attr('data-theme', 'c')
			.addClass('ui-btn')
			.addClass('ui-btn-icon-right')
			.addClass('ui-li-has-arrow')
			.addClass('ui-li')
			.addClass('ui-btn-up-c');
			
			var activityContentDiv = $('<div>').attr('area-hidden', 'true')
			.addClass('ui-btn-inner')
			.addClass('ui-li');
		
			activityContentDiv.append(
				$('<span>').addClass('ui-icon').addClass('ui-icon-arrow-r').addClass('ui-icon-shadow')
			);
			
			activityContainer.append(activityContentDiv);
			
			var link = $('<a>').attr('href', '#report').addClass('ui-link-inherit');
			activityContentDiv.append(link);
			
			link.append(
				$('<p><strong>' + value.time + '</strong></p>').addClass('ui-li-aside').addClass('ui-li-desc')
			);
			
			var activityText = $('<div>').addClass('ui-btn-text');
			activityText.append(
				$('<h3>' + value.definition.type.name + '</h3>').addClass('ui-li-heading')
			);
			
			var desc = '';
			$.each(value.definition.goalValues, function(i, v) {
				var interval = v.measurementType.valueType.code;
				if (interval == "INTERVAL") {
					desc += v.measurementType.name + ': ' + v.minTarget + '-' + v.maxTarget + ' ' + v.measurementType.unit.value; 
				} else if (interval == "SINGLE_VALUE") {
					desc += v.measurementType.name + ': ' + v.target + ' ' + v.measurementType.unit.value;
				}
				
				if (i != value.definition.goalValues.length -1) {
					desc += ', ';
				}
			});
			
			activityText.append(
				$('<p>').addClass('ui-li-desc').html(desc)
			);
			
			if (value.reported != null) {
				
				var reported = '';
				$.each(value.measurements, function(i, v) {
					reported += v.measurementDefinition.measurementType.name + ': ' + v.reportedValue;
					
					if (i != value.definition.goalValues.length -1) {
						reported += ', ';
					}
					
				});
				
				activityText.append(
					$('<p>').addClass('ui-li-desc').html('Rapporterade värden: ' + reported)
				);
			}
			
			link.append(activityText);
			
			parent.append(activityContainer);
			
			link.click(function(e) {
				clickCallback(value.id);
			});
		}
	
			
	};
	
	return public;
	
}