<%--

    Copyright (C) 2011,2012 Callista Enterprise AB <info@callistaenterprise.se>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			$(function() {
				var util = new NC.Util();
				var support = new NC.Support();
				
				/*
				 * Are we a patient or are a care giver
				 */
				var isPatient = util.isPatient('<c:out value="${sessionScope.currentPatient}" />');
				
				/*
				 * Disable the weekdays as well as its
				 * add icon
				 */
				var disableWeekdays = function() {
					$('input[name $= "TimeField"]').attr('disabled', 'disabled');
					$('input[name $= "TimeField"]').next().hide();
				}
				
				/*
				 * Method that will completely reset the
				 * new schedule activity form
				 */ 
				var resetForm = function() {
					NC.log("Disabling weekdays");
					disableWeekdays();
					
					NC.log("Remove added times");
					var rootId = 'div[id$=AddedTimes]';
					$(rootId + ' div').remove();
					$(rootId).hide();
				}
				
				/*
				 * Create date picker for start date
				 */
				$('#startDate input[name="startDate"]').datepicker({
					dateFormat : 'yy-mm-dd',
					firstDay : 1,
					minDate : +0,
					buttonImage : '/netcare-web/img/icons/16/date.png'
				});
				
				support.loadMonths(function(data) {
					$('#startDate input[name="startDate"]').datepicker('option', 'monthNames', data);
				});
				
				support.loadWeekdays(function(data) {
					$('#startDate input[name="startDate"]').datepicker('option', 'dayNamesMin', data);
				});
				
				/*
				 * Disable time input field as well as "add" icon next to it
				 */
				disableWeekdays();
				
				/*
				 * If the user clicks a day checkbox, enable that time field
				 */
				$('input[name="day"]').click(function(event){
					var value = $(this).val() + 'TimeField';
					var enable = $(this).attr('checked');
					var elem = $('input[name="'+ value +'"]');
					var icon = elem.next();
					
					if (enable == "checked") {
						// Enable add time field
						elem.removeAttr('disabled');
						elem.focus();
						
						// Show icon
						icon.show();
					} else {
						elem.attr('disabled', 'disabled');
						icon.hide();
					}
				});
				
				/*
				 * When the user presses enter or on the add icon, get the value of the
				 * time field and print it together with a delete icon
				 */
				var addTime = function(timeField, text) {
					// Find added times div
					var inputName = timeField.attr('name');
					
					// Rip off the 'TimeField' to get the day
					var day = inputName.slice(0, inputName.length - 9);
					
					// Add time
					var elem = $('#' + day + 'AddedTimes');
					
					var addedTimeContainer = $('<div>').css('padding-right', '10px').css('float', 'left');
					var addedTime = $('<span>' + text + '</span>');
					var removeTime = util.createIcon('trash', 16, function() {
						NC.log("Delete time");
						addedTimeContainer.remove();
						
						var timeCount = $('#' + day + 'AddedTimes span').size();
						NC.log("Times count: " + timeCount);
						
						// Hide if no more times
						if (timeCount == 0) {
							NC.log("No more times, hide container");
							elem.hide();
						}
					});
					
					addedTimeContainer.append(addedTime);
					addedTimeContainer.append(removeTime);
					
					elem.append(addedTimeContainer);
					elem.show();
					
					timeField.val('');
					timeField.focus();
				};
				
				/*
				 * Bind all time fields as validated time fields.
				 * The function we pass is executed when the user presses enter
				 * in the field
				 */
				$.each($('input[name$="TimeField"]'), function(index, value) {
					util.validateTimeField($(value), function(text) {
						addTime($(value), text);
					});
				});
				
				/*
				 * Add the time when user presses the image icon
				 */
				$('img[src*="add"]').click(function(event) {
					NC.log("Icon clicked");
					var timeField = $(this).parent().children('input[name$="TimeField"]');
					addTime(timeField, timeField.val());
				});
				
				/*
				 * Start by loading existing activites
				 * and fill the table
				 */
				var healthPlan = <c:out value="${requestScope.result.data.id}" />;
				var hp = new NC.HealthPlan();
				
				hp.listActivities(healthPlan, 'activitiesTable', isPatient);
				
				
				var addMeasureValueInput = function(id, rowCol, label, value) {
					var clearfix = $('<div>').addClass('clearfix');
					clearfix.append(
						$('<label>').attr('for', id).html(label)
					);
					
					var inputDiv = $('<div>').addClass('input');
					var input = $('<input>').attr('type', 'number').attr('step', '1').attr('name', id).attr('id', id).addClass('small');
					
					inputDiv.append(input);
					inputDiv.append($('<span>').html(' ' + value.unit.value));
					
					clearfix.append(inputDiv);
					
					rowCol.append(
						$('<div>').addClass('span3').append(clearfix)
					);
				};
				
				var types = NC.ActivityTypes();
				
				/*
				 * Auto complete activity type field
				 */
				$('input[name="activityType"]').autocomplete('option', {
					source : function(request, response) {
						types.search(request.term, function(data) {
							NC.log("Found " + data.data.length + " activity types");
							response($.map(data.data, function(item) {
								return { label : item.name + ' (' + item.category.name + ')', value : item.name, data : item}
							}));
						});
					},
					select : function(event, ui) {
						
						$('input[name="activityTypeId"]').attr('value', ui.item.data.id);
						$('#measureValues').remove();
						
						var minValue = '';
						var maxValue = '';
						var value = '';
						var title = '';
						new NC.Support().loadCaptions('result', ['targetMinValue', 'targetMaxValue', 'value', 'title'], function(data) {
							minValue = data.targetMinValue;
							maxValue = data.targetMaxValue;
							value = data.value;
						});
						
						/*
						 * Loop through all measure values
						 */
						var data = ui.item.data;
						NC.log("Selected data is: " + data);
						
						var fieldset = $('<fieldset>').attr('id', 'measureValues');
						fieldset.append(
							$('<legend>').html(title)
						);
						
						$.each(data.measureValues, function(i, v) {
							NC.log("Processing " + v.name);
							
							var rowCol = $('<div>').addClass('row').css('background', '#FDF5D9');
							var row = $('<div>').addClass('row').append(
								$('<div>').addClass('span10').append(
									rowCol
								)
							);
							
							rowCol.append(
								$('<div>').addClass('span2').append(
									$('<div>').addClass('clearfix').append(
										$('<label>').html('&nbsp')
									).append(
										$('<span>').css('vertical-align', 'middle').append($('<strong>').html(v.name)) 
									)
								)
							);
							
							if (v.valueType.code == "INTERVAL") {
								
								addMeasureValueInput(v.id + '-1', rowCol, minValue, v);
								addMeasureValueInput(v.id + '-2', rowCol, maxValue, v);
								
							} else if (v.valueType.code == "SINGLE_VALUE") {
								
								addMeasureValueInput(v.id + '-1', rowCol, value, v);
								
							} else {
								throw new Error("Unsupported value type");
							}
							
							fieldset.append(row);
							
						});
						
						$('#activityFieldset').append(fieldset);
						
						/*
						 * Move focus to the first measure value
						 */
						$('#measureValues input').get(0).focus();
					}
				});
				
				/*
				 * Bind the form submission and package what is going
				 * to be sent to the server as a JSON object
				 */
				$('#activityForm :submit').click(function(event) {
					NC.log("Form submission...");
					event.preventDefault();
					
					var activityType = $('input[name="activityTypeId"]').val();
					var goal = $('#activityForm input[name="activityGoal"]').val();
					var startDate = $('input[name="startDate"]').val();
					var activityRepeat = $('input[name="activityRepeat"]').val();
					
					var dayTimes = new Array();
					$.each($('#activityForm input[name="day"]:checked'), function(index, value) {
						var dayTime = new Object();
						dayTime.times = new Array();
						dayTime.day = $(value).attr('value');
						
						NC.log("Processing: " + dayTime.day);
						
						// Pick all times, if not times exist for a day. Just skip
						if ($('#' + dayTime.day + 'AddedTimes span').size() != 0) {
							$.each($('#' + dayTime.day + 'AddedTimes span'), function(index, value) {
								NC.log($(value).html());
								dayTime.times[index] = $(value).html();
							});
							
							dayTimes.push(dayTime);	
						}
					});
					
					var at = new Object();
					at.id = activityType;
					
					var measureValues = new Array();
					var processed = 0;
					$.each($('#measureValues input'), function(i, v) {
						var attr = $(v).attr('id');
						var id = attr.substr(0, 1);
						
						NC.log("Checking " + id + " with " + processed + " to determine whether to process...");
						if (id != processed) {
							var measure = new Object();
							measure.measurementType = new Object();
							measure.measurementType.id = id;
							
							var value1 = $(v).val();
							var value2 = $('#measureValues input[id="'+ id +'-2"]').val();
							if (typeof value2 === 'undefined') {
								measure.target = value1;
							} else {
								measure.minTarget = value1;
								measure.maxTarget = value2;
							} 
							
							measureValues.push(measure);
							processed = id;
						}
					});
					
					
					var activity = new Object();
					activity.goalValues = measureValues;
					activity.type = at;
					activity.startDate = startDate;
					activity.dayTimes = dayTimes;
					activity.activityRepeat = activityRepeat;
					
					if (!isPatient) {
						activity.publicDefinition = true;
					} else {
						if ($('#publicDefinition').attr('checked') == "checked") {
							activity.publicDefinition = true;
						} else {
							activity.publicDefinition = false;
						}
					}
					
					NC.log("Public definition: " + activity.publicDefinition);
					
					var jsonObj = JSON.stringify(activity);
					NC.log("JSON: " + jsonObj.toString());
					
					var hp = new NC.HealthPlan();
					hp.addActivity(healthPlan, jsonObj, function(data) {
						NC.log("Success callback is executing...");
						NC.log("Resetting form");
						
						$('input:reset').click();
						
						resetForm();
						
						hp.listActivities(healthPlan, 'activitiesTable', isPatient)
						
					});
					
					$('#activityForm').hide();
					
				});
				
				$('#showActivityForm').click(function(event) {
					NC.log("Displaying new activity form");
					$('#activityForm').toggle();
					
					if (isPatient != true) {
						$('#publicDefinitionContainer').hide();
					}
				});
				
				$('#activityForm').hide();
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2><c:out value="${requestScope.result.data.name}" /> : <spring:message code="activities" /></h2>
			<p>
				<span class="label notice">Information</span>
				Den här sidan låter dig schemalägga aktiviteter som ingår i hälsoplanen. Du anger
				dagar samt tider som aktiviteten skall utföras.
			</p>
			
			<spring:message code="newActivity" var="title" scope="page" />
			
			<p style="text-align: right; padding-right: 20px">
				<a id="showActivityForm" class="btn addButton"><c:out value="${title}" /></a>
			</p>
			
			<netcare:form id="activityForm" classes="form-stacked">
			
				<fieldset id="activityFieldset">
					<legend><spring:message code="activity" /> <spring:message code="and" /> <spring:message code="goal" /></legend>
					<netcare:row>
						<netcare:col span="3">
							<spring:message code="what" var="what" scope="page" />
							<netcare:field name="activityType" label="${what}">
								<input type="text" name="activityType" class="medium nc-autocomplete" />
								<input type="hidden" name="activityTypeId" />	
								<p><a href="<c:url value="/netcare/admin/activitytypes" />">Lägg till ny aktivitetstyp</a></p>
							</netcare:field>
						</netcare:col>
					</netcare:row>
				</fieldset>
				
				<fieldset id="scheduleFieldset">
					<legend><spring:message code="schedule" /></legend>
					<netcare:row>
						<netcare:col span="3">
							<spring:message code="startDate" var="start" scope="page" />
							<netcare:field containerId="startDate" name="startDate" label="${start}">
								<input name="startDate" type="text" class="medium"/>
							</netcare:field>
						</netcare:col>
						<netcare:col span="5">
							<spring:message code="repeatSchedule" var="repeat" scope="page"/>
							<netcare:field name="activityRepeat" label="${repeat}">
								<input name="activityRepeat" type="number" class="medium" />
								<span><spring:message code="week" /></span>
							</netcare:field>
						</netcare:col>
					</netcare:row>
				</fieldset>
				
				
				<spring:message code="addTime" var="addTime" scope="page" />
				
				<fieldset>
					<legend><spring:message code="pickDaysAndTimes" /></legend>
					<netcare:row id="mondayContainer">
						<netcare:col span="1">
							<spring:message code="monday" var="monday" scope="page" />
							<netcare:field name="day" label="${monday}">
								<input type="checkbox" name="day" value="monday"/>
							</netcare:field>
						</netcare:col>
						<netcare:col span="3">
							<netcare:field name="mondayTimeField" label="${addTime}">
								<input type="text" name="mondayTimeField" class="small" />
								<netcare:image name="add" icon="true" cursor="pointer"/>
							</netcare:field>
						</netcare:col>
						<div id="mondayAddedTimes" class="span6" style="display: none">
							<p><strong><spring:message code="times" /></strong></p>
						</div>
					</netcare:row>
					
					<div id="tuesdayContainer" class="row">
						<div class="span10">
							<div class="row">
								<div class="span1">
									<spring:message code="tuesday" var="tuesday" scope="page" />
									<netcare:field name="day" label="${tuesday}">
										<input type="checkbox" name="day" value="tuesday"/>
									</netcare:field>
								</div>
								<div class="span3">
									<netcare:field name="tuesdayTimeField" label="${addTime}">
										<input type="text" name="tuesdayTimeField" class="small" />
										<netcare:image name="add" icon="true"/>
									</netcare:field>
								</div>
								<div id="tuesdayAddedTimes" class="span6" style="display: none">
									<p><strong><spring:message code="times" /></strong></p>
								</div>
							</div>
						</div>
					</div>
					
					
					<div id="wednesdayContainer" class="row">
						<div class="span10">
							<div class="row">
								<div class="span1">
									<spring:message code="wednesday" var="wednesday" scope="page" />
									<netcare:field name="day" label="${wednesday}">
										<input type="checkbox" name="day" value="wednesday"/>
									</netcare:field>
								</div>
								<div class="span3">
									<netcare:field name="wednesdayTimeField" label="${addTime}">
										<input type="text" name="wednesdayTimeField" class="small" />
										<netcare:image name="add" icon="true"/>
									</netcare:field>
								</div>
								<div id="wednesdayAddedTimes" class="span6" style="display: none">
									<p><strong><spring:message code="times" /></strong></p>
								</div>
							</div>
						</div>
					</div>
					
					
					<div id="thursdayContainer" class="row">
						<div class="span10">
							<div class="row">
								<div class="span1">
									<spring:message code="thursday" var="thursday" scope="page" />
									<netcare:field name="day" label="${thursday}">
										<input type="checkbox" name="day" value="thursday"/>
									</netcare:field>
								</div>
								<div class="span3">
									<netcare:field name="thursdayTimeField" label="${addTime}">
										<input type="text" name="thursdayTimeField" class="small" />
										<netcare:image name="add" icon="true"/>
									</netcare:field>
								</div>
								<div id="thursdayAddedTimes" class="span6" style="display: none">
									<p><strong><spring:message code="times" /></strong></p>
								</div>
							</div>
						</div>
					</div>
					
					
					<div id="fridayContainer" class="row">
						<div class="span10">
							<div class="row">
								<div class="span1">
									<spring:message code="friday" var="friday" scope="page" />
									<netcare:field name="day" label="${friday}">
										<input type="checkbox" name="day" value="friday"/>
									</netcare:field>
								</div>
								<div class="span3">
									<netcare:field name="fridayTimeField" label="${addTime}">
										<input type="text" name="fridayTimeField" class="small" />
										<netcare:image name="add" icon="true"/>
									</netcare:field>
								</div>
								<div id="fridayAddedTimes" class="span6" style="display: none">
									<p><strong><spring:message code="times" /></strong></p>
								</div>
							</div>
						</div>
					</div>
					
					
					<div id="saturdayContainer" class="row">
						<div class="span10">
							<div class="row">
								<div class="span1">
									<spring:message code="saturday" var="saturday" scope="page" />
									<netcare:field name="day" label="${saturday}">
										<input type="checkbox" name="day" value="saturday"/>
									</netcare:field>
								</div>
								<div class="span3">
									<netcare:field name="saturdayTimeField" label="${addTime}">
										<input type="text" name="saturdayTimeField" class="small" />
										<netcare:image name="add" icon="true"/>
									</netcare:field>
								</div>
								<div id="saturdayAddedTimes" class="span6" style="display: none">
									<p><strong><spring:message code="times" /></strong></p>
								</div>
							</div>
						</div>
					</div>
					
					
					<div id="sundayContainer" class="row">
						<div class="span10">
							<div class="row">
								<div class="span1">
									<spring:message code="sunday" var="sunday" scope="page" />
									<netcare:field name="day" label="${sunday}">
										<input type="checkbox" name="day" value="sunday"/>
									</netcare:field>
								</div>
								<div class="span3">
									<netcare:field name="sundayTimeField" label="${addTime}">
										<input type="text" name="sundayTimeField" class="small" />
										<netcare:image name="add" icon="true"/>
									</netcare:field>
								</div>
								<div id="sundayAddedTimes" class="span6" style="display: none">
									<p><strong><spring:message code="times" /></strong></p>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
				
				<netcare:row id="publicDefinitionContainer">
					<netcare:col span="5">
						<netcare:field name="publicDefinition" label="Ska vårdgivare få ta del av denna aktiviteten?">
							<input type="checkbox" name="publicDefinition" value="true" checked="checked"/>
						</netcare:field>
					</netcare:col>
				</netcare:row>
			
				<div class="actions">
					<spring:message code="create" var="create" scope="page" />
					<spring:message code="clear" var="clear" scope="page" />
				
					<input type="submit" class="btn primary" value="${create}"/>
					<input type="reset" class="btn" value="${clear}"/>
				</div>
			
			</netcare:form>
			
			<div id="activityContainer">
				<div class="alert-message info" style="display: none;">
					<p><spring:message code="noActivities" />
				</div>
				<table id="activitiesTable" class="bordered-table zebra-striped shadow-box">
					<thead>
						<tr>
							<th><spring:message code="type" /></th>
							<th><spring:message code="activityCategory" /></th>
							<th><spring:message code="startDate" /></th>
							<th><spring:message code="phome.frequency" /></th>
							<th>&nbsp;</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
			</div>
		</netcare:content>
	</netcare:body>
</netcare:page>