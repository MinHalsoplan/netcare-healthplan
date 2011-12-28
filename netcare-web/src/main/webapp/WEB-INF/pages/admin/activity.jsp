<%--

    Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>

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
				 * Disable time input field
				 */
				$('input[name $= "TimeField"]').attr('disabled', 'disabled');
				
				/*
				 * If the user clicks a day checkbox, enable that time field
				 */
				$('input[name="day"]').click(function(event){
					var value = $(this).val() + 'TimeField';
					var enable = $(this).attr('checked');
					var elem = $('input[name="'+ value +'"]');
					
					if (enable == "checked") {
						// Enable add time field
						elem.removeAttr('disabled');
						elem.focus();	
					} else {
						elem.attr('disabled', 'disabled');
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
					var removeTime = util.createIcon('bullet_delete', function() {
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
				$('img[src*="bullet_add"]').click(function(event) {
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
				
				var units = new Array();
				var select = $('#activityForm select[name="activityType"]');
				
				var showUnit = function(optionElem) {
					console.log("Selected element: " + optionElem.val());
					$.each(units, function(index, value) {
						if (optionElem.html() === units[index].name) {
							$('span.unit').html('<strong>(' + units[index].unit.value + ')</strong>');
						}
					});
				};
				
				types.load(function(data) {
					var firstOption;
					$.each(data, function(index, value) {
						console.log("Processing: " + value.name);
						units[index] = value;
						
						var opt = $('<option>', { value : value.id }).html(value.name);
						if (index == 0) {
							firstOption = opt;
						}
						
						select.append(opt);
					});
					
					/*
					 * Display unit on the currently selected option
					 */
					showUnit(firstOption);
				});
				
				select.change(function() {
					var selected = $('#activityForm select option:selected');
					console.log("Selected element is: " + selected.html());
					
					showUnit(selected);
				});
				
				/*
				 * Bind the form submission and package what is going
				 * to be sent to the server as a JSON object
				 */
				$('#activityForm :submit').click(function(event) {
					console.log("Form submission...");
					event.preventDefault();
					
					var activityType = $('#activityForm select option:selected').val();
					var goal = $('#activityForm input[name="activityGoal"]').val();
					var startDate = $('input[name="startDate"]').val();
					
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
					
					var jsonObj = JSON.stringify(activity);
					console.log("JSON: " + jsonObj.toString());
					
					var hp = new NC.HealthPlan();
					hp.addActivity(healthPlan, jsonObj, function(data) {
						console.log("Success callback is executing...");
						console.log("Resetting form");
						resetForm();
					}, 'activitiesTable');
					
					$('#activityForm').hide();
					
				});
				
				$('#showActivityForm').click(function(event) {
					console.log("Displaying new activity form");
					$('#activityForm').toggle();
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
				<a id="showActivityForm" class="btn">
					<netcare:image name="bullet_add"/> <c:out value="${title}" />
				</a>
			</p>
			
			<netcare:form id="activityForm" classes="form-stacked">
			
				<fieldset>
					<legend><spring:message code="activity" /> <spring:message code="and" /> <spring:message code="goal" /></legend>
					<div class="row">
						<div class="span12">
							<div class="row">
								<div class="span3">
									<spring:message code="what" var="what" scope="page" />
									<netcare:field name="activityType" label="${what}">
										<select name="activityType" class="medium"></select>
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
										<netcare:image name="bullet_add" icon="true" cursor="pointer"/>
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
										<netcare:image name="bullet_add" icon="true"/>
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
										<netcare:image name="bullet_add" icon="true"/>
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
										<netcare:image name="bullet_add" icon="true"/>
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
										<netcare:image name="bullet_add" icon="true"/>
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
										<netcare:image name="bullet_add" icon="true"/>
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
										<netcare:image name="bullet_add" icon="true"/>
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