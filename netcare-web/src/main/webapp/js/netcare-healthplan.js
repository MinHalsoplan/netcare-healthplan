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

		return my;
	})(),
	
	TEMPLATE_SEARCH : (function() {
		var _init;
		var _text;
		var _category;
		var _level;
		var my = {};

		my.init = function(params) {
			var that = this;
			this.params = params;

			if (_init == undefined) {
				_init = true;
				_text = "";
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
				_text = $(this).val();
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

		my.searchTemplates = function(my) {
			NC.log('Searching... Text: ' + _text + ', Category: ' + _category
					+ ', Level: ' + _level);
			var ajax = new NC.Ajax().getWithParams('/activityType/search', {
				'text' : _text,
				'category' : _category,
				'level' : _level
			}, function(data) {

				$('#templateList').empty();

				$.each(data.data, function(i, v) {
					var template = _.template($('#activityTemplate').html());
					$('#templateList').append(template(v));
					
					if (v.accessLevel.code != "CAREUNIT") {
						var t2 = _.template($('#itemNote').html());
						$('#item-' + v.id).next('a.itemNavigation').after(t2(v.accessLevel));
						
						$('#item-' + v.id).find('.actionBody').append(
							$('<div>').addClass('mvk-icon copy').bind('click', function(e) {
								e.preventDefault();
								e.stopPropagation();
								my.copyTemplate(my, v.id);
							})
						);
						
						$('#item-' + v.id).find('.actionBody').append(
							$('<div>').addClass('mvk-icon delete').bind('click', function(e) {
								e.preventDefault();
								e.stopPropagation();
								my.deleteTemplate(my, v.id);
							})
						);
						
					} else {
						
					}
					
					$('#item-' + v.id).live('click', function() {
						window.location = GLOB_CTX_PATH + '/netcare/admin/template/' + v.id;
					});
				});
			});
		};
		
		my.copyTemplate = function(my, templateId) {
			NC.log('Copy template');
			
		};
		
		my.deleteTemplate = function(my, templateId) {
			NC.log('Delete template');
		};
		
		return my;
		
	})(),
		
	ACTIVITY_TEMPLATE : (function() {
		var activityTemplate;
		var my = {};
		var support;
		var typeOpts;
		var unitOpts;
		var nextItemId = -1;
		
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
								at.update(activityTemplate, function(data) {
									activityTemplate = data.data;
									renderItems(my, activityTemplate);
									NC.log(activityTemplate);
								});
							});
			$('#addMeasurementButton').on('click', function() {
				createItem('measurement');
			});
			$('#addEstimationButton').on('click', function() {
				createItem('estimation');
			});
			$('#addYesNoButton').on('click', function() {
				createItem('yesno');
			});
			$('#addTextButton').on('click', function() {
				createItem('text');
			});

			function createItem(type) {
				// ALLA attribut måste vara med här för att json-parsern
				// på servern ska fixa det hela.
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
					"maxScaleValue" : null
				};
				activityTemplate.activityItems.push(item);
				renderItems(my, activityTemplate);
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
			flash($('#item' + itemId).parent());
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
			flash($('#item' + itemId).parent());
		};

		my.deleteItem = function(my, itemId) {
			/*
			 * Delete an activity item from the list and decrease seqno
			 */
			var items = activityTemplate.activityItems;
			var decreaseSeqno = false;
			for ( var i = 0; i < items.length; i++) {
				if (items[i].id == itemId) {
					items.splice(i, 1);
					decreaseSeqno = true;
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
			} else if (item.activityItemTypeName == 'text') {
				template = _.template($('#activityItemTextForm').html());
				$('#activityItemFormContainer').append(template(item));
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

		var removeMeasureValueForm = function() {
			if ($('#measureValueContainer div').size() > 0) {
				$('#measureValueContainer').empty();
			}
		};

		return my;
	})()
};
