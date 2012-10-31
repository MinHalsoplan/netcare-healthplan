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
		var _init;
		var _text;
		var _category;
		var _level;
		
		var activityTemplate;
		var my  = {};
		var support;
		var typeOpts;
		var unitOpts;
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			if (_init == undefined) {
				_init = true;
				_text = "";
				_category = "all";
				_level= "all";
			}
			
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
		
		my.initSingleTemplate = function(params, paramSupport) {
			activityTemplate = new Object();
			support = paramSupport;
			typeOpts = new Array();
			unitOpts = new Array();
			
			var that = this;
			this.params = params
			my.loadTemplate(that, params.templateId);
			initMeasureValues(that);
			initUnitValues(that);
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
					var template = _.template($('#activityTemplate').html());
					$('#templateList').append(template(v));
					
					if (v.accessLevel != "CAREUNIT") {
						$('#item-' + v.id).next('a.itemNavigation').after(
							$('<div>').addClass('itemStateText').append(
								$('<div>').addClass('wrapper').html(v.accessLevel.value)
							)
						);
					}
					
					$('#item-' + v.id).live('click', function() {
						/*
						 * Load new content
						 */
						window.location = GLOB_CTX_PATH + '/netcare/admin/template/' + v.id;
						//NC_MODULE.GLOBAL.loadNewPage('/admin/template/' + v.id, NC_MODULE.ACTIVITY_TEMPLATE, { hsaId : '<c:out value="${currentHsaId}" />', templateId : v.id });
					});
				});
			});
		};

		my.loadTemplate = function(my, templateId) {
			/*
			 * Load single template
			 */
			var at = new NC.ActivityTypes();
			
			at.get(templateId, function(data) {
				activityTemplate = data.data;
				renderItems(my, activityTemplate);
				NC.log(activityTemplate);
			});

		};

		
		my.moveItemUp = function(my, itemId) {
			/*
			 * Move an activity item up in the list
			 */
			var items = activityTemplate.activityItems;
			var i = 0;
			for (i;i<items.length;i++) {
				if(items[i].id==itemId) {
					if(i!=0){
						// Change order
						var temp = items[i-1];
						items[i-1] = items[i];
						items[i] = temp;
						// Set new seqno
						items[i-1].seqno--;
						items[i].seqno++;
						break;
					}
				}
			}
			renderItems(my, activityTemplate);
			flash($('#item'+itemId).parent());
		};

		my.moveItemDown = function(my, itemId) {
			/*
			 * Move an activity item down in the list
			 */
			var items = activityTemplate.activityItems;
			var i = 0;
			for (i;i<items.length;i++) {
				if(items[i].id==itemId) {
					if(i<=items.length-2){
						// Change order
						var temp = items[i+1];
						items[i+1] = items[i];
						items[i] = temp;
						// Set new seqno
						items[i+1].seqno++;
						items[i].seqno--;
						break;
					}
				}
			}
			renderItems(my, activityTemplate);
			flash($('#item'+itemId).parent());
		};

		my.deleteItem = function(my, itemId) {
			/*
			 * Delete an activity item from the list
			 */
			var items = activityTemplate.activityItems;
			for (var i=0;i<items.length;i++) {
				if(items[i].id==itemId) {
					items.splice(i,1);
				}
			}
			renderItems(my, activityTemplate);
		};

		my.showItemForm = function(my, itemId) {
			/*
			 * Display the form for an activity item from the list
			 */
			var items = activityTemplate.activityItems;
			for (var i=0;i<items.length;i++) {
				if(items[i].id==itemId) {
					renderFormForItem(items[i]);
				}
			}
		};

		 var renderItems = function( my, activityTemplate ) {
			$('#activityTypeName').val(activityTemplate.name);
			var template = _.template($('#activityItemTemplate').html());
			$('#activityTypeItems').empty();
			$.each(activityTemplate.activityItems, function(index, value) {
				setDetailsText(value);
				$('#activityTypeItems').append(template(value));
				$('#item' + value.id + 'moveUp').on('click', function() {
					my.moveItemUp(my, value.id);
				});
				$('#item' + value.id + 'moveDown').on('click', function() {
					my.moveItemDown(my, value.id);
				});
				$('#item' + value.id + 'delete').on('click', function() {
					my.deleteItem(my, value.id);
				});
				$('#item' + value.id).on('click', function() {
					my.showItemForm(my, value.id);
				});
				$('#item' + value.id + 'showDetails').on('click', function() {
					my.showItemForm(my, value.id);
				});
			});
		
			function setDetailsText(item) {
				if(item.activityItemTypeName == 'measurement') {
					item.details = item.valueType.value + ' | ' + item.unit.value;
				} else if(item.activityItemTypeName == 'estimation') {
					item.details = 'Från ' + item.minScaleValue + ' (' 
						+ item.minScaleText + ') till ' + item.maxScaleValue + ' (' + item.maxScaleText + ')';
				}
			}
		}
		var renderFormForItem = function(item) {
			var template; 
			$('#activityItemFormContainer').empty();
			if(item.activityItemTypeName == 'measurement') {
				renderMeasurementForm(item);
			} else if(item.activityItemTypeName == 'estimation') {
				template = _.template($('#activityItemEstimationForm').html());
				$('#activityItemFormContainer').append(template(item));
			} else if(item.activityItemTypeName == 'yesno') {
				template = _.template($('#activityItemYesNoForm').html());
				$('#activityItemFormContainer').append(template(item));
			} else if(item.activityItemTypeName == 'text') {
				template = _.template($('#activityItemTextForm').html());
				$('#activityItemFormContainer').append(template(item));
			}
			$('#activityTypeContainer').hide('slide', { direction: 'left' }, 300);
			$('#activityItemFormContainer').show('slide', { direction: 'left' }, 500);
			$('#backButtonForm').on('click', function(){
				$('#activityItemFormContainer').hide('slide', { direction: 'left' }, 400);
				$('#activityTypeContainer').show('slide', { direction: 'left' }, 400);
			});

		}

		var applyTemplate = function(formTemplate, item) {
			template = _.template($('#' + formTemplate).html());
			$('#activityItemFormContainer').append(template(item));
		}

		var renderMeasurementForm = function(item) {
			applyTemplate('activityItemMeasurementForm', item);
			var typeCode = item.valueType.code;
			$.each(typeOpts, function(i, v) {
				if(typeCode==v.attr('value')) {
					v.attr('selected', 'selected');
				}
				$('#valueType').append(v);
			});
			$('#valueType').change(function(e) {
				var val = $(this).val();
				switch(val) {
				case 'SINGLE_VALUE':
					$('#measureValueIntervalOnly').hide();
					break;
				case 'INTERVAL':
					$('#measureValueIntervalOnly').show();
					break;
				}
			});
			// Trigger the event handler
			$('#valueType').change();

			var unitCode = item.unit.code;
			$.each(unitOpts, function(i, v) {
				if(unitCode==v.attr('value')) {
					v.attr('selected', 'selected');
				}
				$('#measurementUnit').append(v);
			});
			
			if(item.alarm) {
				$('#measurementAlarm').attr('checked', 'on');
			}
		}
		
		var flash = function(something) {
			something.animate({
				'backgroundColor' : '#eee'
			}, 100).animate({
				'backgroundColor' : 'white'
			}, 200);
		}
		
		var initMeasureValues = function(my) {
			support.getMeasureValueTypes(function(data) {
				$.each(data.data, function(i, v) {
					typeOpts.push($('<option>').attr('value', v.code).html(v.value));
				});
			});
		}
		var initUnitValues = function(my) {
			support.getUnits(function(data) {
				$.each(data.data, function(i, v) {
					unitOpts.push($('<option>').attr('value', v.code).html(v.value));
				});
			});
		}

		var removeMeasureValueForm = function() {
			if ($('#measureValueContainer div').size() > 0) {
				$('#measureValueContainer').empty();
			}
		};

		return my;
	})()
};








