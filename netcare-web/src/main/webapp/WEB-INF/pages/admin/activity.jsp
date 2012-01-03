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
					console.log("Disabling weekdays");
					disableWeekdays();
					
					console.log("Remove added times");
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
					minDate : +0
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
						console.log("Delete time");
						addedTimeContainer.remove();
						
						var timeCount = $('#' + day + 'AddedTimes span').size();
						console.log("Times count: " + timeCount);
						
						// Hide if no more times
						if (timeCount == 0) {
							console.log("No more times, hide container");
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
					console.log("Icon clicked");
					var timeField = $(this).parent().children('input[name$="TimeField"]');
					addTime(timeField, timeField.val());
				});
				
				/*
				 * Start by loading existing activites
				 * and fill the table
				 */
				var healthPlan = <c:out value="${requestScope.result.data.id}" />;
				var patientId = <c:out value="${sessionScope.currentPatient.id}" />;
				var hp = new NC.HealthPlan();
				
				hp.listActivities(healthPlan, 'activitiesTable', function(data) {
					console.log("Callback executing...");
				});
				
				var types = NC.ActivityTypes();
				var showUnit = function(name) {
					console.log("Selected " + name);
					$('span.unit').html('<strong>(' + name + ')</strong>');
				}
				
				/*
				 * Auto complete activity type field
				 */
				$('input[name="activityType"]').autocomplete('option', {
					source : function(request, response) {
						types.search(request.term, function(data) {
							console.log("Found " + data.data.length + " activity types");
							response($.map(data.data, function(item) {
								return { label : item.name + ' (' + item.category.name + ', ' + item.unit.value + ')', value : item.name, unit : item.unit.value, id : item.id}
							}));
						});
					},
					select : function(event, ui) {
						showUnit(ui.item.unit);
						$('input[name="activityTypeId"]').attr('value', ui.item.id);
						$('input[name="activityGoal"]').focus();
					}
				});
				
				/*
				 * Bind the form submission and package what is going
				 * to be sent to the server as a JSON object
				 */
				$('#activityForm :submit').click(function(event) {
					console.log("Form submission...");
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
						
						console.log("Processing: " + dayTime.day);
						
						// Pick all times, if not times exist for a day. Just skip
						if ($('#' + dayTime.day + 'AddedTimes span').size() != 0) {
							$.each($('#' + dayTime.day + 'AddedTimes span'), function(index, value) {
								console.log($(value).html());
								dayTime.times[index] = $(value).html();
							});
							
							dayTimes.push(dayTime);	
						}
					});
					
					var at = new Object();
					at.id = activityType;
					
					var activity = new Object();
					activity.goal = goal;
					activity.type = at;
					activity.startDate = startDate;
					activity.dayTimes = dayTimes;
					activity.activityRepeat = activityRepeat;
					
					var jsonObj = JSON.stringify(activity);
					console.log("JSON: " + jsonObj.toString());
					
					var hp = new NC.HealthPlan();
					hp.addActivity(healthPlan, jsonObj, function(data) {
						console.log("Success callback is executing...");
						console.log("Resetting form");
						
						$('input:reset').click();
						
						resetForm();
						
					}, 'activitiesTable');
					
					$('#activityForm').hide();
					
				});
				
				$('#showActivityForm').click(function(event) {
					console.log("Displaying new activity form");
					$('#activityForm').toggle();
				});
				
				$('#activityForm').hide();
				
				/*
				 * Bind modal show event,
				 * When the modal is shown we want to download units as
				 * well as activity categories and fill the modal
				 * form
				 */
				$('#addNewType').bind('show', function() {
					console.log("Showing modal... fill form.");
					
					$('#addNewType select[name="unit"]').empty();
					new NC.Support().loadUnits($('#addNewType select[name="unit"]'));
					
					$('#addNewType select[name="category"]').empty();
					new NC.ActivityCategories().loadAsOptions($('#addNewType select[name="category"]'));
				});
				
				/*
				 * Save activity type when user submits the form
				 */
				$('#addNewType :submit').click(function(event) {
					console.log("User submitted new activity type form...");
					event.preventDefault();
					
					var name = $('#addNewType input[name="name"]').val();
					
					var category = new Object();
					category.id = $('#addNewType select[name="category"] option:selected').val();
					
					var unit = new Object();
					unit.code = $('#addNewType select[name="unit"] option:selected').val();
					
					console.log("Name: " + name);
					console.log("Category: " + category.id);
					console.log("Unit: " + unit.code);
					
					var formData = new Object();
					formData.name = name;
					formData.category = category;
					formData.unit = unit;
					
					var jsonObj = JSON.stringify(formData);
					new NC.ActivityTypes().create(jsonObj, function(data) {
						
						showUnit(data.data.unit.value);
						$('input[name="activityType"]').attr('value', data.data.name);
						$('input[name="activityTypeId"]').attr('value', data.data.id);
						$('input[name="activityGoal"]').focus();
						
						$('#addNewType').modal('hide');
					});
				});
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
			
			<div id="addNewType" class="modal hide fade" style="display: none;">
				<form id="addNewActivityTypeForm" class="form-stacked">
					<div class="modal-header">
						<a href="#" class="close">x</a>
						<h3><spring:message code="addActivityType" /></h3>
					</div>
					<div class="modal-body">
						<spring:message code="activityCategory" var="cat" scope="page" />
						<netcare:field name="category" label="${cat}">
							<select name="category" class="xlarge"></select>
						</netcare:field>
						
						<spring:message code="name" var="name" scope="page" />
						<netcare:field name="name" label="${name}">
							<input type="text" name="name" class="xlarge"/>
						</netcare:field>
						
						<spring:message code="unit" var="unit" scope="page" />
						<netcare:field name="unit" label="${unit}">
							<select name="unit" class="xlarge"></select>
						</netcare:field>
					</div>
					<div class="modal-footer">
						<input type="submit" value="<spring:message code="create" />" class="btn primary"/>
					</div>
				</form>
			</div>
			
			<netcare:form id="activityForm" classes="form-stacked">
			
				<fieldset>
					<legend><spring:message code="activity" /> <spring:message code="and" /> <spring:message code="goal" /></legend>
					<div class="row">
						<div class="span12">
							<div class="row">
								<div class="span3">
									<spring:message code="what" var="what" scope="page" />
									<netcare:field name="activityType" label="${what}">
										<input type="text" name="activityType" class="medium nc-autocomplete" />
										<input type="hidden" name="activityTypeId" />	
										<p><a data-backdrop="true" data-controls-modal="addNewType">Lägg till ny aktivitetstyp</a></p>
									</netcare:field>
								</div>
								<div class="span5">
									<spring:message code="goal" var="goal" scope="page"/>
									<netcare:field containerId="activityGoal" name="activityGoal" label="${goal}">
										<input name="activityGoal" type="number" min="0" max="52" class="medium" required/> <span class="unit"></span>
									</netcare:field>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
				
				<fieldset>
					<legend><spring:message code="schedule" /></legend>
					<div class="row">
						<div class="span12">
							<div class="row">
								<div class="span3">
									<spring:message code="startDate" var="start" scope="page" />
									<netcare:field containerId="startDate" name="startDate" label="${start}">
										<input name="startDate" type="text" class="medium"/>
									</netcare:field>
								</div>
								<div class="span5">
									<spring:message code="repeatSchedule" var="repeat" scope="page"/>
									<netcare:field name="activityRepeat" label="${repeat}">
										<input name="activityRepeat" type="number" class="medium" />
										<span><spring:message code="week" /></span>
									</netcare:field>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
				
				
				<spring:message code="addTime" var="addTime" scope="page" />
				
				<fieldset>
					<legend><spring:message code="pickDaysAndTimes" /></legend>
					<div id="mondayContainer" class="row">
						<div class="span12">
							<div class="row">
								<div class="span1">
									<spring:message code="monday" var="monday" scope="page" />
									<netcare:field name="day" label="${monday}">
										<input type="checkbox" name="day" value="monday"/>
									</netcare:field>
								</div>
								<div class="span5">
									<netcare:field name="mondayTimeField" label="${addTime}">
										<input type="text" name="mondayTimeField" class="medium" />
										<netcare:image name="add" icon="true" cursor="pointer"/>
									</netcare:field>
								</div>
								<div id="mondayAddedTimes" class="span6" style="display: none">
									<p><strong><spring:message code="times" /></strong></p>
								</div>
							</div>
						</div>
					</div>
					
					
					<div id="tuesdayContainer" class="row">
						<div class="span12">
							<div class="row">
								<div class="span1">
									<spring:message code="tuesday" var="tuesday" scope="page" />
									<netcare:field name="day" label="${tuesday}">
										<input type="checkbox" name="day" value="tuesday"/>
									</netcare:field>
								</div>
								<div class="span5">
									<netcare:field name="tuesdayTimeField" label="${addTime}">
										<input type="text" name="tuesdayTimeField" class="medium" />
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
						<div class="span12">
							<div class="row">
								<div class="span1">
									<spring:message code="wednesday" var="wednesday" scope="page" />
									<netcare:field name="day" label="${wednesday}">
										<input type="checkbox" name="day" value="wednesday"/>
									</netcare:field>
								</div>
								<div class="span5">
									<netcare:field name="wednesdayTimeField" label="${addTime}">
										<input type="text" name="wednesdayTimeField" class="medium" />
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
						<div class="span12">
							<div class="row">
								<div class="span1">
									<spring:message code="thursday" var="thursday" scope="page" />
									<netcare:field name="day" label="${thursday}">
										<input type="checkbox" name="day" value="thursday"/>
									</netcare:field>
								</div>
								<div class="span5">
									<netcare:field name="thursdayTimeField" label="${addTime}">
										<input type="text" name="thursdayTimeField" class="medium" />
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
						<div class="span12">
							<div class="row">
								<div class="span1">
									<spring:message code="friday" var="friday" scope="page" />
									<netcare:field name="day" label="${friday}">
										<input type="checkbox" name="day" value="friday"/>
									</netcare:field>
								</div>
								<div class="span5">
									<netcare:field name="fridayTimeField" label="${addTime}">
										<input type="text" name="fridayTimeField" class="medium" />
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
						<div class="span12">
							<div class="row">
								<div class="span1">
									<spring:message code="saturday" var="saturday" scope="page" />
									<netcare:field name="day" label="${saturday}">
										<input type="checkbox" name="day" value="saturday"/>
									</netcare:field>
								</div>
								<div class="span5">
									<netcare:field name="saturdayTimeField" label="${addTime}">
										<input type="text" name="saturdayTimeField" class="medium" />
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
						<div class="span12">
							<div class="row">
								<div class="span1">
									<spring:message code="sunday" var="sunday" scope="page" />
									<netcare:field name="day" label="${sunday}">
										<input type="checkbox" name="day" value="sunday"/>
									</netcare:field>
								</div>
								<div class="span5">
									<netcare:field name="sundayTimeField" label="${addTime}">
										<input type="text" name="sundayTimeField" class="medium" />
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
				
			
				<div class="actions">
					<spring:message code="create" var="create" scope="page" />
					<spring:message code="clear" var="clear" scope="page" />
				
					<input type="submit" class="btn primary" value="${create}"/>
					<input type="reset" class="btn" value="${clear}"/>
				</div>
			
			</netcare:form>
			
			<table id="activitiesTable" class="bordered-table zebra-striped">
				<thead>
					<tr>
						<th><spring:message code="type" /></th>
						<th><spring:message code="goal" /></th>
						<th>&nbsp;</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
			
		</netcare:content>
		<netcare:menu />
	</netcare:body>
</netcare:page>