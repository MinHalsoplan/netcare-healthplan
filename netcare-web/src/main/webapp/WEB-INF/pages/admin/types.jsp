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

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<mvk:page>
	<mvk:header title="Netcare 2.0" resourcePath="/netcare/resources" contextPath="${pageContext.request.contextPath}">
		<netcare:css resourcePath="/netcare/resources" />
		<netcare:js resourcePath="/netcare/resources"/>
		<hp:healthplan-js />
		
		<sec:authentication property='principal.careUnit.hsaId' var="currentHsaId" scope="page" />;
	
		<script type="text/javascript">
			$(function() {
				
				var support = new NC.Support();
				var captions;
				support.loadMessages('measureValue.new, measureValue.unit, measureValue.alarm, label.yes, label.no, label.add', function(messages) {
					captions = messages;
				});
				
				var measureValues = new Array();
				
				var unitOpts = new Array();
				support.getUnits(function(data) {
					$.each(data.data, function(i, v) {
						unitOpts.push($('<option>').attr('value', v.code).html(v.value));
					});
				});
				
				var typeOpts = new Array();
				support.getMeasureValueTypes(function(data) {
					$.each(data.data, function(i, v) {
						NC.log("Adding " + v.code + " " + v.value);
						typeOpts.push($('<option>').attr('value', v.code).html(v.value));
					});
					
					$('#valueType').append($('<option>').attr('value', '-1').html('--'));
					$.each(typeOpts, function(i, v) {
						$('#valueType').append(v);
					});
				});
				
				new NC.ActivityCategories().load(function(data) {
					$.each(data.data, function(i, v) {
						NC.log("Processing: " + v.code + " " + v.value);
						$('#activityCategory').append($('<option>').attr('value', v.id).html(v.name));
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
							
							var unitLabel = findOptionName(v.unit.code, unitOpts);
							var valueType = findOptionName(v.valueType.code, typeOpts);
							
							var row = $('<tr>');
							
							row.append(
								$('<td>').html(v.name)
							).append(
								$('<td>').html(valueType)
							).append(
								$('<td>').html(unitLabel)
							);
							
							
							if (v.alarm == "on" || v.alarm == true) {
								row.append($('<td>').html(captions['label.yes']));
							} else {
								row.append($('<td>').html(captions['label.no']));
							}
							
							var deleteIcon = new NC.Util().createIcon('trash', '24', function() {
								removeMeasuredValue(i);
								updateMeasureValueTable();
							}, 'measureValue.remove');
							
							row.append(
								$('<td>').css('text-align', 'right').append(deleteIcon)
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
					var div = $('<div>').addClass('span4');
					
					var label = $('<label>').attr('for', 'measureUnit').html(captions['measureValue.unit']);
					div.append(label);
					
					var input = $('<select>').attr('name', 'measureUnit').attr('id', 'measureUnit').addClass('input-small');
					
					$.each(unitOpts, function(i, v) {
						NC.log("Adding unit: " + v.html());
						input.append(v);
					});
					
					div.append(input);
					$('#measureValueContainer').append(div);
				};
				
				var createAlarmBox = function() {
					var div = $('<div>').addClass('span2');
					var label = $('<label>').attr('for', 'measureAlarm').html(captions['measureValue.alarm']);
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
					var btn = $('<input>').attr('id', 'addMeasureValue').attr('name', 'addMeasureValue').attr('type', 'submit').addClass('btn btn-info').attr('value', captions['label.add']);
					btn.click(function(e) {
						e.preventDefault();
						NC.log("Add measure value");
						
						var formData = new Object();
						formData.name = $('input[name="measureName"]').val();
						
						formData.valueType = new Object();
						formData.valueType.value = $('#valueType option:selected').val();
						formData.valueType.code = $('#valueType option:selected').attr('value');
						
						formData.unit = new Object();
						formData.unit.value = $('#measureUnit option:selected').val();
						formData.unit.code = $('#measureUnit option:selected').attr('value');
						
						if ($('#measureAlarm:checked').val() == "on") {
							formData.alarm = true;	
						} else {
							formData.alarm = false;
						}
						
						
						measureValues.push(formData);
						updateMeasureValueTable();
						resetMeasureValueForm();
					});
					
					
					div.append($('<label>').attr('for', 'addMeasureValue').html('&nbsp;'));
					div.append(btn);
					
					$('#measureValueContainer').append(div);
				};
				
				var showSingleMeasureValue = function() {
					removeMeasureValueForm();
					
					createMeasureUnit();
					createActionButton();
				};
				
				var showIntervalMeasureValue = function() {
					removeMeasureValueForm();
					
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
					e.preventDefault();
					
					var errors = false;
					if (!new NC.Util().validateFieldNotEmpty($('#name'))) {
						errors = true;
					}
					
					if (measureValues.length < 1) {
						errors = true;
					}
					
					if (errors) {
						return false;
					}
					
					var formData = new Object();
					formData.name = $('#name').val();
					
					formData.category = new Object();
					formData.category.id = $('#activityCategory option:selected').attr('value');
					formData.category.name = $('#activityCategory option:selected').text();
					
					if ($('#useSense').val() == 1) {
						formData.measuringSense = true;
					} else {
						formData.measuringSense = false;
					}
					
					if (formData.measuringSense == true) {
						formData.minScaleText = $('#minDescription').val();
						formData.maxScaleText = $('#maxDescription').val();
					}
					
					formData.measureValues = measureValues;
					
					new NC.ActivityTypes().create(formData, function(data) {
						loadExistingTypes();
						$(':reset').click();
					});
				});
				
				
				var hsaId = '<c:out value="${currentHsaId}" />';
				var loadExistingTypes = function() {
					new NC.ActivityTypes().load(hsaId, function(data) {
						if (data.data.length > 0) {
							
							$('#existingTypesContainer table tbody').empty();
							
							$.each(data.data, function(i, v) {
								
								var row = $('<tr>');
								
								row.append(
									$('<td>').html(v.name)
								);
								
								row.append(
									$('<td>').html(v.category.name)
								);
								
								if (v.measuringSense) {
									row.append($('<td>').html('Ja'));	
								} else {
									row.append($('<td>').html('Nej'));
								}
								
								var td = $('<td>');
								$.each(v.measureValues, function(index, value) {
									td.append(
										$('<span>').css('display', 'block').html(value.name + ' | ' + value.valueType.value + ' | ' + value.unit.value)
									);
								});
								
								row.append(td);
								
								$('#existingTypesContainer table tbody').append(row);
								
							});
							
							$('#existingTypesContainer div').hide();
							$('#existingTypesContainer table').show();
							
						} else {
							$('#existingTypesContainer div').show();
							$('#existingTypesContainer table').hide();
						}
					});
				};
				
				var resetForm = function() {
					$('#measureValues tbody').empty();
					$('#measureValues').hide();
					
					$('#measureValueForm').hide();
				}
				
				$(':reset').click(function(e) {
					resetForm();
				});
				
				loadExistingTypes();
				
				$('#showTypesForm').click(function(e) {
					e.preventDefault();
					$('#activityTypesContainer').toggle();
				});
				
				$('#activityTypesContainer').hide();
				
			});
		</script>
	</mvk:header>
	<mvk:body>
		<mvk:pageHeader title="Min hälsoplan - Profil" loggedInUser="Testar Test" loggedInAsText="Inloggad som : "
			logoutUrl="/netcare/security/logout" logoutText="Logga ut" />

		<mvk:pageContent>
			<mvk:leftMenu>
				<hp:menu />
			</mvk:leftMenu>
			<mvk:content title="Aktivitetskategorier">
		
				<section id="types">
					<h2><spring:message code="activityType.title" /></h2>
					<p>
						<span class="label label-info"><spring:message code="information" /></span>
						<spring:message code="activityType.description" />
					</p>
					<p style="text-align: right; padding-right: 20px;">
						<a id="showTypesForm" class="btn addButton"><spring:message code="activityType.new" /></a>
					</p>
					<div id="activityTypesContainer">
						<form>
							<div>
								<netcare:row>
									<netcare:col span="6">
										<spring:message code="activityType.name" scope="page" var="activityName" />
										<netcare:field name="name" label="${activityName}">
											<input type="text" name="name" id="name" class="xlarge"/>
										</netcare:field>
									</netcare:col>
									<netcare:col span="6">
										<spring:message code="activityType.category" var="category" scope="page" />
										<netcare:field name="activityCategory" label="${category}">
											<select name="activityCategory" id="activityCategory" class="xlarge">
											
											</select>
										</netcare:field>
									</netcare:col>
								</netcare:row>
								<netcare:row>
									<netcare:col span="6">
										<spring:message code="activityType.scale" scope="page" var="scale" />
										<netcare:field name="useSense" label="${scale}">
											<input type="checkbox" name="useSense" id="useSense" value="1" />
										</netcare:field>
									</netcare:col>
								</netcare:row>
								<netcare:row id="senseDescriptionContainer">
									<netcare:col span="6">
										<spring:message code="activityType.scaleMinDescription" var="minDesc" scope="page" />
										<spring:message code="activityType.scaleMaxDescription" var="maxDesc" scope="page" />
										<netcare:field name="minDescription" label="${minDesc}">
											<input type="text" name="minDescription" id="minDescription" class="xlarge" />
										</netcare:field>
									</netcare:col>
									<netcare:col span="6">
										<netcare:field name="maxDescription" label="${maxDesc}">
											<input type="text" name="maxDescription" id="minDescription" class="xlarge" />
										</netcare:field>
									</netcare:col>
									
								</netcare:row>
							
								<a id="addMeasureValue" href="#" class="btn addButton"><spring:message code="measureValue.new" /></a>
								
								<br />
								
								<div id="measureValueForm" style="display:none;">
									<netcare:row>
										<netcare:col span="3">
											<spring:message code="measureValue.name" var="lbl" scope="page" />
											<netcare:field name="measureName" label="${lbl}">
												<input type="text" name="measureName" class="input-medium"/>
											</netcare:field>
										</netcare:col>
										<netcare:col span="3">
											<label for="valueType"><spring:message code="measureValue.type" /></label>
											<select name="valueType" id="valueType" class="input-medium">
											</select>										
										</netcare:col>
										<div id="measureValueContainer" class="row-fluid span6"></div>
									</netcare:row>
								</div>
							</div>
							
							<netcare:table id="measureValues" style="display: none;">
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
							</netcare:table>
							<br />
							
							<div class="form-actions">
								<button id="createActivityType" class="btn info"><spring:message code="activityType.new" /></button>
								<button type="reset" class="btn"><spring:message code="clear" /></button>
							</div>
							
						</form>					
					</div>
					
				</section>
				
				<section id="existingTypes">
					<div id="existingTypesContainer">
						<netcare:block-message type="info" style="display:none;">
							<spring:message code="activityType.noTypes" />
						</netcare:block-message>
					
						<netcare:table>
							<thead>
								<tr>
									<th><spring:message code="activityType.name" /></th>
									<th><spring:message code="activityType.category" /></th>
									<th><spring:message code="activityType.scale" /></th>
									<th><spring:message code="activityType.measureValues" /></th>
								</tr>
							</thead>
							<tbody></tbody>
						</netcare:table>
					</div>
				</section>
			</mvk:content>
		</mvk:pageContent>
	</mvk:body>	
</mvk:page>
