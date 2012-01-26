
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
	<spring:message code="report.showHistory" />
</div>

<div id="reportFormDiv" class="modal hide fade" style="display: none;">
	<form id="reportFormId" class="form-stacked">
		<div class="modal-header">
			<a href="#" class="close">x</a>
			<h3>
				<spring:message code="report.title" />
			</h3>
			<h5 id="plannedId"></h5>
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
					<table class="condensed-table">
						<thead>
							<th>1</th>
							<th>2</th>
							<th>3</th>
							<th>4</th>
							<th>5</th>
						</thead>
						<tbody>
							<tr>
								<td><input type="radio" name="gsense" value="1" /></td>
								<td><input type="radio" name="gsense" value="2" /></td>
								<td><input type="radio" name="gsense" value="3" /></td>
								<td><input type="radio" name="gsense" value="4" /></td>
								<td><input type="radio" name="gsense" value="5" /></td>
							</tr>
						</tbody>
					</table>
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

<table id="schemaTable" class="bordered-table zebra-striped shadow-box">
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
</table>