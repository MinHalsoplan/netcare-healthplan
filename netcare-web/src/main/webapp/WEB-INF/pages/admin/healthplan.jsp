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

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<c:set var="patientId" value="${sessionScope.currentPatient.id}" scope="page" />
<c:set var="patient" value="${sessionScope.currentPatient.name}" scope="page" />

<hp:view>
	<hp:viewHeader>
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				
				var params = {
					healthplanId : -1,
					patientId : '<c:out value="${patientId}" />'
				};
				
				NC_MODULE.HEALTH_PLAN.init(params);
				
				/*
				 * Set todays date
				 */
				/*$('input[name="startDate"]').datepicker('setDate', new Date());
				
				var updateDescription = function(count) {
					NC.log("Updating ordination table description");
					if (count == 0) {
						$('#healthPlanContainer div').show();
						$('#ordinationTable').hide();
					} else {
						$('#healthPlanContainer div').hide();
						$('#ordinationTable').show();
					}
				};
				
				var healthPlans = NC.HealthPlan();
				var listCallback = function(data) {
					NC.log("Success. Processing results...");
					
					// Empty the result list
					$('#ordinationTable tbody > tr').empty();
					
					var infoMessages;
					
					support.loadMessages('healthplan.icons.performRenewal, healthplan.icons.stopRenewal, healthplan.inActiveInfo, healthplan.autoRenewal, healthplan.icons.result, healthplan.icons.edit', function(messages) {
						infoMessages = messages;
					});

					
					$.each(data.data, function(index, value) {
						NC.log("Processing index " + index + " value: " + value.name);
						
						var util = NC.Util();
						
						var resultIcon = util.createIcon('results', 24, function() {
							healthPlans.results(value.id);
						}, infoMessages['healthplan.icons.result'], true);
						
						var editIcon = util.createIcon('edit', 24, function() {
							healthPlans.view(value.id);
						}, infoMessages['healthplan.icons.edit'], true).css('padding-left', '10px');
						
						var renewalIcon;
						if (value.autoRenewal) {
							renewalIcon = util.createIcon('exit', 24, function() {
								$('#stop-renewal').modal('show');
								$('#stop-renewal a.btn-info').click(function(e) {
									e.preventDefault();
									healthPlans.stopAutoRenewal(value.id, function(data) {
										$('#hn-' + value.id).html('<strong>' + data.data.name + '</strong>');
										renewalIcon.addClass('ui-state-disabled');
										renewalIcon.unbind('click');
									});
									$('#stop-renewal a.btn-info').unbind('click');
									$('#stop-renewal').modal('hide');
								})
							}, infoMessages['healthplan.icons.stopRenewal'], true);
						} else {
							renewalIcon = util.createIcon('renew', 24, function() {
								$('#perform-renewal').modal('show');
								$('#perform-renewal a.btn-info').click(function(e) {
									e.preventDefault();
									healthPlans.performExplicitRenewal(value.id, function(data) {
										NC.log('Renewal : ' + data.data.endDate);
										var dur = '<strong>' + data.data.endDate + '<br/>' 
											+ ((data.data.iteration > 0) ? (data.data.iteration+1) + 'x' : '') 
											+ value.duration + ' ' + value.durationUnit.value
											+ '</strong>';
										$('#hd-' + value.id).html(dur);
										renewalIcon.addClass('ui-state-disabled');
										renewalIcon.unbind('click');
									});
									$('#perform-renewal a.btn-info').unbind('click');
									$('#perform-renewal').modal('hide');
								})
							}, infoMessages['healthplan.icons.performRenewal'], true);								
						}
						renewalIcon.css('padding-left','10px');
						
						if (!value.active) {
							editIcon.addClass('ui-state-disabled');
							editIcon.unbind('click');
						}
						
						var actionCol = $('<td>').css('text-align', 'right');
						
						resultIcon.appendTo(actionCol);
						editIcon.appendTo(actionCol);
						renewalIcon.appendTo(actionCol);
						

						var duration = value.endDate + '<br/>' + ((value.iteration > 0) ? (value.iteration+1) + 'x' : '') + value.duration + ' ' + value.durationUnit.value;
						var name = value.name;
						if (value.autoRenewal) {
							name += '<br/><i style="font-size: 10px;">'+ infoMessages['healthplan.autoRenewal'] + '</i>';
						} else if (!value.active) {
							name += '<br/><i style="font-size: 10px;">'+ infoMessages['healthplan.inActiveInfo'] + '</i>';							
						}
						
						$('#ordinationTable tbody').append(
								$('<tr>').append(
									$('<td>').attr('id', 'hn-' + value.id).html(name)).append(
										$('<td>').html(value.startDate)).append(
												$('<td>').attr('id', 'hd-' + value.id).html(duration)).append(
														$('<td>').html(value.issuedBy.name)).append(
																actionCol));
					});
					
					NC.log("Updating description");
					updateDescription(data.data.length);
				}
				
				healthPlans.list(<c:out value="${sessionScope.currentPatient.id}" />, function(data) {
					listCallback(data);
				});
				
				updateDescription(0);*/
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Hälsoplaner">
		<c:set var="curPatient" value="${sessionScope.currentPatient.name}" scope="page" />
		<spring:message code="healthplan.new" var="newHealthPlan" scope="page"/>
		
		<h2><spring:message code="healthplan.title" arguments="${curPatient}"/></h2>
		<p>
			<span class="label label-info"><spring:message code="information" /></span>
			<spring:message code="healthplan.desc" arguments="${curPatient},${newHealthPlan}" />
		</p>
		
		<spring:message code="clear" var="clear" scope="page" />
		<spring:message code="healthplan.duration" var="duration" scope="page" />
		<spring:message code="healthplan.name" var="name" scope="page" />
		<spring:message code="healthplan.type" var="type" scope="page" />
		<spring:message code="healthplan.start" var="startDate" scope="page" />
		<spring:message code="healthplan.issuedBy" var="issuedBy" scope="page" />
		<spring:message code="healthplan.autoRenewal" var="autoRenewal" scope="page" />
		
		<p style="text-align: right; padding-right: 20px;">
			<a id="showCreateForm" class="btn addButton"><c:out value="${newHealthPlan}" /></a>
		</p>
		<form id="createHealthPlanForm" action="#" method="post">
			<fieldset>
				<legend><spring:message code="healthplan.new" /></legend>
				<netcare:field name="name" label="${name}">
					<input type="text" name="name" class="xlarge" />
				</netcare:field>
				
				<netcare:field name="startDate" label="${startDate}">
					<netcare:dateInput name="startDate" />
				</netcare:field>
				
				<netcare:row>
					<netcare:col span="6">
						<netcare:field name="duration" label="${duration}">
							<input type="number" min="1" name="duration" class="medium signedNumeric"/>
						</netcare:field>
					</netcare:col>
				</netcare:row>
				
				<netcare:row>
					<netcare:col span="6">
						<netcare:field name="type" label="${type}">
							<select name="type"></select>
						</netcare:field>
					</netcare:col>
				</netcare:row>

				<netcare:field name="autoRenewal" label="${autoRenewal}">
					<input type="checkbox" name="autoRenewal" value="true"/>
				</netcare:field>
			</fieldset>
			
			<div class="form-actions">
				<input type="submit" class="btn btn-info" value="${newHealthPlan}" />
				<input type="reset" class="btn" value="${clear}" />
			</div>
			
		</form>
		
		<mvk:touch-list id="healthPlanContainer">
		
		</mvk:touch-list>

		<netcare:modal titleCode="healthplan.icons.performRenewal" confirmCode="label.yes" id="perform-renewal">
			<p>
				<span class="label label-info"><spring:message code="label.information" /></span>
				<spring:message code="healthplan.confirm.performRenewal" />
			</p>
		</netcare:modal>

		<netcare:modal titleCode="healthplan.icons.stopRenewal" confirmCode="label.yes" id="stop-renewal">
			<p>
				<span class="label label-info"><spring:message code="label.information" /></span>
				<spring:message code="healthplan.confirm.stopAutoRenewal" />
			</p>
		</netcare:modal>
	</hp:viewBody>
</hp:view>
