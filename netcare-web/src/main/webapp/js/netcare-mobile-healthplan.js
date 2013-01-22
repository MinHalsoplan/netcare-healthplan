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
var NC_MOBILE = {

	ACTIVITIES : (function() {
		var my = {};
		
		var templateNames;
		var templates;
		
		var due;
		var actual;
		var reported;

		
		my.init = function(templateNames, reportedLabel) {
			var that = this;
			this.templateNames = templateNames;
			this.reportedLabel = reportedLabel;
			this.templates = {};
			
			my.pageInit(that);
		};
		
		my.pageInit = function(my) {
			$('#start').live('pageinit', function(e) {
				my.precompileTemplates(my);
				my.load(my, setupGUI);
			});
		};

		my.precompileTemplates = function(my) {
			$.each(this.templateNames, function(index, name) {
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

					NC.log('Id: ' + value.id + " Reported: " + value.reported
							+ " Due: " + value.due);

					if (value.reported != null) {
						NC.log("Pushing " + value.id + " to reported");
						reported.push(value);
					} else if (value.reported == null && value.due) {
						NC.log("Pushing " + value.id + " to due");
						due.push(value);
					} else {
						NC.log("Pushing " + value.id + " to actual");
						actual.push(value);
					}
				});

				callback(my);
			});
		};

		var setupGUI = function(my) {
			NC.log("Done fetching data.");

			$('#actual').click(function(e) {
				NC.log("Loading actual activities...");
				my.buildFromArray(my, actual);
			});

			$('#due').click(function(e) {
				NC.log("Loading due activities...");
				my.buildFromArray(my, due);
			});

			$('#reported').click(function(e) {
				NC.log("Loading reported activities...");
				my.buildFromArray(my, reported);
			});

			$('#actual').click();
		};
		
		my.buildFromArray = function(my, ListOfActivities) {
			var currentDay = '';

			$('#schema').empty();

			$.each(ListOfActivities, function(index, activity) {
				NC.log("Processing " + activity.id + " ...");
				
				var date = activity.reported != null ? activity.reportedDate : activity.date;
				
				if (currentDay != date) {
					currentDay = date;
					my.buildListView(my, activity, true);
				} else {
					my.buildListView(my, activity, false);
				}
			});
		};

		my.buildListView = function(my, activity, buildHeader) {
			if (buildHeader) {
				var day = activity.reported != null ? activity.reportedDay.value : activity.day.value;
				var date = activity.reported != null ? activity.reportedDate : activity.date;
				
				my.createListHeader($('#schema'), day + ' ' + date);
			}
			my.createListRow(my, $('#schema'), '#report', activity, my.loadActivity, reportedLabel);
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
			NC.log("Load activity: " + activityId);
			
			my.loadScheduledActivity(activityId, function(activity) {
				NC.log(activity.id + '-' + activity.activityDefinition.healthPlanName);
				$('#reportForm').empty();
				$('#report div h3').html(activity.activityDefinition.type.name);
				$('#report div p').html(activity.day.value + ', ' + activity.date + ' ' + activity.time);

				var reported = (activity.reported != null);
				$.each(activity.activityItemValues,function(index, item) {
					var templateName;
					if(item.definition.activityItemType.activityItemTypeName == 'measurement') {
						if(item.definition.activityItemType.valueType.code=='INTERVAL') {
							templateName = 'measurementIntervalItemTemplate';					
						} else {
							templateName = 'measurementSingleItemTemplate';					
						}
					} else if(item.definition.activityItemType.activityItemTypeName == 'estimation') {
						templateName = 'estimationItemTemplate';					
					} else if(item.definition.activityItemType.activityItemTypeName == 'yesno') {
						templateName = 'yesnoItemTemplate';					
					} else if(item.definition.activityItemType.activityItemTypeName == 'text') {
						templateName = 'textItemTemplate';					
					}  
					var myTemplate = my.templates[templateName];
					$('#reportForm').append(myTemplate(item));
				});
				$('#reportForm').append(my.templates['commonActivityItemTemplate'](activity));
				$('#reportForm').trigger('create'); // Init jQuery Mobile controls
			});

			/*
			 * Report value
			 */
			$('#sendReport').click(function(e) {
				$.mobile.showPageLoadingMsg();
				e.preventDefault();


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
					if(item.definition.activityItemType.activityItemTypeName == 'measurement') {
						activityDataItem.reportedValue = $('#measurement' + item.id).val();
						activityDataItem.target = item.target;
						activityDataItem.minTarget = item.minTarget;
						activityDataItem.maxTarget = item.maxTarget;
					} else if(item.definition.activityItemType.activityItemTypeName == 'estimation') {
						activityDataItem.perceivedSense = $('#slider' + item.id).val();
					} else if(item.definition.activityItemType.activityItemTypeName == 'yesno') {
						activityDataItem.answer = $('#slider' + item.id).val();
					} else if(item.definition.activityItemType.activityItemTypeName == 'text') {
						activityDataItem.textComment = $('#text' + item.id).val();
					}
					activityData.activityItemValues.push(activityDataItem);
				});
				new NC.Ajax().post('/scheduledActivities/' + activityId, activityData, function(data) {
					if(data.success) {
						my.load(my, setupGUI);
						var msg = $('<div>').addClass('ui-bar').addClass('ui-bar-c').append($('<h3>' + data.successMessages[0].message + '</h3>'));
						$('#schema').before(msg);
						$.mobile.hidePageLoadingMsg();
						setTimeout(function() {msg.slideUp('slow');}, 5000);
					}
				});
				$('#sendReport').unbind('click');
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
		
		my.createListRow = function(my, parent, href, value, clickCallback, reportedLabel) {
			if (!value.activityDefinition.active) {
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
			
			var link = $('<a>').attr('href', '#report').attr('data-transition', 'slide').addClass('ui-link-inherit');
			activityContentDiv.append(link);
			
//			link.append(
//				$('<p><strong>' + value.time + '</strong></p>').addClass('ui-li-aside').addClass('ui-li-desc')
//			);
			
			var activityText = $('<div>').addClass('ui-btn-text');
			var time = value.reported != null ? value.reportedTime : value.time;
			activityText.append(
				$('<h3>' + time + ' ' + value.activityDefinition.type.name + '</h3>').addClass('ui-li-heading')
			);
			
			var desc = '';
			$.each(value.activityItemValues, function(i, activityItem) {
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
			});
			
			activityText.append(
				$('<p>').addClass('ui-li-desc').html(desc)
			);
						
			link.append(activityText);
			
			parent.append(activityContainer);
			
			link.click(function(e) {
				clickCallback(my, value.id);
			});
		};
		
		return my;
	})()
	
};
