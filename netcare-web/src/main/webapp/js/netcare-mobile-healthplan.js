/*
 * Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>
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
var NC_MOBILE = {

	ACTIVITIES : (function() {
		var my = {};
		
		my.templateNames = ['measurementSingleItemTemplate', 'measurementIntervalItemTemplate', 
		                     'estimationItemTemplate', 'yesnoItemTemplate', 'textItemTemplate', 
		                     'commonActivityItemTemplate'];
		var templates;		
		var due;
		var actual;
		var reported;

		my.init = function() {
			console.log('my.init()');
			var self = this;
			this.templates = {};

			my.pageInit(self);
		};
		
		my.pageInit = function(my) {
				
				$.mobile.loading('show', {
					text : 'Laddar aktiviteter',
					textVisible : true
				});
				
				my.precompileTemplates(my);
				
				my.load(my, function() {
					setupGUI(my);
					$.mobile.loading('hide');
					my.buildFromArray(my, actual, function() {
						console.log('complete-after-init');
					});
				});

		};

		my.precompileTemplates = function(my) {
			$.each(my.templateNames, function(index, name) {
				my.templates[name] =  _.template($('#'+name).html());
			});
		};
		
		my.load =  function(my, callback) {

			due = new Array();
			actual = new Array();
			reported = new Array();
			var now = new Date();

			var params = {
					reported : true,
					due : true,
					start : new Date().setDate(now.getDate() - 7),
					end : new Date().setDate(now.getDate() + 7)
			}
			new NC.Ajax().getWithParams('/scheduledActivities', params, function(data) {
				$.each(data.data, function(index, value) {
					if (value.reported != null) {
						reported.push(value);
					} else if (value.reported == null && value.due) {
						due.push(value);
					} else {
						actual.push(value);
					}
				});

				callback(my);
			});
		};
		
		var setupGUI = function(my) {

            $('#logout').on('tap', function(e) {
                logout();
            });

            $('#refresh').on('tap', function(e) {
				$('#schema').empty();
				my.load(my, function() {
					console.log('refresh');
					my.buildFromArray(my, actual, function() {
						console.log('complete-after-refresh');
					});
					$('#actual').addClass('ui-btn-active');
					$('#due').removeClass('ui-btn-active');
					$('#reported').removeClass('ui-btn-active');
				});
			});

			$('#refresh').on('taphold', function(e) {
				window.location.href='start';
			});

			$('#actual').on('tap click', function(e) {
				$('#actual').addClass('ui-btn-active');
				$('#due').removeClass('ui-btn-active');
				$('#reported').removeClass('ui-btn-active');
				my.buildFromArray(my, actual, function() {
					console.log('complete');
				});
			});

			$('#due').on('tap', function(e) {
				$('#actual').removeClass('ui-btn-active');
				$('#due').addClass('ui-btn-active');
				$('#reported').removeClass('ui-btn-active');
				my.buildFromArray(my, due);
			});

			$('#reported').on('tap', function(e) {
				$('#actual').removeClass('ui-btn-active');
				$('#due').removeClass('ui-btn-active');
				$('#reported').addClass('ui-btn-active');
				my.buildFromArray(my, reported);
			});

		};

        var logout = function() {
            $.ajax({
                url : NC.getContextPath() + "/mobile/logout",
                cache : false,
                success : function(data) {
                    console.log("Logged out");
                    document.location.href="start#blank";
                },
                error : function(jqXHR, status, error) {
                    console.log(status + ": " + jqXHR.status + " - " + error);
                    document.location.href="start#blank";
                }
            });
        };

		my.buildFromArray = function(my, listOfActivities, onComplete) {
			var currentDay = '';

			$('#schema').empty();
			
			if(listOfActivities.length > 0) {
				
				if(listOfActivities[0].reported != null) {
					listOfActivities.sort(function(a1,a2) {
						a1 = new Date(a1.actDate);
						a2 = new Date(a2.actDate);
						return (a1 < a2 ? -1 : a1>a2 ? 1 : 0);
					});
				}
	
				$.each(listOfActivities, function(index, activity) {
					
					var date = activity.reported != null ? activity.actDate : activity.date;
					
					if (currentDay != date) {
						currentDay = date;
						my.buildListView(my, activity, true);
					} else {
						my.buildListView(my, activity, false);
					}
				});

			}
			if(typeof onComplete !== 'undefined') {
				onComplete();
			}
		};

		my.buildListView = function(my, activity, shouldBuildHeader) {
			var schemaUlTag = $('#schema');
			if (shouldBuildHeader) {
				var day = activity.reported != null ? activity.actDay.value : activity.day.value;
				var date = activity.reported != null ? activity.actDate : activity.date;
				
				my.createListHeader(schemaUlTag, day + ' ' + date);
			}
			my.createListRow(my, schemaUlTag, '#report', activity, my.loadActivity);
		};

		my.createListHeader = function(parent, title) {
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
		};
		
		my.createListRow = function(my, parent, href, activity, clickCallback) {
			if (!activity.activityDefinition.active) {
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
			
			var link = $('<a>').attr('href', '#report').attr('data-transition', 'none').addClass('ui-link-inherit');
			activityContentDiv.append(link);
			
			var activityText = $('<div>').addClass('ui-btn-text');
			var time = activity.reported != null ? activity.actTime : activity.time;
			activityText.append(
				$('<h3>' + time + ' ' + activity.activityDefinition.type.name + '</h3>').addClass('ui-li-heading')
			);
			
			var desc = '';
			$.each(activity.activityItemValues, function(i, activityItem) {
				if (activityItem.definition.active) {
					var next = activityItem.definition.activityItemType.name;
					if((desc.length + next.length)>32) {
						desc += "...";
					} else {
						if(desc.length==0) {
							desc = next;
						} else {
							desc += ', ' + next;
						}
					}
				}
			});
			
			activityText.append(
				$('<p>').addClass('ui-li-desc').html(desc)
			);
						
			link.append(activityText);
			
			parent.append(activityContainer);
			
			link.click(function(e) {
				clickCallback(my, activity.id);
			});
		};

		my.loadScheduledActivity = function(activityId, callback) {
			var activity = findActivityById(activityId);
			if(activity!=null) {
				callback(activity);
			}
		};
		
		function findActivityById(id) {
			var activity;
			var allActivities = due.concat(actual, reported);
			$.each(allActivities, function(index, act){
				if(act.id==id) {
					activity = act;
					return act;
				}
			});
			return activity;
		};

		my.loadActivity = function(my, activityId) {
			$.mobile.loading('show', {
				text : 'Laddar aktivitet',
				textVisible : true
			});
			
			my.loadScheduledActivity(activityId, function(activity) {
				NC.log(activity.id + '-' + activity.activityDefinition.healthPlanName);
				$('#reportForm').empty();
				$('#report div h3').html(activity.activityDefinition.type.name);
				$('#report div p').html(activity.day.value + ', ' + activity.date + ' ' + activity.time);

				var reported = (activity.reported != null);
				for (var i = 0; i < activity.activityItemValues.length; i++) {
					var item = activity.activityItemValues[i];
					if (!item.definition.active) {
						continue;
					}
					
					var templateName;
					if(item.definition.activityItemType.activityItemTypeName == 'measurement') {
						if(item.definition.activityItemType.valueType.code=='INTERVAL') {
							templateName = 'measurementIntervalItemTemplate';					
						} else {
							templateName = 'measurementSingleItemTemplate';					
						}
					} else if(item.definition.activityItemType.activityItemTypeName == 'estimation') {
						if(item.perceivedSense === null) {
							item.perceivedSense = item.definition.activityItemType.minScaleValue;
						}
						templateName = 'estimationItemTemplate';					
					} else if(item.definition.activityItemType.activityItemTypeName == 'yesno') {
						templateName = 'yesnoItemTemplate';					
					} else if(item.definition.activityItemType.activityItemTypeName == 'text') {
						templateName = 'textItemTemplate';					
					}  
					var myTemplate = my.templates[templateName];
					$('#reportForm').append(myTemplate(item));
				}
				
				/*
				 * Report value
				 */
				if(activity.reported != null) {
					$('#sendReport').hide();
				} else {
					$('#sendReport').show();
					$('#sendReport').on('tap', function(e) {
						$('#sendReport').unbind('tap');

						e.preventDefault();
						
						$.mobile.loading('show', {
							text : 'Skickar rapportering...',
							textVisible : true
						});
	
						var activityId = $('#activityId').val();
						var activity = findActivityById(activityId);
	
						var activityData = new Object();
						activityData.id = activity.id;
						activityData.due = activity.due;
						activityData.reported = activity.reported;
						activityData.date = activity.date;
						activityData.time = activity.time;
						activityData.actualTime = $('#date').val() + ' ' + $('#time').val();
						activityData.note = $('#note').val();
						activityData.rejected = false;
						activityData.activityItemValues = new Array();
						$.each(activity.activityItemValues,function(index, item) {
							var activityDataItem = new Object();
							activityDataItem.id = item.id;
							activityDataItem.valueType = item.valueType;
							activityDataItem.definition = item.definition;
							if(item.definition.activityItemType.activityItemTypeName == 'measurement') {
								activityDataItem.reportedValue = $('#measurement' + item.id).val();
								activityDataItem.target = item.target;
								activityDataItem.minTarget = item.minTarget;
								activityDataItem.maxTarget = item.maxTarget;
							} else if(item.definition.activityItemType.activityItemTypeName == 'estimation') {
								activityDataItem.perceivedSense = $('#slider' + item.id).val();
							} else if(item.definition.activityItemType.activityItemTypeName == 'yesno') {
								var yesno = $('[name="radio' + item.id + '"]').val();
								activityDataItem.answer = yesno !== undefined && yesno==='on';
							} else if(item.definition.activityItemType.activityItemTypeName == 'text') {
								activityDataItem.textComment = $('#text' + item.id).val();
							}
							activityData.activityItemValues.push(activityDataItem);
						});
						
						new NC.Ajax().post('/scheduledActivities/' + activityId, activityData, function(data) {
							if (data.success) {
								my.load(my, function() {
									console.log('load-after-post');
									$('#actual').click();
									$.mobile.changePage($("#start"), "slide");
								});
								var msg = $('<div>').addClass('ui-bar ui-bar-e pageMessage').append($('<h3>' + activity.activityDefinition.type.name + ' rapporterades</h3>'));
								$('#schema').before(msg);
								$.mobile.loading('hide');
								setTimeout(function() {msg.slideUp('fast');}, 3000);
							}
						});
					});
				}
				$('#goBack').on('tap', function(e) {
					$('#sendReport').unbind('tap');
				});
				$('#reportForm').append(my.templates['commonActivityItemTemplate'](activity));
				$('#reportForm').trigger('create');

        $('#date').datebox({ mode : 'calbox',
          lockInput : false,
          minDays : 7,
          beforeToday: true,
          useAnimation: false,
          themeHeader : 'c',
          overrideCalStartDay : 1
        });

        $('#time').datebox({
          mode : 'timeflipbox',
          lockInput: true
        });
				
				$.mobile.loading('hide');
			});

		};		
		
		my.validateNumericField = function(numericField, maxLen) {
			numericField.keypress(function(event) {
				if (numericField.val().length >= maxLen || 
						!((event.which == 46 && numericField.val().indexOf('.') == -1) || (event.which >= 48  && event.which <= 57))) { 
					event.preventDefault();
					numericField.css('background', '#F2DEDE');
					setTimeout(function() {
						numericField.css('background', 'white');												
					}, 100);
				} else if (numericField.css('background') != 'white') {
					numericField.css('background', 'white');
				}
			});
		};
		
		my.createReportField = function(id, parent, label, value) {
			var input = $('<input>').attr('type', 'number').attr('id', id).attr('name', name).attr('value', value).addClass('ui-input-text').addClass('ui-body-d').addClass('ui-corner-all').addClass('ui-shadow-inset');
			validateNumericField(input, 6);		
			var div = $('<div>').attr('id', id).attr('data-role', 'fieldcontain').append(
				$('<label>').attr('for', id).html(label).addClass('ui-input-text')
			).append(input);
			
			div.addClass('ui-field-contain').addClass('ui-body').addClass('ui-br');
			
			NC.log("Prepending input div");
			parent.prepend(div);
		};
			
		return my;
	})()
	
};
