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
		
		/**
		 * Method that will make call to the server, if the call was
		 * successful, the onDataLoaded()-function will be executed
		 * with the array of values
		 */
		var _loadOptions = function(url, onDataLoaded) {
			new NC.Ajax().getSynchronous(url, function(data) {
				var arr = new Array();
				$.each(data.data, function(index, value) {				
					arr[index] = value;
				});
				
				onDataLoaded(arr);
			});
		};
		
		var _createOptions = function(options, selectElem) {
			NC.log("Creating options...");
			if (selectElem === undefined) {
				NC.log("Select element is undefined.");
				return false;
			}
			
			$.each(options, function(index, value) {
				NC.log("Creating option: " + value.code);
				var opt = $('<option>', { value : value.code });
				opt.html(value.value);
				opt.appendTo(selectElem);
			});
		};

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
				
		/**
		 * Called when care giver selects a patient to
		 * work with. This method adds the selected patient
		 * to the session scope and the menu should always
		 * display the selected patient
		 */
		my.selectPatient = function(patientId, successFunction) {
			if (patientId === undefined || patientId == '') {
				throw new Error('Cannot select patient since there is no patient to select');
			}
			new NC.Ajax().postSynchronous('/user/' + patientId + '/select', null, successFunction);
		};
		
		my.unselect = function(callback) {
			new NC.Ajax().postSynchronous('/user/unselect', null, callback);
		};
		
		/**
		 * Update the current patient shown in the menu
		 * of the applica
		 */
		my.updateCurrentPatient = function(name) {
			NC.log("Updating current patient. Display: " + name);
			$('#currentpatient a').html(name);
			$('#nopatient').hide();
			$('#currentpatient').show();
		};
		
		/**
		 * Load all durations as options that exist in the
		 * application
		 */
		my.loadDurations = function(selectElem) {
			var url = '/support/durations/load';
			
			_loadOptions(url, function(data) {
				_createOptions(data, selectElem);
			});
		};
		
		my.loadAccessLevels = function(selectElem) {
			var url = '/support/accessLevels';
			_loadOptions(url, function(data) {
				_createOptions(data, selectElem);
			});
		};
		
		my.getMeasureValueTypes = function(callback) {
			new NC.Ajax().get('/support/measureValueTypes', callback);
		};
		
		my.loadCountyCouncils = function(selectElem) {
			var url = '/support/countyCouncils';
			_loadOptions(url, function(data) {
				_createOptions(data, selectElem);
			});
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
					
					v.civicRegistrationNumber = NC.GLOBAL.formatCrn(v.civicRegistrationNumber);
					var dom = t(v);
					
					$('#patientList').append($(dom));
					
					$('#patientItem' + v.id).click(function(e) {
						e.preventDefault();
						
						NC_MODULE.GLOBAL.selectPatient(v.id, function(data) {
							NC_MODULE.GLOBAL.updateCurrentPatient(data.data.name);
							window.location = NC.getContextPath() + '/netcare/admin/healthplans';
						});
					});
				});
				
			});
		};
		
		/**
		 * Called when the care giver wants to find a patient
		 */
		my.findPatients = function(searchValue, successFunction) {
			if (searchValue.length < 3) {
				return false;
			}
			new NC.Ajax().getWithParams('/user/find', { search : searchValue }, successFunction, false);
		};
		
		return my;
	})(),
	
	PATIENT_FORM : (function() {
		var _data;
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_data = {
				firstName : '',
				surName : '',
				civicRegistrationNumber : '',
				phoneNumber : ''
			};
			
			my.initValidate();

			my.initListeners(that);
			my.render(that);

		};
		
		my.initListeners = function(my) {
			
			$('#showCreatePatient').click(function() {
				$('#patientSheet').toggle();
			});
			
			$('input[name="firstName"]').on('keyup change blur', function() {
				my.validate();
				_data.firstName = $(this).val();
			});
			
			$('input[name="surName"]').on('keyup change blur', function() {
				my.validate();
				_data.surName = $(this).val();
			});
			
			$('input[name="civicRegistrationNumber"]').on('keyup change blur', function() {
				my.validate();
				_data.civicRegistrationNumber = $(this).val();
			});
			
			$('input[name="phoneNumber"]').on('keyup change blur', function() {
				_data.phoneNumber = $(this).val();
			});

			$('#patientForm').submit(function(e) {
				e.preventDefault();
				if(my.validate()) {
					new NC.Ajax().post('/user/create', _data, function(data) {
						_data = data.data;
						my.render();
						NC_MODULE.GLOBAL.selectPatient(_data.id, function() {
							window.location = NC.getContextPath() + '/netcare/admin/healthplans';
						});
					});
				}
			});
		};

		my.initValidate = function() {
			$('#patientForm').validate({
				messages: {
					firstName: "Patientens förnamn saknas",
					surName: "Patientens efternamn saknas",
					civicRegistrationNumber: "Felaktigt personnummer"
				},
				errorClass: "field-error",
					highlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').addClass('field-error');
				},
					unhighlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').removeClass('field-error');
				}
			});
		};
		
		my.validate = function() {
			return $('#patientForm').validate().numberOfInvalids()==0;
		};
		
		my.render = function() {
			$('input[name="firstName"]').val(_data.firstName);
			$('input[name="lastName"]').val(_data.surName);
			$('input[name="civicRegistrationNumber"]').val(_data.civicRegistrationNumber);
			$('input[name="phoneNumber"]').val(_data.phoneNumber);
		};
		
		return my;
	})(),
	
	PATIENT_SEARCH : (function() {
		
		var selectPatientSuccess = function(data) {
			var name = data.data.name;
			NC_MODULE.GLOBAL.updateCurrentPatient(name);
		};
		
		var selectPatient = function(event) {
			NC.log("Selecting patient...");
			event.preventDefault();
			NC_MODULE.GLOBAL.selectPatient($('#pickPatientForm input[name="selectedPatient"]').val(), selectPatientSuccess);
			
			NC.log("Hide modal.");
			$('#modal-from-dom').modal('hide');
			
			// Redirect to new health plan
			window.location = GLOB_CTX_PATH + '/netcare/admin/healthplans';
		};
		
		var my = {};
		
		my.init = function() {
			
			$('#modal-from-dom').bind('shown', function() {
				$('input[name="pickPatient"]').focus();
			});
			
			$('input[name="pickPatient"]').autocomplete({
				source : function(request, response) {
					/*
					 * Call find patients. Pass in the search value as well as
					 * a function that should be executed upon success.
					 */
					NC_MODULE.PATIENTS.findPatients(request.term, function(data) {
						NC.log("Found " + data.data.length + " patients.");
						response($.map(data.data, function(item) {
							NC.log("Processing item: " + item.name);
							return { label : item.name + ' (' + NC.GLOBAL.formatCrn(item.civicRegistrationNumber) + ')', value : item.name, patientId : item.id };
						}));
					});
				},
				select : function(event, ui) {
					NC.log("Setting hidden field value to: " + ui.item.patientId);
					$('#pickPatientForm input[name="selectedPatient"]').prop('value', ui.item.patientId);
				}
			});
			
			$('#pickPatient').keypress(function(e) {
				if (e.which == 13) {
					selectPatient(e);
				}
			});
			
			$('#pickPatientForm').submit(function(event) {
				selectPatient(event);
			});	
		}
		
		return my;
	})(),
	
	HEALTH_PLAN : (function() {
		
		var _data = new Object();
		
		var my = {};
		
		my.emptyPlan = function(patientId) {
			_data = new Object();
			_data.duration = 1
			_data.durationUnit = new Object();
			_data.durationUnit.code = $('#createHealthPlanForm select').val();
			_data.patient = new Object();
			_data.patient.id = patientId;
		}
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			NC_MODULE.GLOBAL.loadDurations($('#createHealthPlanForm select'));
			
			if (params.id > 1) {
				my.loadHealthPlan(that);
			} else {
				my.emptyPlan(params.patientId);
			}
			
			my.initValidate();

			my.initListeners();
			
			/*
			 * Load all in list
			 */
			my.loadHealthPlans(that);
			
			if (my.params.showForm != '') {
				$('#createHealthPlanSheet').toggle();
			}
			
		};
		
		my.initListeners = function() {
			
			$('#showCreateHealthPlan').click(function() {
				$('#createHealthPlanSheet').toggle();
			});
			
			$('input[name="name"]').on('keyup blur', function() {
				_data.name = $(this).val();
				my.validate();
				NC.log('Updated name to: ' + _data.name);
			});
			
			$('input[name="startDate"]').on('keyup blur change', function() {
				_data.startDate = $(this).val();
				NC.log('Updated start date to: ' + _data.startDate);
			});
			
			$('input[name="duration"]').on('keyup blur', function() {
				_data.duration = $(this).val();
				my.validate();
				NC.log('Updated duration to: ' + _data.duration);
			});
			
			$('select[name="type"]').on('focus change', function() {
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
				my.validate();
				if (my.validate()) {
					my.save(my);
				}
			})
		};
		
		my.loadHealthPlan = function(my) {
			new NC.Ajax().get('/healthplans/' + my.params.id, function(data) {
				my.initPlan(data.data);
				my.renderForm();
			}, false);
		};
		
		my.loadHealthPlans = function(my) {
			my.list(my.params.patientId, function(data) {
				$('#healthPlanContainer').empty();
                $('#inactiveHealthPlanContainer').empty();

				$.each(data.data, function(i, v) {
					my.buildHealthPlanItem(my, v);
				});
			});
		};
		
		my.list = function(patientId, callback) {
			new NC.Ajax().getWithParams('/healthplans', { patient : patientId }, function(data) {
				callback(data);
			}, false);
		};
		
		my.buildHealthPlanItem = function(my, hp) {
			var t = _.template($('#healthPlanItem').html());
			var dom = t(hp);
            if(hp.active) {
                $('#healthPlanContainer').append($(dom));
            } else {
                $('#inactiveHeader').show();
                $('#inactiveHealthPlanContainer').append($(dom));
            }

			var liElem = $('#healthPlanItem' + hp.id);
            if(hp.active) {
                if (hp.autoRenewal) {
                    liElem.find('.subRow').html(my.params.lang.active + ' | ' + my.params.lang.autoRenew);
                } else {
                    liElem.find('.subRow').html(my.params.lang.active + ' | ' + my.params.lang.ends + ' <span id="endDate">' + hp.endDate + '</span>');
                }
            } else {
                liElem.find('.subRow').html(my.params.lang.inactive + ' | ' + my.params.lang.ended + ' <span id="endDate">' + hp.endDate + '</span>');
            }

			var detailsTemplate = _.template($('#healthPlanDetails').html());
			var detailsDom = detailsTemplate(hp);
			
			liElem.find('.row-fluid').after($(detailsDom));
			
			my.processDefinitions(my, hp);
			
			var expander = $('<div>').addClass('mvk-icon toggle').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				$('#hp-details-' + hp.id).toggle();
				$(this).toggleClass('toggle').toggleClass('toggle-open');
			}) ;
			
			liElem.find('.actionBody').css('text-align', 'right').css('padding-right', '40px')
			.append(expander);

            // Handle activation / inactivation
            NC.log('Active: ' + hp.active);
            if (!hp.active) {
                // Show modal
                $('#hp-activate-' + hp.id).click(function(e) {
                    e.preventDefault();
                    $('#hp-activate-confirmation-' + hp.id).modal('show');
                });

                // Act on button click
                $('#hp-activate-confirmation-' + hp.id).find('.modal-footer .btn').click(function (e) {
                    e.preventDefault();
                    my.activate(my, hp);
                });
            } else {

                $('#hp-inactivate-' + hp.id).click(function(e) {
                    e.preventDefault();
                    $('#hp-remove-confirmation-' + hp.id).modal('show');
                });

                $('#hp-remove-confirmation-' + hp.id).find('.modal-footer .btn').click(function (e) {
                    e.preventDefault();
                    my.inactivate(my, hp);
                });
            }
	
			var actions = liElem.find('.healthplan-actions');
			
			var extend = actions.find('.extend');
			
			extend.find('#hp-extend-plan-' + hp.id).click(function(e) {
				extend.find('#hp-extend-confirmation-' + hp.id).modal('show');
			});
			extend.find('.modal-footer').find('.btn').click(function(e) {
				e.preventDefault();
				NC.log('Extend health plan ' + hp.id);
				my.extendPlan(my, hp.id, liElem);
			});
		};
		
		my.processDefinitions = function(my, hp) {
			
			NC.log('Processing activity definitions of health plan ' + hp.id);
			
			var hpDetails = $('#hp-details-' + hp.id); 
			
			if (hp.activityDefinitions.length == 0) {
				NC.log('No activity definitions yet available');
				hpDetails.find('.healthplan-activity-definitions').append(
					$('<p>').css({'font-style' : 'italic'}).html(my.params.lang.noActivities)
				);
			} else {
				
				var added = 0;
				
				for (var i = 0; i < hp.activityDefinitions.length; i++) {
					var ad = hp.activityDefinitions[i];
					
					//NC.log('Processing ' + ad.type.name + '(' + ad.id + ')');

					var t = _.template($('#healthPlanDefinitions').html());
                    ad.hpActive = hp.active;
					var dom = t(ad);
					
					hpDetails.find('.healthplan-activity-definitions').append($(dom));
					
					my.addEventHandlersForDefinition(ad.id, hpDetails);
					
					added++;
				}
				
				if (added == 0) {
					hpDetails.find('.healthplan-activity-definitions').append(
						$('<p>').css({'font-style' : 'italic'}).html(my.params.lang.noActivities)
					);
				}
			}
		};
		
		my.addEventHandlersForDefinition = function(id, ancestor) {
			var item = ancestor.find('#hp-ad-' + id);
			item.find('#hp-ad-' + id + '-edit').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				window.location = NC.getContextPath() + '/netcare/admin/healthplans/' + id + '/plan/' + id;
			});
			
			item.find('#hp-ad-' + id + '-remove').click(function(e) {
				e.preventDefault();
				ancestor.find('#hp-ad-remove-confirmation-' + id).modal('show');
			});
			item.find('#inactivatebtn-'+id).click(function(e) {
				e.preventDefault();
				NC.log('Inactivating activity.');
				new NC.Ajax().http_delete('/activityPlans/' + id, function(data) {
                    replaceActivity(id, data.data);
                    my.addEventHandlersForDefinition(id, ancestor);
				});
			});

            item.find('#hp-ad-' + id + '-activate').click(function(e) {
                e.preventDefault();
                ancestor.find('#hp-ad-activate-confirmation-' + id).modal('show');
            });

            item.find('#activatebtn-' + id).click(function(e) {
                e.preventDefault();
                NC.log('Re-activating activity.');
                var url = '/activityPlans/' + id + '/activate';
                NC.log(url);
                new NC.Ajax().post(url, null, function(data) {
                    replaceActivity(id, data.data);
                    my.addEventHandlersForDefinition(id, ancestor);
                });
            });
            var replaceActivity = function(id, activitydef) {
                $('#hp-ad-' + id).fadeOut('fast');
                var existing = $('#hp-ad-' + id);
                var t = _.template($('#healthPlanDefinitions').html());
                var dom = $(t(activitydef));
                existing.after(dom.hide());
                existing.remove();
                dom.fadeIn('slow');
            };
        };

        my.inactivate = function(my, healthPlan) {
            var id = healthPlan.id;
            new NC.Ajax().post('/healthplans/' + id + '/inactivate', {}, function() {
                my.loadHealthPlans(my);
            });
        };

        my.activate = function(my, healthPlan) {
            var id = healthPlan.id;
            new NC.Ajax().post('/healthplans/' + id + '/activate', {}, function() {
                my.loadHealthPlans(my);
            });
        };

        my.extendPlan = function(my, id, ancestor) {
			console.log('Extending...')
			new NC.Ajax().post('/healthplans/' + id + '/renew', {}, function(data) {
				var endDate = data.data.endDate;
				var endDateElement = ancestor.find('#endDate');
				console.log(endDateElement);
				if(endDateElement) {
					console.log('found');
				}
				console.log(endDate);
				endDateElement.html(endDate);
				endDateElement.css('font-weight','bold').fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
			}, null);
		};

		my.initValidate = function() {
			$('#createHealthPlanForm').validate({
				messages: {
					name: "Skriv in ett namn på hälsoplanen",
					startDate: "Startdatum saknas",
					duration: "Skriv in varaktighet för hälsoplanen"
				},
				errorClass: "field-error",
				highlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').addClass('field-error');
				},
				unhighlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').removeClass('field-error');
				}
			});	
		};
		
		my.validate = function() {
			var validator = $('#createHealthPlanForm').validate();
			return validator.numberOfInvalids()==0;
		};
		
		my.initPlan = function(data) {
			_data = {
				duration : data.duration,
				durationUnit : {
					code : data.durationUnit.code
				},
				patient : {
					id : data.patient.id
				}
			};
		};
		
		my.save = function(my) {
			NC.log('Saving health plan. Data is: ' + _data);
			new NC.Ajax().post('/healthplans', _data, function(data) {
				
				my.emptyPlan(my.params.patientId);
				
				/*
				 * Add new item in list and hide form
				 */
				$('#createHealthPlanSheet').hide();
				
				NC.log('New healthplan created with id: ' + data.data.id);
				my.buildHealthPlanItem(my, data.data);
			}, true);
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
			
			NC.GLOBAL.showLoader('#plan', 'Laddar planering...');
			
			if (params.definitionId != '') {
				new NC.Ajax().get('/activityPlans/' + params.definitionId, function(data) {
					
					_isNew = false;
					
					_data.id = data.data.id;
					_data.healthPlanId = params.healthPlanId;
                    _data.healthPlanName = data.data.healthPlanName;
                    _data.healthPlanStartDate = data.data.healthPlanStartDate;
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
					
					my.initListeners(that);
					
					my.renderGoals(that);
					my.renderAllTimes(that);
					my.renderForm(that);

                    $('#healthplanTitle').text(_data.healthPlanName);
                    $('#templateTitle').text(_templateData.name);

                    NC.GLOBAL.suspendLoader('#plan');
					$('#planContainer').show();
				});
			} else {
                new NC.Ajax().get('/healthplans/' + params.healthPlanId, function(data) {
                    _data.healthPlanId = params.healthPlanId;
                    _data.healthPlanName = data.data.name;
                    _data.healthPlanStartDate = data.data.startDate;
                    $('#healthplanTitle').text(_data.healthPlanName);
                });

				NC_MODULE.ACTIVITY_TEMPLATE.loadTemplate(params.templateId, function(data) {
					
					_isNew = true;
					
					_templateData = data.data;
					
					_data.type = new Object();
					_data.type.id = _templateData.id;
					_data.activityRepeat = 1;

					my.initListeners(that);

                    $('#templateTitle').text(_templateData.name);

					my.renderGoals(that);
					
					NC.GLOBAL.suspendLoader('#plan');
					$('#planContainer').show();
					
					
				});
			}
		};

		my.renderForm = function(my) {
            $('input[name="startDate"]').val(_data.startDate);
			$('input[name="activityRepeat"]').val(_data.activityRepeat);
		};
		
		my.initListeners = function(my) {
			$('#saveForm').submit(function(e) {
				e.preventDefault();
				my.save(my);
			});

            var specifyTime = $('#specifyTime');

            specifyTime.on('focus', function() {
                specifyTime.data('data-original-value', specifyTime.val());
                specifyTime.val('');
            });
            specifyTime.on('blur', function() {
                var text = specifyTime.val();
                var originalText = specifyTime.data('data-original-value');
                if(text.length===0 && originalText.length>0) {
                    specifyTime.val(originalText);
                }
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
                var value = $(this).val();
                var dateValue = NC.GLOBAL.getDateFromISOString(value);
                var hpStartDate = NC.GLOBAL.getDateFromISOString(_data.healthPlanStartDate);
                if (NC.GLOBAL.isValidISODate(value) &&
                    (NC.GLOBAL.isSameDay(dateValue, hpStartDate) || dateValue > hpStartDate)) {
                    $("#startDateContainer").removeClass('field-error');
                    $("#startDateMsg").empty();
                    _data.startDate = value;
                    NC.log('Setting start date to: ' + _data.startDate);
                } else {
                    $("#startDateContainer").addClass('field-error');
                    $("#startDateMsg").text("Måste vara ett datum från idag och framåt enligt följande exempel: 2013-03-06");
                }
			});
			
			$('input[name="activityRepeat"]').on('blur change keyup', function() {
                var value = $(this).val();
                if(!isNaN(parseFloat(value)) && isFinite(value) && value >=0) {
                    $("#activityRepeatContainer").removeClass('field-error');
                    $("#activityRepeatMsg").empty();
                    _data.activityRepeat = value;
                    NC.log('Setting duration to: ' + _data.activityRepeat);
                } else {
                    $("#activityRepeatContainer").addClass('field-error');
                    $("#activityRepeatMsg").text("Måste vara ett tal större eller lika med 0");
                }
			});
		};
		
		my.renderGoals = function(my) {
            NC.log('Render goals');
			for (var i = 0; i < _templateData.activityItems.length; i++) {
				
				var v = _templateData.activityItems[i];
				
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
				
				// Make it possible to remove item
				my.initItemStateChange(my, v);
			}
		};
		
		my.initItemStateChange = function(my, item) {
			var idx = findGoalValue(item.id);
			
			var container = '#item-value-' + item.id + '-container';
			var incId = '#field-' + item.id + '-include';
			var excId = '#field-' + item.id + '-exclude';
			
			NC.log('Item state: ' + _data.goalValues[idx].active);
			
			if (_data.goalValues[idx].active == true) {
				includeItem(item, container, excId, incId, idx);
				$(excId).toggle();
			} else {
				excludeItem(item, container, excId, incId, idx);
				$(incId).toggle();
			}
			
			$(excId).click(function(e) {
				e.preventDefault();
				excludeItem(item, container, excId, incId, idx);
			});
			
			$(incId).click(function(e) {
				e.preventDefault();
				includeItem(item, container, excId, incId, idx);
			});
		};
		
		var excludeItem = function(item, container, excId, incId, idx) {
			NC.log('Excluding item ' + item.id);
			
			_data.goalValues[idx].active = false;
			$(container).find('h4').css({'text-decoration' : 'line-through', 'color' : '#999' });
			
			var divs = $(container + ' > .row-fluid');
			for (var i = 1; i < divs.length; i++) {
				$(divs.get(i)).hide();
			}
			
			$(excId).toggle();
			$(incId).toggle();
		};
		
		var includeItem = function(item, container, excId, incId, idx) {
			NC.log('Including item ' + item.id);
			
			_data.goalValues[idx].active = true;
			$(container).find('h4').css({'text-decoration' : 'none', 'color' : '#000' } );
			
			var divs = $(container + ' > .row-fluid');
			for (var i = 1; i < divs.length; i++) {
				$(divs.get(i)).show();
			}
			
			$(excId).toggle();
			$(incId).toggle();
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
				gv.active = true;
				
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
			
			var dom = _.template($('#estimationItem').html())(activityItem);
			$('#estimationItemsFieldset').append($(dom));
		};
		
		my.renderYesNoGoal = function(my, activityItem) {
			NC.log('Render yes/no gaol');
			var idx = initGoalValue(activityItem.id, 'yesno');
			
			$('#yesNoItemsFieldset').append(
				$(_.template($('#yesNoItem').html())(activityItem))
			);
		};
		
		my.renderTextGoal = function(my, activityItem) {
			NC.log('Render text goal');
			
			var idx = initGoalValue(activityItem.id, 'text');
			
			$('#textItemsFieldset').append(
				$(_.template($('#textItem').html())(activityItem))
			);
		};
		
		my.renderSingleGoal = function(activityItem) {
			NC.log('Render single goal');
			var t = _.template( $('#singleValue').html() );
			var dom = t(activityItem);
			
			$('#activityFieldset').append($(dom));
			
			var idx = initGoalValue(activityItem.id, 'measurement');
			
			var field = $('#field-' + activityItem.id); 
			field.val(_data.goalValues[idx].target);
			
			field.on('keyup', function() {
				var val = field.val();
				if(NC.GLOBAL.isDecimalNumber(val)) {
					_data.goalValues[idx].target = val;
					NC.log('Target set to: ' + val + ' for ' + activityItem.name);
					$(this).css('background', '#EDEDED');
					$('#savePlanBtn').attr('disabled', false);
				} else {
					$(this).css('background', '#F2DEDE');
					$('#savePlanBtn').attr('disabled', true);
				}
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
				var val = min.val();
				if(NC.GLOBAL.isDecimalNumber(val)) {
					updateIntervalValues(activityItem);
					NC.log('Target set to: ' + val + ' for ' + activityItem.name);
					$(this).css('background', '#EDEDED');
					$('#savePlanBtn').attr('disabled', false);
				} else {
					$(this).css('background', '#F2DEDE');
					$('#savePlanBtn').attr('disabled', true);
				}
			});
			
			$(max).on('change blur keyup', function() {
				var val = max.val();
				if(NC.GLOBAL.isDecimalNumber(val)) {
					updateIntervalValues(activityItem);
					NC.log('Target set to: ' + val + ' for ' + activityItem.name);
					$(this).css('background', '#EDEDED');
					$('#savePlanBtn').attr('disabled', false);
				} else {
					$(this).css('background', '#F2DEDE');
					$('#savePlanBtn').attr('disabled', true);
				}
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
			
			var val = $('#specifyTime').val();
			
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

			NC_MODULE.GLOBAL.loadAccessLevels($('select[name="level"]'));

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
			
			NC.GLOBAL.flash($('#item-' + template.id).parent());
		};

		my.searchTemplates = function(my) {
			NC.log('Searching... Text: ' + _name + ', Category: ' + _category + ', Level: ' + _level);
			new NC.Ajax().getWithParams('/templates/', {
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
			template.accessLevel.code = "CAREUNIT";
			template.accessLevel.value = "Vårdenhetsnivå";
			
			new NC.Ajax().post('/templates/', template, function(data) {
				NC.log('Copied ' + template.id + ' new id is: ' + data.data.id);
				my.buildTemplateItem(my, data.data, '#item-' + template.id);
			});
		};

		my.deleteTemplate = function(my, templateId) {
			NC.log('Delete template');
			new NC.Ajax().http_delete('/templates/' + templateId, function() {
				NC.log('Item removed');
				$('#item-' + templateId).parents('.item:first').fadeOut('fast');
			});
		};

		return my;

	})(),

	ACTIVITY_TEMPLATE : (function() {
		var activityTemplate;
		var my = {};
		var typeOpts;
		var unitOpts;
		var categories;
		var nextItemId = -1;

		my.initSingleTemplate = function(params) {
			activityTemplate = new Object();
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
					
					$('#templateTitle').html(activityTemplate.name);
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

				$('#templateTitle').html('Skapa ny mall')
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
			};
			
			$('#activitySaveButton').on('click', function() {
                NC.log('Save button clicked');

                activityTemplate.name = $('#activityTypeName').val();
                if(my.validate(activityTemplate.name)) {
                    NC.log('validated');

                    activityTemplate.accessLevel = new Object();
                    activityTemplate.accessLevel.code = $('input[name="accessLevel"]:radio:checked').val();

                    activityTemplate.category.id = $('#activityTypeCategory > option:selected').val();

                    for ( var i = 0; i < activityTemplate.activityItems.length; i++) {
                        delete activityTemplate.activityItems[i].details;
                        delete activityTemplate.activityItems[i].isDirty;
                    }
                    if (activityTemplate.id == -1) {
                        new NC.Ajax().post('/templates/', activityTemplate, function(data) {
                            activityTemplate = data.data;
                            renderItems(my, activityTemplate);
                            NC.log(activityTemplate);

                            window.location = NC.getContextPath() + '/netcare/admin/templates';
                        }, true);
                    } else {
                        new NC.Ajax().post('/templates/' + params.templateId, activityTemplate, function(data) {
                                    activityTemplate = data.data;
                                    renderItems(my,
                                            activityTemplate);
                                    NC.log(activityTemplate);

                                    window.location = NC.getContextPath() + '/netcare/admin/templates';

                                }, true);
                    }
                }
			});
            $('#activityCancelButton').on('click', function() {
                window.location.href = '/netcare/admin/templates';
            });
		};
		
		my.validate = function(name) {
			var result = true;
			var msg = 'Mallen måste ha ett namn';
			var container = $('#activityTypeNameContainer');
			if(name == null || name ==='') {
				if(container.find('.nameerror').length==0) {
					container.addClass('field-error').append('<label class="nameerror">' + msg + '</label>')
				}
				result = false;
			} else {
				container.removeClass('field-error');
				$('.nameerror').remove();
			}
			return result;
		};

		my.loadTemplate = function(templateId, callback) {
			new NC.Ajax().get('/templates/' + templateId, callback);
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
				
				var $radios = $('input[name="accessLevel"]');
			    if($radios.is(':checked') === false) {
			        $radios.filter('[value=CAREUNIT]').attr('checked', true);
			    }
			    
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
				
				$('input[name="accessLevel"]').click(function(e) {
					if ($(this).prop('checked')) {
						activityTemplate.accessLevel.code = $(this).val();
					}
				});
				
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
			NC.GLOBAL.flash($('#item' + itemId).parent());
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
			NC.GLOBAL.flash($('#item' + itemId).parent());
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

            // Did we press cancel on a new item? Remove it!
            var lastItem = activityTemplate.activityItems[activityTemplate.activityItems.length-1];
            if(lastItem.removeme != undefined) {
                activityTemplate.activityItems.splice(activityTemplate.activityItems.length-1,1);
            }

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
				addValidationHandlersForEstimation();
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
            $('#backButtonForm').on('click', function() {
                if (validateFormAndUpdateModel(
                    collectAndValidateFunction, item)) {
                    item.isDirty = true;
                    $('#activityItemFormContainer').hide('slide', {
                        direction : 'left'
                    }, 400);
                    $('#activityTypeContainer').show('slide', {
                        direction : 'left'
                    }, 400);
                    renderItems(my, activityTemplate);
                    window.scrollTo(0,520);
                }
            });
            $('#cancelButtonForm').on('click', function() {
                if(item.id<0 && (item.isDirty == undefined || !item.isDirty)) {
                    item.removeme = true;
                }
                $('#activityItemFormContainer').hide('slide', {
                    direction : 'left'
                }, 400);
                $('#activityTypeContainer').show('slide', {
                    direction : 'left'
                }, 400);
                renderItems(my, activityTemplate);
                window.scrollTo(0,520);
            });

		}
		
		var addValidationHandlersForEstimation = function() {
			var minScaleValueItem = $('#minScaleValue');
			var maxScaleValueItem = $('#maxScaleValue');

			$('.estvalue').on('keyup', function() {
				var minval = minScaleValueItem.val();
				var maxval = maxScaleValueItem.val();
				var minok = !isNaN(parseFloat(minval)) && isFinite(minval);
				var maxok = !isNaN(parseFloat(maxval)) && isFinite(maxval);			
				if(minok) {
					minScaleValueItem.css('background', '#EDEDED');
				} else {
					minScaleValueItem.css('background', '#F2DEDE');
				}
				if(maxok) {
					maxScaleValueItem.css('background', '#EDEDED');
				} else {
					maxScaleValueItem.css('background', '#F2DEDE');
				}
				if(minok && maxok) {
					$('#backButtonForm').attr('disabled', false);
				} else {
					$('#backButtonForm').attr('disabled', true);
				}
			});
		}

		var renderMeasurementForm = function(item) {
			var template = _.template($('#activityItemMeasurementForm').html());
			$('#activityItemFormContainer').append(template(item));
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
			NC_MODULE.GLOBAL.getMeasureValueTypes(function(data) {
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
		
		var initValidation = function() {
			$('#unitForm').validate({
				messages: {
					dn : "Unikt namn på enheten saknas",
					name: "Förkortning på enheten saknas"
				},
				errorClass: "field-error",
					highlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').addClass('field-error');
				},
					unhighlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').removeClass('field-error');
				}
			});
		};
		
		var validate = function() {
			var validate = $('#unitForm').validate();
			return validate.numberOfInvalids() == 0;
		};
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_data.id = -1;
			
			my.processUnits(that);
			my.initChangeListeners(that);
			initValidation();
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
				
				if (validate()) {
					my.save(my);
				}
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
					my.buildUnitRow(my, data.data, false);
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
		
		var initValidation = function() {
			$('#activityCategoryForm').validate({
				messages: {
					name: "Namn på kategori saknas"
				},
				errorClass: "field-error",
					highlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').addClass('field-error');
				},
					unhighlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').removeClass('field-error');
				}
			});
		};
		
		var validate = function() {
			var validate = $('#activityCategoryForm').validate();
			return validate.numberOfInvalids() == 0;
		};
		
		var my = {};
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_data.id = -1;
			
			my.loadTable(that);
			my.initChangeListeners(that);
			my.renderForm();
			initValidation();
		};
		
		my.initChangeListeners = function(my) {
			$('input[name="name"]').bind('keyup change blur', function() {
				_data.name = $(this).val();
			});
			
			$('#activityCategoryForm').submit(function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				if (validate() == true) {
					my.save(my);
				}
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
	
	PROFILE : (function() {
		var my = {};
		
		var initValidation = function() {
			$('#profile-form').validate({
				messages: {
					firstName : "Förnamn saknas",
					surName: "Efternamn saknas"
				},
				errorClass: "field-error",
					highlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').addClass('field-error');
				},
					unhighlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').removeClass('field-error');
				}
			});
		};
		
		var validate = function() {
			var validate = $('#profile-form').validate();
			return validate.numberOfInvalids() == 0;
		};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			initValidation();
			my.load(function(data) {
				_data = data.data;
				
				my.initListeners(that);
				my.render(that);
			});
		};
		
		my.load = function(callback) {
			new NC.Ajax().get('/user/' + my.params.patientId + '/load', callback);
		};
		
		my.render = function(my) {
			$('#userprofile input[name="firstName"]').val(_data.firstName);
			$('#userprofile input[name="surName"]').val(_data.surName);
			$('#userprofile input[name="crn"]').val(NC.GLOBAL.formatCrn(_data.civicRegistrationNumber));
			$('#userprofile input[name="email"]').val(_data.email);
			$('#userprofile input[name="phone"]').val(_data.phoneNumber);
		};
		
		my.initListeners = function(my) {
			$('#userprofile input[name="firstName"]').on('keyup change blur', function() {
				_data.firstName = $(this).val();
			});
			
			$('#userprofile input[name="surName"]').on('keyup change blur', function() {
				_data.surName = $(this).val();
			});
			
			$('#userprofile input[name="email"]').on('keyup change blur', function() {
				_data.email = $(this).val();
			});
			
			$('#userprofile input[name="phone"]').on('keyup change blur', function() {
				_data.phoneNumber = $(this).val();
			});
			
			$('#userprofile').submit(function(e) {
				e.preventDefault();
				
				if (validate()) {
					new NC.Ajax().post('/user/' + my.params.patientId + '/update', _data, function(data) {
						_data = data.data;
						my.render();
						my.displaySuccess();
					});
				}
			});
		};
		
		my.displaySuccess = function(my) {
			$('#userprofile').find('.pageMessages').append(
				$('<div>').addClass('alert alert-success').html('Din profil har sparats')
			);
		};
		
		return my;
	})(),
	
	COMMENTS : (function() {
		
		var _data = new Array();
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.renderComments(that);
		};
		
		my.load = function(my, callback) {
			new NC.Ajax().get('/comments', callback);
		};
		
		my.renderComments = function(my) {
			my.load(my, function(data) {
				
				if (data.data.length > 0) {
					
					$('#comments').show();
					
					$.each(data.data, function(i, v) {
						_data[i] = v;
						my.createComment(my, i, false);
					});
				}
			});
		};
		
		my.createComment = function(my, idx, update) {
			
			var markedAsRead = _data[idx].markedAsRead;
			var like = _data[idx].like;
			var reply = _data[idx].reply;
			
			var dom;
			//if (markedAsRead == true || like == true) {
				dom = _.template($('#activity-comment').html())(_data[idx]);
//			} else {
//				dom = _.template($('#activity-comment').html())(_data[idx]);
//			}
			
			if ($('#activity-comment-' + _data[idx].id).size() > 0) {
				$('#activity-comment-' + _data[idx].id).replaceWith($(dom));
			}
			
			if (update == false) {
				$('#comments').append($(dom));
			}
			
			if (markedAsRead == true) {
				$('#activity-awards-' + _data[idx].id).append(
					$('<img>').prop('src', NC.getContextPath() + '/img/icons/read.png')
				);
			}
			
			if (like == true) {
				$('#activity-awards-' + _data[idx].id).append(
					$('<img>').prop('src', NC.getContextPath() + '/img/icons/like.png')
				);
			}
			
			if (reply != null) {
				$('#activity-comment-' + _data[idx].id + '-showReply').show();
			} else {
				$('#activity-comment-' + _data[idx].id + '-replyButton').show();
			}
			
			my.initListenersForComment(my, idx);
		};
		
		my.initListenersForComment = function(my, idx) {
			
			var closeBtn = $('#activity-comment-' + _data[idx].id).find('button.close');
			var replyBtn = $('#activity-comment-' + _data[idx].id + '-replyButton').find('button');
			var replyArea = $('#activity-comment-'+ _data[idx].id +'-replyForm').find('textarea');
			var doReply = $('#activity-comment-'+ _data[idx].id +'-replyForm').find('button');
			
			closeBtn.click(function(e) {
				my.hide(_data[idx].id, function(data) {
					NC.log('Comment hidden');
				});
			});
			
			replyBtn.click(function() {
				replyBtn.parent().hide();
				doReply.parent().show();
				replyArea.focus();
			});
			
			replyArea.bind('blur keyup change', function() {
				_data[idx].reply = $(this).val();
			});
			
			doReply.click(function() {
				new NC.Ajax().post('/comments/' + _data[idx].id, _data[idx], function(data) {
					// Rerender
					_data[idx] = data.data;
					my.createComment(my, idx, true);
				});
			});
		};
		
		my.sendReply = function(replyId, message, callback) {
			new NC.Ajax().post('/comments/' + commentId, message, callback);
		};
		
		my.hide = function(commentId, callback) {
			new NC.Ajax().post('/comments/' + commentId + '/hide', callback);
		};
		
		return my;
	})(),
	
	SCHEDULE : (function() {
		
		var _due;
		var _reported;
		var _start;
		var _end;
		
		var _data = new Array();
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_due = params.due;
			_reported = params.reported;
			_start = params.start;
			_end = params.end;
			
			my.renderSchedule(that);
		};
		
		my.load = function(due, reported, start, end, callback) {
			new NC.Ajax().getWithParams(
				'/scheduledActivities', 
				{ 'due' : due, 'reported' : reported, 'start' : start, 'end' : end }, 
				callback
			);
		};
		
		my.renderSchedule = function(my) {
			// Show loader
			NC.GLOBAL.showLoader('#report', 'Laddar dina aktiviteter...');
			
			my.load(_due, _reported, _start, _end, function(data) {
				NC.log('Scheduled activities loaded: ' + data.data.length);
				if (data.data.length > 0) {
                    var numOfActive = 0;
					$.each(data.data, function(i, v) {
                        if (v.activityDefinition.active == true) {
                            numOfActive++;
						    my.createScheduleItem(my, i, v);
                        }
					});

                    if (numOfActive > 0) {
                        NC.PAGINATION.init({
                            'itemIdPrefix' : 'scheduledActivityItem',
                            'paginationId' : '#siPagination',
                            'data' : data.data,
                            'previousLabel' : '<<',
                            'nextLabel' : '>>'
                        });

                        $('#reportContainer').show();
                    }
				}
				
				NC.GLOBAL.suspendLoader('#report');
			});
		};
		
		my.createScheduleItem = function(my, activityIndex, scheduledActivity, callback) {
			
			_data[activityIndex] = scheduledActivity;
			
			var dom = _.template($('#scheduledActivityItem').html())(scheduledActivity);
			$('#reportList').append($(dom));
			
			my.updateScheduleItem(my, activityIndex, scheduledActivity, callback);
		};
		
		my.updateScheduleItem = function(my, activityIndex, scheduledActivity, callback) {
			var subrow = $('#saItem' + scheduledActivity.id).find('.subRow');
			if (scheduledActivity.reported == null) {
				subrow.html(scheduledActivity.date + ' ' + scheduledActivity.time)
				if (scheduledActivity.due == true) {
					subrow.css({'color' : 'red', 'font-weight' : 'bold' });
				}
			} else if (scheduledActivity.rejected == true) {
				subrow.html('Rapporterad som ej utförd').css({'color' : 'red', 'font-weight' : 'bold'});
				
				if (_reported == false) {
					$('#scheduledActivityItem' + scheduledActivity.id).hide();
				}
				
			} else {
				subrow.html('Rapporterad ' + scheduledActivity.reported);
				
				if (_reported == false) {
					$('#scheduledActivityItem' + scheduledActivity.id).hide();
				}
			}
			
			var liElem = $('#scheduledActivityItem' + scheduledActivity.id);
			var expander = $('<div>').addClass('mvk-icon toggle').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				$('#sa-details-' + scheduledActivity.id).toggle();
				
				$(this).toggleClass('toggle').toggleClass('toggle-open');
			});
			
			liElem.find('.actionBody').empty().css('text-align', 'right').css('padding-right', '40px').append(expander);
			
			var detailsDom = _.template($('#scheduledActivityDetails').html())(scheduledActivity);
			liElem.find('.row-fluid').after($(detailsDom));
			
			my.processItemValues(my, activityIndex, scheduledActivity);
			
			/*
			 * Listen for changes on the activity
			 */
			my.initActivityListeners(my, activityIndex, callback);
		};
		
		my.initActivityListeners = function(my, idx, callback) {
			var id = _data[idx].id;
			var time = $('#' + id + '-report-time');
			var date = $('#' + id + '-report-date');
			var report = $('#sa-report-' + id);
			var noreport = $('#sa-noreport-' + id);
			var note = $('#' + id + '-report-note');
			
			NC.GLOBAL.validateTimeField(time);
			
			// First time init fix.
			_data[idx].actualTime = date.val() + ' ' + time.val();
			
			$(note).bind('change blur keyup', function() {
				_data[idx].note = $(this).val();
			});
			
			$(date).bind('change blur keyup', function(e) {
				e.stopPropagation();
				_data[idx].actualTime = $(this).val() + ' ' + time.val();
			});
			
			$(time).bind('change blur keyup', function(e) {
				e.stopPropagation();
				_data[idx].actualTime = date.val() + ' ' + $(this).val();
			});
			
			$(report).click(function(e) {
				my.performReport(my, idx, callback);
			});
			
			$(noreport).click(function(e) {
				_data[idx].rejected = true;
				my.performReport(my, idx, callback);
			});
		};
		
		my.performReport = function(my, idx, callback) {
			// Remove definition from data
			_data[idx].activityDefinition = undefined;
			_data[idx].patient = undefined;
			
			new NC.Ajax().post('/scheduledActivities/' + _data[idx].id, _data[idx], function(data) {
				
				if (callback != undefined) {
					callback(data);
				} else {
					// Save the updated data
					_data[idx] = data.data;
					
					// Fold the activity
					$('#sa-details-' + _data[idx].id).slideUp('fast');
					
					// Rerender
					my.updateScheduleItem(my, idx, _data[idx]);
					NC.GLOBAL.flash($('#scheduledActivityItem' + _data[idx].id));
				}
			});
		};
		
		my.processItemValues = function(my, activityIndex, activity) {
			for (var idx = 0; idx < activity.activityItemValues.length; idx++) {
				var actItem = activity.activityItemValues[idx];
				
				if (actItem.definition.active == false) {
					continue;
				}
			
				var type = actItem.definition.activityItemType.activityItemTypeName;
				if(type==='measurement') {
					if(actItem.definition.activityItemType.valueType.code
                        && actItem.definition.activityItemType.valueType.code==='SINGLE_VALUE') {
						type += 'Single';
					} else {
						type += 'Interval';
					}
				}
				
				var activityValuesTemplate = '#scheduled-' + type + 'Values';
				var t = _.template($(activityValuesTemplate).html());
				var dom = t(actItem);
				
				$('#sa-details-' + activity.id).find('.sa-details').append(dom);
				
				if (type==='estimation') {
					var initial;
					if (actItem.perceivedSense) {
						initial = actItem.perceivedSense;
					} else {
						initial = Math.floor((actItem.definition.activityItemType.maxScaleValue + actItem.definition.activityItemType.minScaleValue) / 2);
					}
					
					$('#sa-row-slider-' + actItem.id).slider({
						range : 'max',
						min : actItem.definition.activityItemType.minScaleValue,
						max : actItem.definition.activityItemType.maxScaleValue,
						value : initial
					});
					$('#sa-row-slider-' + actItem.id).draggable();
				}
				
				/*
				 * If reporting isn't possible due to scheduled time is more
				 * than a week away. Disable all form elements
				 */
				if (activity.reportingPossible == false) {
					$('#sa-details-' + activity.id).find('input').prop('disabled', true);
					$('#sa-details-' + activity.id).find('textarea').prop('readonly', true);
					$('#sa-row-slider-' + actItem.id).slider( "disable" );
				}
				
				if (activity.reportingPossible == true) {
					my.initActivityItemListener(my, activity.id, activityIndex, idx, actItem.id);
				}
				
				// FIX FOR YES NO INITIAL VALUE CHECKED
				if (actItem.valueType == "yesno") {
					if(actItem.answer == null) {
						actItem.answer = false;
						$('#sa-row-' + actItem.id).find('input[value="false"]').prop('checked', true);
					} else if (actItem.answer == true) {
						$('#sa-row-' + actItem.id).find('input[value="true"]').prop('checked', true);
					} else {
						$('#sa-row-' + actItem.id).find('input[value="false"]').prop('checked', true);
					}
				}
				
				var datefield = $('#' + activity.id + '-report-date');
				var dp = datefield.datepicker({
					dateFormat : 'yy-mm-dd',
					firstDay : 1,
					minDate : +0
				});
				
				if (activity.actDate) {
					dp.datepicker('setDate', activity.actDate);
				} else {
					dp.datepicker('setDate', new Date());
				}
				
				
				if (activity.actTime) {
					$('#' + activity.id + '-report-time').val(activity.actTime);
				} else {
					var d = new Date();
					var min = d.getMinutes() < 10 ? '0' + d.getMinutes() : d.getMinutes();
					var timeString = d.getHours() + ':' + min;
					$('#' + activity.id + '-report-time').val(timeString);
				}

				// Needed for correct deserialization
				actItem.valueType = actItem.definition.valueType; 
			}
		};
		
		my.initActivityItemListener = function(my, activityId, activityIndex, itemIndex, id) {

			var inputs = $('#sa-row-' + id).find('input');
			
			// Determine type
			var actItem = _data[activityIndex].activityItemValues[itemIndex];
			var type = actItem.valueType;
			var reportedField;
			if (type == "measurement") {
				reportedField = "reportedValue";
			} else if (type == "estimation") {
				reportedField = "perceivedSense";
			} else if (type == "yesno") {
				reportedField = "answer";
			} else if (type == "text") {
				reportedField = "textComment";
			} else {
				throw new Error('Unsupported value type: ' + type);
			}


            if (inputs.length == 0) {
                // We probably have a text area
                var textarea = $('#sa-row-' + id).find('textarea');
                if(textarea.length != 0) {
                    textarea.bind('blur', function() {
                        var value = $(this).val();
                        _data[activityIndex].activityItemValues[itemIndex][reportedField] = value;
                        NC.log('Setting value to: ' + value);
                    });
                } else {
                    throw new Error('Expected a text area.');
                }
            } else if (inputs.length == 1) {

                    inputs.bind('change blur keyup', function() {
					var value = $(this).val();
					if($(this).hasClass('decimalNumber')) {
						if(NC.GLOBAL.isDecimalNumber(value)) {
							$(this).css('background', '#EDEDED');
							_data[activityIndex].activityItemValues[itemIndex][reportedField] = value;
							$('#sa-report-' + activityId).attr('disabled', false);
							NC.log('Setting value to: ' + value);
						} else {
							$(this).css('background', '#F2DEDE');
							$('#sa-report-' + activityId).attr('disabled', true);
							NC.log('Invalid value: ' + value);
						}
					} else {
						_data[activityIndex].activityItemValues[itemIndex][reportedField] = value;
						NC.log('Setting value to: ' + value);
					}
				});
				
				if (type === "estimation") {

                    // Default slider in case the user doesn't touch
                    // the slider
                    if (actItem.perceivedSense == null) {
                        var initialVal = Math.floor((actItem.definition.activityItemType.maxScaleValue + actItem.definition.activityItemType.minScaleValue) / 2);
                        inputs.val(initialVal);
                    } else {
                        inputs.val(actItem.perceivedSense);
                    }
                    inputs.change();

					$('#sa-row-slider-' + actItem.id).on('slide', function(e, ui) {
						inputs.val(ui.value);
						inputs.change();
					});
					$('#sa-row-slider-' + actItem.id).draggable();
				}
				
			} else if (inputs.length == 2) {
				
				// yes no input
				$(inputs[0]).bind('change', function() {
					_data[activityIndex].activityItemValues[itemIndex][reportedField] = $(this).val();
					NC.log('Setting value to: ' + $(this).val());
				});
				
				$(inputs[1]).bind('change', function() {
					_data[activityIndex].activityItemValues[itemIndex][reportedField] = $(this).val();
					NC.log('Setting value to: ' + $(this).val());
				});
				
			} else {
				throw new Error('Expected 1 or 2 inputs and nothing else.');
			}
			
		};
		
		return my;
	})(),
	
	PATIENT_SCHEDULE : (function() {
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.initListeners(that);
			my.setFormDefaults();
			
			$('#filter-submit').click();
			
		};
		
		my.setFormDefaults = function() {
			$('#start').datepicker('setDate', new Date());
			$('#end').datepicker('setDate', new Date());
			$('#reported').prop('checked', true);
			$('#due').prop('checked', true);
		};
		
		my.initListeners = function(my) {
			
			$('#filter-submit').click(function(e) {
				e.preventDefault();
				my.search(
					$('#start').datepicker('getDate').getTime(),
					$('#end').datepicker('getDate').getTime(),
					$('#due').is(':checked'),
					$('#reported').is(':checked')
				);
			});
		};
		
		my.search = function(start, end, due, reported) {
			NC.log('Searching scheduled activities');
			$('#reportList').empty();
			$('#siPagination > ul').empty();
			
			NC_MODULE.SCHEDULE.init({
				'due' : due,
				'reported' : reported,
				'start' : start,
				'end' : end
			});
		};
		
		return my;
	})(),
	
	REPORTED_ACTIVITIES : (function() {
		
		var _data = new Object();
		
		var my = {};
		
		my.init = function(msgs) {
			var that = this;
			this.msgs = msgs;
			my.loadReportedActivities(that, msgs);
		};
		
		my.loadReportedActivities = function(my, msgs) {
			NC.GLOBAL.showLoader('#report', 'Vänligen vänta...');
			new NC.Ajax().get('/healthplans/activity/reported/latest', function(data) {
				
				if (data.data.length > 0) {
					$.each(data.data, function(i, v) {
						my.buildReportedActivityItem(my, v, msgs);
					});
					
					NC.PAGINATION.init({
						'itemIdPrefix' : 'reportedActivityItem',
						'paginationId' : '#riPagination',
						'data' : data.data,
						'previousLabel' : '<<',
						'nextLabel' : '>>'
					});
					
					$('#reportContainer').show();
				}
				
				NC.GLOBAL.suspendLoader('#report');
				
			});
		};
		
		my.buildReportedActivityItem = function(my, act, msgs) {
			var t = _.template($('#reportedActivityItem').html());
			var dom = t(act);
			$('#latestActivitiesContainer').append($(dom));
			
			/*
			 * Bind click event
			 */
			var liElem = $('#reportedActivityItem' + act.id);
			
			var detailsTemplate = _.template($('#reportedActivityDetails').html());
			var detailsDom = detailsTemplate(act);
			
			liElem.find('.row-fluid').after($(detailsDom));
			
			my.processValues(my, act);
			
			var expander = $('<div>').addClass('mvk-icon toggle').click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				$('#ra-details-' + act.id).toggle();
				$(this).toggleClass('toggle');
				$(this).toggleClass('toggle-open');
			}) ;

			liElem.find('.actionBody').css('text-align', 'right').css('padding-right', '40px').append(expander);
			
			var commented = false;
			var liked = false;
			var hasBeenRead = false;
			var replied = false;
			if(act.comments.length>0) {
				commented = act.comments[0].comment!=null && act.comments[0].comment!='';
				liked = act.comments[0].like;
				hasBeenRead = act.comments[0].markedAsRead;
				replied = act.comments[0].repliedAt != null;
			}
			var like = liElem.find('.likeReported');
			var likedText = '<span style="font-weight: bold;">' + msgs.liked + '</span>';
			if(liked) {
				like.html(likedText);
			} else {
				like.html(msgs.like);
				like.click(function(e) {
					new NC.Ajax().postWithParams('/healthplans/activity/' + act.id + '/like', { like : true }, function() {
						NC.log('Liked.');
						like.html(likedText);
						like.unbind('click');
					}, true);
				});
			}
			var markAsRead = liElem.find('.markReported');
			var hasBeenReadText = '<span style="font-weight: bold;">' + msgs.markedasread + '</span>';
			if(hasBeenRead) {
				markAsRead.html(hasBeenReadText);
			} else {
				markAsRead.html(msgs.markasread);
				markAsRead.click(function(e) {
					NC.log('Marked-as-read reported value: ' + act.id);
					new NC.Ajax().postWithParams('/healthplans/activity/' + act.id + '/read', { read : true }, function() {
						NC.log('Marked as read.');
						markAsRead.html(hasBeenReadText);
						markAsRead.unbind('click');
					}, true);
				});
			}

			var commentedDiv;
			if(commented) {
				commentedDiv = liElem.find('#actcommented');
				commentedDiv.html('<span style="font-style: italic;">"' + act.comments[0].comment + '"</span> - ' + act.comments[0].commentedBy);
				liElem.find('#actcomment').hide();
				commentedDiv.show();
			} else {
				var commentText = liElem.find('#activitycomment');
				liElem.find('.btn').click(function(e) {
					var text = commentText.val();
					if(text!=null && text!="") {
						
						NC.log('Submit comment: ' + text);
						e.preventDefault();
						new NC.Ajax().postWithParams('/healthplans/activity/' + act.id + '/comment', { comment : text }, function() {
							NC.log('Comment completed.');
							commentText.val('');
							liElem.find('#actcomment').fadeOut(function() {
								commentedDiv = liElem.find('#actcommented');
								commentedDiv.html('<span style="font-style: italic;">"' + text + '"</span>');
								commentedDiv.fadeIn();
							});
						}, true);
					}
				});
			}
			if(replied) {
				var repliedDiv = liElem.find('#actreplied');
				repliedDiv.html('<span style="font-style: italic;">"' + act.comments[0].reply + '"</span> - ' + act.patient.firstName + ' ' + act.patient.surName);
				repliedDiv.show();
				
			}
		};
		
		my.processValues = function(my, act) {
			if (act.activityItemValues.length == 0) {
				NC.log('No activities reported');
				$('#ra-details-' + act.id).find('.span12').append(
					$('<p>').css({'font-style' : 'italic'}).html('Inga aktiviteter rapporterade.')
				);
			} else {
				$.each(act.activityItemValues, function(idx, actItem) {
					var type = actItem.definition.activityItemType.activityItemTypeName;
					if(type==='measurement') {
                        if(actItem.definition.activityItemType.valueType.code
                            && actItem.definition.activityItemType.valueType.code==='SINGLE_VALUE') {
							type += 'Single';
						} else {
							type += 'Interval';
						}
					}
					var activityValuesTemplate = '#' + type	+ 'Values';
					var t = _.template($(activityValuesTemplate).html());
					var dom = t(actItem);
					$('#ra-details-' + act.id).find('.span12').append($(dom));
				});
			}
		};
		
		my.doFilter = function(pnr, date1, date2, msgs) {
			
			var params = {
				personnummer : pnr,
				dateFrom : date1,
				dateTo : date2
			};
			
			NC.GLOBAL.showLoader('#report');
			new NC.Ajax().getWithParams('/healthplans/activity/reported/filter', params, function(data) {
				
				$('#latestActivitiesContainer').empty();
				
				if (data.data.length > 0) {
					$.each(data.data, function(i, v) {
						my.buildReportedActivityItem(my, v, msgs);
					});
					
					NC.PAGINATION.init({
						'itemIdPrefix' : 'reportedActivityItem',
						'paginationId' : '#riPagination',
						'data' : data.data,
						'previousLabel' : '<<',
						'nextLabel' : '>>'
					});
					
					$('#reportContainer').show();	
				}
				
				NC.GLOBAL.suspendLoader('#report');
			});
		};
		
		return my;
		
	})(),

	ALARM : (function() {
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.load(that);
		};
		
		my.load = function() {
			new NC.Ajax().get('/alarm/list', function(data) {
				if (data.data.length == 0) {
					$('#alarms').hide();
				} else {
					$('#alarmContainer').empty();
					var t = _.template($('#alarmPaperSheet').html());
					var dom = t();
					$('#alarmContainer').append($(dom));
					$.each(data.data, function(index, value) {
						var info =  value.cause.value;
						if (value.info != null) {
							info += '<br/>' + value.info;
						}
						value.causeText = info;
						value.contextPath = NC.getContextPath();

						var t = _.template($('#alarmRow').html());
						var dom = t(value);
						$('#alarmsItem table tbody').append(dom);
						
						$('#resolve' + value.id).click(function() {
							NC.log("Resolving alarm...");
							new NC.Ajax().post('/alarm/' + value.id + '/resolve', {}, my.load, true);
						});

					});
				}
			});
		};
		
		return my;
	})(),
	
	REPLIES : (function() {
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.load(that);
		};
		
		my.load = function(my) {
			new NC.Ajax().get('/healthplans/activity/reported/comments/newreplies', function(data) {
				if (data.data.length == 0) {
					$('#replies').hide();
				} else {
					var t = _.template($('#repliesPaperSheet').html());
					var dom = t();
					$('#repliesContainer').append($(dom));
					$.each(data.data, function(index, value) {

						value.contextPath = NC.getContextPath();

						var t = _.template($('#repliesRow').html());
						var dom = t(value);
						$('#repliesItem table tbody').append(dom);
						
						$('#hideComment' + value.id).click(function() {
							NC.log("Hiding comment...");
							new NC.Ajax().post('/healthplans/activity/reported/comments/' + value.id + '/hide', {}, my.load, true);
						});

					});
				}
			});
		};
		
		return my;
	})(),
	
	PATIENT_ACTIVITIES : (function() {
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.createSchemaTable(that);
		};
		
		my.createSchemaTable = function(my) {
			NC.GLOBAL.showLoader('#my-schedule', 'Laddar ditt schema...');
			my.load(function(data) {
                NC.GLOBAL.suspendLoader('#my-schedule');
				if (data.data.length > 0) {
					$.each(data.data, function(i, v) {
						my.createSchemaRow(my, v);
					});
				}
			});
		};
		
		var findMax = function(dayTimes) {
			var max = 0;
			$.each(dayTimes, function(i, v) {
				var cur = v.times.length;
				if (cur > max) {
					max = cur;
				}
			});
			
			return max;
		};
		
		my.createSchemaRow = function(my, activity) {
			
			var period;
			if (activity.activityRepeat == 0) {
				activity.period = activity.startDate;
			} else {
				activity.period = activity.endDate;
			}
			
			activity.totalDone = Math.ceil((activity.numDone / activity.numTotal) * 100);
			activity.targetDone = Math.ceil((activity.numDone / activity.numTarget) * 100);
			
			var dom = _.template($('#patient-schema').html())(activity);
			$('#activity-list').append($(dom));
			
			if (activity.reminder) {
				$('#activityItem' + activity.id + '-reminder').prop('checked', true);
			}
			
			my.initReminderListener(my, activity);
			my.buildSchemaTable(my, activity);
		};
		
		my.initReminderListener = function(my, activity) {
			
			$('#activityItem' + activity.id + '-reminder').click(function() {
				var reminder =  $(this).is(':checked');
				var url = reminder ? '/enableReminder' : '/disableReminder';
				
				new NC.Ajax().post('/activityPlans/' + activity.id + url, null, function(data) {
					NC.log('Activity updated...');
				});
			});
			
		};
		
		my.buildSchemaTable = function(my, activity) {
			// Find out max times for our daytimes
			var rows = findMax(activity.dayTimes);
			for (var i = 0; i < rows; i++) {
				$('#planned-times-' + activity.id + ' tbody').append('<tr>');
			}
			
			var trs = $('#planned-times-' + activity.id + ' tbody tr');
			$.each(trs, function(i, v) {
				for (var j = 0; j < 7; j++) {
					$(this).append($('<td>'));
				}
			});
			
			$.each(activity.dayTimes, function(i, v) {
				
				var col;
				if (v.day == "monday") {
					col = 0;
				}
				
				if (v.day == "tuesday") {
					col = 1;
				}
				
				if (v.day == "wednesday") {
					col = 2;
				}

				if (v.day == "thursday") {
					col = 3;
				}

				if (v.day == "friday") {
					col = 4;
				}

				if (v.day == "saturday") {
					col = 5;
				}

				if (v.day == "sunday") {
					col = 6;
				}
				
				$.each(v.times, function(idx, val) {
					NC.log('Processing ' + val + ' for day ' + v.day + ' td' + col + ', tr' + idx);
					
					var td = $('#planned-times-' + activity.id + ' tr:eq(' + (idx + 1) + ') td:eq(' + col + ')');
					td.html(val);
				})
			});
			
			var text = '';
			switch (activity.activityRepeat) {
			case 0: text += my.params.lang.freqs[0]; break;
			case 1: text += my.params.lang.freqs[1]; break;
			case 2: text +=my.params.lang.freqs[2]; break;
			case 3: text += my.params.lang.freqs[3]; break;
			case 4: text += my.params.lang.freqs[4]; break;
			case 5: text += my.params.lang.freqs[5]; break;
			default:
				text += my.params.lang.every + activityDefinition.activityRepeat + ' ' + my.params.lang.week;
			break;
			}
			
			$('#schedule-' + activity.id + '-repeat > i > small').html(my.params.lang.repeat + ' ' + text);
			
		};
		
		my.load = function(callback, patientId) {
            if (patientId != undefined) {
                var qs = '?patient=' + patientId + '&';
                new NC.Ajax().get('/activityPlans' + qs, callback);
            } else {
                new NC.Ajax().get('/activityPlans', callback);
            }


		};
		
		return my;
	})(),
	
	PATIENT_EXTRA_REPORT : (function() {
		
		var _data;
		
		var findIndexForDefinition = function(id) {
			NC.log('Find definition with id: ' + id);
			for (var i = 0; i < _data.length; i++) {
				
				var d = _data[i];
				if (d.id == id) {
					return i;
				}
			}
		}
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_data = new Array();
			
			my.initListeners();
			my.loadActivities();
		};
		
		my.initListeners = function() {
			$('#activityDefinitions').bind('change select', function() {
				var idx = findIndexForDefinition($(this).find('option:selected').prop('value'));
				var def = _data[idx];
				
				$('#reportList').empty();
				
				/*
				 * Load latest scheduled activity of this definition and use it
				 * as a template
				 */
				new NC.Ajax().getWithParams('/scheduledActivities/latestForDefinition', { 'definitionId' : def.id }, function(data) {
					
					var template = data.data;
					template.activityDefinition = def;
					template.reportingPossible = true;
					template.extra = true;
					
					NC.log('Creating schedule item');
					var schedule = NC_MODULE.SCHEDULE;
					schedule.createScheduleItem(schedule, idx, template, function(data) {
						
						// Callback when save has been success
						my.cleanup(template.id);
						
						$('div.form-actions').prepend(
							$('<div>')
							.css('margin-bottom', '20px')
							.addClass('alert alert-success')
							.html('<i>Rapportering genomförd</i>')
						);
						
					});
					
					// Hide report as not performed
					my.cleanup(template.id);
				});
				
			});
		};
		
		my.cleanup = function(id) {
			$('.subRow').html('<i>Extra rapportering</i>');
			$('div.mvk-icon.toggle').remove();
			$('#sa-noreport-' + id).remove();
			$('div[id*="sa-row-"]').find('input').val('');
			$('textarea[id*="-report-note"]').val('');
			$('div[id*="sa-details-"]').find('.alert').remove();
			
			$('#sa-details-' + id).show();
		};
		
		my.loadActivities = function() {
			NC_MODULE.PATIENT_ACTIVITIES.load(function(data) {
				$.each(data.data, function(i, v) {
					
					_data[i] = v;
					
					$('#activityDefinitions').append(
						$('<option>').prop('value', v.id).html(v.type.name)
					);
				});
				
				$('#activityDefinitions').select();
				
			});
		};
		
		return my;
	})(),
	
	SELECT_RESULTS : (function() {
		
		var _hps;
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			_hps = new Array();
			
			my.loadActivities(that);
		};
		
		my.loadActivities = function(my) {
			NC.log('load for patient: ' + my.params.patientId);
			NC_MODULE.PATIENT_ACTIVITIES.load(function(data) {
				
				// Okey, we need to group the results by health plan
				for (var i = 0; i < data.data.length; i++) {
					_hps.push(data.data[i]);
				};
				
				// Let's render
				my.render(my);
			}, my.params.patientId);
		};
		
		my.render = function(my) {
			for (var i = 0; i < _hps.length; i++) {

                var hpName = _hps[i].healthPlanName;
                if (_hps[i].active) {
                    hpName += ' | Inaktiv'
                }
				
				if ( $('#healthplans h3').html() != hpName) {
					$('#healthplans').append(
						$('<h3>').addClass('title').html(hpName)
					);
					
					$('#healthplans').append(
						$('<ul>').prop('id', 'healthplan-' + _hps[i].healthPlanId).addClass('itemList facility')
					);
				}
				
				// Build comma separated activity string
				var str = '';
				for (var j = 0; j < _hps[i].goalValues.length; j++) {
					str += _hps[i].goalValues[j].activityItemType.name + ', ';
				}
				
				str += 'Anteckning';
				
				_hps[i].cs = str;
				
				var dom = _.template($('#activityResultItem').html())(_hps[i]);
				$('#healthplan-' + _hps[i].healthPlanId).append($(dom));
				
				my.initItemListener(my, '#activityResultItem-' + _hps[i].id, _hps[i].id);
			};
		};
		
		my.initItemListener = function(my, elemId, activityId) {
			
			$(elemId).bind('click', function(e) {
				window.location = NC.getContextPath() + '/netcare/shared/results?activity=' + activityId;
			});
		};
		
		return my; 
	})(),

	RESULTS : (function() {
		
		var activity;
		
		var my = {};
		
		my.init = function(params) {
			this.params = params;
			var that = this;
			
			my.loadActivity(that, params.activityId);
		};
		
		my.loadActivity = function(my, activityId) {
			new NC.Ajax().get('/activityPlans/' + activityId, function(data) {
				activity = data.data;
				Highcharts.setOptions({
					lang: {
						months: ['januari', 'februari', 'mars', 'april', 'maj', 'juni', 'juli', 'augusti', 'september', 'oktober', 'november', 'december'],
						shortMonths : ['jan', 'feb', 'mar', 'apr', 'maj', 'jun', 'jul', 'aug', 'sep', 'okt', 'nov', 'dec'],
						weekdays: ['söndag', 'måndag', 'tisdag', 'onsdag', 'torsdag', 'fredag', 'lördag'],
						thousandsSep: ' '
					},
                    global: {
                        useUTC: false
                    },
                    credits: {
                        enabled: false
                    }
				});

				for(var i = 0; i < activity.goalValues.length; i++) {
                    var included = activity.goalValues[i].active;
                    if (!included) {
                        continue;
                    }

					var id = activity.goalValues[i].id;
					var divId = 'report' + id;
					var popoverDiv = $('<div>').attr('id', divId+'Popover');
					$('#activities').append(popoverDiv);
					
					var div = $('<div>').attr('id', divId).addClass('reportdiagram');
					$('#activities').append(div);
					
					var detailsDiv = $('<div>').attr('id', 'details' + id).addClass('scheduledDetails');
					$('#activities').append(detailsDiv);

					new NC.Ajax().get('/healthplans/activity/item/' + id + '/statistics', function(data) {
						NC.log(data.data);
						var report = data.data;
						var divId = 'report' + report.id;
						var header = _.template($('#reportHead').html())(report);
						if(report.type!=null) {
							if(report.type=='measurement' || report.type=='estimation') {
							 my.renderDiagram(my, report);
							} else if(report.type=='yesno') {
								my.renderYesNo(my, report, divId);
							} else if(report.type=='text') {
								my.renderText(my, report, divId);
							} 
						}
						$('#' + divId).prepend(header);
					});
				}
			});
		};
		
		my.renderDiagram = function(my, report) {
			var divId = 'report' + report.id;
			
			var reportSeries = [{
	            name: report.label,
	            data: report.reportedValues
	         },{
		        data: report.ids,
		        visible: false
		     }];
			
			if(report.subtype!=null) {
				if(report.subtype=='SINGLE_VALUE') {
					reportSeries.push({
						name: 'Målvärde',
						data: report.targets
					});
				} else {
					reportSeries.push({
						name: 'Målvärde (min)',
						data: report.minTargets
					});
					reportSeries.push({
						name: 'Målvärde (max)',
						data: report.maxTargets
					});
				}
			}
		
			var chart = new Highcharts.StockChart({
		         chart: {
		            renderTo: divId,
		            type: 'line'
		         },
		         plotOptions: {
		             series: {
		                 allowPointSelect: true,
		                 point: {
		                     events: {
		                         click: function() {
		                        	 loadAndShowScheduledActivity(this.config[2], report.id, openPopoverOnChart);
		                         }
		                     }
		                 }
		             }
		         },
		         rangeSelector: {
			            enabled: false
		         },
		         title: {
		            text: ''//report.label
		         },
		         xAxis: {
		            dateTimeLabelFormats: {
		                second: '%Y-%m-%d<br/>%H:%M:%S',
		                minute: '%Y-%m-%d<br/>%H:%M',
		                hour: '%Y-%m-%d<br/>%H:%M',
		                day: '%Y<br/>%m-%d',
		                week: '%Y<br/>%m-%d',
		                month: '%Y-%m',
		                year: '%Y'
		            }
		         },
		         yAxis: {
		            title: {
		               text: report.unit
		            }
		         },
		         series: reportSeries
		      });
		};

		var loadAndShowScheduledActivity = function(id, reportId, openFun) {
			new NC.Ajax().get('/healthplans/activity/' + id + '/load', function(data) {
				openFun(data.data, reportId);
			});
		}
		var openPopoverOnChart = function(data, reportId) {
			var popoverId = '#report' + reportId + 'Popover';
			openPopoverWithId(data, popoverId);
		}
		var openPopoverOnTextRow = function(data, rowId) {
			openPopoverWithId(data, '#'+rowId);
		}
		var openPopoverWithId = function(data, id) {
			var popTemplate = '<div class="popover" onclick="$(\'' + id + '\').popover(\'destroy\');"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content"><p></p></div></div></div>';
			var activityTitle = data.activityDefinition.type.name + ' - ' + data.actualTime.substring(0,10);
			var where = $(id);
			where.popover('destroy');
			where.popover({ title: activityTitle, content: getAllActivities(data), trigger:'manual',placement:'bottom', template:popTemplate });
			where.popover('show');
		}
		var getAllActivities = function(data) {
			var values = data.activityItemValues;
			var result = '';
			var label;
			var unit;
			for(var i = 0 ; i < values.length ; i++) {
				var item = values[i];
				label = '<span style="font-weight: bold;">' + item.definition.activityItemType.name + ": </span>";
				if(item.valueType=='measurement') {
					unit = item.definition.activityItemType.unit.name;
					if(item.definition.activityItemType.valueType.code==='SINGLE_VALUE') {
                        result += label + item.reportedValue + ' ' + unit + ' (Mål:' + item.target + ')<br/>';
					} else {
                        result += label + item.reportedValue + ' ' + unit + ' (Min:' + item.minTarget + ', Max: ' + item.maxTarget + ')<br/>';
                    }
				} else if(item.valueType=='estimation') {
					result += label + item.perceivedSense + ' (1=Lätt - 10=Mycket besvärligt)<br/>'; 
				} else if(item.valueType=='yesno') {
					result += label + (item.answer?'Ja':'Nej') + '<br/>'; 
				} else if(item.valueType=='text') {
                    if(item.textComment != undefined && item.textComment != null) {
                        result += label + item.textComment + '<br/>';
                    } else {
                        result += label + ' <br/>';
                    }
				}
			}
			return result;
		};
		
		my.renderYesNo = function(my, report, divId) {
			var dom = _.template($('#yesNoReportRow').html())(report);
			$('#' + divId).append(dom);
		};

		my.renderText = function(my, report, divId) {
			var table = $('<table>').addClass('table table-condensed table-hover').append('<tbody>');
			$('#' + divId).append(table);
			_.each(report.reportedValues, function(item){
				var arg = {
						id: item[2],
						divId: divId + 'r' + item[2],
						date:getFormattedDate(item[0]),
						text:item[1]
				}
				var domRow = _.template($('#textReportRow').html())(arg);
				table.children().append(domRow);
				$('#' + arg.divId).click(function() {
					//loadAndShowScheduledActivity(arg.id, arg.divId, openPopoverOnTextRow));
					loadAndShowScheduledActivity(arg.id,arg.divId,openPopoverOnTextRow);
				});
			});
			
		};

		function getFormattedDate(millis) {
			var date = new Date(millis);
			return date.getFullYear() + "-" + ((date.getMonth()+1)<10?'0':'') + (date.getMonth()+1) + "-" + (date.getDate()<10?'0':'') + date.getDate();
		}
		
		return my; 
	})(),
	
	CARE_UNIT_ADMIN : (function() {
		var _formData = new Object();
		var _data = new Array();
		
		var resetData = function() {
			var cc = $('#countyCouncil option:first').val();
			_formData = {
				id : -1,
				name : '',
				hsaId : '',
				countyCouncil : {
					code : cc
				}
			};
		};
		
		var initValidation = function() {
			$('#careunit-form').validate({
				messages: {
					name: "Namn på vårdenhet saknas",
					hsaId: "Vårdenhetens hsa-id saknas"
				},
				errorClass: "field-error",
					highlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').addClass('field-error');
				},
					unhighlight: function(element, errorClass, validClass) {
						$('#' + element.id + 'Container').removeClass('field-error');
				}
			});
		};
		
		var validate = function() {
			var validator =  $('#careunit-form').validate();
			return validator.numberOfInvalids() == 0;
		};
		
		var my = {};
		
		my.init = function(params) {
			var that = this;
			this.params = params;
			
			my.loadCountyCouncils(that);
			
			resetData();
			
			my.initListeners(that);
			my.renderCareUnits(that);
			my.initFormListeners(that, my.onSave);
			my.renderForm(my, _formData);
			
			initValidation();
		};
		
		my.onSave = function(my, data) {
			resetData();
			my.renderForm(my, _formData);
			my.renderCareUnits(my);
			
			$('#careunits-form-sheet').hide();
		};
		
		my.loadCountyCouncils = function(my) {
			NC_MODULE.GLOBAL.loadCountyCouncils($('#countyCouncil'));
		}
		
		my.initListeners = function(my) {
			$('#show-careunits-sheet').on('click', function(e) {
				e.preventDefault();
				$('#careunit-form-sheet').toggle();
			});
		};
		
		my.initFormListeners = function(my, onSave) {
			$('#name').on('keyup change blur', function(e) {
				_formData.name = $(this).val();
			});
			
			$('#hsaId').on('keyup change blur', function(e) {
				_formData.hsaId = $(this).val();
			});
			
			$('#countyCouncil').on('change', function(e) {
				var cc = new Object();
				cc.code = $(this).find('option:selected').val();
				
				_formData.countyCouncil = cc;
				NC.log('Selected ' + cc.code); 
			});
			
			$('#careunit-form').submit(function(e) {
				e.preventDefault();
				if (validate() == true) {
					my.saveCareUnit(_formData, function(data) {
						my.onSave(my, data);
					});
				}
			});
		};
		
		my.saveCareUnit = function(data, callback) {
			new NC.Ajax().post('/careunits/' + data.id, data, callback);
		};
		
		my.loadCareUnits = function(callback) {
			new NC.Ajax().get('/careunits', callback);
		};
		
		my.renderCareUnits = function(my) {
			NC.GLOBAL.showLoader('#careunits', 'Laddar vårdenheter...');
			$('#careunitsListContainer').empty();
			my.loadCareUnits(function(data) {
				_data = data.data;
				
				$.each(_data, function(i, v) {
					my.renderCareUnit(my, i, v);
				});
				
				NC.PAGINATION.init({
					'itemIdPrefix' : 'careUnitItem-',
					'paginationId' : '#cuPagination',
					'data' : _data,
					'previousLabel' : '<<',
					'nextLabel' : '>>'
				});
				
				if (_data.length > 0) {
					$('#careunitsContainer').show();
				}
				
				NC.GLOBAL.suspendLoader('#careunits');
			});
		};
		
		my.renderCareUnit = function(my, idx, careunit) {
			var dom = _.template($('#careUnitItem').html())(careunit);
			var note = _.template($('#itemNote').html())({ value : careunit.hsaId });
			
			$('#careunitsListContainer').append($(dom));
			$('#careunit-item-' + careunit.id).next('a.itemNavigation').after($(note));
			
			my.initCareUnitListener(my, idx, careunit);
		};
		
		my.initCareUnitListener = function(my, idx, careunit) {
			$('#careunit-item-' + careunit.id).on('click', function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				my.renderForm(my, careunit);
				$('#careunit-form-sheet').show();
			})
		};
		
		my.renderForm = function(my, data) {
			_formData = data;
			$('#name').val(_formData.name);
			$('#hsaId').val(_formData.hsaId);
			$('#countyCouncil option').each(function(i, v) {
				if ($(this).val() == _formData.countyCouncil.code) {
					$(this).prop('selected', true);
				}
			});
		};
		
		return my;
	})()
	
};
