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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<hp:view>
	<hp:viewHeader>
		<sec:authentication property='principal.careUnit.hsaId' var="currentHsaId" scope="page" />
		<hp:templates />
		<script type="text/javascript">
			$(document).ready(function() {
				var params = {
			 		templateId : <c:out value="${currentId}" />,
					hsaId : '<c:out value="${currentHsaId}" />'
				};
				var my = NC_MODULE.ACTIVITY_TEMPLATE;
				my.initSingleTemplate(params, new NC.Support());

			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="NyAktivitetsmall">
		<div id="whole">
			<div id="activityTypeContainer">
				<section id="head">
					<h2>
						<spring:message code="template.title" />
					</h2>
					<p>
						<span class="label label-info"><spring:message code="information" /></span>
						<spring:message code="template.description" />
					</p>
				</section>
				<section id="template">
					<div id="chooseName">
						<h4>
							1.
							<spring:message code="template.select.name" />
						</h4>
						<input id="activityTypeName" name="activityTypeName" type="text" size="32">
					</div>
					<div id="selectCategory">
						<h4>2. <spring:message code="template.select.category" /></h4>
						<select id="categories" name="categories"></select>
					</div>
					<div id="chooseActivities">
						<h4>
							3.
							<spring:message code="template.select.activities" />
						</h4>
						<div id="activityTypeWrapper" style="background-color:white; padding: 3px;">
							<mvk:touch-list id="activityTypeItems">
							</mvk:touch-list>
						</div>
						<div id="addActivityButtons">
							<div id="addListItemWrapper">
								<span style="padding-right: 15px"></span> <a href="#" class="addListItem" id="addMeasurementButton">Mätning</a> <a href="#"
									class="addListItem" id="addEstimationButton">Skattning</a> <a href="#" class="addListItem" id="addYesNoButton">Ja/Nej-fråga</a>
								<a href="#" class="addListItem" id="addTextButton">Text</a>
							</div>
						</div>
					</div>
					<div id="chooseSave">
						<h4>
							4.
							<spring:message code="template.select.saveTemplate" />
						</h4>
						<div id="chooseSaveWrapper" style="background-color: white; padding: 3px;">
							<button id="activitySaveButton" class="btn btn-info" type="button">Spara aktivitetsmall</button>
						</div>
					</div>
				</section>
			</div>
			<div id="activityItemFormContainer" style="display: none;"></div>
		</div>
	</hp:viewBody>
</hp:view>

