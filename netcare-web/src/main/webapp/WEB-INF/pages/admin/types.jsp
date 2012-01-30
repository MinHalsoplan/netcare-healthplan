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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			$(function() {
				var measureValues = new Array();
				
				var unitOpts = new Array();
				new NC.Support().getUnits(function(data) {
					$.each(data.data, function(i, v) {
						unitOpts.push($('<option>').attr('value', v.code).html(v.value));
					});
				});
				
				var typeOpts = new Array();
				new NC.Support().getMeasureValueTypes(function(data) {
					$.each(data.data, function(i, v) {
						NC.log("Adding " + v.code + " " + v.value);
						typeOpts.push($('<option>').attr('value', v.code).html(v.value));
					});
					
					$('#valueType').append($('<option>').attr('value', '-1').html('--'));
					$.each(typeOpts, function(i, v) {
						$('#valueType').append(v);
					});
				});
				
				var categoryOpts = new Array();
				new NC.ActivityCategories().load(function(data) {
					$.each(data.data, function(i, v) {
						var opt = $('<option>').attr('value', v.code).html(v.value);
						
						categoryOpts.push(opt);
						$('#activityCategory').append(opt);
					});
				});
				
				findOptionName = function(value, arr) {
					var text = '';
					$.each(arr, function(i, v) {
						if (v.attr('value') == value) {
							text = v.text();
						}
					});
					
					return text;
				};
				
				var removeMeasuredValue = function(idx) {
					NC.log("Array before removal: " + measureValues);
					
					$.each(measureValues, function(i, v) {
						if (i == idx) {
							measureValues.splice(i, 1);
						}
					});
					
					NC.log("Array is now: " + measureValues);
				};
				
				var updateMeasureValueTable = function() {
					if (measureValues.length > 0) {
						
						$('#measureValues tbody').empty();
						
						$.each(measureValues, function(i, v) {
							
							var unitLabel = findOptionName(v.unit, unitOpts);
							var valueType = findOptionName(v.valueType, typeOpts);
							
							var row = $('<tr>');
							
							row.append(
								$('<td>').html(v.name)
							).append(
								$('<td>').html(valueType)
							).append(
								$('<td>').html(unitLabel)
							);
							
							if (v.alarm == "on") {
								row.append($('<td>').html('Ja'));
							} else {
								row.append($('<td>').html('Nej'));
							}
							
							var deleteIcon = new NC.Util().createIcon('trash', '24', function() {
								removeMeasuredValue(i);
								updateMeasureValueTable();
							});
							
							row.append(
								$('<td>').append(deleteIcon)
							);
							
							$('#measureValues tbody').append(row);
						});
						
						$('#measureValues').show();
					} else {
						$('#measureValues').hide();
					}
				};
				
				var removeMeasureValueForm = function() {
					if ($('#measureValueContainer div').size() > 0) {
						$('#measureValueContainer').empty();
					}
				};
				
				var createMeasureUnit = function() {
					var div = $('<div>').addClass('span2');
					
					var label = $('<label>').attr('for', 'measureUnit').html('Enhet');
					div.append(label);
					
					var input = $('<select>').attr('name', 'measureUnit').attr('id', 'measureUnit').addClass('small');
					
					$.each(unitOpts, function(i, v) {
						NC.log("Adding unit: " + v.html());
						input.append(v);
					});
					
					div.append(input);
					$('#measureValueContainer').append(div);
				};
				
				var createAlarmBox = function() {
					var div = $('<div>').addClass('span1');
					var label = $('<label>').attr('for', 'measureAlarm').html('Alarm');
					div.append(label);
					
					var input = $('<input>').attr('type', 'checkbox').attr('name', 'measaureAlarm').attr('id', 'measureAlarm');
					div.append(input);
					$('#measureValueContainer').append(div);
				};
				
				var resetMeasureValueForm = function() {
					$('input[name="measureName"]').val('');
					$('input[name="measureName"]').focus();
				};
				
				var createActionButton = function() {
					var div = $('<div>').addClass('span2');
					var btn = $('<input>').attr('id', 'addMeasureValue').attr('name', 'addMeasureValue').attr('type', 'submit').addClass('btn').addClass('primary').attr('value', 'Lägg till');
					btn.click(function(e) {
						e.preventDefault();
						NC.log("Add measure value");
						
						var formData = new Object();
						formData.name = $('input[name="measureName"]').val();
						formData.valueType = $('#valueType option:selected').val();
						formData.unit = $('#measureUnit option:selected').val();
						formData.alarm = $('#measureAlarm:checked').val();
						
						measureValues.push(formData);
						updateMeasureValueTable();
						resetMeasureValueForm();
						
						
					});
					
					
					div.append($('<label>').attr('for', 'addMeasureValue').html('&nbsp;'));
					div.append(btn);
					
					$('#measureValueContainer').append(div);
				};
				
				/*var createMeasureValue = function(label) {
					var container = $('<div>').addClass('span2');
					var div = $('<div>').addClass('clearfix');
					
					var label = $('<label>').attr('for', 'measureValue').html(label);
					div.append(label);
					
					var inputDiv = $('<div>').addClass('input');
					var input = $('<input>').attr('type', 'text').attr('name', 'measureValue').attr('id', 'measureValue').addClass('small');
					inputDiv.append(input);
					div.append(inputDiv);
					
					container.append(div);
					$('#measureValueContainer').append(container);
				};*/
				
				var showSingleMeasureValue = function() {
					removeMeasureValueForm();
					
					//createMeasureValue('Målvärde');
					createMeasureUnit();
					createActionButton();
				};
				
				var showIntervalMeasureValue = function() {
					removeMeasureValueForm();
					
					//createMeasureValue('Målvärde (min)');
					//createMeasureValue('Målvärde (max)');
					createMeasureUnit();
					createAlarmBox();
					createActionButton();
				}
				
				var showMeasureValueForm = function() {
					$('#measureGoalMin').hide();
					$('#measureGoalMax').hide();
					$('#measureUnit').hide();
					
					$('#measureValueForm').show();
				};
				
				$('#addMeasureValue').click(function(e) {
					e.preventDefault();
					showMeasureValueForm();
				});
				
				$('#valueType').change(function(e) {
					var val = $(this).val();
					switch(val) {
					case "-1":
						removeMeasureValueForm();
						break;
					case "SINGLE_VALUE":
						showSingleMeasureValue();
						break;
					case "INTERVAL":
						showIntervalMeasureValue();
						break;
					}
				});
				
				$('#useSense').click(function(e) {
					if ($(this).attr('checked') == "checked") {
						$('#senseDescriptionContainer input').prop('disabled', false);
					} else {
						$('#senseDescriptionContainer input').prop('disabled', true);
					}
				});
				
				$('#senseDescriptionContainer input').prop('disabled', true);
				
				
				$('#createActivityType').click(function(e) {
					
					var formData = new Object();
					formData.name = $('#name').val();
					//formData.
					
				});
				
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<section id="types">
				<h2><spring:message code="activityType.title" /></h2>
				<p>
					<span class="label info"><spring:message code="information" /></span>
					<spring:message code="activityType.description" />
				</p>
				<div id="activityTypesContainer">
					<form class="form-stacked">
						<div>
							<netcare:row>
								<netcare:col span="5">
									<spring:message code="activityType.name" scope="page" var="activityName" />
									<netcare:field name="name" label="${activityName}">
										<input type="text" name="name" id="name" />
									</netcare:field>
								</netcare:col>
								<netcare:col span="5">
									<spring:message code="activityType.category" var="category" scope="page" />
									<netcare:field name="activityCategory" label="${category}">
										<select name="activityCategory" id="activityCategory">
										
										</select>
									</netcare:field>
								</netcare:col>
							</netcare:row>
							<netcare:row>
								<netcare:col span="3">
									<spring:message code="activityType.scale" scope="page" var="scale" />
									<netcare:field name="useSense" label="${scale}">
										<input type="checkbox" name="useSense" id="useSense" value="1" />
									</netcare:field>
								</netcare:col>
							</netcare:row>
							<netcare:row id="senseDescriptionContainer">
								<netcare:col span="5">
									<spring:message code="activityType.scaleMinDescription" var="minDesc" scope="page" />
									<spring:message code="activityType.scaleMaxDescription" var="maxDesc" scope="page" />
									<netcare:field name="minDescription" label="${minDesc}">
										<input type="text" name="minDescription" id="minDescription" class="xlarge" />
									</netcare:field>
								</netcare:col>
								<netcare:col span="5">
									<netcare:field name="maxDescription" label="${maxDesc}">
										<input type="text" name="maxDescription" id="minDescription" class="xlarge" />
									</netcare:field>
								</netcare:col>
								
							</netcare:row>
						
							<a id="addMeasureValue" href="#" class="btn addButton"><spring:message code="measureValue.new" /></a>
							
							<br />
							
							<div id="measureValueForm" style="display:none;">
								<netcare:row>
									<netcare:col span="2">
										<spring:message code="measureValue.name" var="lbl" scope="page" />
										<netcare:field name="measureName" label="${lbl}">
											<input type="text" name="measureName" class="small"/>
										</netcare:field>
									</netcare:col>
									<netcare:col span="2">
										<label for="valueType"><spring:message code="measureValue.type" /></label>
										<select name="valueType" id="valueType" class="small">
										</select>										
									</netcare:col>
									<div id="measureValueContainer" class="row span7"></div>
								</netcare:row>
							</div>
						</div>
						
						<table id="measureValues" class="bordered-table zebra-striped shadow-box" style="display: none;">
							<thead>
								<tr>
									<th><spring:message code="measureValue.name" /></th>
									<th><spring:message code="measureValue.type" /></th>
									<th><spring:message code="measureValue.unit" /></th>
									<th><spring:message code="measureValue.alarm" /></th>
									<th>&nbsp;</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
						
						<div class="actions">
							<button id="createActivityType" style="text-align: left;" class="btn primary"><spring:message code="activityType.create" /></button>
						</div>
						
					</form>					
				</div>
				
			</section>
		</netcare:content>
	</netcare:body>
</netcare:page>