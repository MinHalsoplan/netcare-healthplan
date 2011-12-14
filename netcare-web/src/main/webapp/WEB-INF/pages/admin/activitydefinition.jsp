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
				
				var units = new Array();
				var select = $('#activityDefinitionForm select[name="activityType"]');
				
				var showUnit = function(optionElem) {
					console.log("Selected element: " + optionElem.val());
					$.each(units, function(index, value) {
						if (optionElem.html() === units[index].name) {
							$('span.unit').html('<strong>(' + units[index].unit.value + ')</strong>');
						}
					});
				}
				
				types.load(function(data) {
					var firstOption;
					$.each(data, function(index, value) {
						console.log("Processing: " + value.name);
						units[index] = value;
						
						var opt = $('<option>', { value : value.id }).html(value.name);
						if (index == 0) {
							firstOption = opt;
						}
						
						select.append(opt);
					});
					
					/*
					 * Display unit on the currently selected option
					 */
					showUnit(firstOption);
				});
				
				select.change(function() {
					var selected = $('#activityDefinitionForm select option:selected');
					console.log("Selected element is: " + selected.html());
					
					showUnit(selected);
				});
				
				var util = new NC.Util();
				var addTimeField = $('#activityDefinitionForm input[name="addTime"]');
				
				util.validateTimeField(addTimeField, function(text) {
					var liElem = $('<li>').html(text);
					var deleteIcon = util.createIcon('bullet_delete', function() {
						liElem.detach();
						
						/*
						 * If we dont have any times left. Don't
						 * show the container
						 */
						if ($('#addedTimes li').size() == 0) {
							$('#addedTimesContainer').hide();
						}
						
					});
					
					liElem.append(deleteIcon);
					
					$('#addedTimes').append(liElem);
					addTimeField.val('');
					
					/*
					 * Show the container since we just added
					 * a time
					 */
					$('#addedTimesContainer').show();
				});
				
				/*
				 * Initially hide the addedTimesContainer since
				 * no times should have been added
				 */
				$('#addedTimesContainer').hide();
				
				/*
				 * Bind the form submission and package what is going
				 * to be sent to the server as a JSON object
				 */
				$('#activityDefinitionForm :submit').click(function(event) {
					console.log("Form submission...");
					event.preventDefault();
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
					<select name="activityType" class="xlarge"></select>
				</netcare:field>
				
				<netcare:field name="activityGoal" label="Målsättning">
					<input name="activityGoal" type="number" class="xlarge" /> <span class="unit"></span>
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
					<input type="text" name="addTime" class="xlarge" /> <span><strong>(Ex: 10:15)</strong></span>
				</netcare:field>
				
				<div id="addedTimesContainer" class="clearfix">
					<p><strong>Tider</strong></p>
					<ul id="addedTimes">
					
					</ul>
				</div>
			
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