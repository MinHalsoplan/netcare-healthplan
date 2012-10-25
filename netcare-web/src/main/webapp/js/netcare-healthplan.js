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
var NC_MODULE = {
	ACTIVITY_TEMPLATE : (function() {
		var my  = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.loadCategories();
			my.loadTemplates(that);
		};
		
		my.loadCategories = function() {
			var tc = new NC.ActivityCategories();
			tc.loadAsOptions($('select[name="category"]'));
		};
		
		my.loadTemplates = function(my) {
			/*
			 * Load activity template
			 */
			var at = new NC.ActivityTypes();
			at.load(my.params.hsaId, function(data) {
				
				$.each(data.data, function(i, v) {
					var template = _.template($("#activityTemplate").html());
					$('#templateList').append(template(v));
					
					$('#item-' + v.id).live('click', function() {
						window.location = GLOB_CTX_PATH + '/netcare/admin/template/' + v.id;
					});
					
					
				});
				
			}, false);
		};
		
		return my;
	})()
};








