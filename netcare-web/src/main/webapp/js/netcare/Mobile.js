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
			
			activityText.append(
				$('<p>' + value.definition.goal +  ' ' + value.definition.type.unit.value + '</p>').addClass('ui-li-desc')
			);
			
			if (value.reported != null) {
				activityText.append(
					$('<p>Rapporterat värde: ' + value.actualValue +  ' ' + value.definition.type.unit.value + '</p>').addClass('ui-li-desc')
				);
				
				activityText.append(
					$('<p>' + value.reported + '</p>').addClass('ui-li-desc')
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