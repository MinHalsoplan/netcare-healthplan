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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>

<netcare:page>
	<netcare:header>
			<script type="text/javascript">
			$(function() {
				
				var createOption = function(code, value) {
					var option = new Object();
					option.code = code;
					option.value = value;
					return option;
				}
				var support = new NC.Support();

				var report = new NC.PatientReport('schemaDescription', 'schemaTable');
				report.list();

				$('#reportFormDiv input[name="date"]').datepicker({
					dateFormat : 'yy-mm-dd',
					firstDay : 1,
					minDate : -14
				});
				
				support.loadMonths(function(data) {
					$('#reportFormDiv input[name="date"]').datepicker('option', 'monthNames', data);
				});
				
				support.loadWeekdays(function(data) {
					$('#reportFormDiv input[name="date"]').datepicker('option', 'dayNamesMin', data);
				});
				
				var util = new NC.Util();
				util.validateTimeField($('#reportFormDiv input[name="time"]'));
				
								
				$('#reportFormId :submit').click(function(event) {
					event.preventDefault();
					var id = $('#reportFormDiv input[name="activityId"]').val();
					var rep = new Object();
					rep.actualValue = $('#reportFormDiv input[name="value"]').val();
					rep.actualDate = $('#reportFormDiv input[name="date"]').val();
					rep.actualTime = $('#reportFormDiv input[name="time"]').val();
					rep.sense = $('#reportFormDiv input[name="gsense"]:checked').val();
					rep.note = $('#reportFormDiv input[name="note"]').val();
					rep.rejected = false;
					
					var jsonObj = JSON.stringify(rep);					
					report.performReport(id, jsonObj, function(data) {
						$('#reportFormDiv').modal('hide');
					});
				});
				
				$('#historyBoxId').click(function() {
					report.showHistory($(this).is(':checked'));
				});
				
			});
				
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h1><spring:message code="report.header" /></h1>
			<p id="schemaDescription"></p>
			<input id="historyBoxId" type="checkbox" />
			<spring:message code="report.showHistory" />

			<div id="reportFormDiv" class="modal hide fade"
				style="display: none;">
				<form id="reportFormId" class="form-stacked">
					<div class="modal-header">
						<a href="#" class="close">x</a>
						<h3>
							<spring:message code="report.title" />
						</h3>
						<h5 id="plannedId">
						</h5>
					</div>
					<div class="modal-body">
						<input type="hidden" name="activityId" />

						<spring:message code="report.value" var="value" scope="page" />
						<netcare:field name="value" label="${value}">
							<input type="text" name="value" class="small" />
							<div id="unitId" style="display: inline; left-margin: px;"></div>
						</netcare:field>

						<spring:message code="report.time" var="time" scope="page" />
						<netcare:field name="datetime" label="${time}">
							<input type="text" name="date" class="small" />&nbsp;:
							<input type="text" name="time" class="mini" />
						</netcare:field>

						<div id="senseSectionId">
							<spring:message code="report.sense" var="sense" scope="page" />
							<netcare:field name="senseField" label="${sense}">
								<div style="font-size: 10px; font-style: italic" id="senseTextId"></div>
								1&nbsp;
								<input type="radio" name="gsense" value="1" />&nbsp; 
								<input type="radio" name="gsense" value="2" />&nbsp;
								<input type="radio" name="gsense" value="3" />&nbsp; 
								<input type="radio" name="gsense" value="4" />&nbsp;
								<input type="radio" name="gsense" value="5" />&nbsp;5
							</netcare:field>
						</div>

						<spring:message code="report.note" var="note" scope="page" />
						<netcare:field name="note" label="${note}">
							<input type="text" name="note" class="xlarge" />
						</netcare:field>					
					</div>
					<div class="modal-footer">
						<input type="submit" name="save"
							value="<spring:message code="report.save" />" class="btn primary" />
					</div>
				</form>
			</div>

			<table id="schemaTable" style="width: 98%; border-radius: 10px; box-shadow: 2px 2px 5px #333;" class="bordered-table zebra-striped">
				<thead>
					<tr>
						<th colspan='2'><spring:message code="report.scheduled" />
						</th>
						<th><spring:message code="report.activity" />
						</th>
						<th><spring:message code="report.value" />
						</th>
						<th><spring:message code="report.reported" />
						</th>						
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>

		</netcare:content>
	</netcare:body>
</netcare:page>