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
var NETCARE = {

	UNITS : (function() {
		
		var my = {};
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			/*
			 * Load units
			 */
			var units = new Backbone.Collection;
			units.url = NC.getContextPath() + '/api/units';
			units.fetch();
			
			/*
			 * Render view
			 */
			var view = new Backbone.View.extend({
				initialize : function() {
					
				}
			});
			
		};
		
		return my;
		
	})()
		
};
