<%--

    Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>

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
		<sec:authorize access="hasRole('CARE_ACTOR')" var="isCareActor" />
		<sec:authorize access="hasRole('COUNTY_COUNCIL_ADMINISTRATOR')" var="isCountyActor" />
		<sec:authorize access="hasRole('NATION_ADMINISTRATOR')" var="isNationActor" />
		<hp:templates />
		<script type="text/javascript">
			$(document).ready(function() {
				var params = {
			 		templateId : <c:out value="${currentId}" />,
					hsaId : '<c:out value="${currentHsaId}" />',
					isCareActor : '<c:out value="${isCareActor}" />',
					isCountyActor : '<c:out value="${isCountyActor}" />',
					isNationActor : '<c:out value="${isNationActor}" />'
				};
				var my = NC_MODULE.ACTIVITY_TEMPLATE;
				my.initSingleTemplate(params);

			});
		</script>
	</hp:viewHeader>
	<c:url value="/netcare/admin/templates" var="backToUrl" />
	<hp:viewBody title="Ny aktivitetsmall">
			<div id="activityTypeContainer">
			
				<h3 id="templateTitle"></h3>
				
				<div id="accessNote" class="alert alert-error" style="display: none;">
					<spring:message code="template.noAccess" />
				</div>
			
				<div id="template">
					<form id="templateForm">
					<div id="chooseName">
						<spring:message code="template.select.name" var="selectNameLabel" scope="page"/>
						<spring:message code="template.select.category" var="selectCategoryLabel" scope="page"/>
						<spring:message code="template.select.level" var="selectLevelLabel" scope="page"/>
						<h4>1. <spring:message code="template.step1.title" /></h4>
						<netcare:field containerId="activityTypeNameContainer" name="activityTypeName" label="">
							<input id="activityTypeName" name="activityTypeName" type="text" class="required">
						</netcare:field>
					</div>
					<div id="selectCategory">
						<h4>2. ${selectCategoryLabel}</h4>
						<select id="activityTypeCategory" name="activityTypeCategory"></select>
					</div>
					<div id="selectAccessLevel">
						<h4>3. ${selectLevelLabel}</h4>
					</div>
					<div id="chooseActivities">
						<h4>
							4.
							<spring:message code="template.step2.title" />
						</h4>
						<div id="activityTypeWrapper" style="background-color:white; padding: 3px;">
							<mvk:touch-list id="activityTypeItems">
							</mvk:touch-list>
						</div>
						<div id="addActivityButtons">
							<div id="addListItemWrapper">
								<span style="padding-right: 15px"><a href="#" class="addListItem" id="addMeasurementButton"><spring:message code="template.activity.measurement.title" /></a> <a href="#"
									class="addListItem" id="addEstimationButton"><spring:message code="template.activity.estimation.title" /></a> <a href="#" class="addListItem" id="addYesNoButton"><spring:message code="template.activity.yesno.title" /></a>
								<a href="#" class="addListItem" id="addTextButton"><spring:message code="template.activity.text.title" /></a></span> 
							</div>
						</div>
					</div>
					<div id="chooseSave">
						<h4><spring:message code="template.step3.title" /></h4>
						<div id="chooseSaveWrapper" style="background-color: white; padding: 3px;">
              <button id="activitySaveButton" class="btn btn-info" type="button"><spring:message code="template.save" /></button>
              <button id="activityCancelButton" class="btn" type="button"><spring:message code="template.cancel" /></button>
						</div>
					</div>
					</form>
				</div>
			</div>
			<div id="activityItemFormContainer" style="display: none;"></div>
	</hp:viewBody>
</hp:view>

