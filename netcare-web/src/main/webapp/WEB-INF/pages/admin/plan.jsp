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

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<hp:view>
	<hp:viewHeader>
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				
				var params = {
					healthPlanId : '<c:out value="${requestScope.healthPlanId}" />',
					definitionId : '<c:out value="${requestScope.definitionId}" />',
					templateId : '<c:out value="${param.template}" />'
				};
				
				NC_MODULE.PLAN_ACTIVITY.init(params);
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Planera aktivitet">
		
			<fieldset id="activityFieldset">
				<legend><spring:message code="activity.form.goals" /></legend>
			</fieldset>
			
			<fieldset id="scheduleFieldset">
				<legend>Period</legend>
				<netcare:row>
					<netcare:col span="6">
						<spring:message code="startDate" var="start" scope="page" />
						<netcare:field containerId="startDate" name="startDate" label="${start}">
							<netcare:dateInput name="startDate" />
						</netcare:field>
					</netcare:col>
					<netcare:col span="6">
						<spring:message code="repeatSchedule" var="repeat" scope="page"/>
						<netcare:field name="activityRepeat" label="${repeat}">
							<input name="activityRepeat" type="number" value="1"/>
							<span><spring:message code="week" /></span>
						</netcare:field>
					</netcare:col>
				</netcare:row>
			</fieldset>
			
			
			<spring:message code="activity.form.time" var="addTime" scope="page" />
			
			<fieldset>
				<legend><spring:message code="activity.form.specifyTimes" /></legend>
				<form id="addTimesForm" class="form-inline">
					<netcare:field name="specifyTime" label="${addTime}">
						<input id="specifyTime" name="specifyTime" type="text" placeholder="<spring:message code="pattern.time" />" class="span2"/>
						<button type="submit" class="btn btn-info">Lägg till</button>
						<button type="reset" class="btn btn-danger">Rensa</button>
					</netcare:field>
					
					<input type="checkbox" name="monday"> <label for="monday" style="padding-right: 10px;">Må</label>
					<input type="checkbox" name="tuesday"> <label for="tuesday" style="padding-right: 10px;">Ti</label>
					<input type="checkbox" name="wednesday"> <label for="wednesday" style="padding-right: 10px;">On</label>
					<input type="checkbox" name="thursday"> <label for="thursday" style="padding-right: 10px;">To</label>
					<input type="checkbox" name="friday"> <label for="friday" style="padding-right: 10px;">Fr</label>
					<input type="checkbox" name="saturday"> <label for="saturday" style="padding-right: 10px;">Lö</label>
					<input type="checkbox" name="sunday"> <label for="sunday" style="padding-right: 10px;">Sö</label>
				</form>
				
				<div id="monday-container" style="display: none;">
					<h4>Måndag</h4>
					<div class="times"></div>
				</div>
				
				<div id="tuesday-container" style="display: none;">
					<h4>Tisdag</h4>
					<div class="times"></div>
				</div>
				
				<div id="wednesday-container" style="display: none;">
					<h4>Onsdag</h4>
					<div class="times"></div>
				</div>
				
				<div id="thursday-container" style="display: none;">
					<h4>Torsdag</h4>
					<div class="times"></div>
				</div>
				
				<div id="friday-container" style="display: none;">
					<h4>Fredag</h4>
					<div class="times"></div>
				</div>
				
				<div id="saturday-container" style="display: none;">
					<h4>Lördag</h4>
					<div class="times"></div>
				</div>
				
				<div id="sunday-container" style="display: none;">
					<h4>Söndag</h4>
					<div class="times"></div>
				</div>
				
			</fieldset>
			
			<form id="saveForm">
				<div class="form-actions">
					<button type="submit" class="btn info"><spring:message code="activity.form.submit" /></button>
					<button type="reset" class="btn"><spring:message code="clear" /></button>
				</div>
			</form>
	</hp:viewBody>
</hp:view>