
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
<%@ tag language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>

<div id="historyOptions">
	<input id="historyBoxId" type="checkbox" />
	<strong><spring:message code="report.showHistory" /></strong>
</div>

<div id="reportFormDiv" class="modal hide fade" style="display: none;">
	<div class="modal-header">
		<a href="#" class="close" data-dismiss="modal">x</a>
		<h3>
			<spring:message code="report.title" />
		</h3>
		<h5 id="plannedId"></h5>
	</div>
	<div class="modal-body">
		<form id="reportFormId">
			<input type="hidden" name="activityId" />
			<input type="hidden" name="numValueId" />

			<div id="dateTimeInputId" style="display: none;">
				<input type="text" name="date" class="input-small" />&nbsp;-
				<input type="text" name="time" class="input-mini" />				
			</div>
			
			<!--  Space for measurement input -->
			<table id="measurementTableId" class="table-condensed">
				<tbody></tbody>
			</table>

			<div id="senseSectionId">
				<spring:message code="report.sense" var="sense" scope="page" />
				<netcare:field name="senseField" label="${sense}">
					<div style="font-size: 10px; font-style: italic" id="senseTextId"></div>
					<table class="table-condensed">
						<thead>
							<tr>
								<th>&nbsp;</th>
								<th>1</th>
								<th>2</th>
								<th>3</th>
								<th>4</th>
								<th>5</th>
								<th>&nbsp;</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td id="senseLowId">&nbsp;</td>
								<td><input type="radio" name="gsense" value="1" /></td>
								<td><input type="radio" name="gsense" value="2" /></td>
								<td><input type="radio" name="gsense" value="3" /></td>
								<td><input type="radio" name="gsense" value="4" /></td>
								<td><input type="radio" name="gsense" value="5" /></td>
								<td id="senseHighId">&nbsp;</td>
							</tr>
						</tbody>
					</table>
				</netcare:field>
			</div>

			<spring:message code="report.note" var="note" scope="page" />
			<netcare:field name="note" label="${note}">
				<input type="text" name="note" class="input-xlarge" />
			</netcare:field>
		<div class="modal-footer">
			<input type="submit" name="save"
				value="<spring:message code="report.save" />" class="btn btn-primary" />
		</div>
	</form>
	</div>
</div>

<netcare:table id="schemaTable">
	<thead>
		<tr>
			<th colspan='2'><spring:message code="report.scheduled" /></th>
			<th><spring:message code="report.activity" /></th>
			<th><spring:message code="report.value" /></th>
			<th><spring:message code="report.reported" /></th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</netcare:table>
