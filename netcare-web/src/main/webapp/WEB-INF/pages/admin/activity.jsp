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
				var clearErrors = function() {
					$('#addTimeContainer').removeClass('error');
					$('#activityGoal').removeClass('error');
				};
				
				var hideTimeContainer = function() {
					$('#addedTimesContainer').hide();
					$('#addTimeContainer').hide();
				};
				
				var resetForm = function() {
					/* Remove times */
					hideTimeContainer();
					clearErrors();
					
					$('#addedTimes ul').empty();
					$('#activityDefinitionForm input[type="reset"]').click();
				};
				
				var healthPlan = <c:out value="${requestScope.result.data.id}" />
				var types = NC.ActivityTypes();
				
				var units = new Array();
				var select = $('#activityDefinitionForm select[name="activityType"]');
				
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
					var selected = $('#activityDefinitionForm select option:selected');
					console.log("Selected element is: " + selected.html());
					
					showUnit(selected);
				});
				
				$('#activityDefinitionForm input[name="day"]').click(function(event) {
					/*
					 * Is any day checked?
					 */ 
					var size = $('#activityDefinitionForm input[name="day"]:checked').size();
					if (size == 0) {
						hideTimeContainer();
					} else {
						$('#addTimeContainer').fadeIn().show();
						
						/*
						 * Have we added any times?
						 */
						var timeCount = $('#addedTimes li').size();
						console.log("Times count: " + timeCount);
						if (timeCount > 0) {
							$('#addedTimesContainer').fadeIn().show();
						}
					}
				});
				
				var util = new NC.Util();
				
				/*
				 * When the user leaves the goal without enter
				 * any value we need to decorate the field
				 * as erronous
				 */
				util.bindNotEmptyField($('#activityGoal'), $('#activityGoal input[name="activityGoal"]'));
				util.bindNotEmptyField($('#addTimeContainer'), $('#addTimeContainer input[name="addTime"]'));
				
				var addTimeField = $('#activityDefinitionForm input[name="addTime"]');
				
				util.validateTimeField(addTimeField, function(text) {
					var liElem = $('<li>');
					var spanElem = $('<span>').html(text);
					var deleteIcon = util.createIcon('bullet_delete', function() {
						liElem.detach();
						
						/*
						 * If we dont have any times left. Don't
						 * show the container
						 */
						if ($('#addedTimes li').size() == 0) {
							$('#addedTimesContainer').hide();
						}
						
					});
					
					liElem.append(spanElem);
					liElem.append(deleteIcon);
					
					$('#addedTimes').append(liElem);
					addTimeField.val('');
					
					/*
					 * Show the container since we just added
					 * a time
					 */
					$('#addedTimesContainer').show();
					clearErrors();
				});
				
				/*
				 * Bind the form submission and package what is going
				 * to be sent to the server as a JSON object
				 */
				$('#activityDefinitionForm :submit').click(function(event) {
					console.log("Form submission...");
					event.preventDefault();
					
					var activityType = $('#activityDefinitionForm select option:selected').val();
					var goal = $('#activityDefinitionForm input[name="activityGoal"]').val();
					
					var selectedDays = $('#activityDefinitionForm input[name="day"]:checked');
					var days = new Array();
					$.each(selectedDays, function(index, value) {
						days[index] = value.value;
					});
					
					var addedTimes = $('#addedTimes > li > span');
					console.log("Added times size: " + addedTimes.size());
					var times = new Array();
					$.each(addedTimes, function(index, value) {
						/*
						 * Note: we get the native htmlspanelement object
						 * here so we need to wrap it within a jquery object
						 */
						times[index] = $(value).html();
					});
					
					console.log("healthPlan " + healthPlan);
					console.log("Activity type: " + activityType);
					console.log("Activity goal: " + goal);
					console.log("Days: " + days);
					console.log("Times: " + times);
					
					var at = new Object();
					at.id = activityType;
					
					var activity = new Object();
					activity.goal = goal;
					activity.type = at;
					activity.days = days;
					activity.times = times;
					
					var jsonObj = JSON.stringify(activity);
					console.log("JSON: " + jsonObj.toString());
					
					var hp = new NC.HealthPlan();
					hp.addActivity(healthPlan, jsonObj, function(data) {
						console.log("Success callback is executing...");
						/* Success, clear form */
						console.log("Resetting form");
						resetForm();
					});
					
				});
				
				hideTimeContainer();
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2>Aktivitetsdefinition</h2>
			<p>
				Den här sidan låter dig schemalägga en hälsoplan. Du kan ange vilka dagar samt vilka tider som aktiviteten
				skall utföras.
			</p>
			
			<netcare:form title="Ordination" id="activityDefinitionForm" classes="form-stacked">
			
				<netcare:field name="activityType" label="Vad">
					<select name="activityType" class="xlarge"></select>
				</netcare:field>
				
				<netcare:field containerId="activityGoal" name="activityGoal" label="Målsättning">
					<input name="activityGoal" type="number" class="xlarge" /> <span class="unit"></span>
				</netcare:field>
				
				<div class="row">
					<div class="span7">
						<div class="row">
							<spring:message code="monday" var="monday" scope="page" />
							<spring:message code="tuesday" var="tuesday" scope="page" />
							<spring:message code="wednesday" var="wednesday" scope="page" />
							<spring:message code="thursday" var="thursday" scope="page" />
							<spring:message code="friday" var="friday" scope="page" />
							<spring:message code="saturday" var="saturday" scope="page" />
							<spring:message code="sunday" var="sunday" scope="page" />
						
						
							<div class="span1">
								<netcare:field name="day" label="${monday}">
									<input type="checkbox" name="day" value="0"/>
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="day" label="${tuesday}">
									<input type="checkbox" name="day" value="1"/>
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="day" label="${wednesday}">
									<input type="checkbox" name="day" value="2"/>
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="day" label="${thursday}">
									<input type="checkbox" name="day" value="3"/>
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="day" label="${friday}">
									<input type="checkbox" name="day" value="4"/>
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="day" label="${saturday}">
									<input type="checkbox" name="day" value="5"/>
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="day" label="${sunday}">
									<input type="checkbox" name="day" value="6"/>
								</netcare:field>
							</div>
						</div>
					</div>
				</div>
				
				<spring:message code="addTime" var="addTime" scope="page" />
				<netcare:field containerId="addTimeContainer" name="addTime" label="${addTime}">
					<input type="text" name="addTime" class="xlarge" /> <span><strong>(Ex: 10:15)</strong></span>
				</netcare:field>
				
				<div id="addedTimesContainer" class="clearfix">
					<p><strong><spring:message code="times" /></strong></p>
					<ul id="addedTimes">
					
					</ul>
				</div>
			
				<div class="actions">
					<spring:message code="create" var="create" scope="page" />
					<spring:message code="clear" var="clear" scope="page" />
				
					<input type="submit" class="btn primary" value="${create}"/>
					<input type="reset" class="btn" value="${clear}"/>
				</div>
			
			</netcare:form>
			
		</netcare:content>
		<netcare:menu />
	</netcare:body>
</netcare:page>