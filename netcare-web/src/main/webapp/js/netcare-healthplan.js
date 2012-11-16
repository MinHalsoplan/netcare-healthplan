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
					NC.getContextPath() + '/netcare/' + url
							+ ' #maincontainerwrapper', function() {
						module.init(moduleParams);
						// Hide spinner
					});
		};
		
		my.selectPatient = function(patientId, callback) {
			new NC.Ajax().postSynchronous('/user/' + patientId + '/select', null, callback);
		};
		
		my.updateCurrentPatient = function(name) {
			NC.log("Updating current patient. Display: " + name);
			$('#currentpatient a').html(name);
			$('#nopatient').hide();
			$('#currentpatient').show();
		};
		
		my.formatCrn = function(crn) {
			var first = crn.substring(0, 8);
			var last = crn.substring(8, 12);
			
			return first + '-' + last;
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
	
	PATIENTS : (function() {
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.load(that);
		};
		
		my.load = function() {
			new NC.Ajax().get('/user/load', function(data) {
				
				$.each(data.data, function(i, v) {
					
					var t = _.template($('#patientItem').html());
					
					v.civicRegistrationNumber = NC_MODULE.GLOBAL.formatCrn(v.civicRegistrationNumber);
					var dom = t(v);
					
					$('#patientList').append($(dom));
					
					$('#patientItem' + v.id).click(function(e) {
						e.preventDefault();
						
						NC_MODULE.GLOBAL.selectPatient(v.id, function(data) {
							NC_MODULE.GLOBAL.updateCurrentPatient(data.data.name);
							window.location = '/netcare/admin/healthplans?showForm=true';
						});
					});
				});
				
			});
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
			
			if (my.params.showForm != '') {
				$('#createHealthPlanForm').toggle();
			}
		};
		
		my.loadDurations = function() {
			new NC.Support().loadDurations($('#createHealthPlanForm select'));
		};
		
		my.initListeners = function() {
			
			$('#showCreateHealthPlan').click(function() {
				$('#createHealthPlanForm').toggle();
			});
			
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
					my.buildHealthPlanItem(my, v);
				});
				
			}, false);
		};
		
		my.buildHealthPlanItem = function(my, hp) {
			var t = _.template($('#healthPlanItem').html());
			var dom = t(hp);
			$('#healthPlanContainer').append($(dom));
			
			/*
			 * Bind click event
			 */
			var liElem = $('#healthPlanItem' + hp.id);
			liElem.click(function() {
				window.location = NC.getContextPath() + '/netcare/admin/healthplans/' + hp.id;
			});
			
			if (hp.autoRenewal) {
				liElem.find('.subRow').html(my.params.lang.active + ' | ' + my.params.lang.autoRenew);
			} else {
				liElem.find('.subRow').html(my.params.lang.active + ' | ' + my.params.lang.ends + ' ' + hp.endDate);
			}
			
			var detailsTemplate = _.template($('#healthPlanDetails').html());
			var detailsDom = detailsTemplate(hp);
			
			liElem.find('.row-fluid').after($(detailsDom));
			
			my.processDefinitions(my, hp);
			
			var expander = $('<div>').addClass('mvk-icon toggle').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				$('#hp-details-' + hp.id).toggle();
			}) ;
			
			liElem.find('.actionBody').css('text-align', 'right').css('padding-right', '40px')
			.append(expander);
			
			$('#hp-inactivate-' + hp.id).click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				NC.log('Inactivate health plan ' + hp.id);
				my.inactivate(my, hp);
			});
			
		};
		
		my.processDefinitions = function(my, hp) {
			
			NC.log('Processing activity definitions of health plan ' + hp.id);
			
			if (hp.activityDefinitions.length == 0) {
				NC.log('No activity definitions yet available');
				$('#hp-details-' + hp.id).find('.span12').append(
					$('<p>').css({'font-style' : 'italic'}).html(my.params.lang.noActivities)
				);
			} else {
				
				var added = 0;
				
				defLoop:for (var i = 0; i < hp.activityDefinitions.length; i++) {
					var ad = hp.activityDefinitions[i];
					
					NC.log('Processing ' + ad.type.name + '(' + ad.id + ')');
					
					if (!ad.active) {
						ad = null;
						continue defLoop;
					}
					
					var t = _.template($('#healthPlanDefinitions').html());
					var dom = t(ad);
					
					$('#hp-details-' + hp.id).find('.span12').append($(dom));
					
					my.addEventHandlersForDefinition(ad.id);
					
					added++;
				}
				
				if (added == 0) {
					$('#hp-details-' + hp.id).find('.span12').append(
						$('<p>').css({'font-style' : 'italic'}).html(my.params.lang.noActivities)
					);
				}
			}
		};
		
		my.addEventHandlersForDefinition = function(id) {
			$('#hp-ad-' + id + '-edit').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				window.location = NC.getContextPath() + '/netcare/admin/healthplans/' + id + '/plan/' + id;
			});
			
			$('#hp-ad-' + id + '-remove').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				new NC.Ajax().http_delete('/activityPlans/' + id, function(data) {
					$('#hp-ad-' + id).fadeOut('fast');
				});
			});
		};
		
		my.inactivate = function(my, healthPlan) {
			var id = healthPlan.id;
			new NC.Ajax().http_delete('/healthplans/' + id, function() {
				$('#healthPlanItem' + id).fadeOut('fast');
				$('#healthPlanItem' + id).remove();
			});
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
					
					/*
					 * Add new item in list and hide form
					 */
					$('#createHealthPlanForm').hide();
					
					NC.log('New healthplan created with id: ' + data.data.id);
					my.buildHealthPlanItem(data.data);
				}, true);
			}
		};
		
		return my;
		
	})(),
	
	PLAN_ACTIVITY : (function() {
		
		var _isNew = false;
		
		var _templateData = null;
		var _data = new Object();
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_data.goalValues = new Array();
			_data.dayTimes = new Array();
			
			if (params.definitionId != '') {
				new NC.Ajax().get('/activityPlans/' + params.definitionId, function(data) {
					
					_isNew = false;
					
					_data.id = data.data.id;
					_data.healthPlanId = params.healthPlanId;
					_data.goalValues = data.data.goalValues;
					
					$.each(data.data.dayTimes, function(i,v) {
						_data.dayTimes[i] = new Object();
						_data.dayTimes[i].day = v.day;
						_data.dayTimes[i].times = v.times;
					});
					
					_data.startDate = data.data.startDate;
					_data.activityRepeat = data.data.activityRepeat;
					
					_data.type = new Object();
					_data.type.id = data.data.type.id;
					
					_templateData = data.data.type;
					
					my.renderGoals(that);
					my.renderAllTimes(that);
					my.renderForm(that);
				});
			} else {
				NC_MODULE.ACTIVITY_TEMPLATE.loadTemplate(params.templateId, function(data) {
					
					_isNew = true;
					
					_templateData = data;
					
					_data.healthPlanId = params.healthPlanId;
					_data.type = new Object();
					_data.type.id = _templateData.id;
					
					my.renderGoals(that);
				});
			}
			
			/*
			 * Check if we should load an activity
			 * definition here and populate the form
			 */
			my.initListeners(that);
		};
		
		my.renderForm = function(my) {
			$('input[name="startDate"]').val(_data.startDate);
			$('input[name="duration"]').val(_data.activityRepeat);
		};
		
		my.initListeners = function(my) {
			$('#saveForm').submit(function(e) {
				e.preventDefault();
				my.save(my);
			});
			
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
			
			$('#addTimesForm :reset').click(function(e) {
				NC.log('Resetting all times');
				e.preventDefault();
				_data.dayTimes = undefined;
				my.resetTimes(my);
			})
			
			$('input[name="startDate"]').on('blur keyup change', function() {
				_data.startDate = $(this).val();
				NC.log('Setting start date to: ' + _data.startDate);
			});
			
			$('input[name="activityRepeat"]').on('blur change keyup', function() {
				_data.duration = $(this).val();
				NC.log('Setting duration to: ' + _data.activityRepeat);
			});
		};
		
		my.renderGoals = function(my) {
			NC.log('Render goals');
			$.each(_templateData.activityItems, function(i, v) {
				
				if (v.activityItemTypeName == "measurement") {
					if (v.valueType.code == "INTERVAL") {
						my.renderIntervalGoal(v);
					} else if (v.valueType.code == "SINGLE_VALUE") {
						my.renderSingleGoal(v);
					} else {
						throw new Error('Undefined value type for measurement');
					}
					
					$('#activityFieldset').show();
					
				} else if (v.activityItemTypeName == "estimation") {
					my.renderEstimationGoal(my, v);
					$('#estimationItemsFieldset').show();
				} else if (v.activityItemTypeName == "yesno") {
					my.renderYesNoGoal(my, v);
					$('#yesNoItemsFieldset').show();
				} else if (v.activityItemTypeName == "text") {
					my.renderTextGoal(my, v);
					$('#textItemsFieldset').show();
				} else {
					throw new Error('Unknown activity item type');
				}
			});
		};
		
		var findGoalValue = function(id) {
			if (_data.goalValues == undefined) {
				_data.goalValues = new Array();
			}
			
			for (var i = 0; i < _data.goalValues.length; i++) {
				if (_data.goalValues[i].activityItemType.id == id) {
					return i;
				}
			}
			
			return -1;
		};
		
		var initGoalValue = function(id, type) {
			
			var idx = findGoalValue(id);
			if (idx == -1) {
				NC.log('Goal value for ' + id + ' is not yet defined');
				var gv = new Object();
				gv.id = -1;
				
				gv.activityItemType = new Object();
				gv.activityItemType.id = id;
				gv.activityItemType.name = type;
				
				gv.target = '';
				gv.minTarget = '';
				gv.maxTarget = '';
				
				_data.goalValues.push(gv);
				idx = _data.goalValues.length - 1;
			}
			
			// Set valueType of goal value
			_data.goalValues[idx].valueType = type;
			return idx;
		}; 
		
		my.renderEstimationGoal = function(my, activityItem) {
			NC.log('Render estimation goal');
			var idx = initGoalValue(activityItem.id, 'estimation');
			
			var i = activityItem;
			$('#estimationItemsFieldset').append(
				$('<p>').html(i.name + ' (' + i.minScaleValue + ' = ' + i.minScaleText + ', ' + i.maxScaleValue + ' = ' + i.maxScaleText + ')')
			);
		};
		
		my.renderYesNoGoal = function(my, activityItem) {
			NC.log('Render yes/no gaol');
			var idx = initGoalValue(activityItem.id, 'yesno');
			
			$('#yesNoItemsFieldset').append(
				$('<p>').html(activityItem.question)
			);
		};
		
		my.renderTextGoal = function(my, activityItem) {
			NC.log('Render text goal');
			
			var idx = initGoalValue(activityItem.id, 'text');
			
			$('#textItemsFieldset').append(
				$('<p>').html(activityItem.label)
			);
		};
		
		my.renderSingleGoal = function(activityItem) {
			NC.log('Render single goal');
			var t = _.template( $('#singleValue').html() );
			var dom = t(activityItem);
			
			$('#activityFieldset').append($(dom));
			
			var idx = initGoalValue(activityItem.id, 'measurement');
			
			$('#field-' + activityItem.id).val(_data.goalValues[idx].target);
			
			$('#field-' + activityItem.id).on('change blur keyup', function() {
				_data.goalValues[idx].target = $(this).val();
				NC.log('Target set to: ' + $(this).val() + ' for ' + activityItem.name);
			});
		};
		
		my.renderIntervalGoal = function(activityItem) {
			NC.log('Render interval goal');
			var t = _.template( $('#intervalValue').html() );
			var dom = t(activityItem);
			
			$('#activityFieldset').append($(dom));
			
			var min = $('#field-' + activityItem.id + '-min');
			var max = $('#field-' + activityItem.id + '-max');
			
			var idx = initGoalValue(activityItem.id, 'measurement');
			
			min.val(_data.goalValues[idx].minTarget);
			max.val(_data.goalValues[idx].maxTarget);
			
			var updateIntervalValues = function(activityItem) {
				_data.goalValues[idx].minTarget = $(min).val();
				_data.goalValues[idx].maxTarget = $(max).val();
				NC.log('Min target set to: ' + $(min).val() + ' for ' + activityItem.name);
				NC.log('Max target set to: ' + $(max).val() + ' for ' + activityItem.name);
			};
			
			$(min).on('change blur keyup', function() {
				updateIntervalValues(activityItem);
			});
			
			$(max).on('change blur keyup', function() {
				updateIntervalValues(activityItem);
			});
		};
		
		var getIndexForDay = function(day) {
			if (_data.dayTimes == undefined) {
				NC.log('Day times are undefined. Create new array...');
				_data.dayTimes = new Array();
				return 0;
			}
			
			for (var i = 0; i < _data.dayTimes.length; i++) {
				var val = _data.dayTimes[i];
				if (val.day == day) {
					NC.log(day + ' found at index: ' + i);
					return i;
				}
			}
			
			NC.log('Day index not found...');
			return _data.dayTimes.length;
		}
		
		my.doTimeRendering = function(my) {
			
			var val = $('#specifyTime').val().trim();
			
			NC.log('Adding time ' + val);
			
			/*
			 * Loop through days
			 */
			$('#addTimesForm input:checkbox:checked').each(function(i, v) {
				var day = $(v).prop('name');
				
				NC.log(day + ' is selected. Process...');
				
				var idx = getIndexForDay(day);
				NC.log('Index of ' + day + ' is ' + idx);
				
				if (_data.dayTimes[idx] == undefined) {
					NC.log('Initializing daytimes for: ' + day);
					_data.dayTimes[idx] = new Object();
					_data.dayTimes[idx].day = day;
					_data.dayTimes[idx].times = new Array();
				}
				
				// Does time already exist for day?
				if ($.inArray(val, _data.dayTimes[idx].times) == -1) {
					_data.dayTimes[idx].times.push(val);
					my.renderTimes(my, day);
				}
			});
		};
		
		my.renderAllTimes = function(my) {
			$.each(_data.dayTimes, function(i, v) {
				my.renderTimes(my, v.day);
			});
		};
		
		my.resetTimes = function(my) {
			$('#monday-container').hide().find('.times').empty();
			$('#tuesday-container').hide().find('.times').empty();
			$('#wednesday-container').hide().find('.times').empty();
			$('#thursday-container').hide().find('.times').empty();
			$('#friday-container').hide().find('.times').empty();
			$('#saturday-container').hide().find('.times').empty();
			$('#sunday-container').hide().find('.times').empty();
		};
		
		my.renderTimes = function(my, day) {
			
			NC.log('Render times for ' + day);
			
			var idx = getIndexForDay(day);
			var times = _data.dayTimes[idx].times;
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
							var pos = $.inArray(v, _data.dayTimes[idx].times);
							_data.dayTimes[idx].times.splice(pos, 1);
							
							my.renderTimes(my, day);
						})
					);
					
					container.find('.times').append(tc);
				});
				
				container.show();
			}
		};
		
		my.save = function(my) {
			NC.log('Save activity definition');
			
			var json = JSON.stringify(_data);
			NC.log(json);
			
			NC.log('Saving activity plan. Plan is new? ' + _isNew);
			
			if (_isNew == true) {
				new NC.Ajax().post('/activityPlans', _data, function(data) {
					_isNew = false;
					window.location = NC.getContextPath() + '/netcare/admin/healthplans';
				});
			} else {
				new NC.Ajax().post('/activityPlans/' + _data.id, _data, function(data) {
					window.location = NC.getContextPath() + '/netcare/admin/healthplans';
				});
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
				my.searchTemplates(my);
			})
		};

		my.loadCategories = function() {
			var opt = $('<option>', {
				value : 'all',
				selected : 'selected'
			});
			opt.html('-- Alla --');
			
			NC_MODULE.CATEGORIES.load(function(data) {
				$.each(data.data, function(i, v) {
					$('select[name="category"]').append(
						$('<option>').prop('value', v.id).html(v.name)
					);
				});
			});
			
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
					window.location = NC.getContextPath() + '/netcare/admin/healthplans/' + my.params.healthPlanId + '/plan/new?template=' + template.id;
				} else {
					window.location = NC.getContextPath() + '/netcare/admin/template/' + template.id;
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
					activityTemplate = data;
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
				return true;
			});
			$('#addEstimationButton').on('click', function(event) {
				createItem('estimation');
				return true;
			});
			$('#addYesNoButton').on('click', function(event) {
				createItem('yesno');
				return true;
			});
			$('#addTextButton').on('click', function(event) {
				createItem('text');
				return true;
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
						'id' : null,
						'name' : null
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
						activityTemplate.name = $('#activityTypeName').val();
						
						activityTemplate.accessLevel = new Object();
						activityTemplate.accessLevel.code = $('input[name="accessLevel"]:radio:checked').val();
						
						for ( var i = 0; i < activityTemplate.activityItems.length; i++) {
							delete activityTemplate.activityItems[i].details;
						}
						if (activityTemplate.id == -1) {
							at.create(activityTemplate, function(data) {
								activityTemplate = data.data;
								renderItems(my, activityTemplate);
								NC.log(activityTemplate);
								
								window.location = NC.getContextPath() + '/netcare/admin/templates';
							});
						} else {
							at.update(params.templateId, activityTemplate,
									function(data) {
										activityTemplate = data.data;
										renderItems(my,
												activityTemplate);
										NC.log(activityTemplate);
										
										window.location = NC.getContextPath() + '/netcare/admin/templates';
										
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
			NC_MODULE.CATEGORIES.load(function(data) {
				$.each(data.data, function(i, v) {
					$('#activityTypeCategory').append(
						$('<option>').prop('value', v.id).html(v.name)
					);
				});
			});
		};
		
		my.loadAccessLevels = function(my) {
			new NC.Ajax().getSynchronous('/support/accessLevels', function(data) {
				$.each(data.data, function(i, v) {
					$('#selectAccessLevel').append(
						_.template($('#accessLevelsRadioOption').html())(v)
					);
				});
				
				$('#selectAccessLevel').css('margin-bottom', '10px');
				
				/*
				 * Role check, remove items that we do not have access to
				 */
				if (my.params.isCareActor == "true" && my.params.isCountyActor == "false") {
					NC.log('Disabling access levels...');
					$('input[name="accessLevel"]').prop('disabled', true);
					return;
				}
				
				if (my.params.isCountyActor == "true" && my.params.isNationActor == "false") {
					$('#access-level-NATIONAL').prop('disabled', true);
				}
				
				if (my.params.isNationActor == "true" && my.params.isCountyActor == "false") {
					$('#access-level-COUNTY_COUNCIL').prop('disabled', true);
				}
				
			});
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
		
		var canModify = function(my, level) {
			var careActorAccess = (my.params.isCareActor == "true" && level == "CAREUNIT")
			var countyActorAccess = (my.params.isCountyActor == "true" && (level == "CAREUNIT" || level == "COUNTY_COUNCIL"));
			var nationAccess = (my.params.isNationActor == "true" && (level == "CAREUNIT" || level == "NATIONAL"));
			
			NC.log('Care actor access: ' + careActorAccess);
			NC.log('County actor access: ' + careActorAccess);
			NC.log('Nation actor access: ' + careActorAccess);
			
			if (careActorAccess) {
				NC.log('Care actor access');
				return true;
			} else if (countyActorAccess) {
				NC.log('County actor access');
				return true;
			} else if (nationAccess) {
				NC.log('Nation actor access');
				return true;
			} else {
				NC.log('Not permitted to change template');
				return false;
			}
			
		};

		var renderItems = function(my, activityTemplate) {
			var aLevel = activityTemplate.accessLevel.code;
			
			if (!canModify(my, aLevel)) {
				$('#activitySaveButton').prop('disabled', true);
				$('#accessNote').show();
			}
			
			$('#activityTypeName').val(activityTemplate.name);
			$('#activityTypeCategory > option[value="' + activityTemplate.category.id + '"]').prop('selected', true);
			
			$('#access-level-' + aLevel).prop('checked', true);
			
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
						item.details = item.unit.name + ' | '
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
					if (item.question != null && item.question!="") {
						item.details = item.question;
					} else {
						item.details = 'Värden saknas';
					}
				} else if (item.activityItemTypeName == 'text') {
					if (item.label != null && item.label!="") {
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
							NC.log('scrooll');
							window.scrollTo(0,520);
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
				if (item.unit != null && item.unit.id == v.attr('value')) {
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
			item.unit.id = collected.measurementUnitCode;
			item.unit.name = collected.measurementUnitValue;
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

		var initMeasureValues = function(my) {
			support.getMeasureValueTypes(function(data) {
				$.each(data.data, function(i, v) {
					typeOpts.push($('<option>').attr('value', v.code).html(
							v.value));
				});
			});
		}
		var initUnitValues = function(my) {
			NC_MODULE.UNITS.load(function(data) {
				$.each(data.data, function(i, v) {
					unitOpts.push($('<option>').attr('value', v.id).html(
							v.name));
				});
			});
		}

		return my;
	})(),
	
	UNITS : (function() {
		
		var _data = new Object();
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_data.id = -1;
			
			my.processUnits(that);
			my.initChangeListeners(that);
		};
		
		my.initChangeListeners = function(my) {
			$('#dn').bind('blur keyup change', function() {
				_data.dn = $(this).val();
			});
			
			$('#name').bind('blur keyup change', function() {
				_data.name = $(this).val();
			});
			
			$('#unitId').bind('blur change', function() {
				_data.id = $(this).val();
			});
			
			$('#unitForm').submit(function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				my.save(my);
			})
		};
		
		my.buildUnitRow = function(my, unit, update) {
			var template = _.template($('#measureUnitRow').html());
			var dom = template(unit);
			
			if (update == true) {
				NC.log('Updating row')
	 			var row = $('#measure-unit-' + unit.id);
				if (row == undefined) {
					$('#measureUnitsTable tbody').append($(dom));
				} else {
					row.replaceWith($(dom));
				}
			} else {
				$('#measureUnitsTable tbody').append($(dom));
			}
			
			$('#measure-unit-' + unit.id + '-edit').click(function() {
				_data.id = unit.id;
				_data.dn = unit.dn;
				_data.name = unit.name;
				
				my.renderForm(my);
			});
		};
		
		my.load = function(callback) {
			new NC.Ajax().get('/units', function(data) {
				callback(data);
			});
		};
		
		my.processUnits = function(my) {
			my.load(function(data) {
				$.each(data.data, function(i, v) {
					my.buildUnitRow(my, v, false);
				});
				
				if (data.data.length > 0) {
					$('#measureUnitsTable').show();
				}
			});
		};
		
		my.renderForm = function(my) {
			$('#dn').val(_data.dn);
			$('#name').val(_data.name);
			$('#unitId').val(_data.id);
		};
		
		my.resetForm = function() {
			_data.id = -1;
			_data.dn = '';
			_data.name = '';
			
			my.renderForm(my);
		}
		
		my.save = function(my) {
			if (_data.id == -1) {
				new NC.Ajax().post('/units', _data, function(data) {
					my.buildUnitRow(my, data.data, true);
					my.resetForm(my);
				});
			} else {
				new NC.Ajax().post('/units/' + _data.id, _data, function(data) {
					my.buildUnitRow(my, data.data, true);
					my.resetForm(my);
				});
			}
		};
		
		return my;
	})(),
	
	CATEGORIES : (function() {
		
		var _data = new Object();
		
		var my = {};
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_data.id = -1;
			
			my.loadTable(that);
			my.initChangeListeners(that);
			my.renderForm();
		};
		
		my.initChangeListeners = function(my) {
			$('input[name="name"]').bind('keyup change blur', function() {
				_data.name = $(this).val();
			});
			
			$('#activityCategoryForm').submit(function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				my.save(my);
			});
		};
		
		my.resetForm = function() {
			_data.id = -1;
			_data.name = '';
			
			my.renderForm();
		};
		
		my.renderForm = function() {
			$('input[name="id"]').val(_data.id);
			$('input[name="name"]').val(_data.name);
		};
		
		my.buildTableRow = function(rowData) {
			var tr = $('<tr>').append(
				$('<td>' + rowData.name + '</td>')
			);
			
			$('#categoryTable tbody').append(tr);
		};
		
		my.loadTable = function(my) {
			my.load(function(data) {
				$('#categoryTable tbody').empty();
				$.each(data.data, function(index, value) {
					my.buildTableRow(value);
				});
			});
		};
		
		my.load = function(callback) {
			new NC.Ajax().getSynchronous('/categories', callback);
		};
		
		my.save = function(my) {
			new NC.Ajax().post('/categories/', _data, function(data) {
				my.buildTableRow(data.data);
				my.resetForm();
			});
		};
		
		return my;
	})(),
	
	COMMENTS : (function() {
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.load(that);
		};
		
		my.load = function(my) {
			new NC.Ajax().get('/comments', function(data) {
				if (data.data.length > 0) {
					
				}
			});
		};
		
		my.sendReply = function(replyId, message, callback) {
			new NC.Ajax().post('/comments/' + commentId, message, callback);
		};
		
		my.remove = function(commentId, callback) {
			new NC.Ajax().http_delete('/comments/' + commentId, callback);
		};
		
		return my;
	})(),
	
	PATIENT_REPORTS : (function() {
		var my = {};
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.load(that);
		};
		
		my.load = function(my) {
			
		};
		
		return my;
	})(),
	
	SCHEDULE : (function() {
		
		var _data = new Array();
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.renderSchedule(that);
		};
		
		my.load = function(my, callback) {
			new NC.Ajax().get('/scheduledActivities', callback);
		};
		
		my.renderSchedule = function(my) {
			my.load(my, function(data) {
				NC.log('Scheduled activities loaded: ' + data.data.length);
				if (data.data.length > 0) {
					$.each(data.data, function(i, v) {
						_data[i] = v;
						my.renderScheduleItem(my, i, v);
					});
				}
			});
		};
		
		my.renderScheduleItem = function(my, activityIndex, scheduledActivity) {
			var dom = _.template($('#scheduledActivityItem').html())(scheduledActivity);
			$('#reportList').append($(dom));
			
			var subrow = $('#saItem' + scheduledActivity.id).find('.subRow');
			if (scheduledActivity.reported == null) {
				subrow.html(scheduledActivity.date + ' ' + scheduledActivity.time)
				if (scheduledActivity.due == true) {
					subrow.css({'color' : 'red', 'font-weight' : 'bold' });
				}
			} else if (scheduledActivity.rejected == true) {
				subrow.html('Rapporterad som ej utförd');
			} else {
				subrow.html('Rapporterad ' + scheduledActivity.reported);
				
				// Hide reported by default
				$('#scheduledActivityItem' + scheduledActivity.id).hide();
			}
			
			var liElem = $('#scheduledActivityItem' + scheduledActivity.id);
			var expander = $('<div>').addClass('mvk-icon toggle').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				$('#sa-details-' + scheduledActivity.id).toggle();
			}) ;

			liElem.find('.actionBody').css('text-align', 'right').css('padding-right', '40px').append(expander);
			
			var detailsDom = _.template($('#scheduledActivityDetails').html())(scheduledActivity);
			liElem.find('.row-fluid').after($(detailsDom));
			
			my.processItemValues(my, activityIndex, scheduledActivity);
			
			/*
			 * Listen for changes on the activity
			 */
			my.initActivityListeners(my, activityIndex);
		};
		
		my.initActivityListeners = function(my, idx) {
			var id = _data[idx].id;
			var time = $('#' + id + '-report-time');
			var date = $('#' + id + '-report-date');
			var report = $('#sa-report-' + id);
			var noreport = $('#sa-noreport-' + id);
			
			$(date).bind('change blur keyup', function(e) {
				e.stopPropagation();
				_data[idx].actualTime = $(this).val() + ' ' + time.val();
			});
			
			$(time).bind('change blur keyup', function(e) {
				e.stopPropagation();
				_data[idx].actualTime = date.val() + ' ' + $(this).val();
			});
			
			$(report).click(function(e) {
				
				// Remove definition from data
				_data[idx].activityDefinition = undefined;
				_data[idx].patient = undefined;
				new NC.Ajax().post('/scheduledActivities/' + id, _data[idx], function(data) {
					alert('Success');
				});
			});
			
			$(noreport).click(function(e) {
				alert('Hej 2');
			});
		};
		
		my.processItemValues = function(my, activityIndex, activity) {
			$.each(activity.activityItemValues, function(idx, actItem) {
				var activityValuesTemplate = '#scheduled-' + actItem.definition.activityItemType.activityItemTypeName + 'Values';
				var t = _.template($(activityValuesTemplate).html());
				var dom = t(actItem);
				$('#sa-details-' + activity.id).find('.span12').append($(dom));
				
				my.initActivityItemListener(my, activityIndex, idx, actItem.id);
				
				var dp = $('#' + activity.id + '-report-date').datepicker({
					dateFormat : 'yy-mm-dd',
					firstDay : 1,
					minDate : +0
				});
				
				dp.datepicker('setDate', new Date());
				var d = new Date();
				
				var min = d.getMinutes() < 10 ? '0' + d.getMinutes() : d.getMinutes();
				$('#' + activity.id + '-report-time').val(d.getHours() + ':' + min);
				
				// Needed for correct deserialization
				actItem.valueType = actItem.definition.valueType; 
			});
		};
		
		my.initActivityItemListener = function(my, activityIndex, itemIndex, id) {
			
			var inputs = $('#sa-row-' + id).find('input');
			if (inputs.length == 1) {
				
				inputs.bind('change blur keyup', function() {
					_data[activityIndex].activityItemValues[itemIndex].reportedValue = $(this).val();
					NC.log('Setting value to: ' + $(this).val());
				});
				
			} else if (inputs.length == 2) {
				
			} else {
				throw new Error('Expected 1 or 2 inputs and nothing else.');
			}
			
		};
		
		return my;
	})(),
	
	REPORTED_ACTIVITIES : (function() {
		
		var _data = new Object();
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			my.loadReportedActivities(that);
		};
		
		my.loadReportedActivities = function(my) {
			new NC.Ajax().get('/healthplans/activity/reported/latest', function(data) {
				$('#latestActivitiesContainer').empty();
				
				if(data.data.length==0) {
						$('#latestActivitiesContainer').hide();
						$('#noReportedActivities').show();
				} else {
					$('#noReportedActivities').hide();
					$('#latestActivitiesContainer').show();
					$.each(data.data, function(i, v) {
						my.buildReportedActivityItem(my, v);
					});
				}
				
			}, false);
		};
		
		my.buildReportedActivityItem = function(my, act) {
			var t = _.template($('#reportedActivityItem').html());
			var dom = t(act);
			$('#latestActivitiesContainer').append($(dom));
			
			/*
			 * Bind click event
			 */
			var liElem = $('#reportedActivityItem' + act.id);
			
//			liElem.click(function() {
//				window.location = NC.getContextPath() + '/netcare/admin/healthplans/' + hp.id;
//			});

			var detailsTemplate = _.template($('#reportedActivityDetails').html());
			var detailsDom = detailsTemplate(act);
			
			liElem.find('.row-fluid').after($(detailsDom));
			
			my.processValues(my, act);
			
			var expander = $('<div>').addClass('mvk-icon toggle').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				$('#ra-details-' + act.id).toggle();
			}) ;

			liElem.find('.actionBody').css('text-align', 'right').css('padding-right', '40px').append(expander);
			
			var commentText = liElem.find('#activitycomment');
			liElem.find('.btn').click(function(e) {
				var text = commentText.val();
				if(text!=null && text!="") {
					
					NC.log('Submit comment: ' + text);
					e.preventDefault();
					new NC.Ajax().postWithParams('/healthplans/activity/' + act.id + '/comment', { comment : text }, function() {
						NC.log('Comment completed.');
						commentText.val('');
						liElem.find('#actcomment').fadeOut();
					}, true);
				}
			});
		};
		
		my.processValues = function(my, act) {
			if (act.activityItemValues.length == 0) {
				NC.log('No activities reported');
				$('#ra-details-' + act.id).find('.span12').append(
					$('<p>').css({'font-style' : 'italic'}).html('Inga aktiviteter rapporterade.')
				);
			} else {
				$.each(act.activityItemValues, function(idx, actItem) {
					var activityValuesTemplate = '#' + actItem.definition.activityItemType.activityItemTypeName + 'Values';
					var t = _.template($(activityValuesTemplate).html());
					var dom = t(actItem);
					$('#ra-details-' + act.id).find('.span12').append($(dom));
				});
			}
		};
		
		my.doFilter = function(pnr, date1, date2) {
			
			var params = {
				personnummer : pnr,
				dateFrom : date1,
				dateTo : date2
			}
			
			new NC.Ajax().getWithParams('/healthplans/activity/reported/filter', params, function(data) {
				$('#latestActivitiesContainer').empty();
				
				if(data.data.length==0) {
						$('#latestActivitiesContainer').hide();
						$('#noReportedActivities').show();
				} else {
					$('#noReportedActivities').hide();
					$('#latestActivitiesContainer').show();
					$.each(data.data, function(i, v) {
						my.buildReportedActivityItem(my, v);
					});
				}
				
			}, false);
		};
		
		return my;
		
	})()
	
};
