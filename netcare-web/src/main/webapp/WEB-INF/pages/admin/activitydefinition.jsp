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
				var types = NC.ActivityTypes();
				
				types.load(function(data) {
					$.each(data, function(index, value) {
						console.log("Processing: " + value.name);
						var opt = $('<option>', { value : value.id }).html(value.name);
						$('#activityDefinitionForm select').append(opt);
					})
					
				});
				
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2>Aktivitetsdefinition</h2>
			<p>
				Den här sidan låter dig schemalägga en ordination. Du kan ange vilka dagar samt vilka tider som aktiviteten
				skall utföras.
			</p>
			
			<netcare:form title="Schemalägg ordination för xxx" id="activityDefinitionForm" classes="form-stacked">
			
				<netcare:field name="activityType" label="Vad">
					<select name="activityType"></select>
				</netcare:field>
				
				<netcare:field name="activityGoal" label="Målsättning">
					<input name="activityGoal" type="number" class="xlarge" />
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
								<netcare:field name="mon" label="${monday}">
									<input type="checkbox" name="mon" />
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="mon" label="${tuesday}">
									<input type="checkbox" name="mon" />
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="mon" label="${wednesday}">
									<input type="checkbox" name="mon" />
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="mon" label="${thursday}">
									<input type="checkbox" name="mon" />
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="mon" label="${friday}">
									<input type="checkbox" name="mon" />
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="mon" label="${saturday}">
									<input type="checkbox" name="mon" />
								</netcare:field>
							</div>
							<div class="span1">
								<netcare:field name="mon" label="${sunday}">
									<input type="checkbox" name="mon" />
								</netcare:field>
							</div>
						</div>
					</div>
				</div>
				
				<netcare:field name="addTime" label="Lägg till tidpunkt">
					<input type="text" name="addTime" class="xlarge" />
				</netcare:field>
			
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