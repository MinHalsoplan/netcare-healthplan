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

			$('#inboxDetailWrapper .wrapper').load(
					GLOB_CTX_PATH + '/netcare/' + url
							+ ' #maincontainerwrapper', function() {
						module.init(moduleParams);
						// Hide spinner
					});
		};
		
		my.flash = function(something) {
			something.animate({
				'backgroundColor' : '#eee'
			}, 100).animate({
				'backgroundColor' : 'white'
			}, 200);
		};
		
		my.validateField = function(field) {
			if (field.val().length == 0 || field.val() == "") {
				field.parent().parent().addClass('error');
				return true;
			} else {
				
				if (field.hasClass('signedNumeric')) {
					var value = parseInt(field.val());
					NC.log('Value of field is: ' + value);
					
					if (value <= 0) {
						field.parent().parent().addClass('error');
						return true;	
					}
				}
				
				field.parent().parent().removeClass('error').addClass('success');
				return false;
			}
		};

		return my;
	})(),
	
	HEALTH_PLAN : (function() {
		
		var _data = new Object();
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.loadDurations();
			
			if (params.id > 1) {
				my.loadHealthPlan(that);
			} else {
				_data = new Object();
				_data.patient = new Object();
				_data.patient.id = params.patientId;
			}
			
			my.initListeners();
			
			/*
			 * Load all in list
			 */
			my.loadHealthPlans(that);
		};
		
		my.loadDurations = function() {
			new NC.Support().loadDurations($('#createHealthPlanForm select'));
		};
		
		my.initListeners = function() {
			$('input[name="name"]').on('keyup blur', function() {
				_data.name = $(this).val();
				NC.log('Updated name to: ' + _data.name);
			});
			
			$('input[name="startDate"]').on('keyup blur', function() {
				_data.startDate = $(this).val();
				NC.log('Updated start date to: ' + _data.startDate);
			});
			
			$('input[name="duration"]').on('keyup blur', function() {
				_data.duration = $(this).val();
				NC.log('Updated duration to: ' + _data.duration);
			});
			
			$('select[name="type"]').on('focus change', function() {
				_data.durationUnit = new Object();
				_data.durationUnit.code = $(this).val();
				NC.log('Updated duration unit to: ' + _data.durationUnit.code);
			});
			
			$('input[name="autoRenewal"]').on('click', function() {
				var checked = $('input[name="autoRenewal"]:checked').val();
				if (checked != undefined) {
					_data.autoRenewal = true;
				} else {
					_data.autoRenewal = false;
				}
				
				NC.log('Updated auto renewal to: ' + _data.autoRenewal);
			});
			
			$('#createHealthPlanForm').submit(function(e) {
				e.preventDefault();
				my.save();
			})
		};
		
		my.loadHealthPlan = function(my) {
			new NC.Ajax().get('/healthplans/' + my.params.id, function(data) {
				_data = data.data;
				my.renderForm();
			}, false);
		};
		
		my.loadHealthPlans = function(my) {
			new NC.Ajax().getWithParams('/healthplans', { patient : my.params.patientId }, function(data) {
				$('#healthPlanContainer').empty();
				
				$.each(data.data, function(i, v) {
					var t = _.template($('#healthPlanItem').html());
					if (t == undefined) {
						throw new Error('Template undefined');
					}
					
					var dom = t(v);
					$('#healthPlanContainer').append($(dom));
					
					/*
					 * Bind click event
					 */
					var liElem = $('#healthPlanItem' + v.id).next('.item');
					liElem.click(function() {
						window.location = GLOB_CTX_PATH + '/netcare/admin/healthplans/' + v.id;
					});
					
					if (v.autoRenewal) {
						liElem.find('.subRow').html(my.params.lang.active + ' | ' + my.params.lang.autoRenew);
					} else {
						liElem.find('.subRow').html(my.params.lang.active + ' | ' + my.params.lang.ends + ' ' + v.endDate);
					}
					
					var detailsTemplate = _.template($('#healthPlanDetails').html());
					var detailsDom = detailsTemplate(v);
					
					liElem.find('.row-fluid').after($(detailsDom));
					
					if (v.activityDefinitions == null) {
						$('#hp-details-' + v.id).find('.span12').append(
							$('<p>').css({'font-style' : 'italic'}).html('Inga aktiviteter planerade ännu')
						);
					} else {
						$.each(v.activityDefinitions, function(idx, ad) {
							
						});
					}
					
					var expander = $('<div>').addClass('mvk-icon toggle').click(function(e) {
						e.preventDefault();
						e.stopPropagation();
						$('#hp-details-' + v.id).toggle();
					}) ;
					
					liElem.find('.actionBody').css('text-align', 'right').css('padding-right', '40px')
					.append(expander);
					
				});
				
			}, false);
		};
		
		my.renderForm = function(my) {
			
		};
		
		my.validate = function() {
			var errors = false;
			
			errors = NC_MODULE.GLOBAL.validateField($('input[name="name"]'));
			errors = NC_MODULE.GLOBAL.validateField($('input[name="startDate"]'));
			errors = NC_MODULE.GLOBAL.validateField($('input[name="duration"]'));
			
			return !errors;
		};
		
		my.save = function() {
			if (my.validate()) {
				NC.log('Saving health plan. Data is: ' + _data);
				new NC.Ajax().post('/healthplans', _data, function(data) {
					alert('New health plan saved');
				}, true);
			}
		};
		
		return my;
		
	})(),
	
	PLAN_ACTIVITY : (function() {
		var _templateData = null;
		var _data = new Object();
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_data.activityDefinitions = new Array();
			_data.dayTimes = new Array();
			
			NC_MODULE.ACTIVITY_TEMPLATE.loadTemplate(params.templateId, function(data) {
				_templateData = data;
				my.renderGoals(that);
			});
			
			/*
			 * Check if we should load an activity
			 * definition here and populate the form
			 */
			
			my.initListeners(that);
		};
		
		my.initListeners = function(my) {
			$('#addTimesForm').submit(function(e) {
				e.preventDefault();
				
				if ($('#addTimesForm input:checkbox:checked').length == 0) {
					NC.log('No days specified');
				} else {
				
					my.doTimeRendering(my);
				
					/* Uncheck checkboxes and reset value */
					$('#addTimesForm input:checkbox:checked').each(function(i, v) {
						$(this).prop('checked', false);
					});
					
					$('#specifyTime').val('');
				}
			});
		};
		
		my.doTimeRendering = function(my) {
			
			var val = $('#specifyTime').val().trim();
			
			/*
			 * Loop through days
			 */
			$('#addTimesForm input:checkbox:checked').each(function(i, v) {
				var day = $(v).prop('name');
				if (_data.dayTimes[day] == undefined) {
					_data.dayTimes[day] = new Object();
					_data.dayTimes[day].day = day;
					_data.dayTimes[day].times = new Array();
				}
				
				// Does time already exist for day?
				if ($.inArray(val, _data.dayTimes[day].times) == -1) {
					_data.dayTimes[day].times.push(val);
					my.renderTimes(my, day);
				}
			});
		};
		
		my.renderGoals = function(my) {
			NC.log('Render goals');
			$.each(_templateData.activityItems, function(i, v) {
				
				if (v.activityItemTypeName == "measurement" && v.valueType.code == "INTERVAL") {
					my.renderIntervalGoal(v, '');
				} else if (v.activityItemTypeName == "measurement" && v.valueType.code == "SINGLE_VALUE") {
					my.renderSingleGoal(v, '');
				}
			});
		};
		
		my.renderSingleGoal = function(activityItem, value) {
			NC.log('Render single goal');
			var t = _.template( $('#singleValue').html() );
			var dom = t(activityItem);
			
			$('#activityFieldset').append($(dom));
			
			$('#field-' + activityItem.id).on('blur keyup', function() {
				_data.activityDefinitions['' + activityItem.id + ''] = new Object();
				_data.activityDefinitions['' + activityItem.id + ''].id = activityItem.id;
				_data.activityDefinitions['' + activityItem.id + ''].target = $(this).val();
				
				NC.log('Target set to: ' + $(this).val() + ' for ' + activityItem.name);
			});
		};
		
		my.renderIntervalGoal = function(activityItem, value) {
			NC.log('Render interval goal');
			var t = _.template( $('#intervalValue').html() );
			var dom = t(activityItem);
			
			$('#activityFieldset').append($(dom));
			
			var min = $('#field-' + activityItem.id + '-min');
			var max = $('#field-' + activityItem.id + '-max');
			
			var updateIntervalValues = function(activityItem) {
				_data.activityDefinitions['' + activityItem.id + ''] = new Object();
				_data.activityDefinitions['' + activityItem.id + ''].id = activityItem.id;
				_data.activityDefinitions['' + activityItem.id + ''].minTarget = $(min).val();
				_data.activityDefinitions['' + activityItem.id + ''].maxTarget = $(max).val();
				
				NC.log('Min target set to: ' + $(min).val() + ' for ' + activityItem.name);
				NC.log('Max target set to: ' + $(max).val() + ' for ' + activityItem.name);
			};
			
			$(min).on('blur keyup', function() {
				updateIntervalValues(activityItem);
			});
			
			$(max).on('blur keyup', function() {
				updateIntervalValues(activityItem);
			});
		};
		
		my.renderTimes = function(my, day) {
			
			NC.log('Render times for ' + day);
			
			var times = _data.dayTimes[day].times;
			NC.log('Times are: ' + times);
			
			var container = $('#'+ day + '-container');
			
			container.hide();
			container.find('.times').empty();
		
			if (times.length > 0) {
				$.each(times, function(i, v) {
					
					var tc = $('<span>').css({'display' : 'inline', 'padding-right' : '20px'});
					tc.append(
						$('<span>').html(v)
					).append(
						$('<a>').html(' ×').click(function(e) {
							e.preventDefault();
							
							NC.log('Remove ' + v + ' for ' + day);
							var idx = $.inArray(v, _data.dayTimes[day].times);
							_data.dayTimes[day].times.splice(idx, 1);
							
							my.renderTimes(my, day);
						})
					);
					
					container.find('.times').append(tc);
				});
				
				container.show();
			}
		};
		
		return my;
	})(),

	TEMPLATE_SEARCH : (function() {
		var _init;
		var _name;
		var _category;
		var _level;
		var my = {};

		my.init = function(params) {
			var that = this;
			this.params = params;

			if (_init == undefined) {
				_init = true;
				_name = "";
				_category = "all";
				_level = "all";
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
				_name = $(this).val();
			});

			$(':submit').click(function(e) {
				e.preventDefault();
				my.searchTemplates();
			})
		};

		my.loadCategories = function() {
			var opt = $('<option>', {
				value : 'all',
				selected : 'selected'
			});
			opt.html('-- Alla --');

			var tc = new NC.ActivityCategories();
			tc.loadAsOptions($('select[name="category"]'));

			$('select[name="category"]').prepend(opt);
		};

		my.loadLevels = function() {
			var opt = $('<option>', {
				value : 'all',
				selected : 'selected'
			});
			opt.html('-- Alla --');

			var tc = new NC.Support();
			tc.loadAccessLevels($('select[name="level"]'));

			$('select[name="level"]').prepend(opt);
		};
		
		my.buildDeleteIcon = function(my, template) {
			$('#item-' + template.id).find('.actionBody').append(
				$('<div>').addClass('mvk-icon delete').bind('click', function(e) {
					e.preventDefault();
					e.stopPropagation();
					my.deleteTemplate(my, template.id);
				})
			);
		};
		
		my.buildTemplateItem = function(my, template, insertAfter) {
			var t = _.template($('#activityTemplate').html());
			var dom = t(template);

			if (insertAfter == undefined) {
				$('#templateList').append($(dom));
			} else {
				var elem = $(insertAfter).parents('.item:first');
				$(dom).insertAfter(elem);
			}

			if (template.accessLevel.code != "CAREUNIT") {
				var t2 = _.template($('#itemNote').html());
				$('#item-' + template.id).next('a.itemNavigation').after(t2(template.accessLevel));	
			}
			
			$('#item-' + template.id).find('.actionBody').append(
				$('<div>').addClass('mvk-icon copy').bind('click', function(e) {
					e.preventDefault();
					e.stopPropagation();
					my.copyTemplate(my, template);
				})
			);
			
			if (!template.inUse) {
				if (template.accessLevel.code == "CAREUNIT" && my.params.isCareActor == "true") {
					NC.log('Template has care unit level and we have care actor role. Show delete icon');
					my.buildDeleteIcon(my, template);
				}
				
				if (template.accessLevel.code == "COUNTY_COUNCIL" && my.params.isCountyActor == "true") {
					NC.log('Template has county level and we have county role. Show delete icon');
					my.buildDeleteIcon(my, template);
				}
				
				if (template.accessLevel.code == "NATIONAL" && my.params.isNationActor == "true") {
					NC.log('Template has nation level and we have nation role. Show delete icon');
					my.buildDeleteIcon(my, template);
				}
			}
			
			$('#item-' + template.id).live('click', function() {
				NC.log('Health plan: ' + my.params.healthPlanId);
				if (my.params.healthPlanId != '') {
					NC.log('Add to health plan ' + my.params.healthPlanId);
					window.location = GLOB_CTX_PATH + '/netcare/admin/healthplans/' + my.params.healthPlanId + '/plan/new?template=' + template.id;
				} else {
					window.location = GLOB_CTX_PATH + '/netcare/admin/template/' + template.id;
				}
			});
			
			NC_MODULE.GLOBAL.flash($('#item-' + template.id).parent());
		};

		my.searchTemplates = function(my) {
			NC.log('Searching... Text: ' + _name + ', Category: ' + _category
					+ ', Level: ' + _level);
			var ajax = new NC.Ajax().getWithParams('/templates/', {
				'name' : _name,
				'category' : _category,
				'level' : _level
			}, function(data) {
				$('#templateList').empty();
				$.each(data.data, function(i, v) {
					my.buildTemplateItem(my, v);
				});
			});
		};

		my.copyTemplate = function(my, template) {
			NC.log('Copy template');
			template.name = template.name + ' (Kopia)';

			new NC.ActivityTypes().create(template, function(data) {
				NC.log('Copied ' + template.id + ' new id is: ' + data.data.id);
				my.buildTemplateItem(my, data.data, '#item-' + template.id);
			});
		};

		my.deleteTemplate = function(my, templateId) {
			NC.log('Delete template');
			new NC.ActivityTypes().deleteTemplate(templateId, function() {
				NC.log('Item removed');
				$('#item-' + templateId).parents('.item:first').fadeOut('fast');
			});
		};

		return my;

	})(),

	ACTIVITY_TEMPLATE : (function() {
		var activityTemplate;
		var my = {};
		var support;
		var typeOpts;
		var unitOpts;
		var categories;
		var nextItemId = -1;

		my.initSingleTemplate = function(params, paramSupport) {
			var at = new NC.ActivityTypes();
			activityTemplate = new Object();
			support = paramSupport;
			typeOpts = new Array();
			unitOpts = new Array();
			categories = new Array();

			var that = this;
			this.params = params;
			
			if (params.templateId != -1) {
				my.loadTemplate(params.templateId, function(data) {
					activityTemplate = data.data;
					renderItems(my, activityTemplate);
					NC.log(activityTemplate);
				});
			} else {
				activityTemplate = {
					"id" : -1,
					"name" : "",
					"inUse" : false,
					"accessLevel" : {
						"code" : "CAREUNIT",
						"value" : ""
					},
					"category" : {
						"id" : null,
						"name" : null
					},
					"activityItems" : []
				}

			}

			my.loadCategories();
			my.loadAccessLevels(that);

			initMeasureValues(that);
			initUnitValues(that);

			$('#activityTypeName').on('change', function(event) {
				activityTemplate.name = this.value;
			});

			$('#activityTypeCategory').on('change', function(event) {
				activityTemplate.category.id = this.value;
			});
			$('#addMeasurementButton').on('click', function(event) {
				createItem('measurement');
				event.preventDefault();
			});
			$('#addEstimationButton').on('click', function(event) {
				createItem('estimation');
				event.preventDefault();
			});
			$('#addYesNoButton').on('click', function(event) {
				createItem('yesno');
				event.preventDefault();
			});
			$('#addTextButton').on('click', function(event) {
				createItem('text');
				event.preventDefault();
			});

			function createItem(type) {
				var item = {
					'id' : nextItemId--,
					'name' : "Saknar namn",
					'seqno' : activityTemplate.activityItems.length,
					'activityItemTypeName' : type,
					'alarm' : false,
					'valueType' : {
						'code' : null,
						'value' : null
					},
					'unit' : {
						'code' : null,
						'value' : null
					},
					"minScaleText" : null,
					"maxScaleText" : null,
					"minScaleValue" : null,
					"maxScaleValue" : null,
					"question" : null,
					"label" : null
				};
				activityTemplate.activityItems.push(item);
				renderItems(my, activityTemplate);
				$('#item' + item.id + 'showDetails').click();
			}
			$('#activitySaveButton')
			.on(
					'click',
					function() {
						NC.log('Save button clicked');
						activityTemplate.name = $('#activityTypeName')
								.val();
						for ( var i = 0; i < activityTemplate.activityItems.length; i++) {
							delete activityTemplate.activityItems[i].details;
						}
						if (activityTemplate.id == -1) {
							at.create(activityTemplate, function(data) {
								activityTemplate = data.data;
								renderItems(my, activityTemplate);
								NC.log(activityTemplate);
							});
						} else {
							at.update(params.templateId, activityTemplate,
									function(data) {
										activityTemplate = data.data;
										renderItems(my,
												activityTemplate);
										NC.log(activityTemplate);
									});
						}
					});
		};

		my.loadTemplate = function(templateId, callback) {
			/*
			 * Load single template
			 */
			var at = new NC.ActivityTypes();

			at.get(templateId, function(data) {
				callback(data.data);
			});
		};

		my.loadCategories = function() {
			var tc = new NC.ActivityCategories();
			tc.loadAsOptions($('#activityTypeCategory'));
		};
		
		my.loadAccessLevels = function(my) {
			new NC.Support().loadAccessLevels($('#activityTypeAccessLevel'));
			
			/*
			 * Role check, remove items that we do not have access to
			 */
			if (my.params.isCareActor == "true" && my.params.isCountyActor == "false") {
				$('#activityTypeAccessLevel').prop('disabled', 'disabled');
			}
		};
		
		my.moveItemUp = function(my, itemId) {
			/*
			 * Move an activity item up in the list
			 */
			var items = activityTemplate.activityItems;
			var i = 0;
			for (i; i < items.length; i++) {
				if (items[i].id == itemId) {
					if (i != 0) {
						// Change order
						var temp = items[i - 1];
						items[i - 1] = items[i];
						items[i] = temp;
						// Set new seqno
						items[i - 1].seqno--;
						items[i].seqno++;
						break;
					}
				}
			}
			renderItems(my, activityTemplate);
			NC_MODULE.GLOBAL.flash($('#item' + itemId).parent());
		};

		my.moveItemDown = function(my, itemId) {
			/*
			 * Move an activity item down in the list
			 */
			var items = activityTemplate.activityItems;
			var i = 0;
			for (i; i < items.length; i++) {
				if (items[i].id == itemId) {
					if (i <= items.length - 2) {
						// Change order
						var temp = items[i + 1];
						items[i + 1] = items[i];
						items[i] = temp;
						// Set new seqno
						items[i + 1].seqno++;
						items[i].seqno--;
						break;
					}
				}
			}
			renderItems(my, activityTemplate);
			NC_MODULE.GLOBAL.flash($('#item' + itemId).parent());
		};

		my.deleteItem = function(my, itemId) {
			/*
			 * Delete an activity item from the list and decrease seqno
			 */
			var items = activityTemplate.activityItems;
			var decreaseSeqno = false;
			for ( var i = 0; i < items.length; i++) {
				if (items[i].id == itemId) {
					if (i < items.length - 1) {
						decreaseSeqno = true;
					}
					items.splice(i, 1);
				}
				if (decreaseSeqno) {
					items[i].seqno--;
				}
			}
			NC.log(activityTemplate);
			renderItems(my, activityTemplate);
		};

		my.showItemForm = function(my, itemId) {
			/*
			 * Display the form for an activity item from the list
			 */
			var items = activityTemplate.activityItems;
			for ( var i = 0; i < items.length; i++) {
				if (items[i].id == itemId) {
					renderFormForItem(my, items[i]);
				}
			}
		};

		var renderItems = function(my, activityTemplate) {
			$('#activityTypeName').val(activityTemplate.name);
			$('#activityTypeCategory > option[value="' + activityTemplate.category.id + '"]').prop('selected', true);
			$('#activityTypeAccessLevel > option[value="' + activityTemplate.accessLevel.code + '"]').prop('selected', true);
			
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
				if (item.activityItemTypeName == 'measurement') {
					if (item.unit != null && item.valueType != null) {
						item.details = item.unit.value + ' | '
								+ item.valueType.value;
					} else {
						item.details = 'Värden saknas';
					}
				} else if (item.activityItemTypeName == 'estimation') {
					if (item.minScaleValue != null && item.minScaleText != null
							&& item.maxScaleValue != null
							&& item.maxScaleText != null) {
						item.details = 'Från ' + item.minScaleValue + ' ('
								+ item.minScaleText + ') till '
								+ item.maxScaleValue + ' (' + item.maxScaleText
								+ ')';
					} else {
						item.details = 'Värden saknas';
					}
				} else if (item.activityItemTypeName == 'yesno') {
					if (item.question != null) {
						item.details = item.question;
					} else {
						item.details = 'Värden saknas';
					}
				} else if (item.activityItemTypeName == 'text') {
					if (item.label != null) {
						item.details = item.label;
					} else {
						item.details = 'Värden saknas';
					}
				}
			}
		}
		var renderFormForItem = function(my, item) {
			var template;
			$('#activityItemFormContainer').empty();
			var collectAndValidateFunction;
			if (item.activityItemTypeName == 'measurement') {
				renderMeasurementForm(item);
				collectAndValidateFunction = handleMeasurementForm;
			} else if (item.activityItemTypeName == 'estimation') {
				template = _.template($('#activityItemEstimationForm').html());
				$('#activityItemFormContainer').append(template(item));
				collectAndValidateFunction = handleEstimationForm;
			} else if (item.activityItemTypeName == 'yesno') {
				template = _.template($('#activityItemYesNoForm').html());
				$('#activityItemFormContainer').append(template(item));
				collectAndValidateFunction = handleYesNoForm;
			} else if (item.activityItemTypeName == 'text') {
				template = _.template($('#activityItemTextForm').html());
				$('#activityItemFormContainer').append(template(item));
				collectAndValidateFunction = handleTextForm;
			}
			$('#activityTypeContainer').hide('slide', {
				direction : 'left'
			}, 300);
			$('#activityItemFormContainer').show('slide', {
				direction : 'left'
			}, 500);
			$('#backButtonForm').on(
					'click',
					function() {
						if (validateFormAndUpdateModel(
								collectAndValidateFunction, item)) {
							$('#activityItemFormContainer').hide('slide', {
								direction : 'left'
							}, 400);
							$('#activityTypeContainer').show('slide', {
								direction : 'left'
							}, 400);
							renderItems(my, activityTemplate);
						}
					});

		}

		var applyTemplate = function(formTemplate, item) {
			template = _.template($('#' + formTemplate).html());
			$('#activityItemFormContainer').append(template(item));
		}

		var renderMeasurementForm = function(item) {
			applyTemplate('activityItemMeasurementForm', item);
			$.each(typeOpts, function(i, v) {
				if (item.valueType != null
						&& item.valueType.code == v.attr('value')) {
					v.attr('selected', 'selected');
				}
				$('#valueType').append(v);
			});
			$('#valueType').change(function(e) {
				var val = $(this).val();
				switch (val) {
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

			$.each(unitOpts, function(i, v) {
				if (item.unit != null && item.unit.code == v.attr('value')) {
					v.attr('selected', 'selected');
				}
				$('#measurementUnit').append(v);
			});

			if (item.alarm) {
				$('#measurementAlarm').attr('checked', 'on');
			}
		}

		var validateFormAndUpdateModel = function(handleFormFunction, item) {
			NC.log('Collecting form values and validating.');
			return handleFormFunction(item);
		}

		var handleMeasurementForm = function(item) {
			var collected = new Object();
			collected.name = $('#activityItemName').val();
			collected.valueTypeCode = $('#valueType option:selected').val();
			collected.valueTypeValue = $('#valueType option:selected').text();
			collected.measurementUnitCode = $(
					'#measurementUnit option:selected').val();
			collected.measurementUnitValue = $(
					'#measurementUnit option:selected').text();
			collected.alarm = $('#measurementAlarm:checked').val() == "on"
			// Do some validation
			item.name = collected.name;
			item.unit.code = collected.measurementUnitCode;
			item.unit.value = collected.measurementUnitValue;
			item.valueType.code = collected.valueTypeCode;
			item.valueType.value = collected.valueTypeValue;
			item.alarm = collected.alarm;
			return true
		}

		var handleEstimationForm = function(item) {
			var collected = new Object();
			collected.name = $('#activityItemName').val();
			collected.minScaleValue = $('#minScaleValue').val();
			collected.maxScaleValue = $('#maxScaleValue').val();
			collected.minScaleText = $('#minScaleText').val();
			collected.maxScaleText = $('#maxScaleText').val();
			// Do some validation
			item.name = collected.name;
			item.minScaleValue = collected.minScaleValue;
			item.maxScaleValue = collected.maxScaleValue;
			item.minScaleText = collected.minScaleText;
			item.maxScaleText = collected.maxScaleText;
			return true;
		}

		var handleYesNoForm = function(item) {
			var collected = new Object();
			collected.name = $('#activityItemName').val();
			collected.question = $('#yesNoQuestion').val();
			// Do some validation
			item.name = collected.name;
			item.question = collected.question;
			return true;
		}

		var handleTextForm = function(item) {
			var collected = new Object();
			collected.name = $('#activityItemName').val();
			collected.label = $('#textLabel').val();
			// Do some validation
			item.name = collected.name;
			item.label = collected.label;
			return true;
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
					typeOpts.push($('<option>').attr('value', v.code).html(
							v.value));
				});
			});
		}
		var initUnitValues = function(my) {
			support.getUnits(function(data) {
				$.each(data.data, function(i, v) {
					unitOpts.push($('<option>').attr('value', v.code).html(
							v.value));
				});
			});
		}

		return my;
	})()
};
