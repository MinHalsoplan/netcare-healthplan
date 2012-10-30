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
		
	GLOBAL : (function() {
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
		};
		
		my.loadNewPage = function(url, module, moduleParams) {
			
			// Show spinner
			
			$('#inboxDetailWrapper .wrapper').load(GLOB_CTX_PATH + '/netcare/' + url + ' #maincontainerwrapper', function() {
				module.init(moduleParams);
				// Hide spinner
			});
		};
		
		return my;
	})(),
		
	ACTIVITY_TEMPLATE : (function() {
		
		var _text = "";
		var _category = "all";
		var _level = "all";
		
		var my  = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.loadCategories();
			my.loadLevels();
			my.searchTemplates(that);
			my.initEventListeners(that);
		};
		
		my.initEventListeners = function(my) {
			
			$('select[name="category"]').change(function() {
				_category = $(this).find('option:selected').val();
				my.searchTemplates(my);
			});
			
			$('select[name="level"]').change(function() {
				_level = $(this).find('option:selected').val();
				my.searchTemplates(my);
			});
			
			$('input.search-query').keyup(function() {
				_text = $(this).val();
			});
			
			$(':submit').click(function(e) {
				e.preventDefault();
				my.searchTemplates();
			})
		};
		
		my.loadCategories = function() {
			var opt = $('<option>', { value : 'all', selected : 'selected' });
			opt.html('-- Alla --');
			
			var tc = new NC.ActivityCategories();
			tc.loadAsOptions($('select[name="category"]'));
			
			$('select[name="category"]').prepend(opt);
		};
		
		my.loadLevels = function() {
			var opt = $('<option>', { value : 'all', selected : 'selected' });
			opt.html('-- Alla --');
			
			var tc = new NC.Support();
			tc.loadAccessLevels($('select[name="level"]'));
			
			$('select[name="level"]').prepend(opt);
		};
		
		my.searchTemplates = function(my) {
			NC.log('Searching... Text: ' + _text + ', Category: ' + _category + ', Level: ' + _level);
			var ajax = new NC.Ajax().getWithParams('/activityType/search', { 'text' : _text, 'category' : _category, 'level' : _level}, function(data) {
				
				$('#templateList').empty();
				
				$.each(data.data, function(i, v) {
					var template = _.template($("#activityTemplate").html());
					$('#templateList').append(template(v));
					
					$('#item-' + v.id).live('click', function() {
						/*
						 * Load new content
						 */
						NC_MODULE.GLOBAL.loadNewPage('/admin/template/' + v.id, NC_MODULE.ACTIVITY_TEMPLATE, { hsaId : '<c:out value="${currentHsaId}" />' });
					});
				});
			});
		};
		
		return my;
	})()
};








